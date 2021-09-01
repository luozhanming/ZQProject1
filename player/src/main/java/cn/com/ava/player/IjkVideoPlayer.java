package cn.com.ava.player;

import android.view.Surface;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * IJKPlayer
 */
public class IjkVideoPlayer implements VideoPlayer {


    private IjkMediaPlayer mPlayer;

    private Surface mSurface;

    private PlayerCallback mCallback;
    //用于判断是否断流
    private int stopThreshold=0;
    private long lastVideoBytes;


    static {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    public static void deinitIjk() {
        IjkMediaPlayer.native_profileEnd();
    }


    public IjkVideoPlayer() {
        this.mPlayer = new IjkMediaPlayer();
    }

    @Override
    public void setSurface(Object surface) {
        if (surface instanceof Surface) {
            mSurface = (Surface) surface;
        } else throw new IllegalArgumentException("Argument surface is not Surface");
    }

    @Override
    public void setPlaybackSpeed(float speed) {
        mPlayer.setSpeed(speed);
    }
    Disposable disposable;
    @Override
    public void startPlay(final String url, PlayerCallback callback) {
        String tempUrl = url;
        boolean isLive = isLiveVideo(tempUrl);
        mPlayer.reset();
        setLiveOption(isLive);
        mCallback = callback;
        try {
            mPlayer.setDataSource(url);
            mPlayer.setSurface(mSurface);
            mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    mCallback.onStart();
                    iMediaPlayer.start();
                }
            });
            mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    mCallback.onCompleted();
                }
            });
            mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                    mCallback.onError(i);
                    return true;
                }
            });
            if(disposable!=null&&!disposable.isDisposed()){
                disposable.dispose();
            }
            disposable = Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            final long bytes = mPlayer.getVideoCachedBytes();
                            if (lastVideoBytes == bytes) {
                                stopThreshold++;
                            } else {
                                stopThreshold = 0;
                            }
                            lastVideoBytes = bytes;
                            if (stopThreshold >= 10) {
                                disposable.dispose();
                                mCallback.notifyRemoteStop();
                                stopThreshold = 0;
                                startPlay(url,mCallback);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setLiveOption(boolean isLive) {
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        if(isLive){
            /**
             * 播放延时的解决方案
             */
            // 如果是rtsp协议，可以优先用tcp(默认是用udp)
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", 10240L);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);

            //         ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);
            // 设置播放前的最大探测时间
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 100000L);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
            // 最大缓冲大小,单位kb
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 0);
            // 默认最小帧数2
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读

            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 2);
            // 播放重连次数
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5);
            // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);
            // 跳过帧
            //     ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 2);
            // 视频帧处理不过来的时候丢弃一些帧达到同步的效果
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5L);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,  "max_cached_duration", 3); //300
            //    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0);
      //      mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"fflags","nobuffer");
        }else {
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1024*900);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"max-buffer-size",8*1024*128);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 0);  // 无限读
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"min-frames",60);
        }
    }

    private boolean isLiveVideo(String url) {
        return url.startsWith("rtsp") || url.startsWith("rtmp");
    }

    @Override
    public void stopPlay() {
        mPlayer.stop();
    }

    @Override
    public void resume() {
        mPlayer.start();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void release() {
        mPlayer.release();
        disposable.dispose();
        mSurface = null;
    }

    @Override
    public long getCurPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public void seekTo(long pos) {
        mPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void reset() {
        mPlayer.reset();
    }
}
