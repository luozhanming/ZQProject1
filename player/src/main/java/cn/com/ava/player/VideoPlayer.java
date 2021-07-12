package cn.com.ava.player;

public interface VideoPlayer {

    void setSurface(Object surface);

    void setPlaybackSpeed(float speed);

    void startPlay(String url, PlayerCallback callback);

    void stopPlay();

    void resume();

    void pause();

    void release();

    long getCurPosition();

    long getDuration();

    void seekTo(long pos);

    boolean isPlaying();

    void reset();


}
