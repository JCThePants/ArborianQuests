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

package com.jcwhatever.arborianquests.click;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.entity.Player;

/**
 * Click handler builder helper.
 */
public class ClickHandlerBuilder {

    private int _priority;
    private ICanRun _canRun;
    private IRun _run;

    public ClickHandlerBuilder priority(int priority) {
        _priority = priority;
        return this;
    }

    public ClickHandlerBuilder onCanRun(ICanRun canRunHandler) {
        PreCon.notNull(canRunHandler);

        _canRun = canRunHandler;
        return this;
    }

    public ClickHandlerBuilder onRun(IRun runHandler) {
        PreCon.notNull(runHandler);

        _run = runHandler;
        return this;
    }

    public IClickHandler build() {
        return new IClickHandler() {

            @Override
            public int compareTo(IClickHandler o) {
                return Integer.compare(priority(), o.priority());
            }

            @Override
            public int priority() {
                return _priority;
            }

            @Override
            public boolean canRun(Player player, INpc npc) {
                PreCon.notNull(player);

                return _canRun.canRun(player, npc);
            }

            @Override
            public void run(Player player, INpc npc) {
                PreCon.notNull(player);
                PreCon.notNull(npc);

                _run.run(player, npc);
            }
        };
    }

    public interface ICanRun {
        boolean canRun(Player clickPlayer, INpc clickedNpc);
    }

    public interface IRun {
        void run(Player clickPlayer, INpc clickedNpc);
    }
}
