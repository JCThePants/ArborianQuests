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

package com.jcwhatever.arborianquests.commands.admin.waypoints;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.waypoints.WaypointsList;
import com.jcwhatever.arborianquests.waypoints.WaypointsEditor;
import com.jcwhatever.arborianquests.waypoints.WaypointsManager;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="waypoints",
        command = "edit",
        staticParams = { "name=" },
        description = "Edit a waypoints list. Omit name argument if attempting to end current edit.",
        paramDescriptions = {
                "name= The name of the waypoints list to edit."
        })

public class EditSubCommand extends AbstractCommand {

    @Localizable static final String _WAYPOINTS_NOT_FOUND =
            "A waypoints list named '{0: waypoints list name}' was not found.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        Player player = (Player)sender;

        // check if attempting to turn off editor.
        if (args.isDefaultValue("name") && WaypointsEditor.isEditing(player)) {

            WaypointsEditor.stop(player);
            return; // finished
        }

        String name = args.getName("name", 48);

        WaypointsManager manager = ArborianQuests.getWaypointsManager();

        WaypointsList waypoints = manager.get(name);
        if (waypoints == null) {
            tellError(sender, Lang.get(_WAYPOINTS_NOT_FOUND), name);
            return; // finished
        }

        WaypointsEditor.edit(player, waypoints);
    }
}
