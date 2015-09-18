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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * NPC Click handler context.
 */
public class ClickContext {

    private static Comparator<IClickHandler> _comparator = new Comparator<IClickHandler>() {
        @Override
        public int compare(IClickHandler o1, IClickHandler o2) {
            return o1.compareTo(o2);
        }
    };

    private List<IClickHandler> _leftClickhandlers = new ArrayList<>(15);
    private List<IClickHandler> _rightClickhandlers = new ArrayList<>(15);

    public void addLeftClick(IClickHandler handler) {
        PreCon.notNull(handler);

        _leftClickhandlers.add(handler);
        Collections.sort(_leftClickhandlers, _comparator);
    }

    public boolean removeLeftClick(IClickHandler handler) {
        PreCon.notNull(handler);

        return _leftClickhandlers.remove(handler);
    }

    public void addRightClick(IClickHandler handler) {
        PreCon.notNull(handler);

        _rightClickhandlers.add(handler);
        Collections.sort(_rightClickhandlers, _comparator);
    }

    public boolean removeRightClick(IClickHandler handler) {
        PreCon.notNull(handler);

        return _rightClickhandlers.remove(handler);
    }

    public boolean onLeftClick(Player player, INpc npc) {
        PreCon.notNull(player);
        PreCon.notNull(npc);

        for (IClickHandler handler : _leftClickhandlers) {
            if (handler.canRun(player, npc)) {
                handler.run(player, npc);
                return true;
            }
        }
        return false;
    }

    public boolean onRightClick(Player player, INpc npc) {
        PreCon.notNull(player);
        PreCon.notNull(npc);

        for (IClickHandler handler : _rightClickhandlers) {
            if (handler.canRun(player, npc)) {
                handler.run(player, npc);
                return true;
            }
        }
        return false;
    }
}
