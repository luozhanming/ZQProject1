package cn.com.ava.zqproject.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import cn.com.ava.zqproject.R;

/**
 * <pre>
 *     USB状态接收器
 *     author:luozm
 * </pre>
 *
 * */
public class USBHostReceiver extends BroadcastReceiver {

    public static final String ACTION_USB_PERMISSION = "cn.com.ava.controlpanel.usb_permission";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ACTION_USB_PERMISSION)) {    //USB权限被允许;
        } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
            //检测到拔出U盘清理下载任务
            EventBus.getDefault().post(new UDiskOutEvent());
            UsbHelper.getHelper().cancelAllTask();
            UsbHelper.getHelper().clearDownloadingQueue();
            UsbHelper.getHelper().setUsbStorageUri(null);
            ToastUtils.showShort(context.getString(R.string.usb_plugin_out));
        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
        }else if(action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
            EventBus.getDefault().post(new UDiskInEvent());
        }

    }

}
