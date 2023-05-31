package kr.co.bbmc.selforderutil;

import java.util.Date;
import java.util.List;

public class SCEnvironment {
    public long defaultContentHeight = 100;
    public long defaultContentWidth = 100;

    public int defaultContentVolume = 100;
    public int sleepSecs = 0;
    public int defaultContentPlayTime = 20;
    public List<String> lastSearchKeywords;
    public String powerPointViewerFile = "";
    public String introductionImgFile = "";
    public String defaultScheduleFile = "";
    public String defaultMenuFile = "";
    public boolean connectionCheckingRequired = true;
    public boolean displayConnectionError = true;
    public List<Date> holidays;

    public String monBeginTime = "";
    public String monEndTime = "";
    public String tueBeginTime = "";
    public String tueEndTime = "";
    public String wedBeginTime = "";
    public String wedEndTime = "";
    public String thuBeginTime = "";
    public String thuEndTime = "";
    public String friBeginTime = "";
    public String friEndTime = "";
    public String satBeginTime = "";
    public String satEndTime = "";
    public String sunBeginTime = "";
    public String sunEndTime = "";

    public String managerIp = "";
    public int managerTcpPort = 11002;
    public int stbUdpPort = 11001;

    public String customExeFile = "";
    public String customExeArgs = "";

    public String afterClosingAction = "PF";
    public int closingDelaySecs = 60;
    public String monitorAction = "Monitor";
    public String restartHours = "0";
    public String screenExtMode = "0";

    // jason:transitionfinal: 전환 효과 최종(2015/02/12)
    public boolean slideShowFadeInOutApplied = true;
    public String slideShowDefaultFadeTimeType = "M";
    public boolean slideShowTransparentBGApplied = true;
    public boolean slideShowDisplayAfterDuration = true;
//-

    // jason:supportregionandlang: 지역 및 언어 지원(2012/12/20)
    public String defaultLang;
    public String defaultRegion;

    // jason:kioskstartonstartup: 플레이어 실행 즉시 키오스크 프로그램 실행 옵션(2013/01/24)
    public boolean kioskStartOnPlayerStartUp = false;
    public int kioskAutoStartSecs = 0;

    // jason:testnotice: 플레이어 점검중 공지 기능 옵션(2013/01/29)
    public boolean testNoticeDisplayed = false;
    public String testNoticeText = "";

    // jason:globalweather: 글로벌 날씨 지원(2013/02/04)
    public String weatherCity = "";
    public String weatherCityDisplay = "";
    public String weatherUnit = "C";
    public String weatherApiKey = "";

    // jason:topmostoption: 플레이어 최상위 표시 옵션(2013/04/03)
    public boolean topmostDisplayed = false;

    // jason:synctimeoption: 플레이어 시간동기화 옵션(2013/04/12)
    public boolean timeSyncRequired = false;

    // hazel:allscheduleplay: 모든 스케쥴 방송 옵션(2013/05/20)
    public boolean allSchedulePlayMode = false;

    // jason:webbrowseroption: 전체화면 웹브라우저 옵션(2013/08/26)
    public String webBrowserFile = "";

    // jason:simpleclock: 플레이어 간단한 시계(2013/08/29)
    public boolean simpleClockDisplayed = false;
    public boolean simpleClockDisplayedOnlyOntheHour = true;
//-

    // jason:slideprogressbar: 슬라이드의 진행바 기능(2013/09/02)
    public boolean SlideProgressBarDisplayed = false;
    public String SlideProgressBarColor = "Red";
//-

    // jason:logdebuginfo: 플레이어 디버깅 로그 옵션(2014/02/20)
    public boolean OperationInfoDebugged = false;

    // jason:touchlogoption: 플레이어 터치로그 옵션(2014/12/03)
    public boolean TouchInfoLogged = false;

    // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
    public String RoutineSchedPrefix = "";
    public int autoDelSchedDays = 0;
//-

    // jason:volumectrlbytime: 시간별 음량 설정(2015/11/17)
    public String Volumes = "";
    //-
// jason:uploaddebuglog: 디버그 파일 업로드(2015/04/23)
    public Date TempLogDt;

    //-
    public String optionCardServer = "";
    public String optionCardport = "";


}
