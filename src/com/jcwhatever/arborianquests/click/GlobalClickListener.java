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

import com.jcwhatever.nucleus.providers.npc.events.NpcLeftClickEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcRightClickEvent;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Global NPC Click handler context listener.
 */
public class GlobalClickListener implements Listener {

    private final ClickContext _context;

    public GlobalClickListener(ClickContext context) {
        PreCon.notNull(context);

        _context = context;
    }

    public ClickContext getContext() {
        return _context;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onNPCRightClick(NpcRightClickEvent event) {
        if (_context.onRightClick(event.getPlayer(), event.getNpc())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onNPCLeftClick(NpcLeftClickEvent event) {
        if (_context.onLeftClick(event.getPlayer(), event.getNpc())) {
            event.setCancelled(true);
        }
    }
}
