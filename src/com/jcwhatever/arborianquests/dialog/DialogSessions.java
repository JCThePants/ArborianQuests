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
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/*
 * 
 */
public class DialogSessions {

    private DialogSessions() {}

    private static final Map<UUID, IDialogContext> _sessions = new PlayerMap<>(
            ArborianQuests.getPlugin(), Bukkit.getMaxPlayers());

    @Nullable
    public static IDialogContext get(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        if (p == null)
            return null;

        return _sessions.get(p.getUniqueId());
    }

    @Nullable
    public static IDialogContext set(Object player, IDialogContext context) {
        PreCon.notNull(player);
        PreCon.notNull(context);

        Player p = PlayerUtils.getPlayer(player);
        if (p == null)
            return null;

        return _sessions.put(p.getUniqueId(), context);
    }

    @Nullable
    public static IDialogContext remove(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        if (p == null)
            return null;

        return _sessions.remove(p.getUniqueId());
    }

    public static void remove(Object player, IDialogContext context) {
        PreCon.notNull(player);
        PreCon.notNull(context);

        Player p = PlayerUtils.getPlayer(player);
        if (p == null)
            return;

        IDialogContext current = _sessions.get(p.getUniqueId());
        if (context.equals(current)) {
            _sessions.remove(p.getUniqueId());
        }
    }
}
