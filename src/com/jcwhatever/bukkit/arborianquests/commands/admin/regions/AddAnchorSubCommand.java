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


package com.jcwhatever.bukkit.arborianquests.commands.admin.regions;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegion;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.CommandException;
import com.jcwhatever.bukkit.generic.language.Localizable;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="regions",
        command = "addanchor",
        staticParams = { "regionName", "diameter" },
        description = "Add a new quest region using your current location as an anchor.")

public class AddAnchorSubCommand extends AbstractCommand {

    @Localizable static final String _REGION_ALREADY_EXISTS = "There is already a region with the name '{0}'.";
    @Localizable static final String _FAILED = "Failed to add quest region.";
    @Localizable static final String _SUCCESS = "Quest region '{0}' created.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.assertNotConsole(this, sender);

        String regionName = args.getName("regionName", 48);
        int diameter = args.getInteger("diameter");

        ScriptRegionManager regionManager = ArborianQuests.getScriptRegionManager();

        ScriptRegion region = regionManager.getRegion(regionName);
        if (region != null) {
            tellError(sender, Lang.get(_REGION_ALREADY_EXISTS), regionName);
            return; // finished
        }

        Location anchor = ((Player)sender).getLocation();

        region = regionManager.addFromAnchor(regionName, anchor, diameter);
        if (region == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS), region.getName());
    }
}
