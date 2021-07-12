package cn.com.ava.lubosdk.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     描述：布局按钮帮助，主要是获取按钮图片资源
 *     作者：罗展鸣
 * </pre>
 */
public class LayoutButtonHelper {
    private Map<String, Integer> layoutMap;


    private LayoutButtonHelper() {
        layoutMap = new HashMap<>();
//        layoutMap.put("V1&2", R.drawable.record_btn_layout1);
//        layoutMap.put("V6_1", R.drawable.record_btn_layout2);
//        layoutMap.put("V6_2", R.drawable.record_btn_layout3);
//        layoutMap.put("V5_4", R.drawable.record_btn_layout4);
//        layoutMap.put("V2PIP1", R.drawable.record_btn_layout5);
//        layoutMap.put("V6PIP1", R.drawable.record_btn_layout6);
//        layoutMap.put("V6PIP1_2", R.drawable.record_btn_layout7);
//        layoutMap.put("V3_1", R.drawable.record_btn_layout13);
//        layoutMap.put("V3_2", R.drawable.record_btn_layout14);
//        layoutMap.put("V3_2_2", R.drawable.record_btn_layout15);
//        layoutMap.put("V3PIP1", R.drawable.record_btn_layout16);
//        layoutMap.put("V3PIP1_2", R.drawable.record_btn_layout17);
//        layoutMap.put("V5_1", R.drawable.record_btn_layout18);
//        layoutMap.put("V5_2", R.drawable.record_btn_layout19);
//        layoutMap.put("V5PIP1", R.drawable.record_btn_layout20);
//        layoutMap.put("V5PIP1_2", R.drawable.record_btn_layout21);
//        layoutMap.put("V2_1", R.drawable.record_btn_layout22);
//        layoutMap.put("V1_2", R.drawable.record_btn_layout40);
//        layoutMap.put("V1_1", R.drawable.record_btn_layout23);
//        layoutMap.put("V4_1", R.drawable.record_btn_layout24);
//        layoutMap.put("V4_2", R.drawable.record_btn_layout25);
//        layoutMap.put("V4_3", R.drawable.record_btn_layout26);
//        layoutMap.put("V4PIP1", R.drawable.record_btn_layout27);
//        layoutMap.put("V4PIP1_2", R.drawable.record_btn_layout28);
//        layoutMap.put("V6_4", R.drawable.record_btn_layout29);
//        layoutMap.put("V8_1", R.drawable.record_btn_layout30);
//        layoutMap.put("V8_2", R.drawable.record_btn_layout31);
//        layoutMap.put("V8_4", R.drawable.record_btn_layout32);
//        layoutMap.put("V8PIP1", R.drawable.record_btn_layout33);
//        layoutMap.put("V8PIP1_2", R.drawable.record_btn_layout34);
//        layoutMap.put("V7_1", R.drawable.record_btn_layout35);
//        layoutMap.put("V7_2", R.drawable.record_btn_layout36);
//        layoutMap.put("V7_4", R.drawable.record_btn_layout37);
//        layoutMap.put("V7PIP1", R.drawable.record_btn_layout38);
//        layoutMap.put("V7PIP1_2", R.drawable.record_btn_layout39);
    }

    public int getLayoutDrawable(String layoutCmd) {
        if (layoutMap.containsKey(layoutCmd)) {
            return layoutMap.get(layoutCmd);
        } else {
            return -1;
        }
    }

    public static LayoutButtonHelper getInstance() {
        return InstanceHolder.sInstance;
    }

    private static class InstanceHolder {
        static final LayoutButtonHelper sInstance = new LayoutButtonHelper();
    }
}
