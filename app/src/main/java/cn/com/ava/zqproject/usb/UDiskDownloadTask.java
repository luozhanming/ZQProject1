package cn.com.ava.zqproject.usb;

import android.os.AsyncTask;

import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.documentfile.provider.DocumentFile;
import cn.com.ava.common.util.LoggerUtilKt;
import cn.com.ava.lubosdk.entity.RecordFilesInfo;


/**
 * <pre>
 *     描述：U盘下载AsyncTask
 *     作者：luozm
 * </pre>
 */
public class UDiskDownloadTask extends AsyncTask<Object, Integer, Boolean> {

    private DownloadObject<RecordFilesInfo.RecordFile> file;
    private DocumentFile dstFile;


    private boolean isCompleted = false;

    public UDiskDownloadTask(DownloadObject<RecordFilesInfo.RecordFile> file, DocumentFile dstFile) {
        this.file = file;
        this.dstFile = dstFile;

    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
            URL url = new URL(file.getObj().getDownloadUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            long contentLength = conn.getContentLength();
            if (contentLength <= 0) {
                contentLength = file.getObj().getRawFileSize();
            }
            if (conn.getResponseCode() == 200) {
                ToastUtils.showShort("正在下载" + dstFile.getName());
                final OutputStream os = Utils.getApp().getContentResolver().openOutputStream(dstFile.getUri());
                bos = new BufferedOutputStream(os);
                is = conn.getInputStream();
                //   bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024 * 8];
                long hasReceiveSize = 0;
                int len = is.read(buffer);
                int nextPersent = 1;
                publishProgress(0);
                while (len != -1) {
                    bos.write(buffer, 0, len);
                    len = is.read(buffer);
                    hasReceiveSize += len;
                    int percent = (int) ((double) hasReceiveSize / contentLength * 100);
                    if (percent == nextPersent) {
                        publishProgress(percent);
                        nextPersent++;
                        if (nextPersent == 100) nextPersent = 100;
                    }
                }
                conn.disconnect();
            } else return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            CloseUtils.closeIO(bos, is);
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        UsbHelper.getHelper().notifyDownloadStateChanged(file, "/" + UsbHelper.DIRECTORY_DOWNLOAD + "/" + dstFile.getName(), DownloadObject.DOWNLOADING, values[0]);
    }

    public void deleteFile() {
        if (dstFile != null) {
            dstFile.delete();
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        isCompleted = true;
        if (aBoolean) {
            UsbHelper.getHelper().notifyDownloadStateChanged(file, "/" + UsbHelper.DIRECTORY_DOWNLOAD + "/" + dstFile.getName(), DownloadObject.SUCCESS, 100);
            ToastUtils.showShort(dstFile.getName() + "已下载完成");
            UsbHelper.getHelper().notifyNextDownload();
        } else {
            UsbHelper.getHelper().notifyDownloadStateChanged(file, "/" + UsbHelper.DIRECTORY_DOWNLOAD + "/" + dstFile.getName(), DownloadObject.FAILED, 0);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        isCompleted = true;
        UsbHelper.getHelper().notifyDownloadStateChanged(file, "/" + UsbHelper.DIRECTORY_DOWNLOAD + "/" + dstFile.getName(), DownloadObject.FAILED, 0);
        UsbHelper.getHelper().notifyNextDownload();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

}
