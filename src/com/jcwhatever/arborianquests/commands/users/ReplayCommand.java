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

package com.jcwhatever.arborianquests.commands.users;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.quests.Quest;
import com.jcwhatever.arborianquests.quests.QuestStatus;
import com.jcwhatever.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        command = "replay",
        staticParams = { "questName" },
        description = "Clears all data for a specified quest so it can be replayed.",
        paramDescriptions = {
                "questName= The name of the quest to replay."
        })

public class ReplayCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _NOT_FOUND = "Quest named '{0}' not found.";
    @Localizable static final String _FAIL_IN_PROGRESS = "Quest is in progress and cannot be cleared.";
    @Localizable static final String _OP_IN_PROGRESS = "Quest is in progress but is being cleared anyways since you are Opped.";
    @Localizable static final String _SUCCESS = "Quest cleared and ready to replay.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        CommandException.checkNotConsole(getPlugin(), this, sender);

        String questName = args.getName("questName", 48);

        Quest quest = ArborianQuests.getQuestManager().getQuest(questName);
        if (quest == null)
            throw new CommandException(Lang.get(_NOT_FOUND, questName));

        Player p = (Player)sender;

        QuestStatus status = quest.getStatus(p);

        if (status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS) {

            if (p.isOp()) {
                tell(sender, Lang.get(_OP_IN_PROGRESS));
            }
            else {
                throw new CommandException(Lang.get(_FAIL_IN_PROGRESS));
            }
        }

        quest.clearFlags(p.getUniqueId());

        tellSuccess(sender, Lang.get(_SUCCESS));
    }
}
