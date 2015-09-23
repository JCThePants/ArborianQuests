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
import com.jcwhatever.arborianquests.waypoints.WaypointsEditor;
import com.jcwhatever.arborianquests.waypoints.WaypointsList;
import com.jcwhatever.arborianquests.waypoints.WaypointsManager;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="waypoints",
        command = "add",
        staticParams = { "name" },
        description = "Add a new waypoints list. Starts waypoint editor.",
        paramDescriptions = {
                "name= The name of the waypoints list. {NAME}"
        })

public class AddSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _WAYPOINT_ALREADY_EXISTS =
            "There is already a waypoints list with the name '{0: waypoints list name}'.";
    @Localizable static final String _FAILED = "Failed to add waypoints list.";
    @Localizable static final String _SUCCESS = "Waypoints list '{0: waypoints list name}' created.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        final String name = args.getName("name", 48);

        final WaypointsManager manager = ArborianQuests.getWaypointsManager();

        if (manager.contains(name))
            throw new CommandException(Lang.get(_WAYPOINT_ALREADY_EXISTS, name));

        WaypointsList waypoints = manager.add(name);
        if (waypoints == null)
            throw new CommandException(Lang.get(_FAILED));

        tellSuccess(sender, Lang.get(_SUCCESS, name));

        WaypointsEditor.edit((Player)sender, waypoints);
    }
}

