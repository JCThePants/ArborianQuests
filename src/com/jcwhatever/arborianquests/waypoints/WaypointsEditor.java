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

package com.jcwhatever.arborianquests.waypoints;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.Msg;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.player.PlayerBlockSelect;
import com.jcwhatever.nucleus.utils.player.PlayerBlockSelect.BlockSelectResult;
import com.jcwhatever.nucleus.utils.player.PlayerBlockSelect.PlayerBlockSelectHandler;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Map;
import java.util.UUID;

/**
 * Utility for a player to edit a waypoints list by selecting
 * locations in the world.
 */
public class WaypointsEditor {

    private WaypointsEditor() {}

    @Localizable static final String _EDIT =
            "Editing waypoints list named '{0: waypoints list name}'.\n" +
            "Left click a block to add location, right click to remove the last location in the list.\n" +
            "{YELLOW}Type '/quests waypoints edit' to finish.";

    @Localizable static final String _REMOVED =
            "Removed last location '{0: location}' from '{1: waypoints list name}' waypoints list.";

    @Localizable static final String _REMOVE_EMPTY =
            "{RED}Waypoints list '{0: waypoints list name}' is empty. Nothing to remove.";

    @Localizable static final String _FINISHED =
            "{GREEN}Finished editing waypoints list named '{0: waypoints list name}'.";

    private static Map<UUID, WaypointsList> _editing
            = new PlayerMap<>(ArborianQuests.getPlugin(), 5);

    /**
     * Determine if a player is editing a waypoints list.
     *
     * @param player  The player to check.
     */
    public static boolean isEditing(Player player) {
        PreCon.notNull(player);

        return _editing.containsKey(player.getUniqueId());
    }

    /**
     * Stop editing a waypoints list.
     *
     * @param player  The player.
     */
    public static void stop(Player player) {
        PreCon.notNull(player);

        WaypointsList waypoints = _editing.remove(player.getUniqueId());
        if (waypoints != null) {
            PlayerBlockSelect.cancel(player);
            Msg.tell(player, Lang.get(_FINISHED, waypoints.getName()));
        }
    }

    /**
     * Edit waypoints list.
     *
     * @param player     The player editing the list.
     * @param waypoints  The waypoints list to edit.
     */
    public static void edit(Player player, final WaypointsList waypoints) {
        PreCon.notNull(player);
        PreCon.notNull(waypoints);

        // check if player is already editing waypoints
        WaypointsList current = _editing.get(player.getUniqueId());
        if (current != null) {
            Msg.tell(player, Lang.get(_FINISHED, current.getName()));
        }

        // place player and waypoints into edit map
        _editing.put(player.getUniqueId(), waypoints);
        Msg.tell(player, Lang.get(_EDIT, waypoints.getName()));

        // query for a block selection
        PlayerBlockSelect.query(player, new PlayerBlockSelectHandler() {

            @Override
            public BlockSelectResult onBlockSelect(Player player, Block selectedBlock, Action clickAction) {

                Location location = selectedBlock.getLocation().add(0, 1, 0);

                // add location
                if (clickAction == Action.LEFT_CLICK_BLOCK) {
                    waypoints.add(location);
                    Msg.tell(player, "Added location '{0}'", TextUtils.formatLocation(location, true));
                }
                // remove location
                else if (clickAction == Action.RIGHT_CLICK_BLOCK) {

                    if (waypoints.isEmpty()) {
                        Msg.tell(player, Lang.get(_REMOVE_EMPTY,
                                TextUtils.formatLocation(location, true), waypoints.getName()));
                    }
                    else {
                        Location removed = waypoints.remove(waypoints.size() - 1);
                        Msg.tell(player, Lang.get(_REMOVED,
                                TextUtils.formatLocation(removed, true), waypoints.getName()));
                    }
                }

                return BlockSelectResult.CONTINUE;
            }
        });
    }
}
