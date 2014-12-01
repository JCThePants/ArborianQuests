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
import com.jcwhatever.bukkit.generic.items.floating.FloatingItem;
import com.jcwhatever.bukkit.generic.items.floating.FloatingItem.PickupHandler;
import com.jcwhatever.bukkit.generic.items.floating.FloatingItemManager;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class Items {

    private static ApiObject _api;

    static {

        IDataNode dataNode = DataStorage.getStorage(ArborianQuests.getPlugin(), new DataPath("floating-items"));
        FloatingItemManager manager = new FloatingItemManager(ArborianQuests.getPlugin(), dataNode);

        List<FloatingItem> floatingItems = manager.getItems();

        for (FloatingItem item : floatingItems) {
            manager.remove(item.getName());
        }

        _api = new ApiObject(manager);
    }

    public IScriptApiObject getApiObject(@SuppressWarnings("unused") IEvaluatedScript script) {
        return _api;
    }

    public static class ApiObject implements IScriptApiObject {

        private FloatingItemManager _manager;
        private LinkedList<PickupWrapper> _pickupCallbacks = new LinkedList<>();
        private LinkedList<CallbackWrapper> _spawnCallbacks = new LinkedList<>();
        private LinkedList<CallbackWrapper> _despawnCallbacks = new LinkedList<>();
        private boolean _isDisposed;


        public ApiObject (FloatingItemManager manager) {
            _manager = manager;
        }

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {

            List<FloatingItem> floatingItems = _manager.getItems();

            for (FloatingItem item : floatingItems) {
                _manager.remove(item.getName());
            }

            while (!_pickupCallbacks.isEmpty()) {
                PickupWrapper wrapper = _pickupCallbacks.remove();

                wrapper.getItem().removeOnPickup(wrapper);
            }

            while (!_spawnCallbacks.isEmpty()) {
                CallbackWrapper wrapper = _spawnCallbacks.remove();

                wrapper.getItem().removeOnSpawn(wrapper);
            }

            while (!_despawnCallbacks.isEmpty()) {
                CallbackWrapper wrapper = _despawnCallbacks.remove();

                wrapper.getItem().removeOnDespawn(wrapper);
            }

            _isDisposed = true;
        }

        @Nullable
        public ItemStack getItem(String name) {
            PreCon.notNullOrEmpty(name);

            ScriptItem item = ArborianQuests.getPlugin().getScriptItemManager().getItem(name);
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
        public FloatingItem createFloatingItem(ItemStack itemStack, Location location) {
            PreCon.notNull(itemStack);
            PreCon.notNull(location);

            //noinspection ConstantConditions
            return _manager.add(UUID.randomUUID().toString(), itemStack, location);
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
        public void onPickup(FloatingItem item, PickupCallback callback) {
            PreCon.notNull(item);
            PreCon.notNull(callback);

            PickupWrapper wrapper = new PickupWrapper(item, callback);
            item.addOnPickup(wrapper);
            _pickupCallbacks.add(wrapper);
        }

        /**
         * Add an item spawn handler.
         *
         * @param item      The name of the floating item.
         * @param callback  The callback to run when the item is spawned.
         */
        public void onSpawn(FloatingItem item, Runnable callback) {
            PreCon.notNull(item);
            PreCon.notNull(callback);

            CallbackWrapper wrapper = new CallbackWrapper(item, callback);
            item.addOnSpawn(wrapper);
            _spawnCallbacks.add(wrapper);
        }

        /**
         * Add an item despawn handler.
         *
         * @param item      The name of the floating item.
         * @param callback  The callback to run when the item is despawned.
         */
        public void onDespawn(FloatingItem item, Runnable callback) {
            PreCon.notNull(item);
            PreCon.notNull(callback);

            CallbackWrapper wrapper = new CallbackWrapper(item, callback);
            item.addOnDespawn(wrapper);
            _despawnCallbacks.add(wrapper);
        }

        public interface PickupCallback {
            void onPickup(Object player, Object item, Object isCancelled);
        }

        private static class PickupWrapper implements PickupHandler {

            private final FloatingItem _item;
            private final PickupCallback _callback;

            PickupWrapper(FloatingItem item, PickupCallback callback) {
                _item = item;
                _callback = callback;
            }

            public FloatingItem getItem() {
                return _item;
            }

            @Override
            public void onPickup(Player p, FloatingItem item, boolean isCancelled) {
                _callback.onPickup(p, item, isCancelled);
            }
        }

        private static class CallbackWrapper implements Runnable {

            private final FloatingItem _item;
            private final Runnable _callback;

            CallbackWrapper(FloatingItem item, Runnable callback) {
                _item = item;
                _callback = callback;
            }

            public FloatingItem getItem() {
                return _item;
            }

            @Override
            public void run() {
                _callback.run();
            }
        }

    }
}
