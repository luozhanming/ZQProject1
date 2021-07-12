package cn.com.ava.lubosdk.entity;

import java.util.List;

/**
 * 预览窗口列表
 */
public class PreviewVideoWindow {

    /*窗口序号*/
    private int index;
    /*窗口名称*/
    private String windowName;
    /*是否可设置预置位*/
    private boolean isPtz;
    /*预置位的idx*/
    private int ptzIdx;
    /*是否当前输出窗口*/
     private boolean isCurrentOutput;
    /*能否抠像*/
    private boolean canMatImage;
    /*是否有多个源*/
    private boolean hasMultiSource;
    /*视频源列表*/
    private List<String> sources;
    /*视频源指令*/
    private List<String> sourcesCmd;
    /*当前源序号*/
    private int curSourceIndex;
    /*是否将设置预置位*/
    private boolean isPrepareToPreset;
    /*是否正抠像*/
    private boolean isMatting;

    private boolean isCurMain;
    private boolean isCurSub;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public boolean isPtz() {
        return isPtz;
    }

    public void setPtz(boolean ptz) {
        isPtz = ptz;
    }

    public boolean isCurrentOutput() {
        return isCurrentOutput;
    }

    public void setCurrentOutput(boolean currentOutput) {
        isCurrentOutput = currentOutput;
    }

    public boolean isCanMatImage() {
        return canMatImage;
    }

    public void setCanMatImage(boolean canMatImage) {
        this.canMatImage = canMatImage;
    }

    public boolean isHasMultiSource() {
        return hasMultiSource;
    }

    public void setHasMultiSource(boolean hasMultiSource) {
        this.hasMultiSource = hasMultiSource;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public int getCurSourceIndex() {
        return curSourceIndex;
    }

    public void setCurSourceIndex(int curSourceIndex) {
        this.curSourceIndex = curSourceIndex;
    }

    public boolean isPrepareToPreset() {
        return isPrepareToPreset;
    }

    public void setPrepareToPreset(boolean prepareToPreset) {
        isPrepareToPreset = prepareToPreset;
    }

    public boolean isMatting() {
        return isMatting;
    }

    public void setMatting(boolean matting) {
        isMatting = matting;
    }

    public int getPtzIdx() {
        return ptzIdx;
    }

    public void setPtzIdx(int ptzIdx) {
        this.ptzIdx = ptzIdx;
    }

    public List<String> getSourcesCmd() {
        return sourcesCmd;
    }

    public void setSourcesCmd(List<String> sourcesCmd) {
        this.sourcesCmd = sourcesCmd;
    }


    public boolean isCurMain() {
        return isCurMain;
    }

    public void setCurMain(boolean curMain) {
        isCurMain = curMain;
    }

    public boolean isCurSub() {
        return isCurSub;
    }

    public void setCurSub(boolean curSub) {
        isCurSub = curSub;
    }

    @Override
    public String toString() {
        return "PreviewVideoWindow{" +
                "index=" + index +
                ", windowName='" + windowName + '\'' +
                ", isPtz=" + isPtz +
                ", ptzIdx=" + ptzIdx +
                ", isCurrentOutput=" + isCurrentOutput +
                ", canMatImage=" + canMatImage +
                ", hasMultiSource=" + hasMultiSource +
                ", sources=" + sources +
                ", sourcesCmd=" + sourcesCmd +
                ", curSourceIndex=" + curSourceIndex +
                ", isPrepareToPreset=" + isPrepareToPreset +
                ", isMatting=" + isMatting +
                ", isCurMain=" + isCurMain +
                ", isCurSub=" + isCurSub +
                '}';
    }
}
