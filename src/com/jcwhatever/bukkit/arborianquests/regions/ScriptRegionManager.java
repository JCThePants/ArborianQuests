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

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Manages quest scripting regions.
 */
public class ScriptRegionManager {

    private Map<String, ScriptRegion> _scriptRegions = new HashMap<>(20);
    private IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param dataNode  The data node to load and store settings.
     */
    public ScriptRegionManager(IDataNode dataNode) {
        _dataNode = dataNode;

        loadSettings();
    }

    /**
     * Get a scripting region by name.
     *
     * @param name  The name of the region.
     */
    @Nullable
    public ScriptRegion getRegion(String name) {
        PreCon.notNullOrEmpty(name);

        return _scriptRegions.get(name.toLowerCase());
    }

    /**
     * Get all scripting regions.
     */
    public List<ScriptRegion> getRegions() {
        return new ArrayList<>(_scriptRegions.values());
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
    public ScriptRegion addRegion(String name, Location p1, Location p2) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        ScriptRegion region = _scriptRegions.get(name.toLowerCase());
        if (region != null)
            return null;

        IDataNode regionNode = _dataNode.getNode(name);

        region = new ScriptRegion(name, regionNode);
        region.setCoords(p1, p2);

        _scriptRegions.put(region.getSearchName(), region);

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

        return addRegion(name, p1, p2);
    }

    /**
     * Remove a region by name.
     *
     * @param name  The name of the region.
     *
     * @return  True if successful.
     */
    public boolean removeRegion(String name) {
        PreCon.notNullOrEmpty(name);

        ScriptRegion region = _scriptRegions.remove(name.toLowerCase());
        if (region == null)
            return false;

        IDataNode regionNode = _dataNode.getNode(name);
        regionNode.remove();
        regionNode.saveAsync(null);

        region.dispose();

        return true;
    }


    private void loadSettings() {

        Set<String> regionNames = _dataNode.getSubNodeNames();
        if (regionNames != null && !regionNames.isEmpty()) {

            for (String regionName : regionNames) {

                IDataNode regionNode = _dataNode.getNode(regionName);

                ScriptRegion region = new ScriptRegion(regionName, regionNode);

                _scriptRegions.put(region.getSearchName(), region);
            }
        }
    }


}
