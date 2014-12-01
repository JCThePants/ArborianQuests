package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiMeta;

import org.bukkit.plugin.Plugin;

@ScriptApiInfo(
        variableName = "questMeta",
        description = "Gives script access to ArborianQuests meta data.")
public class ApiMeta extends ScriptApiMeta {

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin   The owning plugin
     */
    public ApiMeta(Plugin plugin) {
        super(plugin, ArborianQuests.getPlugin().getMetaDataNode());
    }
}
