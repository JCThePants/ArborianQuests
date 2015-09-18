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
import com.jcwhatever.arborianquests.click.ClickContext;
import com.jcwhatever.arborianquests.click.ClickHandlerBuilder;
import com.jcwhatever.arborianquests.click.IClickHandler;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Script API for global NPC Click handler.
 */
public class NpcClick implements IDisposable {

    private Map<IClickHandler, Void> _handlers = new WeakHashMap<>(50);

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;

        ClickContext context = ArborianQuests.getGlobalClickContext();

        for (IClickHandler handler : _handlers.keySet()) {
            context.removeLeftClick(handler);
            context.removeRightClick(handler);
        }
    }

    public ClickHandlerBuilder getBuilder() {
        return new ClickHandlerBuilder();
    }

    public void onLeftClick(IClickHandler handler) {
        PreCon.notNull(handler);

        ArborianQuests.getGlobalClickContext().addLeftClick(handler);

        _handlers.put(handler, null);
    }

    public void onRightClick(IClickHandler handler) {
        PreCon.notNull(handler);

        ArborianQuests.getGlobalClickContext().addRightClick(handler);

        _handlers.put(handler, null);
    }
}
