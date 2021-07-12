package cn.com.ava.lubosdk.util;

import java.util.HashMap;
import java.util.Map;

public class EffectButtonHelper {
    private Map<String, Integer> effectMap;


    private EffectButtonHelper() {
        effectMap = new HashMap<>();
//        effectMap.put("NONE", R.drawable.btn_stunts2);
//        effectMap.put("CROSSFADE", R.drawable.btn_stunts1);
//        effectMap.put("PUSH_RIGHT", R.drawable.btn_stunts4);
//        effectMap.put("PUSH_LEFT", R.drawable.btn_stunts5);
//        effectMap.put("PUSH_BOTTOM", R.drawable.btn_stunts6);
//        effectMap.put("PUSH_TOP", R.drawable.btn_stunts7);
//        effectMap.put("PUSH_RECT_IN", R.drawable.btn_stunts3);
//        effectMap.put("PUSH_RECT_OUT", R.drawable.btn_stunts8);
//        effectMap.put("PUSH_RHOMBUS_IN", R.drawable.btn_stunts9);
//        effectMap.put("PUSH_RHOMBUS_OUT", R.drawable.btn_stunts10);
//        effectMap.put("PUSH_CYCLE_IN", R.drawable.btn_stunts11);
//        effectMap.put("PUSH_CYCLE_OUT", R.drawable.btn_stunts12);
    }

    public int getEffectDrawable(String layoutCmd) {
        if (effectMap.containsKey(layoutCmd)) {
            return effectMap.get(layoutCmd);
        } else {
            return -1;
        }
    }

    public static EffectButtonHelper getInstance() {
        return InstanceHolder.sInstance;
    }

    private static class InstanceHolder {
        static final EffectButtonHelper sInstance = new EffectButtonHelper();
    }
}
