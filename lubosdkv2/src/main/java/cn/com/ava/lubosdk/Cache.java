package cn.com.ava.lubosdk;

import java.util.ArrayList;
import java.util.List;

import cn.com.ava.lubosdk.entity.LayoutButtonInfo;
import cn.com.ava.lubosdk.entity.LuBoInfo;
import cn.com.ava.lubosdk.entity.PreviewVideoWindow;

public class Cache {

    /**
     * 保存最新的window信息
     */
    private List<PreviewVideoWindow> windowsCache;
    /**
     * 录播信息
     * */
    private LuBoInfo luBoInfo;
    /**
     * 当前系统时间
     * */
    private long systemTime = 0L;

    private List<LayoutButtonInfo> layoutInfoCaches;

    private Cache() {
        windowsCache = new ArrayList<>();
        layoutInfoCaches= new ArrayList<>();
    }


    static class InstanceHolder {
        static Cache sInstance = new Cache();
    }

    public static Cache getCache() {
        return InstanceHolder.sInstance;
    }

    public int getwindowCount()
    {
        if (windowsCache != null) {
            return windowsCache.size();
        } else return 0;
    }

    public void saveWindows(List<PreviewVideoWindow> windows) {
        windowsCache.clear();
        windowsCache.addAll(windows);
    }

    public PreviewVideoWindow getWindowsByIndex(int index) {
        if (index > windowsCache.size()) return null;
        return windowsCache.get(index - 1);
    }

    public void saveLayoutInfos(List<LayoutButtonInfo> infos){
        layoutInfoCaches.clear();
        layoutInfoCaches.addAll(infos);
    }

    public List<LayoutButtonInfo> getLayoutInfosCache(){
        return layoutInfoCaches;
    }

    public List<PreviewVideoWindow> getWindowsCache() {
        return windowsCache;
    }

    public LuBoInfo getLuBoInfo() {
        return luBoInfo;
    }

    public void setLuBoInfo(LuBoInfo luBoInfo) {
        this.luBoInfo = luBoInfo;
    }


    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    public void clear() {
        windowsCache.clear();
        luBoInfo = null;
    }


}
