package info.abibas.lazydroid;

import java.util.HashMap;
import java.util.Map;

public class ActTable {
    private Map<Integer, String> softMap;

    public ActTable() {
        softMap = new HashMap<Integer, String>();
        softMap.put(67, "backspace");
        softMap.put(66, "enter");
        softMap.put(62, "spacebar");
    }

    public String getAct(int keyCode) {
        return softMap.containsKey(keyCode) ? softMap.get(keyCode) : "";
    }
}
