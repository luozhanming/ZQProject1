package cn.com.ava.zqproject.usb;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.com.ava.common.util.LoggerUtilKt;
import cn.com.ava.lubosdk.entity.RecordFilesInfo;

public class TempUsbHelper {

    public static final String DIRECTORY_DOWNLOAD = "AVADownload";
    /**最大下载队列个数*/
    private static final int MAX_QUEUE_COUNT = 3;

    private static TempUsbHelper sInstance;

    private Map<DownloadObject< RecordFilesInfo.RecordFile>, UDiskDownloadTask> mTasks = new LinkedHashMap<>();
    private ArrayBlockingQueue<DownloadObject< RecordFilesInfo.RecordFile>> mDownloadQueue = new ArrayBlockingQueue<>(MAX_QUEUE_COUNT);
    private Executor mExecutor;
    private Uri mUSBUri;
    private List<TempUsbHelper.FileDownloadCallback> mDLCallbackList = new ArrayList<>();  //注意释放
    private String downloadPath;

    public static TempUsbHelper getHelper() {
        if (sInstance == null) {
            sInstance = new TempUsbHelper();
        }
        return sInstance;
    }

    /**
     * @return 获取U盘的路径
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getUDiskPath() {
        StorageManager mStorageManager = (StorageManager) Utils.getApp().getSystemService(Context.STORAGE_SERVICE);
        //获取所有挂载的设备（内部sd卡、外部sd卡、挂载的U盘）
        List<StorageVolume> volumes = mStorageManager.getStorageVolumes();
        List<String> paths = new ArrayList<>();
        try {
            Class<?> storageVolumeClazz = Class
                    .forName("android.os.storage.StorageVolume");
            //通过反射调用系统hide的方法
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            for (int i = 0; i < volumes.size(); i++) {
                StorageVolume storageVolume = volumes.get(i);//获取每个挂载的StorageVolume
                //通过反射调用getPath、isRemovable
                String storagePath = (String) getPath.invoke(storageVolume); //获取路径
                if (!storagePath.equals("/storage/emulated/0")) {
                    paths.add(storagePath);
                } else {
                    continue;
                }
            }

        } catch (Exception e) {
        }
        return paths;
    }


    /**
     * @return 获取U盘信息
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static StorageVolume getUDiskStorageVolume() {
        StorageManager mStorageManager = (StorageManager) Utils.getApp().getSystemService(Context.STORAGE_SERVICE);
        //获取所有挂载的设备（内部sd卡、外部sd卡、挂载的U盘）
        List<StorageVolume> volumes = mStorageManager.getStorageVolumes();
        try {
            Class<?> storageVolumeClazz = Class
                    .forName("android.os.storage.StorageVolume");
            //通过反射调用系统hide的方法
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            for (int i = 0; i < volumes.size(); i++) {
                StorageVolume storageVolume = volumes.get(i);//获取每个挂载的StorageVolume
                //通过反射调用getPath、isRemovable
                String storagePath = (String) getPath.invoke(storageVolume); //获取路径
                if (!storagePath.equals("/storage/emulated/0")) {
                    return storageVolume;
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 注册下载回调
     *
     * @param callback
     * @return 返回下载句柄，用于解注册用
     */
    public synchronized int registerDownloadCallback(TempUsbHelper.FileDownloadCallback callback) {
        if (!mDLCallbackList.contains(callback)) {
            mDLCallbackList.add(callback);
        }
        return mDLCallbackList.indexOf(callback);
    }

    /**
     * 注册下载回调
     *
     * @param handle 句柄
     * @return 返回下载句柄，用于解注册用
     */
    public synchronized void unregisterDownloadCallback(int handle) {
        mDLCallbackList.remove(handle);
    }



    /**
     * 检查U盘权限
     *
     * @return
     */
    public boolean checkUDiskPermission() {
        if (mUSBUri == null) {
            ToastUtils.showShort("请先允许应用读写权限");
            return false;
        } else {
            final DocumentFile documentFile = DocumentFile.fromTreeUri(Utils.getApp(), mUSBUri);
            return documentFile.canWrite();
        }
    }


