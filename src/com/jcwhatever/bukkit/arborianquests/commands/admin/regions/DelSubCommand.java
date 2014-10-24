package com.jcwhatever.bukkit.arborianquests.commands.admin.regions;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegion;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import org.bukkit.command.CommandSender;


@ICommandInfo(
        parent="regions",
        command = "del",
        staticParams = { "regionName" },
        usage = "/{plugin-command} regions del <regionName>",
        description = "Remove a quest region.")

public class DelSubCommand extends AbstractCommand {

    @Localizable static final String _REGION_NOT_FOUND = "A quest region named '{0}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove quest region.";
    @Localizable static final String _SUCCESS = "Quest region '{0}' removed.";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidValueException {

        String regionName = args.getName("regionName", 32);

        ScriptRegionManager regionManager = ArborianQuests.getInstance().getScriptRegionManager();

        ScriptRegion region = regionManager.getRegion(regionName);
        if (region == null) {
            tellError(sender, Lang.get(_REGION_NOT_FOUND), regionName);
            return; // finished
        }


        if (!regionManager.removeRegion(regionName)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS), region.getName());
    }
}

