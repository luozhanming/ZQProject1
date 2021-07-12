package cn.com.ava.zqproject.usb;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import org.greenrobot.eventbus.EventBus;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;
import cn.com.ava.zqproject.vo.RecordFilesInfo;

public class UsbHelper {

    public static final String DIRECTORY_DOWNLOAD = "AVADownload";
    private static final int MAX_QUEUE_COUNT = 3;

    private static UsbHelper sInstance;

    private Map<RecordFilesInfo.RecordFile,UDiskDownloadTask> mTasks = new LinkedHashMap<>();
    private ArrayBlockingQueue<RecordFilesInfo.RecordFile> mDownloadQueue = new ArrayBlockingQueue<>(MAX_QUEUE_COUNT);
    private Executor mExecutor;
    private Uri mUSBUri;


    public static UsbHelper getHelper() {
        if (sInstance == null) {
            sInstance = new UsbHelper();
        }
        return sInstance;
    }

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
        final Set<Map.Entry<RecordFilesInfo.RecordFile, UDiskDownloadTask>> entries = mTasks.entrySet();
        //同一时间只能有一个下载任务
        for (Map.Entry<RecordFilesInfo.RecordFile, UDiskDownloadTask> entry : entries) {
            if (!entry.getValue().isCompleted()) {
                if (mDownloadQueue.size() < MAX_QUEUE_COUNT) {
                    ToastUtils.showShort(entry.getKey().getDownloadFileName() + "已加入下载队列");
                    mDownloadQueue.add(src);
                    EventBus.getDefault().post(new RecordFileDLStateEvent(src, "", RecordFileDLStateEvent.STATE_IN_QUEUEN, 0));
                    return;
                } else {
                    ToastUtils.showShort("下载队列已满");
                    return;
                }
            }
        }
        if (mUSBUri == null) {
            ToastUtils.showShort("未授予U盘访问权限");
        } else {
            final DocumentFile documentFile = DocumentFile.fromTreeUri(Utils.getApp(), mUSBUri);
            DocumentFile downloadDirectory = documentFile.findFile(DIRECTORY_DOWNLOAD);
            if (downloadDirectory == null) {
                downloadDirectory = documentFile.createDirectory(DIRECTORY_DOWNLOAD);
            }
            DocumentFile file = downloadDirectory.findFile(name);
            if (file == null) {
                file = downloadDirectory.createFile("video/mp4", name);
            } else {
                file.delete();
                file = downloadDirectory.createFile("video/mp4", name);
            }
            if (mExecutor == null) {
                mExecutor = Executors.newSingleThreadExecutor();
            }
           UDiskDownloadTask task = new UDiskDownloadTask(src, file);
            task.executeOnExecutor(mExecutor, "");
            mTasks.put(src, task);
        }
    }

    /**
     * 进行下一个下载
     */
    public void notifyNextDownload() {
        if (mDownloadQueue.size() > 0) {
            final RecordFilesInfo.RecordFile next = mDownloadQueue.poll();
            downloadFile2UDisk(next, next.getDownloadFileName());
        }
    }

    /**
     * 取消下载任务并删除正在下载的任务文件
     */
    public void cancelAllTask() {
        String fileName = "";
        final Set<Map.Entry<RecordFilesInfo.RecordFile, UDiskDownloadTask>> entries = mTasks.entrySet();
        if(mTasks.size()==0)return;
        for (Map.Entry<RecordFilesInfo.RecordFile, UDiskDownloadTask> entry : entries) {
            final UDiskDownloadTask task = entry.getValue();
            if (!task.isCompleted()) {
                task.cancel(true);
                task.deleteFile();
            }
        }
    }

    public Queue<RecordFilesInfo.RecordFile> getDownloadQueue() {
        return mDownloadQueue;
    }


    public void setUsbStorageUri(Uri uri) {
        mUSBUri = uri;
    }

    public Uri getUSBUri() {
        return mUSBUri;
    }

    public void clearDownloadingQueue() {
        mDownloadQueue.clear();
    }
}
