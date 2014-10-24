package com.jcwhatever.bukkit.arborianquests.commands.admin.regions;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;

@ICommandInfo(
        command={"regions"},
        description="Manage quest regions.")
public class RegionsCommand extends AbstractCommand {

    public RegionsCommand() {
        super();

        registerSubCommand(AddSubCommand.class);
        registerSubCommand(AddAnchorSubCommand.class);
        registerSubCommand(DelSubCommand.class);
        registerSubCommand(RedefineSubCommand.class);
        registerSubCommand(ListSubCommand.class);
    }
}
