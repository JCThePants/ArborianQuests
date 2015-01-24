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


package com.jcwhatever.arborianquests.locations;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Manages script locations.
 */
public class ScriptLocationManager extends NamedInsensitiveDataManager<ScriptLocation> {

    /**
     * Constructor.
     *
     * @param dataNode  The data node where locations are stored.
     */
    public ScriptLocationManager(IDataNode dataNode) {
        super(dataNode, true);
    }

    /**
     * Add a scripted location.
     *
     * @param name      The name of the location.
     * @param location  The location.
     *
     * @return  The new {@code ScriptLocation} or null if the name is already in use.
     */
    @Nullable
    public ScriptLocation add(String name, Location location) {

        if (contains(name))
            return null;

        ScriptLocation scriptLocation = new ScriptLocation(name, location);

        add(scriptLocation);

        return scriptLocation;
    }

    @Nullable
    @Override
    protected ScriptLocation load(String name, IDataNode itemNode) {
        Location location = itemNode.getLocation("");
        if (location == null)
            return null;

        return new ScriptLocation(name, location);
    }

    @Nullable
    @Override
    protected void save(ScriptLocation item, IDataNode itemNode) {
        itemNode.set("", item.getLocation());
    }
}
