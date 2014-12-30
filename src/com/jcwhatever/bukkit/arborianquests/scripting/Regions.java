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


package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.regions.IScriptRegionResult;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegion;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class Regions {

    private static ApiObject _api = new ApiObject();

    public IScriptApiObject getApiObject(@SuppressWarnings("unused") IEvaluatedScript script) {
        return _api;
    }

    public static class ApiObject implements IScriptApiObject {

        private Set<ScriptRegion> _referencedRegions = new HashSet<>(15);
        private boolean _isDisposed;

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {
            for (ScriptRegion region : _referencedRegions) {
                region.clearHandlers();
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
        public void onEnter(String regionName, IScriptRegionResult onEnter) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onEnter);

            ScriptRegion region = ArborianQuests.getScriptRegionManager().get(regionName);
            if (region == null) {
                throw new RuntimeException("Failed to find quest region named:" + regionName);
            }

            region.addOnEnter(onEnter);
            _referencedRegions.add(region);
        }

        /**
         * Add a handler to execute whenever a player enters a script region and is on the
         * specified quest. Execution goes to the most recent quest the player is on.
         *
         * @param regionName The name of the script region.
         * @param questName  The name of the quest.
         * @param onEnter    The handler.
         */
        public void onQuestEnter(String regionName, String questName, IScriptRegionResult onEnter) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNullOrEmpty(questName);
            PreCon.notNull(onEnter);

            ScriptRegion region = ArborianQuests.getScriptRegionManager().get(regionName);
            if (region == null) {
                throw new RuntimeException("Failed to find quest region named:" + regionName);
            }

            if (!region.addOnQuestEnter(questName, onEnter)) {
                throw new RuntimeException("Failed to find quest named:" + questName);
            }

            _referencedRegions.add(region);
        }

        /**
         * Add a handler to execute whenever a player leaves a script region.
         *
         * @param regionName The name of the script region.
         * @param onLeave    The handler.
         */
        public void onLeave(String regionName, IScriptRegionResult onLeave) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onLeave);

            ScriptRegion region = ArborianQuests.getScriptRegionManager().get(regionName);
            if (region == null) {
                throw new RuntimeException("Failed to find quest region named:" + regionName);
            }

            region.addOnLeave(onLeave);
            _referencedRegions.add(region);
        }

        /**
         * Add a handler to execute whenever a player leaves a script region and is on
         * the specified quest. Execution goes to the most recent quest the player is on.
         *
         * @param regionName The name of the script region.
         * @param questName  The name of the quest.
         * @param onLeave    The handler.
         */
        public void onQuestLeave(String regionName, String questName, IScriptRegionResult onLeave) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onLeave);

            ScriptRegion region = ArborianQuests.getScriptRegionManager().get(regionName);
            if (region == null) {
                throw new RuntimeException("Failed to find quest region named:" + regionName);
            }

            if (!region.addOnQuestLeave(questName, onLeave)) {
                throw new RuntimeException("Failed to find quest named:" + questName);
            }
        }
    }

}
