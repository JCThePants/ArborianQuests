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


package com.jcwhatever.bukkit.arborianquests;

import com.jcwhatever.generic.messaging.ChatPaginator;
import com.jcwhatever.generic.messaging.IMessenger;
import com.jcwhatever.generic.utils.text.TextUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class Msg {

    private Msg() {}

    public static void tell(CommandSender sender, String message, Object...params) {
        msg().tell(sender, message, params);
    }

    public static void tell(Player p, String message, Object...params) {
        msg().tell(p, message, params);
    }

    public static void tellImportant(UUID playerId, String context, String message, Object...params) {
        msg().tellImportant(playerId, context, message, params);
    }

    public static void info(String message, Object...params) {
        msg().info(message, params);
    }

    public static void debug(String message, Object...params) {
        if (!ArborianQuests.getPlugin().isDebugging())
        	return;

        msg().debug(message, params);
    }

    public static void warning(String message, Object...params) {
        msg().warning(message, params);
    }

    public static void severe(String message, Object...params) {
        msg().severe(message, params);
    }

    public static void broadcast(String message, Object...params) {
        msg().broadcast(message, params);
    }

    public static void broadcast(String message, Collection<Player> exclude, Object...params) {
        msg().broadcast(message, exclude, params);
    }

    public static ChatPaginator getPaginator(String title, Object...params) {
        return new ChatPaginator(ArborianQuests.getPlugin(), 6, TextUtils.format(title, params));
    }

    private static IMessenger msg() {
        return ArborianQuests.getPlugin().getMessenger();
    }

}
