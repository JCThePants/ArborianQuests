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
import com.jcwhatever.arborianquests.waypoints.WaypointsManager;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="waypoints",
        command = "del",
        staticParams = { "name" },
        description = "Remove a waypoints list.",
        paramDescriptions = {
                "name= The name of the waypoints list to delete."
        })

public class DelSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _WAYPOINTS_NOT_FOUND =
            "A waypoints list named '{0: waypoints list name}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove waypoints list.";
    @Localizable static final String _SUCCESS = "Waypoints list '{0: waypoints list name}' removed.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        String name = args.getName("name", 48);

        WaypointsManager manager = ArborianQuests.getWaypointsManager();

        WaypointsList waypoints = manager.get(name);
        if (waypoints == null)
            throw new CommandException(Lang.get(_WAYPOINTS_NOT_FOUND, name));

        if (!manager.remove(name))
            throw new CommandException(Lang.get(_FAILED));

        tellSuccess(sender, Lang.get(_SUCCESS), waypoints.getName());
    }
}
