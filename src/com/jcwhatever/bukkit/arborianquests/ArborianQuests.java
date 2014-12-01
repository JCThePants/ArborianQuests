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


package com.jcwhatever.bukkit.arborianquests;

import com.jcwhatever.bukkit.arborianquests.commands.CommandHandler;
import com.jcwhatever.bukkit.arborianquests.items.ScriptItemManager;
import com.jcwhatever.bukkit.arborianquests.locations.ScriptLocationManager;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.bukkit.arborianquests.scripting.QuestsApi;
import com.jcwhatever.bukkit.generic.GenericsPlugin;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiRepo;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;

public class ArborianQuests extends GenericsPlugin {

    private static ArborianQuests _instance;

    public static ArborianQuests getPlugin() {
        return _instance;
    }

    private ScriptRegionManager _scriptRegionManager;
    private ScriptLocationManager _scriptLocationManager;
    private ScriptItemManager _scriptItemManager;

    private IDataNode _metaNode;

    public ArborianQuests() {
        super();

        _instance = this;
    }

    public ScriptRegionManager getScriptRegionManager() {
        return _scriptRegionManager;
    }

    public ScriptLocationManager getScriptLocationManager() {
        return _scriptLocationManager;
    }

    public ScriptItemManager getScriptItemManager() {
        return _scriptItemManager;
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
    protected void onEnablePlugin() {

        _metaNode = DataStorage.getStorage(ArborianQuests.getPlugin(), new DataPath("meta"));
        _metaNode.load();

        IDataNode regionNode = DataStorage.getStorage(this, new DataPath("regions"));
        regionNode.load();

        IDataNode locationNode = DataStorage.getStorage(this, new DataPath("locations"));
        locationNode.load();

        IDataNode itemsNode = DataStorage.getStorage(this, new DataPath("items"));
        itemsNode.load();

        _scriptRegionManager = new ScriptRegionManager(regionNode);
        _scriptLocationManager = new ScriptLocationManager(locationNode);
        _scriptItemManager = new ScriptItemManager(itemsNode);

        registerCommands(new CommandHandler());

        ScriptApiRepo.registerApiType(this, QuestsApi.class);
    }

    @Override
    protected void onDisablePlugin() {

        ScriptApiRepo.unregisterApiType(this, QuestsApi.class);
    }
}
