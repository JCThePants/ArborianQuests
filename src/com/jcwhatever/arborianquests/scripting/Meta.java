package com.jcwhatever.arborianquests.scripting;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.nucleus.scripting.api.SAPI_Meta;

// annotation required but not used
public class Meta extends SAPI_Meta {

    /**
     * Constructor. Automatically adds variable to script.
     *
     */
    public Meta() {
        super(ArborianQuests.getPlugin().getMetaDataNode());
    }
}
