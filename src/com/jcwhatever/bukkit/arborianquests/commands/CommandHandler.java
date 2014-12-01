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


package com.jcwhatever.bukkit.arborianquests.commands;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.commands.admin.ListCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.items.ItemsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.locations.LocationsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.regions.RegionsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.users.BaseCommand;
import com.jcwhatever.bukkit.arborianquests.commands.users.ReplayCommand;
import com.jcwhatever.bukkit.generic.commands.AbstractCommandHandler;

public class CommandHandler extends AbstractCommandHandler{

    /**
     * Constructor
     */
    public CommandHandler() {
        super(ArborianQuests.getPlugin());
    }

    @Override
    protected void registerCommands() {

        setBaseCommand(BaseCommand.class);

        registerCommand(ReplayCommand.class);
        registerCommand(ItemsCommand.class);
        registerCommand(LocationsCommand.class);
        registerCommand(RegionsCommand.class);
        registerCommand(ListCommand.class);
    }
}
