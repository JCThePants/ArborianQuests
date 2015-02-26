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

package com.jcwhatever.arborianquests.scripting;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.waypoints.WaypointsList;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sub script API for named locations that can be retrieved by scripts.
 *
 * @see QuestsApi
 */
public class Waypoints {

    /**
     * Get a script API object for the specified script.
     *
     * @param script  The script to get the API object for.
     */
    public IScriptApiObject getApiObject(@SuppressWarnings("unused") IEvaluatedScript script) {
        return new ApiObject();
    }

    public static class ApiObject implements IScriptApiObject {

        private boolean _isDisposed;

        ApiObject() {}

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {
            _isDisposed = true;
        }

        /**
         * Get a waypoints list by name.
         *
         * @param name  The name of the waypoints list.
         */
        public List<Location> get(String name) {
            PreCon.notNullOrEmpty(name, "name");

            WaypointsList waypoints = ArborianQuests.getWaypointsManager().get(name);
            PreCon.isValid(waypoints != null, "Waypoints named {0} not found.", name);

            List<Location> results = new ArrayList<>(waypoints.size());

            for (Location location : waypoints) {
                results.add(LocationUtils.copy(location));
            }

            return results;
        }

        /**
         * Get all script location objects.
         */
        public Collection<ScriptWaypoints> getWaypointLists() {
            Collection<WaypointsList> waypoints = ArborianQuests.getWaypointsManager().getAll();

            List<ScriptWaypoints> results = new ArrayList<>(waypoints.size());

            for (WaypointsList list : waypoints) {
                results.add(new ScriptWaypoints(list));
            }

            return results;
        }

        public static class ScriptWaypoints extends ArrayList<Location> implements INamedInsensitive {

            private final String _name;
            private final String _searchName;

            ScriptWaypoints(WaypointsList list) {
                _name = list.getName();
                _searchName = list.getSearchName();

                for (Location location : list)
                    add(LocationUtils.copy(location));
            }

            @Override
            public String getSearchName() {
                return _name;
            }

            @Override
            public String getName() {
                return _searchName;
            }
        }
    }
}
