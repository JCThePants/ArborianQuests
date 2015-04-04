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


package com.jcwhatever.arborianquests.regions;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.nucleus.regions.RegionManager;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.regions.SimpleRegionSelection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Manages quest scripting regions.
 */
public class ScriptRegionManager extends RegionManager<ScriptRegion> {

    /**
     * Constructor.
     *
     * @param dataNode  The data node to load and store settings.
     */
    public ScriptRegionManager(IDataNode dataNode) {
        super(ArborianQuests.getPlugin(), dataNode, true);
    }

    /**
     * Add a scripting region using an anchor location and radius.
     *
     * <p>The script region is created with the anchor as its center location.
     * Although the term 'radius' is used, the region is not a sphere, it will be
     * a perfect cube.</p>
     *
     * @param name    The name of the region.
     * @param anchor  The anchor location.
     * @param radius  The radius.
     *
     * @return  The newly created {@link ScriptRegion} or null if failed.
     */
    @Nullable
    public ScriptRegion addFromAnchor(String name, Location anchor, int radius) {
        PreCon.notNull(name);
        PreCon.notNull(anchor);
        PreCon.greaterThanZero(radius);

        Location p1 = new Location(anchor.getWorld(),
                anchor.getBlockX() + radius,
                anchor.getBlockY() + radius,
                anchor.getBlockZ() + radius);

        Location p2 = new Location(anchor.getWorld(),
                anchor.getBlockX() - radius,
                anchor.getBlockY() - radius,
                anchor.getBlockZ() - radius);

        return add(name, new SimpleRegionSelection(p1, p2));
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

    @Override
    protected ScriptRegion create(String name, @Nullable IDataNode dataNode, IRegionSelection selection) {
        ScriptRegion region = new ScriptRegion(name, dataNode);
        region.setCoords(selection.getP1(), selection.getP2());

        return region;
    }
}
