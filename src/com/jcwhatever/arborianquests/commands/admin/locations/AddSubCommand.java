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


package com.jcwhatever.arborianquests.commands.admin.locations;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.locations.ScriptLocation;
import com.jcwhatever.arborianquests.locations.ScriptLocationManager;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.arguments.ILocationHandler;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="locations",
        command = "add",
        staticParams = { "locationName", "location" },
        description = "Add a new quest location.",
        paramDescriptions = {
                "locationName= The name of the location. {NAME}",
                "location= The location. {LOCATION}"
        })

public class AddSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _LOCATION_ALREADY_EXISTS =
            "There is already a location with the name '{0}'.";

    @Localizable static final String _FAILED = "Failed to add location.";
    @Localizable static final String _SUCCESS = "Quest location '{0}' created.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        final String locationName = args.getName("locationName", 48);

        final ScriptLocationManager manager = ArborianQuests.getScriptLocationManager();

        ScriptLocation scriptLocation = manager.get(locationName);
        if (scriptLocation != null)
            throw new CommandException(Lang.get(_LOCATION_ALREADY_EXISTS, locationName));

        args.getLocation(sender, "location", new ILocationHandler() {

            @Override
            public void onLocationRetrieved(Player player, Location result) {

                ScriptLocation scriptLocation = manager.add(locationName, result);
                if (scriptLocation == null) {
                    tellError(player, Lang.get(_FAILED));
                }
                else {
                    tellSuccess(player, Lang.get(_SUCCESS), locationName);
                }
            }
        });

    }
}
