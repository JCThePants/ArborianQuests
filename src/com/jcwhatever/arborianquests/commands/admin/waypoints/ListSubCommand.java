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
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Collection;

@CommandInfo(
        parent="waypoints",
        command = "list",
        staticParams = { "page=1" },
        floatingParams = { "name=", "search=" },
        description = "List all waypoints or locations in a waypoints list.",
        paramDescriptions = {
                "page= {PAGE}",
                "name= The name of the waypoints list to list locations from.",
                "search= Optional. Use to show waypoints that contain the specified search text."
        })

public class ListSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE_ALL = "Waypoints Lists";

    @Localizable static final String _PAGINATOR_TITLE_LOCATIONS =
            "Locations in Waypoints List '{0: waypoints list name}'";

    @Localizable static final String _WAYPOINTS_NOT_FOUND =
            "A waypoints list named '{0: waypoints list name}' was not found.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        ChatPaginator pagin;

        pagin = args.isDefaultValue("name")
                ? list(args)
                : listLocations(args, args.getName("name", 48));

        if (pagin == null)
            return; // finished

        if (!args.isDefaultValue("search"))
            pagin.setSearchTerm(args.getString("search"));

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }

    /*
     *  Get list of locations in waypoints list.
     */
    @Nullable
    private ChatPaginator listLocations(ICommandArguments args, String waypointsName)
            throws CommandException {

        WaypointsManager manager = ArborianQuests.getWaypointsManager();

        WaypointsList waypoints = manager.get(waypointsName);
        if (waypoints == null)
            throw new CommandException(Lang.get(_WAYPOINTS_NOT_FOUND, waypointsName));

        ChatPaginator pagin = createPagin(args, 7, Lang.get(_PAGINATOR_TITLE_LOCATIONS, waypoints.getName()));

        for (int i=0; i < waypoints.size(); i++) {
            pagin.add(i, TextUtils.formatLocation(waypoints.get(i), true));
        }

        return pagin;
    }

    /*
     * Get list of all waypoints lists.
     */
    private ChatPaginator list(ICommandArguments args) {

        WaypointsManager manager = ArborianQuests.getWaypointsManager();

        Collection<WaypointsList> waypoints = manager.getAll();

        ChatPaginator pagin = createPagin(args, 7, Lang.get(_PAGINATOR_TITLE_ALL));

        for (WaypointsList waypoint : waypoints) {
            pagin.add(waypoint.getName(), waypoint.size() + " locations.");
        }

        return pagin;
    }
}
