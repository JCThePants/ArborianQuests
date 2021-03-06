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

package com.jcwhatever.arborianquests.commands.admin.flags;

import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.quests.Quest;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandInfo(
        parent="flags",
        command = "set",
        staticParams = { "questPath", "playerName", "flagName" },
        description = "Set a flag for the specified quest on the specified player.",
        paramDescriptions = {
                "questPath= The path to the quest using dots as delimiters. i.e questName.subQuestName",
                "playerName= The name of the player.",
                "flagName= The name of the flag to set. {NAME}"
        })

public class SetSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PATH_NOT_FOUND = "Failed to find quest path '{0: quest path}'.";
    @Localizable static final String _PLAYER_NOT_FOUND = "A player named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Flag '{0}' set for player '{1}' in quest '{1}'.";

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws CommandException {

        String questPath = args.getString("questPath");
        String playerName = args.getString("playerName");
        String flagName = args.getName("flagName", 32);

        Quest quest = Quest.getQuestFromPath(questPath);
        if (quest == null)
            throw new CommandException(Lang.get(_PATH_NOT_FOUND, questPath));

        UUID playerId = PlayerUtils.getPlayerId(playerName);
        if (playerId == null)
            throw new CommandException(Lang.get(_PLAYER_NOT_FOUND, playerName));

        quest.setFlag(playerId, flagName);
        tellSuccess(sender, Lang.get(_SUCCESS, flagName, playerName, questPath));
    }
}
