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

import com.jcwhatever.nucleus.mixins.INamedInsensitive;

import org.bukkit.inventory.ItemStack;

/**
 * A named {@code ItemStack} that can be retrieved from scripts
 */
public class ScriptItem implements INamedInsensitive {

    private final String _name;
    private final String _searchName;
    private final ItemStack _itemStack;

    /**
     * Constructor.
     *
     * @param name       The name of the {@code ItemStack}
     * @param itemStack  The {@code ItemStack}
     */
    public ScriptItem(String name, ItemStack itemStack) {
        _name = name;
        _itemStack = itemStack;
        _searchName = name.toLowerCase();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    /**
     * Get the item stack.
     *
     * @return  A clone of the {@code ItemStack}.
     */
    public ItemStack getItem() {
        return _itemStack.clone();
    }
}
