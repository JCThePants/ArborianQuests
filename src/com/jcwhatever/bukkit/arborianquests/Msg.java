package com.jcwhatever.bukkit.arborianquests;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator.PaginatorTemplate;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class Msg {

    private Msg() {}

    public static void tell(CommandSender sender, String message, Object...params) {
        Messenger.tell(ArborianQuests.getInstance(), sender, message, params);
    }

    public static void tell(Player p, String message, Object...params) {
        Messenger.tell(ArborianQuests.getInstance(), p, message, params);
    }

    public static void tellImportant(UUID playerId, String context, String message, Object...params) {
        Messenger.tellImportant(ArborianQuests.getInstance(), playerId, context, message, params);
    }

    public static void info(String message, Object...params) {
        Messenger.info(ArborianQuests.getInstance(), message, params);
    }

    public static void debug(String message, Object...params) {
        //if (!Plugin.getInstance().isDebugging())
        //	return;
        Messenger.debug(ArborianQuests.getInstance(), message, params);
    }

    public static void warning(String message, Object...params) {
        Messenger.warning(ArborianQuests.getInstance(), message, params);
    }

    public static void severe(String message, Object...params) {
        Messenger.severe(ArborianQuests.getInstance(), message, params);
    }

    public static void broadcast(String message, Object...params) {
        Messenger.broadcast(ArborianQuests.getInstance(), message, params);
    }

    public static void broadcast(String message, Collection<Player> exclude, Object...params) {
        Messenger.broadcast(ArborianQuests.getInstance(), message, exclude, params);
    }

    public static ChatPaginator getPaginator(String title, Object...params) {
        return new ChatPaginator(ArborianQuests.getInstance(), 6, PaginatorTemplate.HEADER, PaginatorTemplate.FOOTER, TextUtils.format(title, params));
    }

}
