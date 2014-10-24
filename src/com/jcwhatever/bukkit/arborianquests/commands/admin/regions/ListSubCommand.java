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

    @Localizable
    static final String _PAGINATOR_TITLE = "Quest Regions";
    static final String _UNDEFINED_REGION = "<undefined>";

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidValueException {

        int page = args.getInt("page");

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        List<ScriptRegion> regions = ArborianQuests.getInstance().getScriptRegionManager().getRegions();

        for (ScriptRegion region : regions) {
            pagin.add(region.getName(), region.isDefined() ? region.getWorld().getName() : Lang.get(_UNDEFINED_REGION));
        }

        pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
    }
}