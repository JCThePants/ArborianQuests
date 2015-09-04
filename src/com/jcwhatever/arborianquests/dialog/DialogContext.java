/*
 * This file is part of ArborianQuests for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.arborianquests.dialog;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.dialog.action.IDialogAction;
import com.jcwhatever.arborianquests.dialog.action.NpcTalkAction;
import com.jcwhatever.arborianquests.dialog.action.PadTimeAction;
import com.jcwhatever.arborianquests.dialog.action.PauseAction;
import com.jcwhatever.arborianquests.dialog.action.PlayerTalkAction;
import com.jcwhatever.arborianquests.dialog.action.RunnableAction;
import com.jcwhatever.arborianquests.dialog.output.ActionBarOutput;
import com.jcwhatever.arborianquests.dialog.output.ChatOutput;
import com.jcwhatever.arborianquests.dialog.output.IDialogOutput;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * 
 */
public class DialogContext implements IDialogContext {

    private static Set<DialogRunner> _runners = new HashSet<DialogRunner>(Bukkit.getMaxPlayers());
    private static MasterDialogRunner _masterRunner;

    private static final ActionBarOutput ACTION_BAR_OUTPUT = new ActionBarOutput();
    private static final ChatOutput CHAT_OUTPUT = new ChatOutput();

    private final List<IDialogAction> _actions = new ArrayList<>(20);
    private final DialogRunner _dialogRunner = new DialogRunner();
    private final NamedUpdateAgents _agents = new NamedUpdateAgents();
    private final Settings _settings = new Settings();

    private String _npcName;
    private Player _player;
    private IDialogOutput _output = ACTION_BAR_OUTPUT;
    private int _currentIndex = 0;
    private int _ticksLeft;
    private int _padTicksLeft;
    private boolean _isDisposed;

    private String _npcFormat;
    private String _playerFormat;

    public DialogContext() {

        if (_masterRunner == null) {
            _masterRunner = new MasterDialogRunner();
            Scheduler.runTaskRepeat(ArborianQuests.getPlugin(), 1, 1, _masterRunner);
        }
    }

    @Override
    public IDialogContextSettings getSettings() {
        return _settings;
    }

    @Override
    public DialogContext setNpc(@Nullable INpc npc) {
        _npcName = npc == null ? null : npc.getDisplayName();
        return this;
    }

    @Override
    public DialogContext setNpcName(String name) {
        PreCon.notNull(name);

        _npcName = name;
        return this;
    }

    @Override
    public DialogContext setOutput(IDialogOutput output) {
        PreCon.notNull(output);
        _output = output;
        return this;
    }

    @Override
    public DialogContext setChatOutput() {
        _output = CHAT_OUTPUT;
        return this;
    }

    @Override
    public DialogContext setActionBarOutput() {
        _output = ACTION_BAR_OUTPUT;
        return this;
    }

