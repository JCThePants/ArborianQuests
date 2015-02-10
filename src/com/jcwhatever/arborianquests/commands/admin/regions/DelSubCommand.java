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
import com.jcwhatever.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.arborianquests.regions.ScriptRegion;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import org.bukkit.command.CommandSender;


@CommandInfo(
        parent="regions",
        command = "del",
        staticParams = { "regionName" },
        description = "Remove a quest region.",
        paramDescriptions = {
                "regionName= The name of the region to delete."
        })

public class DelSubCommand extends AbstractCommand {

    @Localizable static final String _REGION_NOT_FOUND = "A quest region named '{0}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove quest region.";
    @Localizable static final String _SUCCESS = "Quest region '{0}' removed.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        String regionName = args.getName("regionName", 48);

        ScriptRegionManager regionManager = ArborianQuests.getScriptRegionManager();

        ScriptRegion region = regionManager.get(regionName);
        if (region == null) {
            tellError(sender, Lang.get(_REGION_NOT_FOUND), regionName);
            return; // finished
        }


        if (!regionManager.remove(regionName)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS), region.getName());
    }
}
