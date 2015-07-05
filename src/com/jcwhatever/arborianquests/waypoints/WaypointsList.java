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

import com.jcwhatever.nucleus.collections.wrap.ListWrapper;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;

/**
 * A list of locations.
 */
public class WaypointsList extends ListWrapper<Location> implements INamedInsensitive {

    private final String _name;
    private final String _searchName;
    private final List<Location> _list;
    private final IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param name      The waypoint list name.
     * @param dataNode  The waypoint list data node.
     */
    public WaypointsList(String name, IDataNode dataNode) {
        _name = name;
        _searchName = name.toLowerCase();
        _dataNode = dataNode;

        _list = new ArrayList<>(dataNode.size() + 5);
        PriorityQueue<Waypoint> toSort = new PriorityQueue<>(dataNode.size());

        for (IDataNode locationNode : dataNode) {
            toSort.add(new Waypoint(locationNode.getName(), locationNode.getLocation("")));
        }

        while (!toSort.isEmpty()) {
            Waypoint waypoint = toSort.remove();
            _list.add(waypoint.location);
        }
    }

    /**
     * Get the world the waypoints are in.
     *
     * @return  The {@link org.bukkit.World} or null if no locations added yet.
     */
    @Nullable
    public World getWorld() {
        if (!_list.isEmpty())
            return _list.get(0).getWorld();

        return null;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    protected List<Location> list() {
        return _list;
    }

    @Override
    protected boolean onPreAdd(Location location) {

        World world = getWorld();
        if (world != null && !world.equals(location.getWorld()))
            return false;

        return true;
    }

    @Override
    protected void onAdded(Location location) {
        save();
    }

    @Override
    protected void onRemoved(Object o) {
        save();
    }

    @Override
    protected void onClear(Collection<Location> values) {
        save();
    }

    private void save() {
        _dataNode.clear();

        for (int i=0; i < _list.size(); i++) {
            _dataNode.set("l" + i, _list.get(i));
        }
        _dataNode.save();
    }

    private static class Waypoint implements Comparable<Waypoint> {

        int order;
        Location location;

        Waypoint(String nodeName, Location location) {
            Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(nodeName);

            if (!matcher.find())
                throw new IllegalStateException("Invalid waypoint data node name: " + nodeName);


            this.order = TextUtils.parseInt(matcher.group(), 0);
            this.location = location;
        }

        @Override
        public int compareTo(Waypoint o) {
            return Integer.compare(order, o.order);
        }
    }
}
