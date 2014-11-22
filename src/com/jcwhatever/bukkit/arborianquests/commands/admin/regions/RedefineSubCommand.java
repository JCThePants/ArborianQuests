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
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegion;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.regions.data.RegionSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="regions",
        command = "redefine",
        staticParams = { "regionName" },
        usage = "/{plugin-command} regions redefine <regionName>",
        description = "Redefine quest region coordinates.")

public class RedefineSubCommand extends AbstractCommand {

    @Localizable static final String _NOT_CONSOLE = "Console can't select a world edit region.";
    @Localizable static final String _REGION_NOT_FOUND = "A quest region named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Quest region '{0}' redefined.";

    @Override
    public void execute (CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, Lang.get(_NOT_CONSOLE));

        String regionName = args.getName("regionName", 32);

        ScriptRegionManager regionManager = ArborianQuests.getPlugin().getScriptRegionManager();

        ScriptRegion region = regionManager.getRegion(regionName);
        if (region == null) {
            tellError(sender, Lang.get(_REGION_NOT_FOUND), regionName);
            return; // finished
        }

        RegionSelection selection = getWorldEditSelection((Player)sender);
        if (selection == null)
            return; // finished

        region.setCoords(selection.getP1(), selection.getP2());

        tellSuccess(sender, Lang.get(_SUCCESS), region.getName());
    }
}
