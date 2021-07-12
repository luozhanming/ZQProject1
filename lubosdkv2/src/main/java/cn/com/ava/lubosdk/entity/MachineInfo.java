package cn.com.ava.lubosdk.entity;

public class MachineInfo implements QueryResult {

    /*录制参数*/
    private RecordParams recordParams;

    /*剩余控件*/
    private StoreSpace remainSpace;

    /*FTP上传*/
    private String hasFTPTask;

    /*CPU温度*/
    private String CPUTemperature;


    public RecordParams getRecordParams() {
        return recordParams;
    }

    public void setRecordParams(RecordParams recordParams) {
        this.recordParams = recordParams;
    }

    public StoreSpace getRemainSpace() {
        return remainSpace;
    }

    public void setRemainSpace(StoreSpace remainSpace) {
        this.remainSpace = remainSpace;
    }

    public String getHasFTPTask() {
        return hasFTPTask;
    }

    public void setHasFTPTask(String hasFTPTask) {
        this.hasFTPTask = hasFTPTask;
    }

    public String getCPUTemperature() {
        return CPUTemperature;
    }

    public void setCPUTemperature(String CPUTemperature) {
        this.CPUTemperature = CPUTemperature;
    }
}
