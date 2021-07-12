package cn.com.ava.lubosdk.entity.whiteboard;

import cn.com.ava.lubosdk.entity.QueryResult;

public class BlankBoardState implements QueryResult {

  private boolean isLaunch;  //白板开关状态
  private  int mode;  //书写模式
  private boolean bgSwitch;  //底图开关
  private boolean isWriting;  //是否开始答题
  private boolean hasAnswer;  //是否已经答过题
  private int clientType;//用户类型

  public boolean isLaunch() {
    return isLaunch;
  }

  public void setLaunch(boolean launch) {
    this.isLaunch = launch;
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public boolean isBgSwitch() {
    return bgSwitch;
  }

  public void setBgSwitch(boolean bgSwitch) {
    this.bgSwitch = bgSwitch;
  }

  public boolean isWriting() {
    return isWriting;
  }

  public void setWriting(boolean writing) {
    isWriting = writing;
  }

  public boolean isHasAnswer() {
    return hasAnswer;
  }

  public void setHasAnswer(boolean hasAnswer) {
    this.hasAnswer = hasAnswer;
  }


  public int getClientType() {
    return clientType;
  }

  public void setClientType(int clientType) {
    this.clientType = clientType;
  }
}
