package cn.com.ava.player;

public interface PlayerCallback {

    void onStart();

    void onCompleted();

    void onError(int error);

    void notifyRemoteStop();
}
