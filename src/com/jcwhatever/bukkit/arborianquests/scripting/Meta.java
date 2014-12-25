package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.generic.scripting.ScriptApiInfo;
import com.jcwhatever.generic.scripting.api.ScriptApiMeta;

import org.bukkit.plugin.Plugin;

// annotation required but not used
@ScriptApiInfo(
        variableName = "questsScriptApiMeta",
        description = "")
public class Meta extends ScriptApiMeta {

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin   The owning plugin
     */
    public Meta(Plugin plugin) {
        super(plugin, ArborianQuests.getPlugin().getMetaDataNode());
    }
}
