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


package com.jcwhatever.arborianquests;

import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Static convenience methods for sending chat and console messages.
 */
public class Msg {

    private Msg() {}

    public static void tell(CommandSender sender, CharSequence message, Object...params) {
        msg().tell(sender, message, params);
    }

    public static void tellImportant(UUID playerId, String context, CharSequence message, Object...params) {
        msg().tellImportant(playerId, context, message, params);
    }

    public static void info(CharSequence message, Object...params) {
        msg().info(message, params);
    }

    public static void debug(CharSequence message, Object...params) {
        if (!ArborianQuests.getPlugin().isDebugging())
        	return;

        msg().debug(message, params);
    }

    public static void warning(CharSequence message, Object...params) {
        msg().warning(message, params);
    }

    public static void severe(CharSequence message, Object...params) {
        msg().severe(message, params);
    }

    public static void broadcast(CharSequence message, Object...params) {
        msg().broadcast(message, params);
    }

    public static void broadcast(Collection<? extends Player> exclude, CharSequence message, Object...params) {
        msg().broadcast(exclude, message, params);
    }

    private static IMessenger msg() {
        return ArborianQuests.getPlugin().getMessenger();
    }
}
