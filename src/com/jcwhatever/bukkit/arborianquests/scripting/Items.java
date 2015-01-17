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

package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.items.ScriptItem;
import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberLinkedList;
import com.jcwhatever.nucleus.utils.floatingitems.FloatingItem;
import com.jcwhatever.nucleus.utils.floatingitems.FloatingItemManager;
import com.jcwhatever.nucleus.utils.floatingitems.IFloatingItem;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.scripting.api.ScriptUpdateSubscriber;
import com.jcwhatever.nucleus.scripting.api.ScriptUpdateSubscriber.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public class Items {

    private static ApiObject _api;
    private static FloatingItemManager _manager;

    static {

        IDataNode dataNode = DataStorage.get(ArborianQuests.getPlugin(), new DataPath("floating-items"));
        dataNode.load();

        _manager = new FloatingItemManager(ArborianQuests.getPlugin(), dataNode);

        List<IFloatingItem> floatingItems = _manager.getAll();

        // remove all items, ensures items are removed if
        // the server is not shut down properly.
        for (IFloatingItem item : floatingItems) {
            _manager.remove(item.getName());
        }

        _api = new ApiObject();
    }

    public IScriptApiObject getApiObject(@SuppressWarnings("unused") IEvaluatedScript script) {
        return _api;
    }

    public static class ApiObject implements IScriptApiObject {

        private Map<IFloatingItem, Void> _floatingItems = new WeakHashMap<>(20);
        private SubscriberLinkedList<ISubscriber> _subscribers = new SubscriberLinkedList<>();
        private boolean _isDisposed;

        public ApiObject () {

        }

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {

            Iterator<IFloatingItem> iterator = _floatingItems.keySet().iterator();

            while (iterator.hasNext()) {
                IFloatingItem item = iterator.next();
                _manager.remove(item.getName());
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

            IFloatingItem floatingItem = _manager.add(UUID.randomUUID().toString(),
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
        public boolean disposeFloatingItem(FloatingItem item) {
            return _manager.remove(item.getName());
        }

        /**
         * Add an item pickup handler.
         *
         * @param item      The item to add the callback to.
         * @param callback  The callback to run when the item is picked up.
         */
        public void onPickup(FloatingItem item, IScriptUpdateSubscriber callback) {
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
        public void onSpawn(FloatingItem item, IScriptUpdateSubscriber callback) {
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
        public void onDespawn(FloatingItem item, IScriptUpdateSubscriber callback) {
            PreCon.notNull(item);
            PreCon.notNull(callback);

            ScriptUpdateSubscriber<Entity> subscriber = new ScriptUpdateSubscriber<>(callback);
            item.onDespawn(subscriber);
            _subscribers.add(subscriber);
        }
    }
}
