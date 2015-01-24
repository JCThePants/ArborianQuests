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


package com.jcwhatever.arborianquests.scripting;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.regions.ScriptRegion;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.scripting.api.ScriptUpdateSubscriber.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Sub script API for quest regions that can be retrieved by scripts.
 */
public class Regions {

    private static ApiObject _api = new ApiObject();

    /**
     * Get an API object for the specified script.
     *
     * @param script  The script to get an API object for.
     */
    public IScriptApiObject getApiObject(@SuppressWarnings("unused") IEvaluatedScript script) {
        return _api;
    }

    public static class ApiObject implements IScriptApiObject {

        private Set<ScriptRegion> _referencedRegions = new HashSet<>(15);
        private boolean _isDisposed;

        ApiObject() {}

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {
            for (ScriptRegion region : _referencedRegions) {
                region.clearSubscribers();
            }
            _referencedRegions.clear();
            _isDisposed = true;
        }

        /**
         * Get a quest region by name.
         *
         * @param name  The name of the region.
         */
        @Nullable
        public ScriptRegion getRegion(String name) {
            PreCon.notNullOrEmpty(name);

            return ArborianQuests.getScriptRegionManager().get(name);
        }

        /**
         * Add a handler to execute whenever a player enters a script region.
         *
         * @param regionName The name of the script region.
         * @param onEnter    The handler.
         */
        public void onEnter(String regionName, IScriptUpdateSubscriber onEnter) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onEnter);

            ScriptRegion region = ArborianQuests.getScriptRegionManager().get(regionName);
            if (region == null) {
                throw new RuntimeException("Failed to find quest region named:" + regionName);
            }

            region.onEnter(onEnter);
            _referencedRegions.add(region);
        }

        /**
         * Add a handler to execute whenever a player leaves a script region.
         *
         * @param regionName The name of the script region.
         * @param onLeave    The handler.
         */
        public void onLeave(String regionName, IScriptUpdateSubscriber onLeave) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onLeave);

            ScriptRegion region = ArborianQuests.getScriptRegionManager().get(regionName);
            if (region == null) {
                throw new RuntimeException("Failed to find quest region named:" + regionName);
            }

            region.onLeave(onLeave);
            _referencedRegions.add(region);
        }
    }
}
