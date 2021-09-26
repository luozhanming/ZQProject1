package cn.com.ava.lubosdk.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;



public class RecordFilesInfo implements QueryResult {


    private List<RecordFile> files;

    public List<RecordFile> getFiles() {
        return files;
    }

    public void setFiles(List<RecordFile> files) {
        this.files = files;
    }

    public static class RecordFile implements Serializable {

        private String rawFileName;

        private String streamIndex;
        private String fileSize;

        private String rtspUrl;
        private String classTheme;
        private String teacher;
        private String recordBeginTime;
        private String recordRawEndTime;
        private String recordEndTime;
        private String duration;
        private String rawDuration;
        private String downloadUrl;
        private long rawFileSize;

        private String recordRawBeginTime;

        //视频信息
        private String videoSize;
        private String GOP;
        private String streamMode;
        private String fps;
        private String videoBps;
        private String videoCodecMode;

        //音频信息
        private String audioBps;
        private String sampleRate;
        private String channelCount;
        private String sampleBit;
        private String audioCodecMode;

        //        //下载信息
        private String downloadFileName;
        // 下载进度
        private int downloadProgress;
        // 下载路径
        private String downloadDstPath;

        // 上传状态， 0-等待上传，1-上传成功，2-上传中，3-上传失败
        private int uploadState;
        // 上传进度
        private String uploadProgress;
        // 上传用到的UUID
        private String uploadUUID;

        public String getUploadUUID() {
            return uploadUUID;
        }

        public void setUploadUUID(String uploadUUID) {
            this.uploadUUID = uploadUUID;
        }

        public RecordFile() {

        }

        public RecordFile(RecordFile file) {
            this.rawFileName = file.getRawFileName();
            this.streamIndex = file.getStreamIndex();
            this.fileSize = file.getFileSize();
            this.rtspUrl = file.getRtspUrl();
            this.classTheme = file.getClassTheme();
            this.teacher = file.getTeacher();
            this.recordBeginTime = file.getRecordBeginTime();
            this.recordRawEndTime = file.getRecordRawEndTime();
            this.recordEndTime = file.getRecordEndTime();
            this.duration = file.getDuration();
            this.rawDuration = file.getRawDuration();
            this.downloadUrl = file.getDownloadUrl();
            this.rawFileSize = file.getRawFileSize();
            this.recordRawBeginTime = file.getRecordRawBeginTime();
            this.videoSize = file.getVideoSize();
            this.GOP = file.getGOP();
            this.streamMode = file.getStreamMode();
            this.fps = file.getFps();
            this.videoBps = file.getVideoBps();
            this.videoCodecMode = file.getVideoCodecMode();
            this.audioBps = file.getAudioBps();
            this.sampleRate = file.getSampleRate();
            this.channelCount = file.getChannelCount();
            this.sampleBit = file.getSampleBit();
            this.audioCodecMode = file.getAudioCodecMode();
            this.downloadFileName = file.getDownloadFileName();
            this.downloadProgress = file.getDownloadProgress();
            this.downloadDstPath = file.getDownloadDstPath();
            this.uploadState = file.getUploadState();
            this.uploadProgress = file.getUploadProgress();
            this.transmissionType = file.getTransmissionType();
            this.uploadUUID = file.getUploadUUID();
        }

        public int getUploadState() {
            return uploadState;
        }

        public void setUploadState(int uploadState) {
            this.uploadState = uploadState;
        }

        public String getUploadProgress() {
            return uploadProgress;
        }

        public void setUploadProgress(String uploadProgress) {
            this.uploadProgress = uploadProgress;
        }

        public int getTransmissionType() {
            return transmissionType;
        }

        public void setTransmissionType(int transmissionType) {
            this.transmissionType = transmissionType;
        }

        // 传输类型 1-下载，2-上传
        private int transmissionType;

        public String getRtspUrl() {
            return rtspUrl;
        }

