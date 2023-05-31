package kr.co.bbmc.selforderutil;

public class PlayerOptionEnv {
    public static String[][] initPlayerOptions = new String[][] {
            new String[] { "Lang", "" },
            new String[] { "Region", "" },

            new String[] { "SlideShow.GuideImageFile", "" },
            new String[] { "SlideShow.PptViewerFile", "" },
            new String[] { "SlideShow.ScheduleFile", "" },
            new String[] { "SlideShow.StoreMenuFile", "" },
            new String[] { "SlideShow.ConnectionCheck", "False" },
            new String[] { "SlideShow.ConnectionErrorDisplayed", "False" },
            new String[] { "SlideShow.CustomExec", "" },
            new String[] { "SlideShow.CustomArgs", "" },
            new String[] { "SlideShow.SleepSecs", "0" },
            new String[] { "SlideShow.KioskStartOnPlayerStartUp", "False" },
            new String[] { "SlideShow.KioskAutoStartSecs", "0" },
            new String[] { "SlideShow.AfterClosingAction", "PF" },
            new String[] { "SlideShow.ClosingDelaySecs", "60" },
            new String[] { "SlideShow.MonitorAction", "Monitor" },
            new String[] { "SlideShow.RestartHours", "3" },
            new String[] { "SlideShow.ScreenExtMode", "0" },
            new String[] { "SlideShow.TestNoticeDisplayed", "False" },
            new String[] { "SlideShow.TestNoticeText", "" },
            new String[] { "SlideShow.TopmostDisplayed", "False" },
            new String[] { "SlideShow.TimeSyncRequired", "False" },
            new String[] { "SlideShow.AllSchedulePlayMode", "False" },
            new String[] { "SlideShow.WebBrowserFile", "" },
            new String[] { "SlideShow.SimpleClockDisplayed", "False" },
            new String[] { "SlideShow.SimpleClockDisplayedOnlyOntheHour", "True" },
            new String[] { "SlideShow.SlideProgressBarDisplayed", "False" },
            new String[] { "SlideShow.SlideProgressBarColor", "Red" },
            new String[] { "SlideShow.OperationInfoDebugged", "False" },
            new String[] { "SlideShow.TouchInfoLogged", "False" },
            new String[] { "SlideShow.DefaultFadeApplied", "True" },
            new String[] { "SlideShow.DefaultFadeTimeType", "M" },
            new String[] { "SlideShow.TransparentBGApplied", "False" },
            new String[] { "SlideShow.DisplayAfterDuration", "True" },
            new String[] { "SlideShow.RoutineSchedPrefix", "" },
            new String[] { "SlideShow.AutoDelSchedDays", "0" },

            new String[] { "Playtime.MonBeginTime", "00:00" },
            new String[] { "Playtime.MonEndTime", "00:00" },
            new String[] { "Playtime.TueBeginTime", "00:00" },
            new String[] { "Playtime.TueEndTime", "00:00" },
            new String[] { "Playtime.WedBeginTime", "00:00" },
            new String[] { "Playtime.WedEndTime", "00:00" },
            new String[] { "Playtime.ThuBeginTime", "00:00" },
            new String[] { "Playtime.ThuEndTime", "00:00" },
            new String[] { "Playtime.FriBeginTime", "00:00" },
            new String[] { "Playtime.FriEndTime", "00:00" },
            new String[] { "Playtime.SatBeginTime", "00:00" },
            new String[] { "Playtime.SatEndTime", "00:00" },
            new String[] { "Playtime.SunBeginTime", "00:00" },
            new String[] { "Playtime.SunEndTime", "00:00" },

            new String[] { "Communication.ManagerTcpPort", "11002" },
            new String[] { "Communication.StbUdpPort", "11001" },

            new String[] { "Weather.City", "" },
            new String[] { "Weather.Display", "" },
            new String[] { "Weather.Unit", "C" },
            new String[] { "Weather.ApiKey", "" },

            new String[] { "Volume", "" },
            new String[] { "SlideShow.StoreMenuFile", "" },
            new String[] { "Communication.cardServer", "" },
            new String[] { "Communication.cardport", "" },
    };

    public String optionDefaultLang;
    public String optionDefaultRegion;

    public String optionIntroductionImgFile;
    public String optionPowerPointViewerFile;
    public String optionDefaultScheduleFile;
    public String optionDefaultMenuFile;
    public boolean optionConnectionCheckingRequired;    //default false
    public boolean optionDisplayConnectionError;     //default false
    public String optionCustomExeFile;
    public String optionCustomExeArgs;
    public int optionSleepSecs;  //0
    public boolean optionKioskStartOnPlayerStartUp; //false
    public int optionKioskAutoStartSecs;    //0
    public String optionAfterClosingAction; //PF
    public int optionClosingDelaySecs;  //60
    public String optionMonitorAction;  //"Monitor"
    public String optionRestartHours;   //"3"
    public String optionScreenExtMode;  //"0"
    public boolean optionTestNoticeDisplayed;   //OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
    public String optionTestNoticeText;         //OptUtil.GetValue("SlideShow.TestNoticeText");
    public boolean optionTopmostDisplayed;      //OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
    public boolean optionTimeSyncRequired;      //OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
    public boolean optionAllSchedulePlayMode;   //OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
    public String optionWebBrowserFile;         //OptUtil.GetValue("SlideShow.WebBrowserFile");
    public boolean optionSimpleClockDisplayed;  //OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
    public boolean optionSimpleClockDisplayedOnlyOntheHour; //OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
    public boolean optionSlideProgressBarDisplayed; //OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
    public String optionSlideProgressBarColor;  //OptUtil.GetValue("SlideShow.SlideProgressBarColor", "Red");
    public boolean optionOperationInfoDebugged; //OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
    public boolean optionTouchInfoLogged;       //OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

