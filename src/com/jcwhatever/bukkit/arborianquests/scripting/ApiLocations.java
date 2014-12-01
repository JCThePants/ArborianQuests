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
import com.jcwhatever.bukkit.arborianquests.locations.ScriptLocation;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.api.GenericsScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.List;
import javax.annotation.Nullable;

@ScriptApiInfo(
        variableName = "questLocations",
        description = "Get quest scriptable locations.")
public class ApiLocations extends GenericsScriptApi {

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public ApiLocations(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject();
    }

    public static class ApiObject implements IScriptApiObject {

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Get a quest script location by name.
         *
         * @param name  The name of the location.
         */
        @Nullable
        public Location get(String name) {
            PreCon.notNullOrEmpty(name);

            ScriptLocation result = ArborianQuests.getPlugin().getScriptLocationManager().getLocation(name);
            if (result == null)
                return null;

            return result.getLocation();
        }

        /**
         * Get all script location objects.
         */
        public List<ScriptLocation> getScriptLocations() {
            return ArborianQuests.getPlugin().getScriptLocationManager().getLocations();
        }
    }
}
