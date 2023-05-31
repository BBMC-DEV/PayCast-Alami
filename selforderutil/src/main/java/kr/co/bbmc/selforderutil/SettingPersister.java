package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingPersister {
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEditor;
    private static Context mContext;
    //통합
    private static final String SETTING_STB_OPTION_ENV = "pref_key_stb_option_env";
    private static final String DEFAULT_SCHEDULE_FILE = "pref_key_type_schedule_file";
    private static final String SETTING_PLAYER_OPTION_ENV = "pref_key_player_option_env";
    private static final String SETTING_PLAYER_STATUS = "pref_key_player_status";
    private static final String SETTING_PLAYER_TEST_COUNT = "pref_key_player_test_count";
    private static final String SETTING_SEND_CONFIG_SEQ  = "pref_key_send_config_seq";
    private static final String SETTING_SEND_CONFIG_NOW  = "pref_key_send_config_now";
    private static final String SETTING_RECV_CONFIG_NOW  = "pref_key_recv_config_now";
    private static final String SETTING_CAPTURE_NOW  = "pref_key_capture_now";
    private static final String SETTING_CAPTURE_PARAM  = "pref_key_capture_param";
    private static final String SETTING_SEND_CONFIG_FILENAME  = "pref_key_send_config_filename";
    private static final String SETTING_RECV_CONFIG_FILENAME  = "pref_key_recv_config_filename";




    private SettingPersister() {

    }

    public static void initPrefs(Context context) {
        if( mPrefs == null){
            mContext = context;
            mPrefs = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            mPrefsEditor = mPrefs.edit();
        }
    }
    /**
     * StbOptionEnv
     * @return
     */
    public static StbOptionEnv getStbOptionEnv() {
//        ArrayList<StbOptionEnv> array = new ArrayList<StbOptionEnv>();
        String s = mPrefs.getString(SETTING_STB_OPTION_ENV, null);
        StbOptionEnv stbOpt = new StbOptionEnv();
        if (s != null) {
            try {
                JSONArray json = new JSONArray(s);
                for (int i = 0; i <json.length(); i++) {
                    JSONObject j = json.getJSONObject(i);

                    stbOpt.playerStart = (boolean)j.get("playerStart");
                    stbOpt.monitorMins = (int)j.get("monitorMins");
                    stbOpt.stbId = (int)j.get("stbId");
                    stbOpt.stbName = (String)j.get("stbName");
                    stbOpt.stbUdpPort = (int)j.get("stbUdpPort");
                    stbOpt.stbServiceType = (String)j.get("stbServiceType");
                    stbOpt.serverHost = (String )j.get("serverHost");
                    stbOpt.serverPort = (int)j.get("serverPort");
                    stbOpt.serverUkid = (String )j.get("serverUkid");
                    stbOpt.ftpActiveMode = (boolean)j.get("ftpActiveMode");
                    stbOpt.ftpHost = (String)j.get("ftpHost");
                    stbOpt.ftpPort = (int)j.get("ftpPort");
                    stbOpt.ftpUser = (String)j.get("ftpUser");
                    stbOpt.ftpPassword = (String)j.get("ftpPassword");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            stbOpt.playerStart = true;
            stbOpt.monitorMins = 3;
            stbOpt.stbId = -1;
            stbOpt.stbUdpPort = 11001;
            stbOpt.stbServiceType = "I";
            stbOpt.serverHost = "saas.signcast.co.kr";
            stbOpt.serverPort = 80;
            stbOpt.serverUkid = "signcast";
            stbOpt.ftpActiveMode = false;
            stbOpt.ftpHost = "saas.signcast.co.kr";
            stbOpt.ftpPort = 21;
            stbOpt.ftpUser = "signcast";
            stbOpt.ftpPassword = "signcastpwd";
        }
        return stbOpt;
    }

    public static void setStbOptionEnv(StbOptionEnv stbOpt) throws JSONException {
        mPrefsEditor = mPrefs.edit();
        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();
        j.put("playerStart", stbOpt.playerStart);
        j.put("monitorMins", stbOpt.monitorMins);
        j.put("stbId", stbOpt.stbId);
        j.put("stbName", stbOpt.stbName);
        j.put("stbUdpPort", stbOpt.stbUdpPort);
        j.put("stbServiceType", stbOpt.stbServiceType);
        j.put("serverUkid", stbOpt.serverUkid);
        j.put("serverHost", stbOpt.serverHost);
        j.put("serverPort", stbOpt.serverPort);
        j.put("ftpActiveMode", stbOpt.ftpActiveMode);
        j.put("ftpHost", stbOpt.ftpHost);
        j.put("ftpPort", stbOpt.ftpPort);
        j.put("ftpUser", stbOpt.ftpUser);
        j.put("ftpPassword", stbOpt.ftpPassword);
        json.put(j);

        mPrefsEditor.putString(SETTING_STB_OPTION_ENV, json.toString());
        mPrefsEditor.apply();
    }

    public static String getTypeAlarmBell() {
        return mPrefs.getString(DEFAULT_SCHEDULE_FILE, "");
    }

    public static void setTypeAlarmBell(String value) {
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString(DEFAULT_SCHEDULE_FILE, value);
        mPrefsEditor.commit();
    }
    /**
     * StbOptionEnv
     * @return
     */
    public static PlayerOptionEnv getPlayerOptionEnv() {
//        ArrayList<StbOptionEnv> array = new ArrayList<StbOptionEnv>();
        String s = mPrefs.getString(SETTING_PLAYER_OPTION_ENV, null);
        PlayerOptionEnv playerOpt = new PlayerOptionEnv();
        if (s != null) {
            try {
                JSONArray json = new JSONArray(s);
                for (int i = 0; i <json.length(); i++) {
                    JSONObject j = json.getJSONObject(i);

                    playerOpt.optionDefaultLang = (String)j.get(mContext.getResources().getString(R.string.playerLang));
                    playerOpt.optionDefaultRegion =(String)j.get(mContext.getResources().getString(R.string.playerRegion));
                    playerOpt.optionIntroductionImgFile =(String)j.get(mContext.getResources().getString(R.string.playerGuidImgFile));
                    playerOpt.optionPowerPointViewerFile = (String)j.get(mContext.getResources().getString(R.string.playerPptViewFile));
                    playerOpt.optionDefaultScheduleFile =(String)j.get(mContext.getResources().getString(R.string.playerScheduleFile));
                    playerOpt.optionConnectionCheckingRequired = (boolean)j.get(mContext.getResources().getString(R.string.playerConnectionCheck));    //default false
                    playerOpt.optionDisplayConnectionError= (boolean)j.get(mContext.getResources().getString(R.string.playervConnectionErrorDisplayed));     //default false
                    playerOpt.optionCustomExeFile = (String)j.get(mContext.getResources().getString(R.string.playerCustomExec));
                    playerOpt.optionCustomExeArgs = (String)j.get(mContext.getResources().getString(R.string.playerCustomArgs));
                    playerOpt.optionSleepSecs = (int)j.get(mContext.getResources().getString(R.string.playerSleepSecs));  //0
                    playerOpt.optionKioskStartOnPlayerStartUp= (boolean)j.get(mContext.getResources().getString(R.string.playerKioskStartOnPlayerStartUp)); //false
                    playerOpt.optionKioskAutoStartSecs = (int)j.get(mContext.getResources().getString(R.string.playerKioskAutoStartSecs));    //0
                    playerOpt.optionAfterClosingAction = (String)j.get(mContext.getResources().getString(R.string.playerAfterClosingAction)); //PF
                    playerOpt.optionClosingDelaySecs= (int)j.get(mContext.getResources().getString(R.string.playerClosingDelaySecs));  //60
                    playerOpt.optionMonitorAction = (String)j.get(mContext.getResources().getString(R.string.playerMonitorAction));  //"Monitor"
                    playerOpt.optionRestartHours= (String)j.get(mContext.getResources().getString(R.string.playerRestartHours));   //"3"
                    playerOpt.optionScreenExtMode = (String)j.get(mContext.getResources().getString(R.string.playerScreenExtMode));  //"0"
                    playerOpt.optionTestNoticeDisplayed= (boolean)j.get(mContext.getResources().getString(R.string.playerTestNoticeDisplayed));   //OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
                    playerOpt.optionTestNoticeText = (String)j.get(mContext.getResources().getString(R.string.playerTestNoticeText));         //OptUtil.GetValue("SlideShow.TestNoticeText");
                    playerOpt.optionTopmostDisplayed = (boolean)j.get(mContext.getResources().getString(R.string.playerTopmostDisplayed));      //OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
                    playerOpt.optionTimeSyncRequired = (boolean)j.get(mContext.getResources().getString(R.string.playerTimeSyncRequired));      //OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
                    playerOpt.optionAllSchedulePlayMode = (boolean)j.get(mContext.getResources().getString(R.string.playerAllSchedulePlayMode));   //OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
                    playerOpt.optionWebBrowserFile = (String)j.get(mContext.getResources().getString(R.string.playerWebBrowserFile));         //OptUtil.GetValue("SlideShow.WebBrowserFile");
                    playerOpt.optionSimpleClockDisplayed = (boolean)j.get(mContext.getResources().getString(R.string.playerSimpleClockDisplayed));  //OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
                    playerOpt.optionSimpleClockDisplayedOnlyOntheHour = (boolean)j.get(mContext.getResources().getString(R.string.playerSimpleClockDisplayedOnlyOntheHour)); //OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
                    playerOpt.optionSlideProgressBarDisplayed = (boolean)j.get(mContext.getResources().getString(R.string.playerSlideProgressBarDisplayed)); //OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
                    playerOpt.optionSlideProgressBarColor = (String)j.get(mContext.getResources().getString(R.string.playerSlideProgressBarColor));  //OptUtil.GetValue("SlideShow.SlideProgressBarColor", "Red");
                    playerOpt.optionOperationInfoDebugged = (boolean)j.get(mContext.getResources().getString(R.string.playerOperationInfoDebugged)); //OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
                    playerOpt.optionTouchInfoLogged = (boolean)j.get(mContext.getResources().getString(R.string.playerTouchInfoLogged));       //OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

                    // jason:transitionfinal: 전환 효과 최종(2015/02/12)
                    playerOpt.optionSlideShowFadeInOutApplied = (boolean)j.get(mContext.getResources().getString(R.string.playerDefaultFadeApplied));     //OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
                    playerOpt.optionSlideShowDefaultFadeTimeType = (String)j.get(mContext.getResources().getString(R.string.playerDefaultFadeTimeType));   //OptUtil.GetValue("SlideShow.DefaultFadeTimeType", "M");
                    playerOpt.optionSlideShowTransparentBGApplied = (boolean)j.get(mContext.getResources().getString(R.string.playerTransparentBGApplied)); //OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
                    playerOpt.optionSlideShowDisplayAfterDuration = (boolean)j.get(mContext.getResources().getString(R.string.playerDisplayAfterDuration)); //OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
                    //-

                    // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
                    playerOpt.optionRoutineSchedPrefix = (String)j.get(mContext.getResources().getString(R.string.playerRoutineSchedPrefix));
                    playerOpt.optionAutoDelSchedDays = (int)j.get(mContext.getResources().getString(R.string.playerAutoDelSchedDays));  //0
                    //-

                    playerOpt.timeMonBeginTime = (String)j.get(mContext.getResources().getString(R.string.playerMonBegin));
                    playerOpt.timeMonEndTime = (String)j.get(mContext.getResources().getString(R.string.playerMonEnd));
                    playerOpt.timeTueBeginTime =(String)j.get(mContext.getResources().getString(R.string.playerTueBegin));
                    playerOpt.timeTueEndTime = (String)j.get(mContext.getResources().getString(R.string.playerTueEnd));
                    playerOpt.timeWedBeginTime =(String)j.get(mContext.getResources().getString(R.string.playerWedBegin));
                    playerOpt.timeWedEndTime = (String)j.get(mContext.getResources().getString(R.string.playerWedEnd));
                    playerOpt.timeThuBeginTime = (String)j.get(mContext.getResources().getString(R.string.playerThuBegin));
                    playerOpt.timeThuEndTime = (String)j.get(mContext.getResources().getString(R.string.playerThuEnd));
                    playerOpt.timeFriBeginTime = (String)j.get(mContext.getResources().getString(R.string.playerFriBegin));
                    playerOpt.timeFriEndTime = (String)j.get(mContext.getResources().getString(R.string.playerFriEnd));
                    playerOpt.timeSatBeginTime =(String)j.get(mContext.getResources().getString(R.string.playerSatBegin));
                    playerOpt.timeSatEndTime =(String)j.get(mContext.getResources().getString(R.string.playerSatEnd));
                    playerOpt.timeSunBeginTime =(String)j.get(mContext.getResources().getString(R.string.playerSunBegin));
                    playerOpt.timeSunEndTime =(String)j.get(mContext.getResources().getString(R.string.playerSunEnd));

                    playerOpt.optionManagerTcpPort= (int)j.get(mContext.getResources().getString(R.string.playerTcpPort));    // 11002
                    playerOpt.optionStbUdpPort = (int)j.get(mContext.getResources().getString(R.string.playerUdpport));    //11001

                    playerOpt.optionDebugDueTime = (String)j.get(mContext.getResources().getString(R.string.playerDebugDueTime));

                    playerOpt.optionWeatherCity = (String)j.get(mContext.getResources().getString(R.string.weatherCity));            //OptUtil.GetValue("Weather.City");
                    playerOpt.optionWeatherCityDisplay = (String)j.get(mContext.getResources().getString(R.string.weatherDisp));     //OptUtil.GetValue("Weather.Display");
                    playerOpt.optionWeatherUnit = (String)j.get(mContext.getResources().getString(R.string.weatherUnit));            //OptUtil.GetValue("Weather.Unit", "C");
                    playerOpt.optionWeatherApiKey = (String)j.get(mContext.getResources().getString(R.string.weatherApiKey));          //OptUtil.GetValue("Weather.ApiKey");

                    playerOpt.optionVolume = (String)j.get(mContext.getResources().getString(R.string.playerVolume));

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            playerOpt.optionDefaultLang = "Lang";
            playerOpt.optionDefaultRegion = "Region";
            playerOpt.optionIntroductionImgFile = "";
            playerOpt.optionPowerPointViewerFile = "";
            playerOpt.optionDefaultScheduleFile = "";
            playerOpt.optionConnectionCheckingRequired = false;    //default false
            playerOpt.optionDisplayConnectionError = false;     //default false
            playerOpt.optionCustomExeFile = "";
            playerOpt.optionCustomExeArgs = "";
            playerOpt.optionSleepSecs = 0;  //0
            playerOpt.optionKioskStartOnPlayerStartUp = false; //false
            playerOpt.optionKioskAutoStartSecs = 0;    //0
            playerOpt.optionAfterClosingAction = "PF"; //PF
            playerOpt.optionClosingDelaySecs = 60;  //60
            playerOpt.optionMonitorAction = "Monitor";  //"Monitor"
            playerOpt.optionRestartHours = "3";   //"3"
            playerOpt.optionScreenExtMode = "0";  //"0"
            playerOpt.optionTestNoticeDisplayed = false;   //OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
            playerOpt.optionTestNoticeText = "";         //OptUtil.GetValue("SlideShow.TestNoticeText");
            playerOpt.optionTopmostDisplayed = false;      //OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
            playerOpt.optionTimeSyncRequired = false;      //OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
            playerOpt.optionAllSchedulePlayMode = false;   //OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
            playerOpt.optionWebBrowserFile = "";         //OptUtil.GetValue("SlideShow.WebBrowserFile");
            playerOpt.optionSimpleClockDisplayed = false;  //OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
            playerOpt.optionSimpleClockDisplayedOnlyOntheHour = true; //OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
            playerOpt.optionSlideProgressBarDisplayed = false; //OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
            playerOpt.optionSlideProgressBarColor = "Red";  //OptUtil.GetValue("SlideShow.SlideProgressBarColor", "Red");
            playerOpt.optionOperationInfoDebugged = false; //OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
            playerOpt.optionTouchInfoLogged = false;       //OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

            // jason:transitionfinal: 전환 효과 최종(2015/02/12)
            playerOpt.optionSlideShowFadeInOutApplied = true;     //OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
            playerOpt.optionSlideShowDefaultFadeTimeType = "M";   //OptUtil.GetValue("SlideShow.DefaultFadeTimeType", "M");
            playerOpt.optionSlideShowTransparentBGApplied = false; //OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
            playerOpt.optionSlideShowDisplayAfterDuration = true; //OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
            //-

            // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
            playerOpt.optionRoutineSchedPrefix = "";
            playerOpt.optionAutoDelSchedDays = 0;  //0
            //-

            playerOpt.timeMonBeginTime = "00:00";
            playerOpt.timeMonEndTime = "00:00";
            playerOpt.timeTueBeginTime = "00:00";
            playerOpt.timeTueEndTime = "00:00";
            playerOpt.timeWedBeginTime ="00:00";
            playerOpt.timeWedEndTime = "00:00";
            playerOpt.timeThuBeginTime ="00:00";
            playerOpt.timeThuEndTime ="00:00";
            playerOpt.timeFriBeginTime = "00:00";
            playerOpt.timeFriEndTime = "00:00";
            playerOpt.timeSatBeginTime = "00:00";
            playerOpt.timeSatEndTime = "00:00";
            playerOpt.timeSunBeginTime ="00:00";
            playerOpt.timeSunEndTime ="00:00";

            playerOpt.optionManagerTcpPort = 11002;    // 11002
            playerOpt.optionStbUdpPort = 11001;    //11001

            playerOpt.optionDebugDueTime = "";

            playerOpt.optionWeatherCity = "";            //OptUtil.GetValue("Weather.City");
            playerOpt.optionWeatherCityDisplay = "";     //OptUtil.GetValue("Weather.Display");
            playerOpt.optionWeatherUnit = "C";            //OptUtil.GetValue("Weather.Unit", "C");
            playerOpt.optionWeatherApiKey = "";          //OptUtil.GetValue("Weather.ApiKey");

            playerOpt.optionVolume = "";
        }
        return playerOpt;
    }

    public static void setPlayerOptionEnv(PlayerOptionEnv playerOpt) throws JSONException {
        mPrefsEditor = mPrefs.edit();
        JSONArray json = new JSONArray();
        JSONObject j = new JSONObject();

        j.put(mContext.getResources().getString(R.string.playerLang), playerOpt.optionDefaultLang);
        j.put(mContext.getResources().getString(R.string.playerRegion), playerOpt.optionDefaultRegion);
        j.put(mContext.getResources().getString(R.string.playerGuidImgFile), playerOpt.optionIntroductionImgFile);
        j.put(mContext.getResources().getString(R.string.playerPptViewFile), playerOpt.optionPowerPointViewerFile);
        j.put(mContext.getResources().getString(R.string.playerScheduleFile), playerOpt.optionDefaultScheduleFile);
        j.put(mContext.getResources().getString(R.string.playerConnectionCheck), playerOpt.optionConnectionCheckingRequired);    //default false
        j.put(mContext.getResources().getString(R.string.playervConnectionErrorDisplayed), playerOpt.optionDisplayConnectionError);     //default false
        j.put(mContext.getResources().getString(R.string.playerCustomExec), playerOpt.optionCustomExeFile);
        j.put(mContext.getResources().getString(R.string.playerCustomArgs), playerOpt.optionCustomExeArgs);
        j.put(mContext.getResources().getString(R.string.playerSleepSecs), playerOpt.optionSleepSecs);  //0
        j.put(mContext.getResources().getString(R.string.playerKioskStartOnPlayerStartUp), playerOpt.optionKioskStartOnPlayerStartUp); //false
        j.put(mContext.getResources().getString(R.string.playerKioskAutoStartSecs), playerOpt.optionKioskAutoStartSecs);    //0
        j.put(mContext.getResources().getString(R.string.playerAfterClosingAction), playerOpt.optionAfterClosingAction); //PF
        j.put(mContext.getResources().getString(R.string.playerClosingDelaySecs), playerOpt.optionClosingDelaySecs);  //60
        j.put(mContext.getResources().getString(R.string.playerMonitorAction), playerOpt.optionMonitorAction);  //"Monitor"
        j.put(mContext.getResources().getString(R.string.playerRestartHours), playerOpt.optionRestartHours);   //"3"
        j.put(mContext.getResources().getString(R.string.playerScreenExtMode), playerOpt.optionScreenExtMode);  //"0"
        j.put(mContext.getResources().getString(R.string.playerTestNoticeDisplayed), playerOpt.optionTestNoticeDisplayed);   //OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
        j.put(mContext.getResources().getString(R.string.playerTestNoticeText), playerOpt.optionTestNoticeText);         //OptUtil.GetValue("SlideShow.TestNoticeText");
        j.put(mContext.getResources().getString(R.string.playerTopmostDisplayed), playerOpt.optionTopmostDisplayed);      //OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
        j.put(mContext.getResources().getString(R.string.playerTimeSyncRequired), playerOpt.optionTimeSyncRequired);      //OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
        j.put(mContext.getResources().getString(R.string.playerAllSchedulePlayMode), playerOpt.optionAllSchedulePlayMode);   //OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
        j.put(mContext.getResources().getString(R.string.playerWebBrowserFile), playerOpt.optionWebBrowserFile);         //OptUtil.GetValue("SlideShow.WebBrowserFile");
        j.put(mContext.getResources().getString(R.string.playerSimpleClockDisplayed), playerOpt.optionSimpleClockDisplayed );  //OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
        j.put(mContext.getResources().getString(R.string.playerSimpleClockDisplayedOnlyOntheHour), playerOpt.optionSimpleClockDisplayedOnlyOntheHour ); //OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
        j.put(mContext.getResources().getString(R.string.playerSlideProgressBarDisplayed), playerOpt.optionSlideProgressBarDisplayed); //OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
        j.put(mContext.getResources().getString(R.string.playerSlideProgressBarColor), playerOpt.optionSlideProgressBarColor );  //OptUtil.GetValue("SlideShow.SlideProgressBarColor", "Red");
        j.put(mContext.getResources().getString(R.string.playerOperationInfoDebugged), playerOpt.optionOperationInfoDebugged); //OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
        j.put(mContext.getResources().getString(R.string.playerTouchInfoLogged), playerOpt.optionTouchInfoLogged);       //OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

        // jason:transitionfinal: 전환 효과 최종(2015/02/12)
        j.put(mContext.getResources().getString(R.string.playerDefaultFadeApplied), playerOpt.optionSlideShowFadeInOutApplied );     //OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
        j.put(mContext.getResources().getString(R.string.playerDefaultFadeTimeType), playerOpt.optionSlideShowDefaultFadeTimeType);   //OptUtil.GetValue("SlideShow.DefaultFadeTimeType", "M");
        j.put(mContext.getResources().getString(R.string.playerTransparentBGApplied), playerOpt.optionSlideShowTransparentBGApplied); //OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
        j.put(mContext.getResources().getString(R.string.playerDisplayAfterDuration), playerOpt.optionSlideShowDisplayAfterDuration); //OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
        //-

        // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
        j.put(mContext.getResources().getString(R.string.playerRoutineSchedPrefix), playerOpt.optionRoutineSchedPrefix);
        j.put(mContext.getResources().getString(R.string.playerAutoDelSchedDays), playerOpt.optionAutoDelSchedDays);  //0
        //-

        j.put(mContext.getResources().getString(R.string.playerMonBegin), playerOpt.timeMonBeginTime);
        j.put(mContext.getResources().getString(R.string.playerMonEnd), playerOpt.timeMonEndTime );
        j.put(mContext.getResources().getString(R.string.playerTueBegin), playerOpt.timeTueBeginTime);
        j.put(mContext.getResources().getString(R.string.playerTueEnd), playerOpt.timeTueEndTime);
        j.put(mContext.getResources().getString(R.string.playerWedBegin), playerOpt.timeWedBeginTime);
        j.put(mContext.getResources().getString(R.string.playerWedEnd), playerOpt.timeWedEndTime );
        j.put(mContext.getResources().getString(R.string.playerThuBegin), playerOpt.timeThuBeginTime );
        j.put(mContext.getResources().getString(R.string.playerThuEnd), playerOpt.timeThuEndTime);
        j.put(mContext.getResources().getString(R.string.playerFriBegin), playerOpt.timeFriBeginTime);
        j.put(mContext.getResources().getString(R.string.playerFriEnd), playerOpt.timeFriEndTime);
        j.put(mContext.getResources().getString(R.string.playerSatBegin), playerOpt.timeSatBeginTime);
        j.put(mContext.getResources().getString(R.string.playerSatEnd), playerOpt.timeSatEndTime);
        j.put(mContext.getResources().getString(R.string.playerSunBegin), playerOpt.timeSunBeginTime);
        j.put(mContext.getResources().getString(R.string.playerSunEnd), playerOpt.timeSunEndTime);

        j.put(mContext.getResources().getString(R.string.playerTcpPort), playerOpt.optionManagerTcpPort);    // 11002
        j.put(mContext.getResources().getString(R.string.playerUdpport), playerOpt.optionStbUdpPort);    //11001

        j.put(mContext.getResources().getString(R.string.playerDebugDueTime), playerOpt.optionDebugDueTime);

        j.put(mContext.getResources().getString(R.string.weatherCity), playerOpt.optionWeatherCity);            //OptUtil.GetValue("Weather.City");
        j.put(mContext.getResources().getString(R.string.weatherDisp), playerOpt.optionWeatherCityDisplay );     //OptUtil.GetValue("Weather.Display");
        j.put(mContext.getResources().getString(R.string.weatherUnit), playerOpt.optionWeatherUnit);            //OptUtil.GetValue("Weather.Unit", "C");
        j.put(mContext.getResources().getString(R.string.weatherApiKey), playerOpt.optionWeatherApiKey);          //OptUtil.GetValue("Weather.ApiKey");

        j.put(mContext.getResources().getString(R.string.playerVolume), playerOpt.optionVolume);

        json.put(j);

        mPrefsEditor.putString(SETTING_PLAYER_OPTION_ENV, json.toString());
        mPrefsEditor.apply();
    }

    public static void setPlayerStatus(String playerStatus) {
        //0: 미확인, 2 : 장비꺼짐, 3:모니터 꺼짐, 4:플레이어 꺼짐, 5:스케줄 미지정, 6: 정상방송
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString(SETTING_PLAYER_STATUS, playerStatus);
        mPrefsEditor.commit();
    }
    public static int getPlayerCount() {
        return mPrefs.getInt(SETTING_PLAYER_TEST_COUNT, 0);
    }
    public static void setPlayerCount(int count) {
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putInt(SETTING_PLAYER_TEST_COUNT, count);
        mPrefsEditor.commit();
    }
    public static String getPlayerStatus() {
        //0: 미확인, 2 : 장비꺼짐, 3:모니터 꺼짐, 4:플레이어 꺼짐, 5:스케줄 미지정, 6: 정상방송
        return mPrefs.getString(SETTING_PLAYER_STATUS, "0");
    }

    public static int getSendConfigSeq() {

        return mPrefs.getInt(SETTING_SEND_CONFIG_SEQ, 0);
    }

    public static void setSendConfigSeq(int seq) {

        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putInt(SETTING_SEND_CONFIG_SEQ, seq);
        mPrefsEditor.commit();
    }
    public static String getSendConfigTempFileName() {

        return mPrefs.getString(SETTING_SEND_CONFIG_FILENAME, null);
    }

    public static void SetSendConfigTempFileName(String fileName) {

        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString(SETTING_SEND_CONFIG_FILENAME, fileName);
        mPrefsEditor.commit();
    }
    public static String getRecConfigTempFileName() {

        return mPrefs.getString(SETTING_RECV_CONFIG_FILENAME, null);
    }

    public static void setRecConfigTempFileName(String fileName) {

        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString(SETTING_RECV_CONFIG_FILENAME, fileName);
        mPrefsEditor.commit();
    }

    public static boolean getSendConfigNow() {

        return mPrefs.getBoolean(SETTING_SEND_CONFIG_NOW, false);
    }

    public static void setSendConfigNow(boolean now) {

        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean(SETTING_SEND_CONFIG_NOW, now);
        mPrefsEditor.commit();
    }
    public static boolean getRecvConfigNow() {

        return mPrefs.getBoolean(SETTING_RECV_CONFIG_NOW, false);
    }

    public static void setRecvConfigNow(boolean now) {

        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean(SETTING_RECV_CONFIG_NOW, now);
        mPrefsEditor.commit();
    }

    public static boolean getCaptureNow() {

        return mPrefs.getBoolean(SETTING_CAPTURE_NOW, false);
    }

    public static void setCaptureNow(boolean now) {

        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean(SETTING_CAPTURE_NOW, now);
        mPrefsEditor.commit();
    }

    public static String getCaptureParam() {

        return mPrefs.getString(SETTING_CAPTURE_PARAM, null);
    }

    public static void setCaptureParam(String param) {

        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString(SETTING_CAPTURE_PARAM, param);
        mPrefsEditor.commit();
    }
}
