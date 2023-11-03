package org.thwiecz.otrcut;

import java.util.HashMap;
import java.util.Map;

public class CutList {

    // Class for the cutlist Map

    private Map<String, Map<String, String>> cutListFileContents = new HashMap<>();

    public void addCutlistItem(String section, Map<String, String> subSectionMap) {

        cutListFileContents.put(section, subSectionMap);
    }

    public Map<String, Map<String,String>> getCutlistItems() {

        return cutListFileContents;
    }

}
