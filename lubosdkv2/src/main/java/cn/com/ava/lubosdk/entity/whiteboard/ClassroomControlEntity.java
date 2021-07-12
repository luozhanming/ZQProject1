package cn.com.ava.lubosdk.entity.whiteboard;

public class ClassroomControlEntity {


    /*课室id*/
    private String id;
    /*课室名称*/
    private String classRoomName;
    /*书写权限*/
    private boolean canWrite;
    /*连接状态*/
    private boolean isConnected;

    private boolean isPrepareOutput;

    private boolean isOutput;

    public ClassroomControlEntity() {
    }

    public ClassroomControlEntity(String key, String className) {
        this.id = key;
        this.classRoomName = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassRoomName() {
        return classRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        this.classRoomName = classRoomName;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isPrepareOutput() {
        return isPrepareOutput;
    }

    public void setPrepareOutput(boolean prepareOutput) {
        isPrepareOutput = prepareOutput;
    }


    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    }
}
