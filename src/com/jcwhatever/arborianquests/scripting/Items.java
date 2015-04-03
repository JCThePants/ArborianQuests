/*
 * This file is part of ArborianQuests for Bukkit, licensed under the MIT License (MIT).
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
import com.jcwhatever.arborianquests.items.ScriptItem;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberLinkedList;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.items.floating.IFloatingItem;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptUpdateSubscriber;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Sub script API for named {@link org.bukkit.inventory.ItemStack}'s that can be retrieved by scripts.
 */
public class Items implements IDisposable {

    static {

        Collection<IFloatingItem> items = new ArrayList<>(
                Nucleus.getFloatingItems().getAll(ArborianQuests.getPlugin()));

        for (IFloatingItem item : items) {
            Nucleus.getFloatingItems().remove(ArborianQuests.getPlugin(), item.getName());
        }
    }

    private final Plugin _plugin = ArborianQuests.getPlugin();
    private final Map<IFloatingItem, Void> _floatingItems = new WeakHashMap<>(20);
    private final SubscriberLinkedList<ISubscriber> _subscribers = new SubscriberLinkedList<>();
    private boolean _isDisposed;


    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        Iterator<IFloatingItem> iterator = _floatingItems.keySet().iterator();

        while (iterator.hasNext()) {
            IFloatingItem item = iterator.next();
            Nucleus.getFloatingItems().remove(_plugin, item.getName());
            iterator.remove();
        }

        while (!_subscribers.isEmpty()) {
            ISubscriber subscriber = _subscribers.remove();
            subscriber.dispose();
        }

        _isDisposed = true;
    }

    @Nullable
    public ItemStack getItem(String name) {
        PreCon.notNullOrEmpty(name);

        ScriptItem item = ArborianQuests.getScriptItemManager().get(name);
        if (item == null)
            return null;

        return item.getItem();
    }

    /**
     * Create floating item from an item stack and location.
     *
     * @param itemStack  The item stack.
     * @param location   The location the item will spawn in.
     */
    @Nullable
    public IFloatingItem createFloatingItem(ItemStack itemStack, Location location) {
        PreCon.notNull(itemStack);
        PreCon.notNull(location);

        IFloatingItem floatingItem = Nucleus.getFloatingItems().add(
                _plugin, UUID.randomUUID().toString(),
                itemStack.clone(), location);

        if (floatingItem != null)
            _floatingItems.put(floatingItem, null);

        return floatingItem;
    }

    /**
     * Dispose floating item.
     *
     * @param item  The item to remove and dispose.
     *
     * @return  True if successful.
     */
    public boolean disposeFloatingItem(IFloatingItem item) {
        return Nucleus.getFloatingItems().remove(ArborianQuests.getPlugin(), item.getName());
    }

    /**
     * Add an item pickup handler.
     *
     * @param item      The item to add the callback to.
     * @param callback  The callback to run when the item is picked up.
     */
    public void onPickup(IFloatingItem item, IScriptUpdateSubscriber<Player> callback) {
        PreCon.notNull(item);
        PreCon.notNull(callback);

        ScriptUpdateSubscriber<Player> subscriber = new ScriptUpdateSubscriber<>(callback);
        item.onPickup(subscriber);
        _subscribers.add(subscriber);
    }

    /**
     * Add an item spawn handler.
     *
     * @param item      The name of the floating item.
     * @param callback  The callback to run when the item is spawned.
     */
    public void onSpawn(IFloatingItem item, IScriptUpdateSubscriber<Entity> callback) {
        PreCon.notNull(item);
        PreCon.notNull(callback);

        ScriptUpdateSubscriber<Entity> subscriber = new ScriptUpdateSubscriber<>(callback);
        item.onSpawn(subscriber);
        _subscribers.add(subscriber);
    }

    /**
     * Add an item despawn handler.
     *
     * @param item      The name of the floating item.
     * @param callback  The callback to run when the item is despawned.
     */
    public void onDespawn(IFloatingItem item, IScriptUpdateSubscriber<Entity> callback) {
        PreCon.notNull(item);
        PreCon.notNull(callback);

        ScriptUpdateSubscriber<Entity> subscriber = new ScriptUpdateSubscriber<>(callback);
        item.onDespawn(subscriber);
        _subscribers.add(subscriber);
    }
}
