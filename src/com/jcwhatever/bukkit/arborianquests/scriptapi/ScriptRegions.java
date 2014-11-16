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


package com.jcwhatever.bukkit.arborianquests.scriptapi;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.regions.IScriptRegionResult;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegion;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.api.GenericsScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Provide scripts with API access to {@code ScriptRegion} events.
 */
@IScriptApiInfo(
        variableName = "questRegions",
        description = "Provide scripts with API access to ScriptRegion events.")
public class ScriptRegions extends GenericsScriptApi {

    private static ApiObject _api;

    /**
     * Constructor.
     *
     * @param plugin  Required plugin parameter for script api repository.
     */
    public ScriptRegions(Plugin plugin) {

        // quests is always the owning plugin
        super(ArborianQuests.getPlugin());

        _api = new ApiObject();
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return _api;
    }

    public static class ApiObject implements IScriptApiObject {

        private Set<ScriptRegion> _referencedRegions = new HashSet<>(15);

        @Override
        public void reset() {
            for (ScriptRegion region : _referencedRegions) {
                region.clearHandlers();
            }
            _referencedRegions.clear();
        }

        /**
         * Get a quest region by name.
         *
         * @param name  The name of the region.
         */
        @Nullable
        public ScriptRegion getRegion(String name) {
            PreCon.notNullOrEmpty(name);

            return ArborianQuests.getPlugin().getScriptRegionManager().getRegion(name);
        }

        /**
         * Add a handler to execute whenever a player enters a script region.
         *
         * @param regionName The name of the script region.
         * @param onEnter    The handler.
         * @return True if successful.
         */
        public boolean onEnter(String regionName, IScriptRegionResult onEnter) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onEnter);

            ScriptRegion region = ArborianQuests.getPlugin().getScriptRegionManager().getRegion(regionName);
            if (region == null)
                return false;

            if (region.addOnEnter(onEnter)) {
                _referencedRegions.add(region);
                return true;
            }

            return false;
        }

        /**
         * Add a handler to execute whenever a player enters a script region and is on the
         * specified quest. Execution goes to the most recent quest the player is on.
         *
         * @param regionName The name of the script region.
         * @param questName  The name of the quest.
         * @param onEnter    The handler.
         * @return True if successful.
         */
        public boolean onQuestEnter(String regionName, String questName, IScriptRegionResult onEnter) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNullOrEmpty(questName);
            PreCon.notNull(onEnter);

            ScriptRegion region = ArborianQuests.getPlugin().getScriptRegionManager().getRegion(regionName);
            if (region == null)
                return false;

            if (region.addOnQuestEnter(questName, onEnter)) {
                _referencedRegions.add(region);
                return true;
            }

            return false;
        }

        /**
         * Add a handler to execute whenever a player leaves a script region.
         *
         * @param regionName The name of the script region.
         * @param onLeave    The handler.
         * @return True if successful.
         */
        public boolean onLeave(String regionName, IScriptRegionResult onLeave) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onLeave);

            ScriptRegion region = ArborianQuests.getPlugin().getScriptRegionManager().getRegion(regionName);
            if (region == null)
                return false;

            if (region.addOnLeave(onLeave)) {
                _referencedRegions.add(region);
                return true;
            }

            return false;
        }

        /**
         * Add a handler to execute whenever a player leaves a script region and is on
         * the specified quest. Execution goes to the most recent quest the player is on.
         *
         * @param regionName The name of the script region.
         * @param questName  The name of the quest.
         * @param onLeave    The handler.
         * @return True if successful.
         */
        public boolean onQuestLeave(String regionName, String questName, IScriptRegionResult onLeave) {
            PreCon.notNullOrEmpty(regionName);
            PreCon.notNull(onLeave);

            ScriptRegion region = ArborianQuests.getPlugin().getScriptRegionManager().getRegion(regionName);
            if (region == null)
                return false;

            if (region.addOnQuestLeave(questName, onLeave)) {
                _referencedRegions.add(region);
                return true;
            }

            return false;
        }
    }

}