        public void setRtspUrl(String rtspUrl) {
            this.rtspUrl = rtspUrl;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getRawFileName() {
            return rawFileName;
        }

        public void setRawFileName(String rawFileName) {
            this.rawFileName = rawFileName;
        }

        public String getClassTheme() {
            return classTheme;
        }

        public void setClassTheme(String classTheme) {
            this.classTheme = classTheme;
        }


        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getRecordBeginTime() {
            return recordBeginTime;
        }

        public void setRecordBeginTime(String recordBeginTime) {
            this.recordBeginTime = recordBeginTime;
        }

        public String getRecordRawBeginTime() {
            return recordRawBeginTime;
        }

        public void setRecordRawBeginTime(String recordRawBeginTime) {
            this.recordRawBeginTime = recordRawBeginTime;
        }

        public String getRecordEndTime() {
            return recordEndTime;
        }

        public void setRecordEndTime(String recordEndTime) {
            this.recordEndTime = recordEndTime;
        }

        public String getRecordRawEndTime() {
            return recordRawEndTime;
        }

        public void setRecordRawEndTime(String recordRawEndTime) {
            this.recordRawEndTime = recordRawEndTime;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getVideoSize() {
            return videoSize;
        }

        public void setVideoSize(String videoSize) {
            this.videoSize = videoSize;
        }

        public String getGOP() {
            return GOP;
        }

        public void setGOP(String GOP) {
            this.GOP = GOP;
        }

        public String getStreamMode() {
            return streamMode;
        }

        public void setStreamMode(String streamMode) {
            this.streamMode = streamMode;
        }

        public String getFps() {
            return fps;
        }

        public void setFps(String fps) {
            this.fps = fps;
        }

        public String getVideoBps() {
            return videoBps;
        }

        public void setVideoBps(String videoBps) {
            this.videoBps = videoBps;
        }

        public String getVideoCodecMode() {
            return videoCodecMode;
        }

        public void setVideoCodecMode(String videoCodecMode) {
            this.videoCodecMode = videoCodecMode;
        }

        public String getAudioBps() {
            return audioBps;
        }

        public void setAudioBps(String audioBps) {
            this.audioBps = audioBps;
        }

        public String getSampleRate() {
            return sampleRate;
        }

        public void setSampleRate(String sampleRate) {
            this.sampleRate = sampleRate;
        }

        public String getChannelCount() {
            return channelCount;
        }

        public void setChannelCount(String channelCount) {
            this.channelCount = channelCount;
        }

        public String getSampleBit() {
            return sampleBit;
        }

        public void setSampleBit(String sampleBit) {
            this.sampleBit = sampleBit;
        }

        public String getAudioCodecMode() {
            return audioCodecMode;
        }

        public void setAudioCodecMode(String audioCodecMode) {
            this.audioCodecMode = audioCodecMode;
        }


        public String getStreamIndex() {
            return streamIndex;
        }

        public void setStreamIndex(String streamIndex) {
            this.streamIndex = streamIndex;
        }

        public String getRawDuration() {
            return rawDuration;
        }

        public void setRawDuration(String rawDuration) {
            this.rawDuration = rawDuration;
        }


        public String getDownloadFileName() {
            return downloadFileName;
        }

        public void setDownloadFileName(String downloadFileName) {
            this.downloadFileName = downloadFileName;
        }

        public int getDownloadProgress() {
            return downloadProgress;
        }

        public void setDownloadProgress(int downloadProgress) {
            this.downloadProgress = downloadProgress;
        }

        public String getDownloadDstPath() {
            return downloadDstPath;
        }

        public void setDownloadDstPath(String downloadDstPath) {
            this.downloadDstPath = downloadDstPath;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof RecordFile) {
                RecordFile file = (RecordFile) obj;
                if (file.getDownloadFileName().equals(downloadFileName)) {
                    return true;
                }
            }
            return super.equals(obj);
        }

        public void setRawFileSize(long fileSize) {
            this.rawFileSize = fileSize;
        }

        public long getRawFileSize() {
            return rawFileSize;
        }

        @Override
        public String toString() {
            return "RecordFile{" +
                    "rawFileName='" + rawFileName + '\'' +
                    ", streamIndex='" + streamIndex + '\'' +
                    ", fileSize='" + fileSize + '\'' +
                    ", rtspUrl='" + rtspUrl + '\'' +
                    ", classTheme='" + classTheme + '\'' +
                    ", teacher='" + teacher + '\'' +
                    ", recordBeginTime='" + recordBeginTime + '\'' +
                    ", recordRawEndTime='" + recordRawEndTime + '\'' +
                    ", recordEndTime='" + recordEndTime + '\'' +
                    ", duration='" + duration + '\'' +
                    ", rawDuration='" + rawDuration + '\'' +
                    ", downloadUrl='" + downloadUrl + '\'' +
                    ", rawFileSize=" + rawFileSize +
                    ", recordRawBeginTime='" + recordRawBeginTime + '\'' +
                    ", videoSize='" + videoSize + '\'' +
                    ", GOP='" + GOP + '\'' +
                    ", streamMode='" + streamMode + '\'' +
                    ", fps='" + fps + '\'' +
                    ", videoBps='" + videoBps + '\'' +
                    ", videoCodecMode='" + videoCodecMode + '\'' +
                    ", audioBps='" + audioBps + '\'' +
                    ", sampleRate='" + sampleRate + '\'' +
                    ", channelCount='" + channelCount + '\'' +
                    ", sampleBit='" + sampleBit + '\'' +
                    ", audioCodecMode='" + audioCodecMode + '\'' +
                    ", downloadFileName='" + downloadFileName + '\'' +
                    ", downloadProgress=" + downloadProgress +
                    ", downloadDstPath='" + downloadDstPath + '\'' +
                    ", uploadState=" + uploadState +
                    ", uploadProgress='" + uploadProgress + '\'' +
                    ", uploadUUID='" + uploadUUID + '\'' +
                    ", transmissionType=" + transmissionType +
                    '}';
        }
    }
}
