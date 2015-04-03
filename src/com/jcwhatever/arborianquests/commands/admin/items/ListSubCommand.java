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

package com.jcwhatever.arborianquests.commands.admin.items;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.items.ScriptItem;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;

import java.util.Collection;

@CommandInfo(
        parent="items",
        command = "list",
        staticParams = { "page=1" },
        floatingParams = { "search="},
        description = "List all quest items.",
        paramDescriptions = {
                "page= {PAGE}",
                "search= Optional. Use to show items that contain the specified search text."
        })

public class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Quest Items";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        int page = args.getInteger("page");

        ChatPaginator pagin = createPagin(Lang.get(_PAGINATOR_TITLE));

        Collection<ScriptItem> items = ArborianQuests.getScriptItemManager().getAll();

        for (ScriptItem item : items) {
            pagin.add(item.getName(), ItemStackUtils.serialize(item.getItem()));
        }

        if (!args.isDefaultValue("search"))
            pagin.setSearchTerm(args.getString("search"));

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
