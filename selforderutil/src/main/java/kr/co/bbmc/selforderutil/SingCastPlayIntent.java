package kr.co.bbmc.selforderutil;

public class SingCastPlayIntent {
    public static final int PLAYER_STOP = 4;
    public static final int PLAYER_READY = 5;
    public static final int PLAYER_START = 6;
    public static final int PLAYER_SHOW_END = 7;

    public static final int PLAYER_RESTART = 10;
    public static final int AGENT_RESTART = 11;
    public static final int AGENT_RESTART_OK = 12;
    public static final int AGENT_DBUPDATE = 13;
    public static final int SERVICE_DESTROY = 14;

    //activity result
    public static final int PLAY_RESULT = 200;

    public static final int RESULT_START = 400;
    public static final int RESULT_RESTART = 401;
    public static final int RESULT_SHOW_END = 402;
    public static final int RESULT_MF = 403;

    public static final int RESULT_SHUTDOWN = 500;
    public static final int RESULT_STOP = 501;
    public static final int RESULT_CHGSCHEDULE = 600;
    public static final int RESULT_PLAYSTART_NOTYET = 700;
    public static final int RESULT_REACH_PLAYENDTIME = 701;
    public static final int RESULT_PLAYER_QUIT = 800;
    public static final int RESULT_CONTENT_NOT_FOUND = 801;
    public static final int RESULT_REVOKE_AUTH_SUCCESS = 900;
    public static final int RESULT_REVOKE_AUTH_FAIL = 901;

    public static final String ACTION_PLAYER_START = "kr.co.bbmc.player.start";
    public static final String ACTION_PLAYER_STOP = "kr.co.bbmc.player.stop";
    public static final String ACTION_PLAYER_RESTART = "kr.co.bbmc.player.restart";

    public static final String ACTION_SCHEDULE_UPDATE = "kr.co.bbmc.kiosk.update";
    public static final String ACTION_PLAYER_COMMAND = "kr.co.bbmc.kiosk.playerCommand";
    public static final String ACTION_SERVICE_COMMAND = "kr.co.bbmc.kiosk.serviceCommand";
    public static final String ACTION_MAINSERVICE_START = "kr.co.bbmc.kiosk.mainService.Start";
    public static final String ACTION_SCRCAPTURE_COMMAND = "kr.co.bbmc.kiosk.screenCapture";
    public static final String ACTION_USBTOGO_COMMAND = "kr.co.bbmc.kiosk.usbtogo";

}
