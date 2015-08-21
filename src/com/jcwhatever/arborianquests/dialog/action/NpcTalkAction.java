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

package com.jcwhatever.arborianquests.dialog.action;

import com.jcwhatever.arborianquests.dialog.IDialogContext;
import com.jcwhatever.arborianquests.dialog.output.IDialogOutput;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.entity.Player;

/**
 * Message action for NPC dialog.
 */
public class NpcTalkAction extends MessageAction {

    private final String _npcName;
    private final Object[] _args;

    /**
     * Constructor.
     *
     * @param npcName   The name of the npc.
     * @param output    The dialog output handler.
     * @param duration  The duration of the message in ticks.
     * @param message   The message.
     * @param args      The message arguments.
     */
    public NpcTalkAction(String npcName, IDialogOutput output, int duration, String message, Object[] args) {
        super(output, duration, message);
        PreCon.notNull(npcName);
        PreCon.notNull(args);

        _npcName = npcName;
        _args = args;
    }

    @Override
    protected String getMessage(Player player, IDialogContext context) {

        String message = TextUtils.format(super.getMessage(player, context), _args);
        String format = context.getSettings().getNpcFormat();

        if (format != null && _npcName != null)
            message = TextUtils.format(format, _npcName, message);

        return message;
    }
}
