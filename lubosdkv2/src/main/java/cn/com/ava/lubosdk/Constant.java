package cn.com.ava.lubosdk;


import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

public class Constant {


    public static final long HTTP_WRITE_TIME_OUT = 20000;
    public static final long HTTP_READ_TIME_OUT = 20000;
    public static final long HTTP_CONNECT_TIME_OUT = 20000;

    public static final String TRACK_FULL_AUTO = "start";
    public static final String TRACK_MID_AUTO = "onlytrack";
    public static final String TRACK_HAND = "stop";

    @StringDef({TRACK_FULL_AUTO, TRACK_MID_AUTO, TRACK_HAND})
    public @interface CamTrackMode {
    }

    public static final int FOCUS_LEVEL_1 = 0;
    public static final int FOCUS_LEVEL_2 = 250;
    public static final int FOCUS_LEVEL_3 = 500;
    public static final int FOCUS_LEVEL_4 = 700;

    @IntDef({FOCUS_LEVEL_1, FOCUS_LEVEL_2, FOCUS_LEVEL_3, FOCUS_LEVEL_4})
    public @interface FocusLevel {
    }


    public static final int ZOOM_NEAR = 98;
    public static final int ZOOM_FAR = 99;

    @IntDef({ZOOM_NEAR, ZOOM_FAR})
    public @interface ZoomOrientation {
    }


    public static final int RECORD_PAUSE = 2001;
    public static final int RECORD_RECORDING = 2002;
    public static final int RECORD_STOP = 2003;
    public static final int RECORD_RESUME = 2004;

    @IntDef({RECORD_PAUSE, RECORD_RECORDING, RECORD_STOP, RECORD_RESUME})
    public @interface RecordState {
    }

    public static final String INTERAC_MODE_RECORD = "record";
    public static final String INTERAC_MODE_TEACH = "teach";
    public static final String INTERAC_MODE_LISTEN = "class";
    public static final String INTERAC_MODE_CONFERENCE = "conference";

    @StringDef({INTERAC_MODE_RECORD, INTERAC_MODE_TEACH, INTERAC_MODE_LISTEN, INTERAC_MODE_CONFERENCE})
    public @interface InteracMode {
    }

    public static final int TYPE_TEACHER = 1001;
    public static final int TYPE_STUDENT = 1002;
    public static final int TYPE_HDMI = 1003;

    @IntDef({TYPE_TEACHER, TYPE_STUDENT, TYPE_HDMI})
    public @interface TSType {
    }

    public static final int SOURCE_COMPUTER = 1001;
    public static final int SOURCE_TEACHER = 1002;
    public static final int SOURCE_STUDENT = 1003;
    public static final int SOURCE_REMOTE = 1004;
    public static final int SOURCE_CANCEL = 1005;

    @IntDef({SOURCE_COMPUTER, SOURCE_TEACHER, SOURCE_STUDENT, SOURCE_REMOTE,SOURCE_CANCEL})
    public @interface InteraSourceType {
    }


}
