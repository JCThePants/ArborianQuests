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

import com.jcwhatever.arborianquests.dialog.action.IDialogAction;
import com.jcwhatever.arborianquests.dialog.output.IDialogOutput;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/*
 * 
 */
public interface IDialogContext extends IDisposable {

    IDialogContextSettings getSettings();

    IDialogContext setNpc(@Nullable INpc npc);

    IDialogContext setNpcName(String name);

    IDialogContext setOutput(IDialogOutput output);

    IDialogContext setChatOutput();

    IDialogContext setActionBarOutput();

    IDialogContext npc(int readTicks, String message);

    IDialogContext npc(int readTicks, String message, Object... args);

    IDialogContext npc(int readTicks, String[] dialogArray);

    IDialogContext player(int readTicks, String dialog);

    IDialogContext player(int readTicks, String dialog, Object... args);

    IDialogContext player(int readTicks, String[] dialogArray);

    IDialogContext pause(int ticks);

    IDialogContext pad(int ticks);

    IDialogContext run(Runnable runnable);

    IDialogContext action(IDialogAction action);

    void start(Player player);

    void end();

    IDialogContext onStart(IScriptUpdateSubscriber<IDialogContext> subscriber);

    IDialogContext onComplete(IScriptUpdateSubscriber<IDialogContext> subscriber);

    IDialogContext onCancel(IScriptUpdateSubscriber<IDialogContext> subscriber);
}
