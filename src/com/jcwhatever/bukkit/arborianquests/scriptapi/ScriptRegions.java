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

/**
 * Provide scripts with API access to {@code ScriptRegion} events.
 */
@IScriptApiInfo(
        variableName = "regions",
        description = "Provide scripts with API access to ScriptRegion events.")
public class ScriptRegions extends GenericsScriptApi {

    private Set<ScriptRegion> _referencedRegions = new HashSet<>(15);

    /**
     * Constructor.
     *
     * @param plugin  Required plugin parameter for script api repository.
     */
    public ScriptRegions(Plugin plugin) {

        // quests is always the owning plugin
        super(ArborianQuests.getInstance());
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return this;
    }

    @Override
    public void reset() {
        for (ScriptRegion region: _referencedRegions) {
            region.clearHandlers();
        }
        _referencedRegions.clear();
    }

    /**
     * Add a handler to execute whenever a player enters a script region.
     *
     * @param regionName  The name of the script region.
     * @param onEnter     The handler.
     *
     * @return  True if successful.
     */
    public boolean onEnter(String regionName, IScriptRegionResult onEnter) {
        PreCon.notNullOrEmpty(regionName);
        PreCon.notNull(onEnter);

        ScriptRegion region = ArborianQuests.getInstance().getScriptRegionManager().getRegion(regionName);
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
     * @param regionName  The name of the script region.
     * @param questName   The name of the quest.
     * @param onEnter     The handler.
     *
     * @return  True if successful.
     */
    public boolean onQuestEnter(String regionName, String questName, IScriptRegionResult onEnter) {
        PreCon.notNullOrEmpty(regionName);
        PreCon.notNullOrEmpty(questName);
        PreCon.notNull(onEnter);

        ScriptRegion region = ArborianQuests.getInstance().getScriptRegionManager().getRegion(regionName);
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
     * @param regionName  The name of the script region.
     * @param onLeave     The handler.
     *
     * @return  True if successful.
     */
    public boolean onLeave(String regionName, IScriptRegionResult onLeave) {
        PreCon.notNullOrEmpty(regionName);
        PreCon.notNull(onLeave);

        ScriptRegion region = ArborianQuests.getInstance().getScriptRegionManager().getRegion(regionName);
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
     * @param regionName  The name of the script region.
     * @param questName   The name of the quest.
     * @param onLeave     The handler.
     *
     * @return  True if successful.
     */
    public boolean onQuestLeave(String regionName, String questName, IScriptRegionResult onLeave) {
        PreCon.notNullOrEmpty(regionName);
        PreCon.notNull(onLeave);

        ScriptRegion region = ArborianQuests.getInstance().getScriptRegionManager().getRegion(regionName);
        if (region == null)
            return false;

        if (region.addOnQuestLeave(questName, onLeave)) {
            _referencedRegions.add(region);
            return true;
        }

        return false;
    }

}
