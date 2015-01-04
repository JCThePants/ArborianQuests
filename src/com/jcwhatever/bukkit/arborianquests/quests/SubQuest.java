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

package com.jcwhatever.bukkit.arborianquests.quests;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.LinkedList;
import javax.annotation.Nullable;

/**
 * A child quest of a a parent quest.
 */
public class SubQuest extends Quest {

    private final Quest _parent;
    private LinkedList<Quest> _fullPath;
    private String fullName;

    /**
     * Constructor.
     *
     * @param parent       The parent quest.
     * @param questName    The quest name.
     * @param displayName  The display name.
     * @param dataNode     The quests data node.
     */
    public SubQuest(Quest parent, String questName, String displayName, IDataNode dataNode) {
        super(questName, displayName, dataNode);

        PreCon.notNull(parent);

        _parent = parent;
    }

    /**
     * Get the parent node.
     *
     * @return  Null if the node is a top level node.
     */
    @Nullable
    @Override
    public Quest getParent() {
        return _parent;
    }

    /**
     * Get the nodes children.
     */
    @Override
    public Collection<Quest> getChildren() {
        return getQuests();
    }

    /**
     * Get an object path of the quests parent hierarchy.
     */
    public LinkedList<Quest> getFullPath() {

        if (_fullPath == null) {
            Quest quest = this;
            LinkedList<Quest> quests = new LinkedList<>();

            while (quest != null) {

                quests.add(quest);

                if (quest instanceof SubQuest) {
                    SubQuest subQuest = (SubQuest) quest;

                    quest = subQuest._parent;
                } else if (quest instanceof PrimaryQuest) {
                    break;
                }
            }

            _fullPath = quests;
        }
        return _fullPath;
    }

    @Override
    public String getPathName() {
        if (fullName == null) {
            LinkedList<Quest> quests = getFullPath();

            StringBuilder sb = new StringBuilder(quests.size() * 15);

            while (!quests.isEmpty()) {
                sb.append(quests.getLast());

                if (!quests.isEmpty()) {
                    sb.append('.');
                }
            }

            fullName = sb.toString();
        }

        return fullName;
    }
}
