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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommandInfo(
        parent="regions",
        command = "addanchor",
        staticParams = { "regionName", "diameter" },
        usage = "/{plugin-command} regions addanchor <regionName> <diameter>",
        description = "Add a new quest region using your current location as an anchor.")

public class AddAnchorSubCommand extends AbstractCommand {

    @Localizable
    static final String _NOT_CONSOLE = "Console has no location.";
    @Localizable static final String _REGION_ALREADY_EXISTS = "There is already a region with the name '{0}'.";
    @Localizable static final String _FAILED = "Failed to add quest region.";
    @Localizable static final String _SUCCESS = "Quest region '{0}' created.";

    @Override
    public void execute (CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, Lang.get(_NOT_CONSOLE));

        String regionName = args.getName("regionName", 32);
        int diameter = args.getInt("diameter");

        ScriptRegionManager regionManager = ArborianQuests.getInstance().getScriptRegionManager();

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
