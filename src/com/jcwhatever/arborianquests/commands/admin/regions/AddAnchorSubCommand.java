/* This file is part of ArborianQuests for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.arborianquests.commands.admin.regions;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.regions.ScriptRegion;
import com.jcwhatever.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="regions",
        command = "addanchor",
        staticParams = { "regionName", "radius" },
        description = "Add a new cube quest region using your current location as the center anchor.",
        paramDescriptions = {
                "regionName= The name of the region. {NAME}",
                "radius= The 'radius' of the region. The region is not actually a sphere, the " +
                        "resulting region will be a perfect cube."
        })

public class AddAnchorSubCommand extends AbstractCommand {

    @Localizable static final String _REGION_ALREADY_EXISTS = "There is already a region with the name '{0}'.";
    @Localizable static final String _FAILED = "Failed to add quest region.";
    @Localizable static final String _SUCCESS = "Quest region '{0}' created.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        String regionName = args.getName("regionName", 48);
        int radius = args.getInteger("radius");

        ScriptRegionManager regionManager = ArborianQuests.getScriptRegionManager();

        ScriptRegion region = regionManager.get(regionName);
        if (region != null) {
            tellError(sender, Lang.get(_REGION_ALREADY_EXISTS), regionName);
            return; // finished
        }

        Location anchor = ((Player)sender).getLocation();

        region = regionManager.addFromAnchor(regionName, anchor, radius);
        if (region == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS), region.getName());
    }
}
