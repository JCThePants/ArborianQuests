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


package com.jcwhatever.bukkit.arborianquests.commands.admin.regions;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.Msg;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegion;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="regions",
        command = "list",
        staticParams = { "page=1" },
        usage = "/{plugin-command} regions list [page]",
        description = "List all quest regions.")

public class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Quest Regions";
    @Localizable static final String _UNDEFINED_REGION = "<undefined>";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidValueException {

        int page = args.getInt("page");

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        List<ScriptRegion> regions = ArborianQuests.getPlugin().getScriptRegionManager().getRegions();

        for (ScriptRegion region : regions) {
            pagin.add(region.getName(), region.isDefined() ? region.getWorld().getName() : Lang.get(_UNDEFINED_REGION));
        }

        pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
    }
}