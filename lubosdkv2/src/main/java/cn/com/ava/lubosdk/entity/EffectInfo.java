package cn.com.ava.lubosdk.entity;

import java.util.List;

/**
 * 特技信息
 */
public class EffectInfo implements QueryResult{

    /**
     * 特效列表
     * */
    private List<Effect> effectList;

    /**
     * 当前选中特效
     * */
    private Effect curEffect;

    /**
     * 特效时间
     * */
    private int effectTime;


    public List<Effect> getEffectList() {
        return effectList;
    }

    public void setEffectList(List<Effect> effectList) {
        this.effectList = effectList;
    }

    public Effect getCurEffect() {
        return curEffect;
    }

    public void setCurEffect(Effect curEffect) {
        this.curEffect = curEffect;
    }

    public int getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(int effectTime) {
        this.effectTime = effectTime;
    }

    public static class Effect {
        private String effectCmd;
        private int drawableId;
        private boolean isCurEffect;

        public String getEffectCmd() {
            return effectCmd;
        }

        public void setEffectCmd(String effectCmd) {
            this.effectCmd = effectCmd;
        }

        public int getDrawableId() {
            return drawableId;
        }

        public void setDrawableId(int drawableId) {
            this.drawableId = drawableId;
        }

        public boolean isCurEffect() {
            return isCurEffect;
        }

        public void setCurEffect(boolean curEffect) {
            isCurEffect = curEffect;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Effect) {
                Effect effect = (Effect) obj;
                if (effect.getEffectCmd().equals(effectCmd)) {
                    return true;
                }
            }
            return false;
        }
    }
}
