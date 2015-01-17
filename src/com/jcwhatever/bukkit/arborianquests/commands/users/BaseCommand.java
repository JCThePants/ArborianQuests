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


package com.jcwhatever.bukkit.arborianquests.commands.users;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.arborianquests.Msg;
import com.jcwhatever.bukkit.arborianquests.commands.admin.ListCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.flags.FlagsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.items.ItemsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.locations.LocationsCommand;
import com.jcwhatever.bukkit.arborianquests.commands.admin.regions.RegionsCommand;
import com.jcwhatever.bukkit.arborianquests.quests.PrimaryQuest;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.nucleus.collections.HierarchyNode;
import com.jcwhatever.nucleus.collections.TreeNode;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.messaging.ChatPaginator;
import com.jcwhatever.nucleus.messaging.ChatTree;
import com.jcwhatever.nucleus.messaging.ChatTree.NodeLineWriter;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@CommandInfo(
        command={"quests"},
        staticParams = { "page=1" },
        description="Get information about your quests.",
        permissionDefault= PermissionDefault.TRUE,
        isHelpVisible=false)
public class BaseCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE="My Current Quests";
    @Localizable static final String _HELP = "Type '/{plugin-command} ?' for a list of commands.";

    public BaseCommand() {
        super();

        registerCommand(FlagsCommand.class);
        registerCommand(ReplayCommand.class);
        registerCommand(ItemsCommand.class);
        registerCommand(LocationsCommand.class);
        registerCommand(RegionsCommand.class);
        registerCommand(ListCommand.class);
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        int page = args.getInteger("page");
        Player p = (Player)sender;

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        List<Quest> quests = ArborianQuests.getQuestManager().getQuests();
        List<HierarchyNode<Quest>> nodes = new ArrayList<>(quests.size());

        for (Quest quest : quests) {

            // make sure the player is in the quest
            QuestStatus status = quest.getStatus(p);
            if (status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS) {

                // check all sub nodes an remove quests the player is not in
                HierarchyNode<Quest> node = new HierarchyNode<>(quest);
                Iterator<TreeNode<Quest>> iterator = node.iterator();

                while (iterator.hasNext()) {

                    TreeNode<Quest> questNode = iterator.next();
                    status = questNode.getValue().getStatus(p);

                    if (status.getCurrentStatus() != CurrentQuestStatus.IN_PROGRESS) {
                        iterator.remove();
                    }
                }

                nodes.add(node);
            }
        }

        // Create a chat hierarchy
        ChatTree<Quest> questTree = new ChatTree<>(ArborianQuests.getPlugin());
        questTree.addAllRootNodes(nodes);

        // add chat hierarchy to paginator
        pagin.addAll(questTree.toChatLines(new NodeLineWriter<Quest>() {
            @Override
            public String write(Quest quest) {
                return quest instanceof PrimaryQuest
                        ? TextUtils.format(FormatTemplate.LIST_ITEM_DESCRIPTION,
                        quest.getName(), quest.getDisplayName())
                        : TextUtils.format("{GRAY}{0}", quest.getName());
            }
        }));

        pagin.show(p, page, FormatTemplate.RAW);

        tell(sender, Lang.get(_HELP));
    }
}

