package cn.com.ava.authcode;


import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.com.ava.common.util.LoggerUtilKt;

/**
 * 生成方式机器码前4位后3位拼装，DES加密
 */
public class AuthKeyUtil {


    private static final String DEVICEID_PATH = Environment.getExternalStorageDirectory().getPath() + "/deviceid.txt";
    private static final String CDKEY_PATH = Environment.getExternalStorageDirectory().getPath() + "/cdkey.txt";


    static {
        System.loadLibrary("native-lib");
    }


    private AuthKeyUtil() {
    }

    public static String encryptMachineCode(String machineCode) {
        return EncryptUtils.encrypt3DES2HexString(machineCode.getBytes(),
                getAuthKey().getBytes(), "DES", null);
    }

    public static String decrptyMachineCode(String pass) {
        if (pass == null || pass == "") {
            return "";
        }
        byte[] des = null;
        try {
            des = EncryptUtils.decryptHexStringDES(pass, getAuthKey().getBytes(), "DES", null);
        } catch (Exception e) {
            return "";
        }
        if (des == null || des.length == 0) {
            return "";
        }
        return new String(des);
    }


    /**
     * 验证授权码
     * @param code 输入授权码
     * @return 成功失败
     * */
    public static boolean validateCode(String code) {
        final String deviceId = DeviceUtils.getUniqueDeviceId();
        String pre4 = deviceId.substring(0, 4);
        String last3 = deviceId.substring(deviceId.length() - 3);
        final String deviceId2 = decrptyMachineCode(code);
        if (deviceId2.length() == 7) {
            final String pre4_2 = deviceId2.substring(0, 4);
            final String last3_2 = deviceId2.substring(deviceId2.length() - 3);
            return pre4_2.equals(pre4) && last3_2.equals(last3);
        } else {
            return false;
        }
    }


    /**
     * 输出device.txt到sd卡根目录
     * */
    public static boolean generateDeviceFile() {
        if (FileUtils.isFileExists(DEVICEID_PATH)) {   //存在就不用再生成了
            return true;
        }
        final boolean createResult = FileUtils.createOrExistsDir(DEVICEID_PATH);
        if(createResult){
            String devicecode = DeviceUtils.getUniqueDeviceId();
            String first = devicecode.substring(0, 4);
            String last = devicecode.substring(devicecode.length() - 3);
            File file = new File(DEVICEID_PATH);
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                fw.write(first + last);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }finally {
                CloseUtils.closeIO(fw);
            }
            MediaScannerConnection.scanFile(Utils.getApp(), new String[]{DEVICEID_PATH}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        }
        return true;
    }


    /**
     * 获取cdkey
     * */
    public static String getCDKey(){
        if(FileUtils.isFileExists(CDKEY_PATH)){
            File file = new File(CDKEY_PATH);
            FileReader fr = null;
            StringBuffer sb = new StringBuffer();
            try {
                fr =new FileReader(file);
                char[] buf = new char[16];
                int len = 0;
                while ((len = fr.read(buf))!=0){
                    fr.read(buf);
                    sb.append(buf);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                CloseUtils.closeIO(fr);
            }
            return sb.toString();
        }else return "";
    }


    public native static String getAuthKey();
}
