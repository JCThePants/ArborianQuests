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

package com.jcwhatever.bukkit.arborianquests.items;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Manages {@code ScriptItem}'s
 */
public class ScriptItemManager extends NamedInsensitiveDataManager<ScriptItem> {

    /**
     * Constructor.
     *
     * @param dataNode  The data node where {@code ItemStacks are stored.}
     */
    public ScriptItemManager(IDataNode dataNode) {
        super(dataNode);
    }

    /**
     * Add a scripted {@code ItemStack}.
     *
     * @param name  The name of the {@code ItemStack}.
     * @param item  The {@code ItemStack}.
     *
     * @return  The created {@code ScriptItem} or null if the name is already in use.
     */
    @Nullable
    public ScriptItem add(String name, ItemStack item) {

        if (contains(name))
            return null;

        ScriptItem scriptItem = new ScriptItem(name, item);

        add(scriptItem);

        return scriptItem;
    }

    @Nullable
    @Override
    protected ScriptItem load(String name, IDataNode itemNode) {

        ItemStack[] items = itemNode.getItemStacks("");
        if (items == null || items.length == 0)
            return null;

        return new ScriptItem(name, items[0]);
    }

    @Nullable
    @Override
    protected void save(ScriptItem item, IDataNode itemNode) {
        itemNode.set("", item.getItem());
    }
}