    public boolean optionSlideShowFadeInOutApplied;     //OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
    public String optionSlideShowDefaultFadeTimeType;   //OptUtil.GetValue("SlideShow.DefaultFadeTimeType", "M");
    public boolean optionSlideShowTransparentBGApplied; //OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
    public boolean optionSlideShowDisplayAfterDuration; //OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
    //-

    public String optionRoutineSchedPrefix;
    public int optionAutoDelSchedDays;  //0
    //-

    public String timeMonBeginTime;
    public String timeMonEndTime;
    public String timeTueBeginTime;
    public String timeTueEndTime;
    public String timeWedBeginTime;
    public String timeWedEndTime;
    public String timeThuBeginTime;
    public String timeThuEndTime;
    public String timeFriBeginTime;
    public String timeFriEndTime;
    public String timeSatBeginTime;
    public String timeSatEndTime;
    public String timeSunBeginTime;
    public String timeSunEndTime;

    public String optionCardport;

    public int optionManagerTcpPort;    // 11002
    public int optionStbUdpPort;    //11001

    public String optionDebugDueTime;

    public String optionWeatherCity;            //OptUtil.GetValue("Weather.City");
    public String optionWeatherCityDisplay;     //OptUtil.GetValue("Weather.Display");
    public String optionWeatherUnit;            //OptUtil.GetValue("Weather.Unit", "C");
    public String optionWeatherApiKey;          //OptUtil.GetValue("Weather.ApiKey");
    public String optionManagerIp;

    public String optionCardServer;

    public String optionVolume;
    public void init(){
        optionDefaultLang = "Lang";
        optionDefaultRegion = "Region";
        optionIntroductionImgFile = "";
        optionPowerPointViewerFile = "";
        optionDefaultScheduleFile = "";
        optionConnectionCheckingRequired = false;    //default false
        optionDisplayConnectionError = false;     //default false
        optionCustomExeFile = "";
        optionCustomExeArgs = "";
        optionSleepSecs = 0;  //0
        optionKioskStartOnPlayerStartUp = false; //false
        optionKioskAutoStartSecs = 0;    //0
        optionAfterClosingAction = "PF"; //PF
        optionClosingDelaySecs = 60;  //60
        optionMonitorAction = "Monitor";  //"Monitor"
        optionRestartHours = "3";   //"3"
        optionScreenExtMode = "0";  //"0"
        optionTestNoticeDisplayed = false;   //OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
        optionTestNoticeText = "";         //OptUtil.GetValue("SlideShow.TestNoticeText");
        optionTopmostDisplayed = false;      //OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
        optionTimeSyncRequired = false;      //OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
        optionAllSchedulePlayMode = false;   //OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
        optionWebBrowserFile = "";         //OptUtil.GetValue("SlideShow.WebBrowserFile");
        optionSimpleClockDisplayed = false;  //OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
        optionSimpleClockDisplayedOnlyOntheHour = true; //OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
        optionSlideProgressBarDisplayed = false; //OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
        optionSlideProgressBarColor = "Red";  //OptUtil.GetValue("SlideShow.SlideProgressBarColor", "Red");
        optionOperationInfoDebugged = false; //OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
        optionTouchInfoLogged = false;       //OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

        // jason:transitionfinal: ?�환 ?�과 최종(2015/02/12)
        optionSlideShowFadeInOutApplied = true;     //OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
        optionSlideShowDefaultFadeTimeType = "M";   //OptUtil.GetValue("SlideShow.DefaultFadeTimeType", "M");
        optionSlideShowTransparentBGApplied = false; //OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
        optionSlideShowDisplayAfterDuration = true; //OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
        //-

        // jason:scheduleautomation: ?��?�??�동 관�?기능(2015/04/02)
        optionRoutineSchedPrefix = "";
        optionAutoDelSchedDays = 0;  //0
        //-

        timeMonBeginTime = "00:00";
        timeMonEndTime = "00:00";
        timeTueBeginTime = "00:00";
        timeTueEndTime = "00:00";
        timeWedBeginTime ="00:00";
        timeWedEndTime = "00:00";
        timeThuBeginTime ="00:00";
        timeThuEndTime ="00:00";
        timeFriBeginTime = "00:00";
        timeFriEndTime = "00:00";
        timeSatBeginTime = "00:00";
        timeSatEndTime = "00:00";
        timeSunBeginTime ="00:00";
        timeSunEndTime ="00:00";

        optionManagerTcpPort = 11002;    // 11002
        optionStbUdpPort = 11001;    //11001

        optionDebugDueTime = "";

        optionWeatherCity = "";            //OptUtil.GetValue("Weather.City");
        optionWeatherCityDisplay = "";     //OptUtil.GetValue("Weather.Display");
        optionWeatherUnit = "C";            //OptUtil.GetValue("Weather.Unit", "C");
        optionWeatherApiKey = "";          //OptUtil.GetValue("Weather.ApiKey");

        optionVolume = "";

        optionCardport = "1234";
        optionCardServer = "192.168.0.123";

    }
}
