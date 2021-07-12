package cn.com.ava.lubosdk.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 最近呼叫返回结果
 */
public class LastCallResult {
    private int total;
    private List<LastCallUser> lastCall;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LastCallUser> getLastCall() {
        return lastCall;
    }

    public void setLastCall(List<LastCallUser> lastCall) {
        this.lastCall = lastCall;
    }

    public static class LastCallUser  {
        private String username;
        @SerializedName("last_time")
        private String lastCallTime;
        private boolean isSelected;
        //是否上次呼叫
        private boolean isLastCall;


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLastCallTime() {
            return lastCallTime;
        }

        public void setLastCallTime(String lastCallTime) {
            this.lastCallTime = lastCallTime;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public boolean isLastCall() {
            return isLastCall;
        }

        public void setLastCall(boolean lastCall) {
            isLastCall = lastCall;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LastCallUser) {
                if (username.equals(((LastCallUser) (obj)).username)) return true;
            }
            return false;
        }
    }
}
