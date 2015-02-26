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

package com.jcwhatever.arborianquests.waypoints;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import javax.annotation.Nullable;

/**
 * Manages waypoints lists.
 */
public class WaypointsManager extends NamedInsensitiveDataManager<WaypointsList> {

    /**
     * Constructor.
     *
     * @param dataNode The data node.
     */
    public WaypointsManager(IDataNode dataNode) {
        super(dataNode, true);
    }

    @Nullable
    public WaypointsList add(String name) {
        PreCon.notNullOrEmpty(name);

        if (contains(name))
            return null;

        WaypointsList waypoints = new WaypointsList(name, getNode(name));

        add(waypoints);

        return waypoints;
    }

    @Nullable
    @Override
    protected WaypointsList load(String name, IDataNode itemNode) {
        return new WaypointsList(name, itemNode);
    }

    @Override
    protected void save(WaypointsList item, IDataNode itemNode) {
        // do nothing
    }
}
