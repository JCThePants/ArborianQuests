package com.jcwhatever.bukkit.arborianquests.scriptapi;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiFlags;

import org.bukkit.plugin.Plugin;

@IScriptApiInfo(
        variableName = "questFlags",
        description = "Provide script access to ArborianQuests flags.")
public class ScriptFlags extends ScriptApiFlags {

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin   The owning plugin
     */
    public ScriptFlags(Plugin plugin) {
        super(plugin, ArborianQuests.getPlugin().getFlagsDataNode());
    }
}
