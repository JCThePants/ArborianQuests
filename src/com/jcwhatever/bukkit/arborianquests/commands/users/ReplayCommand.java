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

package com.jcwhatever.bukkit.arborianquests.commands.users;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        command = "replay",
        staticParams = { "questName" },
        usage = "/{plugin-command} replay <questName>",
        description = "Clears all data for a specified quest so it can be replayed.")

public class ReplayCommand extends AbstractCommand {

    @Localizable static final String _NOT_CONSOLE = "Console does not have quests.";
    @Localizable static final String _NOT_FOUND = "Quest named '{0}' not found.";
    @Localizable static final String _FAIL_IN_PROGRESS = "Quest is in progress and cannot be cleared.";
    @Localizable static final String _OP_IN_PROGRESS = "Quest is in progress but is being cleared anyways since you are Opped.";
    @Localizable static final String _SUCCESS = "Quest cleared and ready to replay.";

    @Override
    public void execute (CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, Lang.get(_NOT_CONSOLE));

        String questName = args.getName("questName", 48);

        Quest quest = ArborianQuests.getQuestManager().get(questName);
        if (quest == null) {
            tellError(sender, Lang.get(_NOT_FOUND), questName);
            return; // finished
        }

        Player p = (Player)sender;

        QuestStatus status = quest.getStatus(p);

        if (status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS) {

            if (p.isOp()) {
                tell(sender, Lang.get(_OP_IN_PROGRESS));
            }
            else {

                tellError(sender, Lang.get(_FAIL_IN_PROGRESS));
                return; // finished
            }
        }

        quest.clearFlags(p.getUniqueId());

        tellSuccess(sender, Lang.get(_SUCCESS));
    }
}
