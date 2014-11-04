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


package com.jcwhatever.bukkit.arborianquests.commands.admin.locations;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.arborianquests.locations.ScriptLocation;
import com.jcwhatever.bukkit.arborianquests.locations.ScriptLocationManager;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="locations",
        command = "del",
        staticParams = { "locationName" },
        usage = "/{plugin-command} {command} del <locationName>",
        description = "Remove a quest location.")

public class DelSubCommand extends AbstractCommand {

    @Localizable static final String _LOCATION_NOT_FOUND = "A quest location named '{0}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove quest location.";
    @Localizable static final String _SUCCESS = "Quest location '{0}' removed.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidValueException {

        String locationName = args.getName("locationName", 32);

        ScriptLocationManager manager = ArborianQuests.getPlugin().getScriptLocationManager();

        ScriptLocation scriptLocation = manager.getLocation(locationName);
        if (scriptLocation == null) {
            tellError(sender, Lang.get(_LOCATION_NOT_FOUND), locationName);
            return; // finished
        }

        if (!manager.removeLocation(locationName)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS), scriptLocation.getName());
    }
}

