/*
 * This file is part of ArborianQuests for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.arborianquests.commands.admin.items;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.arborianquests.items.ScriptItem;
import com.jcwhatever.bukkit.arborianquests.items.ScriptItemManager;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandInfo(
        parent="items",
        command = "add",
        staticParams = { "itemName", "item" },
        usage = "/{plugin-command} {command} add <itemName> <item>",
        description = "Add a new quest item.")

public class AddSubCommand extends AbstractCommand {

    @Localizable static final String _ONE_ONLY = "You must provide only one item stack.";
    @Localizable static final String _ITEM_ALREADY_EXISTS = "There is already an item with the name '{0}'.";
    @Localizable static final String _FAILED = "Failed to add item.";
    @Localizable static final String _SUCCESS = "Quest item '{0}' created.";

    @Override
    public void execute (CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        String itemName = args.getName("itemName", 48);
        ItemStack[] items = args.getItemStack(sender, "item");

        if (items.length != 1) {
            tellError(sender, Lang.get(_ONE_ONLY));
            return; // finished
        }

        ScriptItemManager manager = ArborianQuests.getScriptItemManager();

        ScriptItem item = manager.getItem(itemName);
        if (item != null) {
            tellError(sender, Lang.get(_ITEM_ALREADY_EXISTS, item.getName()));
            return; // finished
        }

        if (manager.addItem(itemName, items[0]) == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS, itemName));
    }
}

