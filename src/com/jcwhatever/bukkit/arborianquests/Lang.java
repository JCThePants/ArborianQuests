package com.jcwhatever.bukkit.arborianquests;

import com.jcwhatever.bukkit.generic.language.LanguageManager;
import com.jcwhatever.bukkit.generic.language.Localized;

/**
 * Created by John on 10/13/2014.
 */
public class Lang {

    private Lang() {}

    private static LanguageManager _languageManager = new LanguageManager();


    @Localized
    public static String get(String text, Object... params) {
        return _languageManager.get(ArborianQuests.getInstance(), text, params);
    }
}
