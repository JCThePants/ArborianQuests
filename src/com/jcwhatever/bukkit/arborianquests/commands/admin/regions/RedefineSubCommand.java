package com.jcwhatever.bukkit.arborianquests.commands.admin.regions;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegion;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.regions.RegionSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommandInfo(
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

        ScriptRegionManager regionManager = ArborianQuests.getInstance().getScriptRegionManager();

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
