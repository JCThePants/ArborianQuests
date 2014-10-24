package com.jcwhatever.bukkit.arborianquests.commands.admin.scripts;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="scripts",
        command = "reload",
        usage = "/{plugin-command} scripts reload",
        description = "Reload quest scripts.")

public class ReloadSubCommand extends AbstractCommand {

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidValueException {

        ArborianQuests.getInstance().reloadScripts();

        tellSuccess(sender, "Quest scripts reloaded.");
    }
}