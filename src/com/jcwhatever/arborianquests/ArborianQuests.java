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


package com.jcwhatever.arborianquests;

import com.jcwhatever.arborianquests.click.ClickContext;
import com.jcwhatever.arborianquests.click.ClickExemptNpcTrait;
import com.jcwhatever.arborianquests.click.GlobalClickListener;
import com.jcwhatever.arborianquests.commands.users.BaseCommand;
import com.jcwhatever.arborianquests.items.ScriptItemManager;
import com.jcwhatever.arborianquests.locations.ScriptLocationManager;
import com.jcwhatever.arborianquests.quests.QuestManager;
import com.jcwhatever.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.arborianquests.scripting.QuestsApi;
import com.jcwhatever.arborianquests.waypoints.WaypointsManager;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.managed.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.managed.scripting.IScriptApi;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi;
import com.jcwhatever.nucleus.managed.scripting.SimpleScriptApi.IApiObjectCreator;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.text.TextColor;
import org.bukkit.plugin.Plugin;

/**
 * ArborianQuests plugin.
 *
 * <p>A script centric quest provider primarily centered on providing basic quest facilities that
 * can be accessed by scripts via the script API. The script API can be accessed from scripts running
 * in NucleusFramework or other plugins using Nucleus scripting by including the API:
 * {@code include.api("ArborianQuests", "quests", "quests");}</p>
 *
 * <p>The script will be dependent on ArborianQuests and should specify this using:
 * {@code depends.on("ArborianQuests")...}</p>
 *
 * <p>Besides providing basic quest facilities, ArborianQuests makes it easy to set up items, locations,
 * and regions which can be referenced from scripts by name.</p>
 */
public class ArborianQuests extends NucleusPlugin {

    private static ArborianQuests _instance;

    public static ArborianQuests getPlugin() {
        return _instance;
    }

    private QuestManager _questManager;
    private ScriptRegionManager _scriptRegionManager;
    private ScriptLocationManager _scriptLocationManager;
    private WaypointsManager _waypointsManager;
    private ScriptItemManager _scriptItemManager;
    private ClickContext _globalClickContext;

    private IScriptApi _scriptApi;
    private IDataNode _metaNode;

    /**
     * Get the quest manager.
     */
    public static QuestManager getQuestManager() {
        return _instance._questManager;
    }

    /**
     * Get the region manager.
     */
    public static ScriptRegionManager getScriptRegionManager() {
        return _instance._scriptRegionManager;
    }

    /**
     * Get the location manager.
     */
    public static ScriptLocationManager getScriptLocationManager() {
        return _instance._scriptLocationManager;
    }

    /**
     * Get the waypoints manager.
     */
    public static WaypointsManager getWaypointsManager() {
        return _instance._waypointsManager;
    }

    /**
     * Get the item manager.
     */
    public static ScriptItemManager getScriptItemManager() {
        return _instance._scriptItemManager;
    }

    /**
     * Get the global NPC click context.
     */
    public static ClickContext getGlobalClickContext() {
        return _instance._globalClickContext;
    }

    @Override
    public String getChatPrefix() {
        return TextColor.WHITE + "[" + TextColor.BLUE + "Quests" + TextColor.WHITE + "] ";
    }

    @Override
    public String getConsolePrefix() {
        return "[ArborianQuests] ";
    }

    public IDataNode getMetaDataNode() {
        return _metaNode;
    }

    @Override
    protected void onInit() {
        _instance = this;
    }

    @Override
    protected void onEnablePlugin() {

        _instance = this;

        _metaNode = DataStorage.get(ArborianQuests.getPlugin(), new DataPath("meta"));
        _metaNode.load();

        IDataNode regionNode = DataStorage.get(this, new DataPath("regions"));
        regionNode.load();

        IDataNode locationNode = DataStorage.get(this, new DataPath("locations"));
        locationNode.load();

        IDataNode waypointsNode = DataStorage.get(this, new DataPath("waypoints"));
        waypointsNode.load();

        IDataNode itemsNode = DataStorage.get(this, new DataPath("items"));
        itemsNode.load();

        INpcProvider provider = Nucleus.getProviders().getNpcs();
        if (provider != null) {
            provider.registerTrait(new ClickExemptNpcTrait());
        }

        _globalClickContext = new ClickContext();
        _questManager = new QuestManager(this, getDataNode());
        _scriptRegionManager = new ScriptRegionManager(regionNode);
        _scriptLocationManager = new ScriptLocationManager(locationNode);
        _waypointsManager = new WaypointsManager(waypointsNode);
        _scriptItemManager = new ScriptItemManager(itemsNode);

        registerCommand(BaseCommand.class);

        _scriptApi = new SimpleScriptApi(this, "quests", new IApiObjectCreator() {
            @Override
            public IDisposable create(Plugin plugin, IEvaluatedScript script) {
                return new QuestsApi();
            }
        });

        Nucleus.getScriptApiRepo().registerApi(_scriptApi);
        registerEventListeners(new GlobalClickListener(_globalClickContext));
    }

    @Override
    protected void onDisablePlugin() {

        Nucleus.getScriptApiRepo().unregisterApi(_scriptApi);
    }
}