    @Override
    public DialogContext npc(int readTicks, String dialog) {
        PreCon.positiveNumber(readTicks);
        PreCon.notNull(dialog);

        return npc(readTicks, dialog, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public DialogContext npc(int readTicks, String dialog, Object... args) {
        PreCon.positiveNumber(readTicks);
        PreCon.notNull(dialog);

        _actions.add(new NpcTalkAction(_npcName, _output, readTicks, dialog, args));
        return this;
    }

    @Override
    public DialogContext npc(int readTicks, String[] dialogArray) {
        PreCon.positiveNumber(readTicks);
        PreCon.notNull(dialogArray);

        for (String dialog : dialogArray) {
            npc(readTicks, dialog);
        }
        return this;
    }

    @Override
    public DialogContext player(int readTicks, String dialog) {
        PreCon.positiveNumber(readTicks);
        PreCon.notNull(dialog);

        return player(readTicks, dialog, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public DialogContext player(int readTicks, String message, Object... args) {
        PreCon.positiveNumber(readTicks);
        PreCon.notNull(message);

        _actions.add(new PlayerTalkAction(_output, readTicks, message, args));
        return this;
    }

    @Override
    public DialogContext player(int readTicks, String[] dialogArray) {
        PreCon.positiveNumber(readTicks);
        PreCon.notNull(dialogArray);

        for (String dialog : dialogArray) {
            player(readTicks, dialog);
        }
        return this;
    }

    @Override
    public DialogContext pause(int ticks) {
        PreCon.positiveNumber(ticks);

        _actions.add(new PauseAction(ticks));
        return this;
    }

    @Override
    public DialogContext pad(int ticks) {
        _actions.add(new PadTimeAction(ticks));
        return this;
    }

    @Override
    public DialogContext run(Runnable runnable) {
        PreCon.notNull(runnable);

        _actions.add(new RunnableAction(runnable));
        return this;
    }

    @Override
    public DialogContext action(IDialogAction action) {
        PreCon.notNull(action);

        _actions.add(action);
        return this;
    }

    @Override
    public void start(Player player) {
        PreCon.notNull(player);

        _dialogRunner.cancel();
        _player = player;

        IDialogContext current = DialogSessions.set(player, this);
        if (current != null)
            current.end();

        _dialogRunner.start();
    }

    @Override
    public void end() {
        _agents.update("onCancel", this);
        _dialogRunner.cancel();
    }

    @Override
    public DialogContext onStart(IScriptUpdateSubscriber<IDialogContext> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onStart").addSubscriber(new ScriptUpdateSubscriber<>(subscriber));
        return this;
    }

    @Override
    public DialogContext onComplete(IScriptUpdateSubscriber<IDialogContext> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onComplete").addSubscriber(new ScriptUpdateSubscriber<>(subscriber));
        return this;
    }

    @Override
    public DialogContext onCancel(IScriptUpdateSubscriber<IDialogContext> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onCancel").addSubscriber(new ScriptUpdateSubscriber<>(subscriber));
        return this;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        _actions.clear();

        if (_isDisposed)
            return;

        _isDisposed = true;
        _agents.disposeAgents();
        _dialogRunner.cancel();
    }

    private class DialogRunner {

        boolean isCancelled;

        void run() {

            if (_isDisposed) {
                cancel();
                return;
            }

            if (!_player.isOnline()) {
                end();
                return;
            }

            if (_padTicksLeft > 0)
                _padTicksLeft--;

            if (_ticksLeft > 0) {
                _ticksLeft--;
                return;
            }

            if (_currentIndex >= _actions.size()) {

                if (_padTicksLeft <= 0)
                    cancel();

                return;
            }

            IDialogAction action = _actions.get(_currentIndex);
            _ticksLeft = action.duration();

            try {
                action.run(_player, DialogContext.this);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }

            _currentIndex++;
        }

        void cancel() {

            this.isCancelled = true;
            _runners.remove(this);

            if (_player != null) {
                DialogSessions.remove(_player, DialogContext.this);
                _agents.update("onComplete", DialogContext.this);
            }

            _player = null;
            _ticksLeft = 0;
            _padTicksLeft = 0;
            _currentIndex = 0;
        }

        void start() {
            this.isCancelled = false;
            _runners.add(this);
            _agents.update("onStart", this);
        }
    }

    private static class MasterDialogRunner implements Runnable {

        ArrayList<DialogRunner> runners = new ArrayList<>(50);

        @Override
        public void run() {

            runners.ensureCapacity(_runners.size());
            runners.addAll(_runners);

            for (DialogRunner runner : runners) {
                if (!runner.isCancelled)
                    runner.run();
            }

            runners.clear();
        }
    }

    private class Settings implements IDialogContextSettings {

        @Override
        public int getPadTime() {
            return _padTicksLeft;
        }

        @Override
        public int increasePadTime(int amount) {
            return _padTicksLeft += amount;
        }

        @Nullable
        @Override
        public String getNpcFormat() {
            return _npcFormat;
        }

        @Override
        public Settings setNpcFormat(@Nullable String format) {
            _npcFormat = format;
            return this;
        }

        @Nullable
        @Override
        public String getPlayerFormat() {
            return _playerFormat;
        }

        @Override
        public Settings setPlayerFormat(@Nullable String format) {
            _playerFormat = format;
            return this;
        }
    }
}
