package com.jcwhatever.bukkit.arborianquests.commands;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.commands.admin.ListCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.regions.RegionsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.scripts.ScriptsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.users.BaseCommand;
import com.jcwhatever.bukkit.generic.commands.AbstractCommandHandler;

public class CommandHandler extends AbstractCommandHandler{

    /**
     * Constructor
     */
    public CommandHandler() {
        super(ArborianQuests.getInstance());
    }

    @Override
    protected void registerCommands() {

        setBaseCommand(BaseCommand.class);

        registerCommand(RegionsCommand.class);
        registerCommand(ScriptsCommand.class);
        registerCommand(ListCommand.class);
    }
}
