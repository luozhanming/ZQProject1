package cn.com.ava.lubosdk.util;

import android.text.TextUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLHexEncodeDecodeUtil {

    public static synchronized String hexToStringUrlDecode(String content){
        String result = null;
        if(TextUtils.isEmpty(content))
            return result;
        try{
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < content.length() ; i += 2){
                builder.append("%").append(content.charAt(i)).append(content.charAt(i + 1));
            }
            result = URLDecoder.decode(builder.toString() , "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static synchronized String hexToStringUrlDecode(String content, String charset){
        String result = null;
        if(TextUtils.isEmpty(content))
            return result;
        try{
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < content.length() ; i += 2){
                builder.append("%").append(content.charAt(i)).append(content.charAt(i + 1));
            }
            result = URLDecoder.decode(builder.toString() , charset);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static synchronized String stringToHexEncode(String content){

        if(!TextUtils.isEmpty(content)){
            //去掉空格
            content = content.trim();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                String cur = String.valueOf(content.charAt(i));
                String enStr = "";
                try{
                    enStr = URLEncoder.encode(cur,"UTF-8");
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (cur.equals(enStr)) {
                    stringBuilder.append("%").append(Integer.toHexString(content.charAt(i)));
                } else {
                    stringBuilder.append(enStr);
                }
            }
            return stringBuilder.toString().replaceAll("%" , "");
        }
        return content;
    }

    /**
     * hex编码，自选类型
     * */
    public static synchronized String stringToHexEncode(String content, String encode){

        if(!TextUtils.isEmpty(content)){
            //去掉空格
            content = content.trim();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                String cur = String.valueOf(content.charAt(i));
                String enStr = "";
                if(cur.equals(" ")){
                    stringBuilder.append("%20");
                    continue;
                }
                try{
                    enStr = URLEncoder.encode(cur,encode);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (cur.equals(enStr)) {
                    stringBuilder.append("%").append(Integer.toHexString(content.charAt(i)));
                } else {
                    stringBuilder.append(enStr);
                }
            }
            return stringBuilder.toString().replaceAll("%" , "");
        }
        return content;
    }
}
