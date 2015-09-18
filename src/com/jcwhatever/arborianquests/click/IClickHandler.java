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
import org.bukkit.entity.Player;

/**
 * NPC Click handler interface.
 */
public interface IClickHandler extends Comparable<IClickHandler> {

    /**
     * Get the handler priority.
     */
    int priority();

    /**
     * Determine if the handler can be run for the specified player and NPC.
     *
     * @param player  The player that clicked the NPC.
     * @param npc     The NPC that was clicked.
     */
    boolean canRun(Player player, INpc npc);

    /**
     * Run the handler for the specified player and NPC.
     *
     * @param player  The player that clicked the NPC.
     * @param npc     The NPC that was clicked.
     */
    void run(Player player, INpc npc);
}
