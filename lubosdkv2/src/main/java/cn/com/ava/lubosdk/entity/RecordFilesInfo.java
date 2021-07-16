package cn.com.ava.lubosdk.entity;

import java.io.Serializable;
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
//        private @RecordFileDLStateEvent.State
//        int state;
//        private int progress;
//
//        private boolean isSelected;
        private String downloadFileName;

//        @DownloadState
//        private int downloadState = STATE_IDLE;


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

    }
}
