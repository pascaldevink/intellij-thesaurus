package net.tweakers.intellij.thesaurus.config;

import java.util.HashMap;
import java.util.Map;

public enum ThesaurusApiClass {
    AlterVista("AlterVista"),
    BigHugeThesaurus("Big Huge Thesaurus");

    private String label;
    ThesaurusApiClass(String label)
    {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    private static final Map<String, ThesaurusApiClass> lookupMap;
    static {
        lookupMap = new HashMap(ThesaurusApiClass.values().length);
        for(ThesaurusApiClass thesaurusApiClass : ThesaurusApiClass.values())
        {
            lookupMap.put(thesaurusApiClass.label, thesaurusApiClass);
        }
    }

    public static ThesaurusApiClass getThesaurusApiClassByLabel(String label)
    {
        return lookupMap.get(label);
    }
}
