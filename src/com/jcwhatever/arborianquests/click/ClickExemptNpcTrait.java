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

package com.jcwhatever.arborianquests.click;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

/**
 * Trait that prevents NPC's from being candidates for global click handlers.
 *
 * <p>Lookup name is 'ArborianQuests:ClickExempt'</p>
 */
public class ClickExemptNpcTrait  extends NpcTraitType {

    public static final String NAME = "ClickExempt";

    /**
     * Constructor.
     */
    public ClickExemptNpcTrait() {
        super(ArborianQuests.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new ClickExempt(this);
    }

    public static class ClickExempt extends NpcTrait {

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        ClickExempt(NpcTraitType type) {
            super(type);
        }
    }
}