    /**
     * 下载录课文件到U盘
     *
     * @param src  文件信息
     * @param name 创建的文件名
     */
    public void downloadFile2UDisk(@NonNull RecordFilesInfo.RecordFile src, String name) {
        final Set<Map.Entry<DownloadObject<RecordFilesInfo.RecordFile>, UDiskDownloadTask>> entries = mTasks.entrySet();
        //同一时间只能有一个下载任务
        for (Map.Entry<DownloadObject< RecordFilesInfo.RecordFile>, UDiskDownloadTask> entry : entries) {
            if (!entry.getValue().isCompleted()) {   //如果有一个正在下载的任务
                if (mDownloadQueue.size() < MAX_QUEUE_COUNT) {
                    ToastUtils.showShort(entry.getKey().getObj().getDownloadFileName() + "已加入下载队列");
                    DownloadObject<RecordFilesInfo.RecordFile> download = new DownloadObject<>(src);
                    mDownloadQueue.add(download);
                    //进入队列
                    notifyDownloadStateChanged(download, "", DownloadObject.IN_QUEUE, 0);
                    return;
                } else {
                    ToastUtils.showShort("下载队列已满");
                    return;
                }
            }
        }

            if (mExecutor == null) {
                mExecutor = Executors.newSingleThreadExecutor();
            }
            DownloadObject<RecordFilesInfo.RecordFile> download = new DownloadObject<>(src);

            File saveFile = new File(new File(downloadPath), "download/" + name);
            if(!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            DocumentFile docFile = DocumentFile.fromFile(saveFile);
            UDiskDownloadTask task = new UDiskDownloadTask(download, docFile);
            task.executeOnExecutor(mExecutor, "");
            mTasks.put(download, task);
    }

    void notifyDownloadStateChanged(DownloadObject<RecordFilesInfo.RecordFile> download,
                                    String dstPath,
                                    @DownloadObject.DownloadState int state,
                                    int progress) {
        download.setState(state);
        for (int i = 0, size = mDLCallbackList.size(); i < size; i++) {
            final TempUsbHelper.FileDownloadCallback callback = mDLCallbackList.get(i);
            callback.onDownloadStateChanged(download, dstPath, state, progress);
        }
//        if(state==DownloadObject.IN_QUEUE){
//            LoggerUtilKt.logd(this,String.format("%s加入下载队列",download.getObj().getDownloadFileName()));
//        }else if(state==DownloadObject.DOWNLOADING){
//            LoggerUtilKt.logd(this,String.format("%s下载中，进度%d%",download.getObj().getDownloadFileName(),progress));
//        }else if(state==DownloadObject.SUCCESS){
//            LoggerUtilKt.logd(this,String.format("%s下载成功，保存至%s",download.getObj().getDownloadFileName(),dstPath));
//        }else if(state==DownloadObject.FAILED){
//            LoggerUtilKt.logd(this,String.format("%s下载失败",download.getObj().getDownloadFileName()));
//        }
    }

    /**
     * 进行下一个下载
     */
    public void notifyNextDownload() {
        if (mDownloadQueue.size() > 0) {
            final DownloadObject<RecordFilesInfo.RecordFile> next = mDownloadQueue.poll();
            downloadFile2UDisk(next.getObj(), next.getObj().getDownloadFileName());
        }
    }

    /**
     * 取消下载任务并删除正在下载的任务文件
     */
    public void cancelAllTask() {
        final Set<Map.Entry<DownloadObject<RecordFilesInfo.RecordFile>, UDiskDownloadTask>> entries = mTasks.entrySet();
        if (mTasks.size() == 0) return;
        for (Map.Entry<DownloadObject<RecordFilesInfo.RecordFile>, UDiskDownloadTask> entry : entries) {
            final UDiskDownloadTask task = entry.getValue();
            if (!task.isCompleted()) {
                task.cancel(true);
                task.deleteFile();
            }
        }
        mTasks.clear();
        mDownloadQueue.clear();

    }

    public Queue<DownloadObject<RecordFilesInfo.RecordFile>> getDownloadQueue() {
        return mDownloadQueue;
    }


    public void setUsbStorageUri(Uri uri) {
        mUSBUri = uri;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public Uri getUSBUri() {
        return mUSBUri;
    }

    public void clearDownloadingQueue() {
        mDownloadQueue.clear();
    }


    public interface FileDownloadCallback {

        void onDownloadStateChanged(DownloadObject<RecordFilesInfo.RecordFile> file,
                                    String dstPath,
                                    @RecordFileDLStateEvent.State int state,
                                    int progress);
    }
}
