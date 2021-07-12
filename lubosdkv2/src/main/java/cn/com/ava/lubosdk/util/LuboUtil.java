package cn.com.ava.lubosdk.util;

public final class LuboUtil {

    /**
     * 判断请求key是否失效
     * */
    public static boolean checkKeyInvalid(String response){
        return response.trim().startsWith("-2")||response.contains("jwt_check:-2");
    }


    /**
     * 计算录制时间
     * */
    public static String computeRecordTime(long systemTime, String s){
        if (systemTime == 0) {
            return "00:00:00";
        }
        int hour = 0, minute = 0, second = 0;
        final String[] s1 = s.split(" ");
        long recTime = Long.parseLong(s1[0]);
        final long beginTime = Long.parseLong(s1[1]);
        if (recTime >= 0 && beginTime >= 0) {  //录制中
            recTime = recTime + systemTime - beginTime;
        } else if (recTime > 0 && beginTime == -1) {  //暂停中
            recTime = recTime;
        }
        second = (int) (recTime / 1000) % 60 % 60;
        minute = (int) (recTime / 1000 / 60 % 60);
        hour = (int) (recTime / 1000 / 60 / 60);
        String hourStr = hour < 10 ? "0" + hour : hour + "";
        String secondStr = second < 10 ? "0" + second : second + "";
        String minuteStr = minute < 10 ? "0" + minute : minute + "";
        return String.format("%s:%s:%s", hourStr, minuteStr, secondStr);
    }
}
