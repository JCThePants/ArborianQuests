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

import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/*
 * 
 */
public class ScriptItemManager {

    private final IDataNode _dataNode;
    private final Map<String, ScriptItem> _items = new HashMap<>(30);

    public ScriptItemManager(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        _dataNode = dataNode;

        loadSettings();
    }

    @Nullable
    public ScriptItem addItem(String name, ItemStack item) {

        if (_items.containsKey(name.toLowerCase()))
            return null;

        ScriptItem scriptItem = new ScriptItem(name, item);

        _items.put(scriptItem.getSearchName(), scriptItem);

        _dataNode.set(name, item);
        _dataNode.saveAsync(null);

        return scriptItem;
    }

    public boolean removeItem(String name) {

        ScriptItem item = _items.remove(name.toLowerCase());
        if (item == null)
            return false;

        _dataNode.set(name, null);
        _dataNode.saveAsync(null);

        return true;
    }

    @Nullable
    public ScriptItem getItem(String name) {
        PreCon.notNullOrEmpty(name);

        return _items.get(name.toLowerCase());
    }

    public List<ScriptItem> getItems() {
        return new ArrayList<>(_items.values());
    }

    private void loadSettings() {

        Set<String> names = _dataNode.getSubNodeNames();

        for (String name : names) {

            ItemStack[] items = _dataNode.getItemStacks(name);
            if (items == null || items.length == 0)
                continue;

            ScriptItem scriptItem = new ScriptItem(name, items[0]);
            _items.put(scriptItem.getSearchName(), scriptItem);
        }
    }

}
