package com.jcwhatever.bukkit.arborianquests.commands.users;

import com.jcwhatever.bukkit.arborianquests.Msg;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.bukkit.arborianquests.quests.QuestManager;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.arborianquests.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

@ICommandInfo(
        command={"quests"},
        usage="/{plugin-command} quests",
        staticParams = { "page=1" },
        description="Get information about your quests.",
        permissionDefault= PermissionDefault.TRUE,
        isHelpVisible=false)
public class BaseCommand extends AbstractCommand {

    @Localizable static final String _NOT_CONSOLE = "Console does not have quests. Use '?' argument for list of commands.";
    @Localizable static final String _PAGINATOR_TITLE="My Current Quests";
    @Localizable static final String _HELP = "Type '/quests ?' for a list of commands.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, Lang.get(_NOT_CONSOLE));

        int page = args.getInt("page");
        Player p = (Player)sender;

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        List<Quest> quests = QuestManager.getQuests();
        List<Quest> myQuests = new ArrayList<>(quests.size());

        for (Quest quest : quests) {
            if (quest.getStatus(p).getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS)
                myQuests.add(quest);
        }

        for (Quest quest : myQuests) {
            pagin.add(quest.getName(), quest.getDisplayName());
        }

        pagin.show(p, page, FormatTemplate.ITEM_DESCRIPTION);

        tell(sender, Lang.get(_HELP));
    }
}

