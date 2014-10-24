package com.jcwhatever.bukkit.arborianquests.commands.admin.scripts;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;

@ICommandInfo(
        command={"scripts"},
        description="Manage quest scripts.")
public class ScriptsCommand extends AbstractCommand {

    public ScriptsCommand() {
        super();

        registerSubCommand(ListSubCommand.class);
        registerSubCommand(ReloadSubCommand.class);
    }
}
