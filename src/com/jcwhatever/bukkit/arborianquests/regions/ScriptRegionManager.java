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


package com.jcwhatever.bukkit.arborianquests.regions;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Manages quest scripting regions.
 */
public class ScriptRegionManager extends NamedInsensitiveDataManager<ScriptRegion> {

    /**
     * Constructor.
     *
     * @param dataNode  The data node to load and store settings.
     */
    public ScriptRegionManager(IDataNode dataNode) {
        super(dataNode);
    }

    /**
     * Add a scripting region.
     *
     * @param name  The name of the region.
     * @param p1    The first region point.
     * @param p2    The second region point.
     *
     * @return  The newly created {@code ScriptRegion} or null if failed.
     */
    @Nullable
    public ScriptRegion add(String name, Location p1, Location p2) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        if (contains(name))
            return null;

        IDataNode regionNode = _dataNode.getNode(name);

        ScriptRegion region = new ScriptRegion(name, regionNode);
        region.setCoords(p1, p2);

        add(region);

        return region;
    }

    /**
     * Add a scripting region using an anchor location and diameter.
     *
     * @param name      The name of the region.
     * @param anchor    The anchor location.
     * @param diameter  The diameter.
     *
     * @return  The newly created {@code ScriptRegion} or null if failed.
     */
    @Nullable
    public ScriptRegion addFromAnchor(String name, Location anchor, int diameter) {
        PreCon.notNull(name);
        PreCon.notNull(anchor);
        PreCon.greaterThanZero(diameter);

        Location p1 = new Location(anchor.getWorld(),
                anchor.getBlockX() + diameter,
                anchor.getBlockY() + diameter,
                anchor.getBlockZ() + diameter);

        Location p2 = new Location(anchor.getWorld(),
                anchor.getBlockX() - diameter,
                anchor.getBlockY() - diameter,
                anchor.getBlockZ() - diameter);

        return add(name, p1, p2);
    }

    @Nullable
    @Override
    protected ScriptRegion load(String name, IDataNode itemNode) {
        return new ScriptRegion(name, itemNode);
    }

    @Nullable
    @Override
    protected void save(ScriptRegion item, IDataNode itemNode) {
        // do nothing
    }
}
