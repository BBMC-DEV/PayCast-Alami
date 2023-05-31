package kr.co.bbmc.selforderutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlOptionParser {
    private static final String TAG = "XmlOptionParser";
    private static final boolean LOG = false;
    // jason:newoptionfile: 프로그램 옵션 파일 기능 단순화(2015/02/02)
    public SCEnvironment onGetPlayerConfigData(Context c)
    {

        SCEnvironment data = new SCEnvironment();

        OptUtil.ReadOptions(FileUtils.Player, c);

        data.defaultLang = OptUtil.GetValue("Lang", data.defaultLang);
        data.defaultRegion = OptUtil.GetValue("Region", data.defaultRegion);

        data.introductionImgFile = OptUtil.GetValue("SlideShow.GuideImageFile");
        data.powerPointViewerFile = OptUtil.GetValue("SlideShow.PptViewerFile");
        data.defaultScheduleFile = OptUtil.GetValue("SlideShow.ScheduleFile");
        data.defaultMenuFile = OptUtil.GetValue("SlideShow.StoreMenuFile");
        data.connectionCheckingRequired = OptUtil.GetValue("SlideShow.ConnectionCheck", false);
        data.displayConnectionError = OptUtil.GetValue("SlideShow.ConnectionErrorDisplayed", false);
        data.customExeFile = OptUtil.GetValue("SlideShow.CustomExec");
        data.customExeArgs = OptUtil.GetValue("SlideShow.CustomArgs");
        data.sleepSecs = OptUtil.GetValue("SlideShow.SleepSecs", 0);
        data.kioskStartOnPlayerStartUp = OptUtil.GetValue("SlideShow.KioskStartOnPlayerStartUp", false);
        data.kioskAutoStartSecs = OptUtil.GetValue("SlideShow.KioskAutoStartSecs", 0);
        data.afterClosingAction = OptUtil.GetValue("SlideShow.AfterClosingAction", "PF");
        data.closingDelaySecs = OptUtil.GetValue("SlideShow.ClosingDelaySecs", 60);
        data.monitorAction = OptUtil.GetValue("SlideShow.MonitorAction", "Monitor");
        data.restartHours = OptUtil.GetValue("SlideShow.RestartHours", "3");
        data.screenExtMode = OptUtil.GetValue("SlideShow.ScreenExtMode", "0");
        data.testNoticeDisplayed = OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
        data.testNoticeText = OptUtil.GetValue("SlideShow.TestNoticeText");
        data.topmostDisplayed = OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
        data.timeSyncRequired = OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
        data.allSchedulePlayMode = OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
        data.webBrowserFile = OptUtil.GetValue("SlideShow.WebBrowserFile");
        data.simpleClockDisplayed = OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
        data.simpleClockDisplayedOnlyOntheHour = OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
        data.SlideProgressBarDisplayed = OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
        data.SlideProgressBarColor = OptUtil.GetValue("SlideShow.SlideProgressBarColor", "Red");
        data.OperationInfoDebugged = OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
        data.TouchInfoLogged = OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

        // jason:transitionfinal: 전환 효과 최종(2015/02/12)
        data.slideShowFadeInOutApplied = OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
        data.slideShowDefaultFadeTimeType = OptUtil.GetValue("SlideShow.DefaultFadeTimeType", "M");
        data.slideShowTransparentBGApplied = OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
        data.slideShowDisplayAfterDuration = OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
        //-

        // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
        data.RoutineSchedPrefix = OptUtil.GetValue("SlideShow.RoutineSchedPrefix");
        data.autoDelSchedDays = OptUtil.GetValue("SlideShow.AutoDelSchedDays", 0);
        //-

        data.monBeginTime = OptUtil.GetValue("Playtime.MonBeginTime");
        data.monEndTime = OptUtil.GetValue("Playtime.MonEndTime");
        data.tueBeginTime = OptUtil.GetValue("Playtime.TueBeginTime");
        data.tueEndTime = OptUtil.GetValue("Playtime.TueEndTime");
        data.wedBeginTime = OptUtil.GetValue("Playtime.WedBeginTime");
        data.wedEndTime = OptUtil.GetValue("Playtime.WedEndTime");
        data.thuBeginTime = OptUtil.GetValue("Playtime.ThuBeginTime");
        data.thuEndTime = OptUtil.GetValue("Playtime.ThuEndTime");
        data.friBeginTime = OptUtil.GetValue("Playtime.FriBeginTime");
        data.friEndTime = OptUtil.GetValue("Playtime.FriEndTime");
        data.satBeginTime = OptUtil.GetValue("Playtime.SatBeginTime");
        data.satEndTime = OptUtil.GetValue("Playtime.SatEndTime");
        data.sunBeginTime = OptUtil.GetValue("Playtime.SunBeginTime");
        data.sunEndTime = OptUtil.GetValue("Playtime.SunEndTime");

        data.managerTcpPort = OptUtil.GetValue("Communication.ManagerTcpPort", 11002);
        data.stbUdpPort = OptUtil.GetValue("Communication.StbUdpPort", 11001);

        data.weatherCity = OptUtil.GetValue("Weather.City", data.weatherCity);
        data.weatherCityDisplay = OptUtil.GetValue("Weather.Display", data.weatherCityDisplay);
        data.weatherUnit = OptUtil.GetValue("Weather.Unit", data.weatherUnit);
        data.weatherApiKey = OptUtil.GetValue("Weather.ApiKey", data.weatherApiKey);

        data.holidays = OptUtil.ReadOtherDateTimeOptions(FileUtils.Holiday);

        // jason:uploaddebuglog: 디버그 파일 업로드(2015/04/23)
        data.TempLogDt = OptUtil.OptDateTimeValue(OptUtil.GetValue("Temp.DebugDueTime"));
        //-

        // jason:volumectrlbytime: 시간별 음량 설정(2015/11/17)
        data.Volumes = OptUtil.GetValue("Volume");
        //-
        data.optionCardServer = OptUtil.GetValue("Communication.cardServer");
        data.optionCardport = OptUtil.GetValue("Communication.cardport", "1234");

        return data;
    }
    //-

    public StbOptionEnv getStbInfoFromServer(XmlPullParser pullParser, StbOptionEnv env)
    {
        StbOptionEnv optionEnv = env;
        for(int i = 0; i < pullParser.getAttributeCount(); i++)
        {
            if(LOG)
                Log.d(TAG, "startTag- attribute["+i+"]="+pullParser.getAttributeName(i)+" value="+pullParser.getAttributeValue(i));
            switch(pullParser.getAttributeName(i))
            {
                case "stbid" :
                    optionEnv.stbId = Integer.valueOf(pullParser.getAttributeValue(i));
                    if(LOG)
                        Log.d(TAG, "case stbid");
                    break;
                case "stbname" :
                    optionEnv.stbName = pullParser.getAttributeValue(i);
                    if(LOG)
                        Log.d(TAG, "case stbname");
                    break;
                case "stbudpport" :
                    optionEnv.stbUdpPort = Integer.valueOf(pullParser.getAttributeValue(i));
                    if(LOG)
                        Log.d(TAG, "case stbudpport");
                    break;
                case "stbservicetype" :
                    optionEnv.stbServiceType = pullParser.getAttributeValue(i);
                    if(LOG)
                        Log.d(TAG, "case stbservicetype");
                    break;
                case "ftphost" :
                    optionEnv.ftpHost = pullParser.getAttributeValue(i);
                    if(LOG)
                        Log.d(TAG, "case ftphost");
                    break;
                case "ftpport" :
                    optionEnv.ftpPort = Integer.valueOf(pullParser.getAttributeValue(i));
                    if(LOG)
                        Log.d(TAG, "case ftpport");
                    break;
                case "ftpuser" :
                    optionEnv.ftpUser = pullParser.getAttributeValue(i);
                    if(LOG)
                        Log.d(TAG, "case ftpuser");
                    break;
                case "ftppassword" :
                    optionEnv.ftpPassword = pullParser.getAttributeValue(i);
                    if(LOG)
                        Log.d(TAG, "case ftppassword");
                    break;
                case "serverhost" :
                    optionEnv.serverHost = pullParser.getAttributeValue(i);
                    if(LOG)
                        Log.d(TAG, "case serverhost");
                    break;
                case "serverport" :
                    optionEnv.serverPort = Integer.valueOf(pullParser.getAttributeValue(i));
                    if(LOG)
                        Log.d(TAG, "case serverport");
                    break;
                case "serverukid" :
                    optionEnv.serverUkid = pullParser.getAttributeValue(i);
                    if(LOG)
                        Log.d(TAG, "case serverukid");
                    break;
                default:
                    break;
            }
        }
        return optionEnv;
    }
    public Boolean applyPropAndOptFromServer(XmlPullParser pullParser)
    {
        return true;
    }
    public SCEnvironment onGetPlayerConfigData(SCEnvironment env, String tag, String text)
    {
        SCEnvironment playerOpt = env;

        switch(tag)
        {
            case "Lang" :
                if(LOG)
                    Log.d(TAG, "case Lang text="+text);
                playerOpt.defaultLang = text;
                break;
            case "Region" :
                if(LOG)
                    Log.d(TAG, "case Region text="+text);
                playerOpt.defaultRegion = text;
                break;

            case "SlideShow.GuideImageFile" :
                if(LOG)
                    Log.d(TAG, "case SlideShow.GuideImageFile text="+text);
                playerOpt.introductionImgFile = text;
                break;
            case "Communication.ManagerTcpPort" :
                playerOpt.managerTcpPort = Integer.valueOf(text);
                break;
            case "Communication.StbUdpPort" :
                playerOpt.stbUdpPort = Integer.valueOf(text);
                break;
            case "Playtime.FriBeginTime" :
                playerOpt.friBeginTime = text;
                break;

            case "Playtime.FriEndTime" :
                playerOpt.friEndTime = text;
                break;
            case "Playtime.MonBeginTime" :
                playerOpt.monBeginTime = text;
                break;
            case "Playtime.MonEndTime" :
                playerOpt.monEndTime = text;
                break;
            case "Playtime.SatBeginTime" :
                playerOpt.satBeginTime = text;
                break;
            case "Playtime.SatEndTime" :
                playerOpt.satEndTime = text;
                break;
            case "Playtime.SunBeginTime" :
                playerOpt.sunBeginTime = text;
                break;
            case "Playtime.SunEndTime" :
                playerOpt.sunEndTime = text;
                break;
            case "Playtime.ThuBeginTime" :
                playerOpt.thuBeginTime = text;
                break;
            case "Playtime.ThuEndTime" :
                playerOpt.thuEndTime = text;
                break;
            case "Playtime.TueBeginTime" :
                playerOpt.tueBeginTime = text;
                break;
            case "Playtime.TueEndTime" :
                playerOpt.tueEndTime = text;
                break;
            case "Playtime.WedBeginTime" :
                playerOpt.wedBeginTime = text;
                break;
            case "Playtime.WedEndTime" :
                playerOpt.wedEndTime = text;
                break;
            case "SlideShow.AfterClosingAction" :
                playerOpt.afterClosingAction = text;
                break;
            case "SlideShow.AllSchedulePlayMode" :
                playerOpt.allSchedulePlayMode = Boolean.valueOf(text);
                break;

            case "SlideShow.AutoDelSchedDays" :
                playerOpt.autoDelSchedDays = Integer.valueOf(text);
                break;

            case "SlideShow.ClosingDelaySecs" :
                playerOpt.closingDelaySecs = Integer.valueOf(text);
                break;
            case "SlideShow.ConnectionCheck" :
                playerOpt.connectionCheckingRequired = Boolean.valueOf(text);
                break;
            case "SlideShow.ConnectionErrorDisplayed" :
                playerOpt.displayConnectionError = Boolean.valueOf(text);
                break;
            case "SlideShow.CustomArgs" :
                playerOpt.customExeArgs = text;
                break;
            case "SlideShow.CustomExec" :
                playerOpt.customExeFile = text;
                break;
            case "SlideShow.DefaultFadeApplied" :
                playerOpt.slideShowFadeInOutApplied = Boolean.valueOf(text);
                break;
            case "SlideShow.DefaultFadeTimeType" :
                playerOpt.slideShowDefaultFadeTimeType = text;
                break;
            case "SlideShow.DisplayAfterDuration" :
                playerOpt.slideShowDisplayAfterDuration = Boolean.valueOf(text);
                break;
            case "SlideShow.KioskAutoStartSecs" :
                playerOpt.kioskAutoStartSecs = Integer.valueOf(text);
                break;
            case "SlideShow.KioskStartOnPlayerStartUp" :
                playerOpt.kioskStartOnPlayerStartUp = Boolean.valueOf(text);
                break;
            case "SlideShow.MonitorAction" :
                playerOpt.monitorAction = text;
                break;
            case "SlideShow.OperationInfoDebugged" :
                playerOpt.OperationInfoDebugged = Boolean.valueOf(text);
                break;
            case "SlideShow.PptViewerFile" :
                playerOpt.powerPointViewerFile = text;
                break;
            case "SlideShow.RestartHours" :
                playerOpt.restartHours = text;
                break;
            case "SlideShow.RoutineSchedPrefix" :
                playerOpt.RoutineSchedPrefix = text;
                break;
            case "SlideShow.ScheduleFile" :
                playerOpt.defaultScheduleFile = text;
                break;
            case "SlideShow.ScreenExtMode" :
                playerOpt.screenExtMode = text;
                break;
            case "SlideShow.SimpleClockDisplayed" :
                playerOpt.simpleClockDisplayed = Boolean.valueOf(text);
                break;
            case "SlideShow.SimpleClockDisplayedOnlyOntheHour" :
                playerOpt.simpleClockDisplayedOnlyOntheHour = Boolean.valueOf(text);
                break;
            case "SlideShow.SleepSecs" :
                playerOpt.sleepSecs = Integer.valueOf(text);
                break;
            case "SlideShow.SlideProgressBarColor" :
                playerOpt.SlideProgressBarColor = text;
                break;
            case "SlideShow.SlideProgressBarDisplayed" :
                playerOpt.SlideProgressBarDisplayed = Boolean.valueOf(text);
                break;
            case "SlideShow.TestNoticeDisplayed" :
                playerOpt.testNoticeDisplayed = Boolean.valueOf(text);
                break;
            case "SlideShow.TestNoticeText" :
                playerOpt.testNoticeText = text;
                break;
            case "SlideShow.TimeSyncRequired" :
                playerOpt.timeSyncRequired = Boolean.valueOf(text);
                break;
            case "SlideShow.TopmostDisplayed" :
                playerOpt.topmostDisplayed = Boolean.valueOf(text);
                break;
            case "SlideShow.TouchInfoLogged" :
                playerOpt.TouchInfoLogged = Boolean.valueOf(text);
                break;
            case "SlideShow.TransparentBGApplied" :
                playerOpt.slideShowTransparentBGApplied = Boolean.valueOf(text);
                break;
            case "SlideShow.WebBrowserFile" :
                playerOpt.webBrowserFile = text;
                break;
            case "Temp.DebugDueTime" :  // 디버그 파일 업로드(2015/04/23)
//                playerOpt.?? = text;  디버깅 위한 사항으로 나중에 해야 함.
                break;
            case "Volume" :
                playerOpt.defaultContentVolume = Integer.valueOf(text);
                break;
            case "Weather.ApiKey" :
                playerOpt.weatherApiKey = text;
                break;
            case "Weather.City" :
                playerOpt.weatherCity = text;
                break;
            case "Weather.Display" :
                playerOpt.weatherCityDisplay = text;
                break;
            case "Weather.Unit" :
                playerOpt.weatherUnit = text;
                break;

            case "SlideShow.StoreMenuFile" :
                playerOpt.defaultMenuFile = text;
                break;
            case "Communication.cardServer" :
                playerOpt.optionCardServer = text;
                break;
            case "Communication.cardport" :
                playerOpt.optionCardport = text;
                break;

            default:
                break;
        }
        return playerOpt;

    }

    public PlayerOptionEnv parsePlayerOptionXML(String fileName, PlayerOptionEnv option, Context c){

        try {
            File fXmlFile = new File(fileName);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder dBuilder = factory.newDocumentBuilder();

            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Option");

            for (int temp = 0; temp < nList.getLength(); temp++)
            {
                Node nNode = nList.item(temp);
//                Log.d(TAG, "Current Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;

                    option.optionDefaultLang = eElement.getElementsByTagName(c.getResources().getString(R.string.playerLang)).item(0).getTextContent();

                    option.optionDefaultRegion = eElement.getElementsByTagName(c.getResources().getString(R.string.playerRegion)).item(0).getTextContent();
                    option.optionIntroductionImgFile = eElement.getElementsByTagName(c.getResources().getString(R.string.playerGuidImgFile)).item(0).getTextContent();
                    option.optionPowerPointViewerFile = eElement.getElementsByTagName(c.getResources().getString(R.string.playerPptViewFile)).item(0).getTextContent();
                    option.optionDefaultScheduleFile = eElement.getElementsByTagName(c.getResources().getString(R.string.playerScheduleFile)).item(0).getTextContent();
                    option.optionDefaultMenuFile = eElement.getElementsByTagName(c.getResources().getString(R.string.playerStoreMenuFile)).item(0).getTextContent();
                    option.optionConnectionCheckingRequired = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerConnectionCheck)).item(0).getTextContent());    //default false
                    option.optionDisplayConnectionError = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playervConnectionErrorDisplayed)).item(0).getTextContent());     //default false
                    option.optionCustomExeFile = eElement.getElementsByTagName(c.getResources().getString(R.string.playerCustomExec)).item(0).getTextContent();
                    option.optionCustomExeArgs = eElement.getElementsByTagName(c.getResources().getString(R.string.playerCustomArgs)).item(0).getTextContent();
                    option.optionSleepSecs = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerSleepSecs)).item(0).getTextContent());  //0
                    option.optionKioskStartOnPlayerStartUp = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerKioskStartOnPlayerStartUp)).item(0).getTextContent());     //default false
                    option.optionKioskAutoStartSecs = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerKioskAutoStartSecs)).item(0).getTextContent());    //0
                    option.optionAfterClosingAction = eElement.getElementsByTagName(c.getResources().getString(R.string.playerAfterClosingAction)).item(0).getTextContent();
                    option.optionClosingDelaySecs = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerClosingDelaySecs)).item(0).getTextContent());  //60
                    option.optionMonitorAction = eElement.getElementsByTagName(c.getResources().getString(R.string.playerMonitorAction)).item(0).getTextContent();
                    option.optionRestartHours = eElement.getElementsByTagName(c.getResources().getString(R.string.playerRestartHours)).item(0).getTextContent();
                    option.optionScreenExtMode = eElement.getElementsByTagName(c.getResources().getString(R.string.playerScreenExtMode)).item(0).getTextContent();
                    option.optionTestNoticeDisplayed = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerTestNoticeDisplayed)).item(0).getTextContent());     //default false   //OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
                    option.optionTestNoticeText = eElement.getElementsByTagName(c.getResources().getString(R.string.playerTestNoticeText)).item(0).getTextContent();
                    option.optionTopmostDisplayed = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerTopmostDisplayed)).item(0).getTextContent());     //default false  //OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
                    option.optionTimeSyncRequired = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerTimeSyncRequired)).item(0).getTextContent());      //OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
                    option.optionAllSchedulePlayMode = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerAllSchedulePlayMode)).item(0).getTextContent());   //OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
                    option.optionWebBrowserFile = eElement.getElementsByTagName(c.getResources().getString(R.string.playerWebBrowserFile)).item(0).getTextContent();
                    option.optionSimpleClockDisplayed = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerSimpleClockDisplayed)).item(0).getTextContent());  //OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
                    option.optionSimpleClockDisplayedOnlyOntheHour = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerSimpleClockDisplayedOnlyOntheHour)).item(0).getTextContent()); //OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
                    option.optionSlideProgressBarDisplayed = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerSlideProgressBarDisplayed)).item(0).getTextContent()); //OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
                    option.optionSlideProgressBarColor = eElement.getElementsByTagName(c.getResources().getString(R.string.playerSlideProgressBarColor)).item(0).getTextContent();
                    option.optionOperationInfoDebugged = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerOperationInfoDebugged)).item(0).getTextContent()); //OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
                    option.optionTouchInfoLogged = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerTouchInfoLogged)).item(0).getTextContent());       //OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

                    // jason:transitionfinal: 전환 효과 최종(2015/02/12)
                    option.optionSlideShowFadeInOutApplied = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerDefaultFadeApplied)).item(0).getTextContent());      //OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
                    option.optionSlideShowDefaultFadeTimeType = eElement.getElementsByTagName(c.getResources().getString(R.string.playerDefaultFadeTimeType)).item(0).getTextContent();
                    option.optionSlideShowTransparentBGApplied = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerTransparentBGApplied)).item(0).getTextContent()); //OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
                    option.optionSlideShowDisplayAfterDuration = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerDisplayAfterDuration)).item(0).getTextContent()); //OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
                    //-

                    // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
                    option.optionRoutineSchedPrefix = eElement.getElementsByTagName(c.getResources().getString(R.string.playerRoutineSchedPrefix)).item(0).getTextContent();
                    option.optionAutoDelSchedDays = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerAutoDelSchedDays)).item(0).getTextContent());  //0
                    //-

                    option.timeMonBeginTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerMonBegin)).item(0).getTextContent();
                    option.timeMonEndTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerMonEnd)).item(0).getTextContent();
                    option.timeTueBeginTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerTueBegin)).item(0).getTextContent();
                    option.timeTueEndTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerTueEnd)).item(0).getTextContent();
                    option.timeWedBeginTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerWedBegin)).item(0).getTextContent();
                    option.timeWedEndTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerWedEnd)).item(0).getTextContent();
                    option.timeThuBeginTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerThuBegin)).item(0).getTextContent();
                    option.timeThuEndTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerThuEnd)).item(0).getTextContent();
                    option.timeFriBeginTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerFriBegin)).item(0).getTextContent();
                    option.timeFriEndTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerFriEnd)).item(0).getTextContent();
                    option.timeSatBeginTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerSatBegin)).item(0).getTextContent();
                    option.timeSatEndTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerSatEnd)).item(0).getTextContent();
                    option.timeSunBeginTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerSunBegin)).item(0).getTextContent();
                    option.timeSunEndTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerSunEnd)).item(0).getTextContent();

                    option.optionManagerTcpPort = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerTcpPort)).item(0).getTextContent());    // 11002
                    option.optionStbUdpPort = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerUdpport)).item(0).getTextContent());    //11001

                    option.optionDebugDueTime = eElement.getElementsByTagName(c.getResources().getString(R.string.playerDebugDueTime)).item(0).getTextContent();
                    if(option.optionDebugDueTime==null)
                        option.optionDebugDueTime="";

                    option.optionWeatherCity = eElement.getElementsByTagName(c.getResources().getString(R.string.weatherCity)).item(0).getTextContent();
                    option.optionWeatherCityDisplay = eElement.getElementsByTagName(c.getResources().getString(R.string.weatherDisp)).item(0).getTextContent();
                    option.optionWeatherUnit = eElement.getElementsByTagName(c.getResources().getString(R.string.weatherUnit)).item(0).getTextContent();
                    option.optionWeatherApiKey = eElement.getElementsByTagName(c.getResources().getString(R.string.weatherApiKey)).item(0).getTextContent();

                    option.optionVolume = eElement.getElementsByTagName(c.getResources().getString(R.string.playerVolume)).item(0).getTextContent();

                    option.optionCardServer = eElement.getElementsByTagName(c.getResources().getString(R.string.option_card_server)).item(0).getTextContent();
//                    Log.e(TAG, "option.optionCardServer="+option.optionCardServer);
                    option.optionCardport = eElement.getElementsByTagName(c.getResources().getString(R.string.option_card_port)).item(0).getTextContent();
//                    Log.e(TAG, "1 option.optionCardport="+option.optionCardport);
                    option.optionCardport = eElement.getElementsByTagName("Communication.cardport").item(0).getTextContent();
//                    Log.e(TAG, "2 option.optionCardport="+option.optionCardport);

                }
            }
        }catch (Exception e){
            Log.d(TAG,"5. Error in ParseXML()",e);
            return option;
        }
        return option;
    }

    // jason:multictntmount: 다중 스케줄 탑재(2013/01/21)
//    public static String FileRepositoryName { get; set; }
//    public static boolean FileMountedSeparately { get; set; }

    public static String MountPointFromSchedule(String fullFilename)
    {
        File fInfo = new File(fullFilename);
        String mountdir = "";
        if (fInfo.exists())
        {
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder dBuilder = factory.newDocumentBuilder();

                Document doc = dBuilder.parse(fInfo);

                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("SignCast");

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    Log.d(TAG, "Current Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        mountdir = eElement.getAttribute("mountdir");
                        if((mountdir!=null)&&!mountdir.isEmpty()) {
//                        mountdir = eElement.getElementsByTagName("mountdir").item(0).getTextContent();
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Utils.debugException("SignCastModel.MountPointFromSchedule", ex);
            }
        }
        return mountdir;
    }
    // -
    public StbOptionEnv parseAgentOptionXML(String fileName, StbOptionEnv stbOption, Context c) {

        try {
            File fXmlFile = new File(fileName);

            Log.d(TAG, "parseAgentOptionXML fileName=" + fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder dBuilder = factory.newDocumentBuilder();

            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Option");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Log.d(TAG, "Current Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    stbOption.ftpActiveMode = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.ftpActiveMode)).item(0).getTextContent());
                    stbOption.monitorMins = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.monitorMins)).item(0).getTextContent());
                    stbOption.playerStart = Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.playerStart)).item(0).getTextContent());
                    stbOption.ftpHost = eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpHost)).item(0).getTextContent();
                    stbOption.ftpPassword = eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpPassword)).item(0).getTextContent();
                    stbOption.ftpPort = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpPort)).item(0).getTextContent());
                    stbOption.ftpUser = eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpUser)).item(0).getTextContent();
                    stbOption.serverHost = eElement.getElementsByTagName(c.getResources().getString(R.string.serverserverHost)).item(0).getTextContent();
                    stbOption.serverPort = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverserverPort)).item(0).getTextContent());
                    stbOption.serverUkid = eElement.getElementsByTagName(c.getResources().getString(R.string.serverserverUkid)).item(0).getTextContent();
                    stbOption.stbId = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbId)).item(0).getTextContent());
                    stbOption.stbName = eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbName)).item(0).getTextContent();
                    stbOption.stbServiceType = eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbServiceType)).item(0).getTextContent();
                    stbOption.stbUdpPort = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbUdpPort)).item(0).getTextContent());

                    stbOption.storeName = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_name)).item(0).getTextContent();
                    stbOption.storeAddr = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_addr)).item(0).getTextContent();
                    stbOption.storeBusinessNum = eElement.getElementsByTagName(c.getResources().getString(R.string.server_business_num)).item(0).getTextContent();
                    stbOption.storeTel = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_tel)).item(0).getTextContent();
                    stbOption.storeMerchantNum = eElement.getElementsByTagName(c.getResources().getString(R.string.server_merchant_num)).item(0).getTextContent();
                    stbOption.storeCatId = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_catid)).item(0).getTextContent();
                    stbOption.storeRepresent = eElement.getElementsByTagName(c.getResources().getString(R.string.server_represent)).item(0).getTextContent();
                    stbOption.storeId = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_id)).item(0).getTextContent();
                    stbOption.deviceId = eElement.getElementsByTagName(c.getResources().getString(R.string.server_device_id)).item(0).getTextContent();

                }
            }
        } catch (Exception e) {
            Log.d(TAG, "3. Error in ParseXML()", e);
            return stbOption;
        }
        return stbOption;
    }
    public boolean ParseXML(String fls, Context c, PropUtil prop, StbOptionEnv stbOpt, List<CommandObject> commandList, List<DownFileInfo> downloadList, List<CommandObject> newcommandList) {
        CommandObject cmd = null;
        boolean addCmd = true;
        List<String> optKey = new ArrayList<>();
        List<String> optVal = new ArrayList<>();
        boolean onMakeConfigData = false;
        boolean onMakeConfigDataEdition = false;
        List<String> optEditionKey = new ArrayList<>();
        List<String> optEditionVal = new ArrayList<>();
        Context context = c;
        boolean result = false;

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

//            parser.setInput(fls);
//            InputStream is = getResources().openRawResource(R.raw.agentoptdata);
            InputStream is = new ByteArrayInputStream(fls.getBytes());
            if(is==null) {
                Log.e(TAG, "InputStream... null!!!!");
                return result;
            }

            parser.setInput(is, null);
            int eventType = parser.getEventType();
            boolean isItemTag = false;
            DownFileInfo downloadInfo = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT : {
                        String name = parser.getName();
//                        if (name.equals(getResources().getString(R.string.items)))
                        {
                            if(LOG)
                                Log.e(TAG, "executeSyncContent() XmlPullParser.START_DOCUMENT name="+name);
                        }
                    }
                    break;

                    case XmlPullParser.START_TAG: {
                        String name = parser.getName();
                        //Log.d(TAG, "START_TAG.name="+name);
                        if (name.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server))) {

                            String ref = parser.getAttributeValue(null, "ref");
                            //Log.d(TAG, "ref:" + ref);
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                //Log.d(TAG, "parser.getAttributeName[" + i + "]" + parser.getAttributeName(i));
                                if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.ftpActiveMode))) {
                                    stbOpt.ftpActiveMode = Boolean.valueOf(parser.getAttributeValue(i));
                                    //Log.d(TAG, "FtpActiveMode:" + mStbOpt.ftpActiveMode);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.monitorMins))) {
                                    stbOpt.monitorMins = Integer.valueOf(parser.getAttributeValue(i));
                                    //Log.d(TAG, "MonitorMins:" + mStbOpt.monitorMins);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.playerStart))) {
                                    stbOpt.playerStart = Boolean.parseBoolean(parser.getAttributeValue(i));
                                    //Log.d(TAG, "PlayerStart:" + mStbOpt.playerStart);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.ftpHost))) {
                                    stbOpt.ftpHost = parser.getAttributeValue(i);
                                    //Log.d(TAG, "Server.FtpHost:" + mStbOpt.ftpHost);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.ftpPassword))) {
                                    stbOpt.ftpPassword = parser.getAttributeValue(i);
                                    //Log.d(TAG, "Server.FtpPassword:" + mStbOpt.ftpPassword);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.ftpPort))) {
                                    stbOpt.ftpPort = Integer.valueOf(parser.getAttributeValue(i));
                                    //Log.d(TAG, "Server.FtpPort:" + mStbOpt.ftpPort);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.ftpUser))) {
                                    stbOpt.ftpUser = parser.getAttributeValue(i);
                                    //Log.d(TAG, "Server.FtpUser:" + mStbOpt.ftpUser);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.serverHost))) {
                                    stbOpt.serverHost = parser.getAttributeValue(i);
                                    //Log.d(TAG, "Server.ServerHost:" + mStbOpt.serverHost);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.serverPort))) {
                                    stbOpt.serverPort = Integer.valueOf(parser.getAttributeValue(i));
                                    //Log.d(TAG, "Server.ServerPort:" + mStbOpt.serverPort);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.serverUkid))) {
                                    stbOpt.serverUkid = parser.getAttributeValue(i);
                                    //Log.d(TAG, "Server.ServerUkid:" + mStbOpt.serverUkid);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.stbId))) {
                                    stbOpt.stbId = Integer.valueOf(parser.getAttributeValue(i));
                                    if ((stbOpt.stbId > 0) && (stbOpt.stbStatus == 0))
                                        stbOpt.stbStatus = 5;
                                    //Log.d(TAG, "Server.StbId:" + mStbOpt.stbId);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.stbName))) {
                                    stbOpt.stbName = parser.getAttributeValue(i);
                                    //Log.d(TAG, "Server.StbName:" + mStbOpt.stbName);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.stbServiceType))) {
                                    stbOpt.stbServiceType = parser.getAttributeValue(i);
                                    //Log.d(TAG, "Server.StbServiceType:" + mStbOpt.stbServiceType);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.stbUdpPort))) {
                                    stbOpt.stbUdpPort = Integer.valueOf(parser.getAttributeValue(i));
                                    //Log.d(TAG, "Server.StbUdpPort:" + mStbOpt.stbUdpPort);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_store_name))) {
                                    stbOpt.storeName = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_store_addr))) {
                                    stbOpt.storeAddr = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_business_num))) {
                                    stbOpt.storeBusinessNum = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_store_tel))) {
                                    stbOpt.storeTel = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_merchant_num))) {
                                    stbOpt.storeMerchantNum = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_store_catid))) {
                                    stbOpt.storeCatId = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_represent))) {
                                    stbOpt.storeRepresent = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_store_id))) {
                                    stbOpt.storeId = parser.getAttributeValue(i);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.server_device_id))) {
                                    stbOpt.deviceId = parser.getAttributeValue(i);
                                }
                            }
                            FileUtils.updateFile(FileUtils.BBMC_DIRECTORY + FileUtils.AGENT_OPT_FILE, c, stbOpt);
                        }
                        else if (name.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.properties_tag))) {
                            onMakeConfigData = true;

                        }
                        else if (name.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.editionProperties))) {
                            onMakeConfigDataEdition  = true;
                        }
                        else if (name.equals("command")) {
                            if (parser.getAttributeCount() == 0) {
                                if (stbOpt.stbStatus == 2) {
//에러로 인해 막음.                                    isAdditionalReportRequired = false;
                                }
                                cmd = null;
                            } else
                                cmd = new CommandObject();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
//                                Log.d(TAG, "parser.getAttributeName[" + i + "]" + parser.getAttributeName(i));
                                if (attrName.equals("rcCommandId")) {
                                    cmd.rcCommandid = parser.getAttributeValue(i);
                                } else if (attrName.equals("command")) {
                                    cmd.command = parser.getAttributeValue(i);
                                } else if (attrName.equals("execTime")) {
                                    cmd.execTime = parser.getAttributeValue(i);
                                } else if (attrName.equals("CDATA")) {
                                    cmd.execTime = parser.getAttributeValue(i);
                                }

                            }

                            for (CommandObject cobj : commandList) {
                                if ((cmd.rcCommandid != null) && !cmd.command.isEmpty()) {
                                    if (cobj.rcCommandid == cmd.rcCommandid) {
                                        addCmd = false;
                                        break;
                                    }
                                } else {
                                    addCmd = false;
                                    break;
                                }
                            }
                            if (cmd == null) {
                                addCmd = false;
                            }
                        }else if (name.equals("![CDATA")) {
                            if(LOG)
                                Log.d(TAG, "![CDATA!!  = " + name);
                        }
                        else if (name.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.item))) {
                            downloadInfo = new DownFileInfo();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
//                                Log.d(TAG, "parser.getAttributeName[" + i + "]" + parser.getAttributeName(i));
                                if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.foldername))) {
                                    downloadInfo.folderName = stbOpt.storeCatId+parser.getAttributeValue(i);
//                                    Log.d(TAG, "folderName:" + downloadInfo.folderName);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.filename))) {
                                    downloadInfo.fileName = parser.getAttributeValue(i);
//                                    Log.d(TAG, "fileName:" + downloadInfo.fileName);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.filelength))) {
                                    downloadInfo.fileLength = Long.parseLong(parser.getAttributeValue(i));
//                                    Log.d(TAG, "filelength:" + downloadInfo.fileLength);
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.stbfileid))) {
                                    downloadInfo.stbfileid = Integer.valueOf(parser.getAttributeValue(i));
                                    if (downloadInfo.stbfileid == -1) {
                                        downloadInfo.scheduleContent = false;
                                    } else {
                                        downloadInfo.scheduleContent = true;
                                        downloadInfo.downFileId = downloadInfo.stbfileid;
                                    }
//                                    Log.d(TAG, "stbfileid:" + parser.getAttributeValue(i));
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.kfileid))) {
                                    downloadInfo.kfileid = Integer.valueOf(parser.getAttributeValue(i));
                                    if (downloadInfo.stbfileid == -1)
                                        downloadInfo.downFileId = downloadInfo.kfileid;
//                                    Log.d(TAG, "kfileid:" + parser.getAttributeValue(i));
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.kroot))) {
                                    downloadInfo.kroot = parser.getAttributeValue(i);
//                                    Log.d(TAG, "kroot:" + parser.getAttributeValue(i));
                                } else if (attrName.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.playatonce))) {
                                    downloadInfo.playatonce = parser.getAttributeValue(i);
//                                    Log.d(TAG, "playatonce:" + parser.getAttributeValue(i));
                                }
                            }
                            boolean addDown = true;
                            for (DownFileInfo d : downloadList) {
                                if (d.fileName.equals(downloadInfo.fileName) && d.folderName.equals(downloadInfo.folderName)) {
                                    addDown = false;
                                    break;
                                }
                            }
                            if (addDown) {
                                if ((downloadInfo.fileName != null) && (downloadInfo.fileLength > 0))
                                    downloadList.add(downloadInfo);
                            }
/*
                            Log.e(TAG, "1 executeSyncContent() checkContentFileExistence()");
                            checkContentFileExistence();
*/
                        }
                        else if(name.equals(c.getString(kr.co.bbmc.selforderutil.R.string.updateFTPServerURL))) {
                            if(onMakeConfigData)
                            {
                                optKey.add(name);
                            }
/*
                            HashMap<String, String> options = new HashMap();
                            String temp = (String)parser.getProperty(name);

                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                options.put(name, parser.getAttributeValue(i));
                            }
                            options.put(name,temp);
                            mPropUtil.onSaveConfigFile(options, null);
*/
                        }
                        else if(name.equals(c.getString(kr.co.bbmc.selforderutil.R.string.updateInfoURL))){
                            if(onMakeConfigData)
                            {
                                optKey.add(name);
                            }
                        }
                        else
                        {
                            if(onMakeConfigData)
                            {
                                optKey.add(name);
                            }
                            else if(onMakeConfigDataEdition)
                            {
                                optEditionKey.add(name);
                            }

                        }
                    }
                    break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (LOG)
                            Log.d(TAG, "TEXT = " + text);
                        if ((text != null) && !text.isEmpty()) {
                            if (cmd != null)
                                cmd.prameter = text;
                            else if(onMakeConfigData)
                            {
                                optVal.add(text);
                            }
                            else if(onMakeConfigDataEdition)
                            {
                                optEditionVal.add(text);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String name = parser.getName();
                        if (LOG)
                            Log.d(TAG, "END_TAG.name=" + name + "");
                        if (name.equals("command")) {
                            if (cmd != null) {
                                if (addCmd) {
                                    if (newcommandList == null)
                                        newcommandList = new ArrayList<>();
                                    newcommandList.add(cmd);
                                    Utils.LOG("--------------------");
                                    Utils.LOG(c.getString(kr.co.bbmc.selforderutil.R.string.Log_CmdAdditionalCommand) + " #: " + cmd.rcCommandid);
                                    Utils.LOG(c.getString(kr.co.bbmc.selforderutil.R.string.Log_CmdCommandName) + ": " + cmd.command);
                                    Utils.LOG(c.getString(kr.co.bbmc.selforderutil.R.string.Log_CmdExecTime) + ": " + cmd.execTime);
                                    Utils.LOG(c.getString(kr.co.bbmc.selforderutil.R.string.Log_CmdParameter) + ": " + cmd.prameter);
                                    Utils.LOG(c.getString(kr.co.bbmc.selforderutil.R.string.Log_CmdTotalCommandCount) + ": " + newcommandList.size());
                                    Utils.LOG("--------------------");
                                }
                            }
                        }
                        else if(name.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.properties_tag))) {
                            onMakeConfigData = false;
                        }
                        else if (name.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.editionProperties))) {
                            onMakeConfigDataEdition = false;
                        }
                        else if (name.equals("SignCast")) {
                            HashMap<String, String> options = new HashMap();
                            HashMap<String, String> optionsEdition = new HashMap();
                            if(optKey.size()>0) {
                                for (int i = 0; (i < optKey.size()) && (i < optVal.size()); i++)
                                    options.put(optKey.get(i), optVal.get(i));
                            }
                            if(optEditionKey.size()>0 ){
                                for (int i = 0; (i < optEditionKey.size()) && (i < optEditionVal.size()); i++)
                                    optionsEdition.put(optEditionKey.get(i), optEditionVal.get(i));
                            }

                            prop.onSaveConfigFile(options, optionsEdition);

                        }
                        else if (name.equals(c.getResources().getString(kr.co.bbmc.selforderutil.R.string.items))) {
                            Log.e(TAG, "1 executeSyncContent() checkContentFileExistence()");
//agent 에서는 막음.                            checkContentFileExistence();
                        }
                        break;
                    default:
                        Log.d(TAG, "EVENT TYPE = " + eventType);
                        break;
                }
//                Log.d(TAG, "parser.NEXT="+parser.nextToken());
                eventType = parser.next();

            }
            result = true;


        } catch (Exception e) {
            Log.d(TAG, "2 Error in ParseXML()", e);
            result = false;
        }
        return result;

    }

    public MenuCatagoryObject getStoreCatagory(XmlPullParser pullParser, MenuCatagoryObject env) {
        MenuCatagoryObject optionEnv = env;
        for(int i = 0; i < pullParser.getAttributeCount(); i++)
        {
            if(LOG)
                Log.d(TAG, "startTag- attribute["+i+"]="+pullParser.getAttributeName(i)+" value="+pullParser.getAttributeValue(i));
            switch(pullParser.getAttributeName(i))
            {
                case "storeId" :
                    if(LOG)
                        Log.d(TAG, "case storeId");
                    optionEnv.storeId = pullParser.getAttributeValue(i);
                    break;

                case "storename" :
                    if(LOG)
                        Log.d(TAG, "case storename");
                    optionEnv.storename = pullParser.getAttributeValue(i);
                    break;
                case "storeimage" :
                    if(LOG)
                        Log.d(TAG, "case storeimage");
                    optionEnv.storeImage = pullParser.getAttributeValue(i);
                    break;
                case "menutype" :
                    if(LOG)
                        Log.d(TAG, "case menutype");
                    optionEnv.menutype = pullParser.getAttributeValue(i);
                    break;
                case "background" :
                    if(LOG)
                        Log.d(TAG, "case background");
                    optionEnv.storebackground = pullParser.getAttributeValue(i);
                    break;
                case "catatorynum" :
                    if(LOG)
                        Log.d(TAG, "case catatorynum");
                    optionEnv.catagorynum = pullParser.getAttributeValue(i);
                    break;
                default:
                    break;
            }
        }
        return optionEnv;
    }
    public MenuObject getMenuItem( XmlPullParser pullParser, MenuObject env)  {
        MenuObject optionEnv = env;
        for(int i = 0; i < pullParser.getAttributeCount(); i++)
        {
            if(LOG)
            Log.d(TAG, "startTag- attribute["+i+"]="+pullParser.getAttributeName(i)+" value="+pullParser.getAttributeValue(i));
            switch(pullParser.getAttributeName(i))
            {
                case "id" :
                    if(LOG)
                        Log.d(TAG, "case id");
                    optionEnv.productId =pullParser.getAttributeValue(i);
                    break;
                case "seq" :
                    if(LOG)
                        Log.d(TAG, "case seq");
                    optionEnv.seq =pullParser.getAttributeValue(i);
                    break;
                case "name" :
                    if(LOG)
                        Log.d(TAG, "case name");
                    optionEnv.name = pullParser.getAttributeValue(i);
                    break;
                case "price" :
                    if(LOG)
                        Log.d(TAG, "case price");
                    optionEnv.price = pullParser.getAttributeValue(i);
                case "exchange" :
                    if(LOG)
                        Log.d(TAG, "case exchange");
                    optionEnv.exchange = pullParser.getAttributeValue(i);
                    break;

                case "file" :
                    if(LOG)
                        Log.d(TAG, "case file");
                    optionEnv.imagefile = pullParser.getAttributeValue(i);
                    break;
                case "description" :
                    if(LOG)
                        Log.d(TAG, "case description");
                    optionEnv.description = pullParser.getAttributeValue(i);
                    break;
                case "popular" :
                    if(LOG)
                        Log.d(TAG, "case popular");
                    optionEnv.popular = pullParser.getAttributeValue(i);
                    break;
                case "newmenu" :
                    if(LOG)
                        Log.d(TAG, "case newmenu");
                    optionEnv.newmenu = pullParser.getAttributeValue(i);
                    break;
                case "soldout" :
                    if(LOG)
                        Log.d(TAG, "case soldout");
                    optionEnv.soldout = pullParser.getAttributeValue(i);
                    break;
                default:
                break;
            }
        }
        return optionEnv;
    }

    public CatagoryObject getMenuCatagory(XmlPullParser pullParser, CatagoryObject env)  {
        CatagoryObject optionEnv = env;
        for(int i = 0; i < pullParser.getAttributeCount(); i++)
        {
            if(LOG)
                Log.d(TAG, "startTag- attribute["+i+"]="+pullParser.getAttributeName(i)+" value="+pullParser.getAttributeValue(i));
            switch(pullParser.getAttributeName(i))
            {
                case "seq" :
                    if(LOG)
                        Log.d(TAG, "case width");
                    optionEnv.seq = pullParser.getAttributeValue(i);
                    break;
                case "name" :
                    if(LOG)
                        Log.d(TAG, "case name");
                    optionEnv.name = pullParser.getAttributeValue(i);
                    break;
                case "image" :
                    if(LOG)
                        Log.d(TAG, "case image");
                    optionEnv.image = pullParser.getAttributeValue(i);
                    break;
                case "menunum" :
                    if(LOG)
                        Log.d(TAG, "case menunum");
                    optionEnv.menuNum = pullParser.getAttributeValue(i);
                    break;

                default:
                    break;
            }
        }
        return optionEnv;
    }
    public ArrayList<KioskOrderInfoForPrint> onParseOrderMenuPrintJson(String orderPrtStr) throws JSONException {
        ArrayList<KioskOrderInfoForPrint> orderInfoList = new  ArrayList<KioskOrderInfoForPrint>();
        JSONArray json = new JSONArray(orderPrtStr);
        if((json==null)||(json.length()==0))
            return null;
        for (int i = 0; i <json.length(); i++)
        {
            JSONObject order = json.getJSONObject(i);
            KioskOrderInfoForPrint printMenu = new KioskOrderInfoForPrint();

            printMenu.recommandId =  order.getString("recommand");
            if(printMenu.recommandId.equals("0"))
            {
                return orderInfoList;
            }
            printMenu.goodsAmt =  order.getString("goodsAmt");
            printMenu.orderDate =  order.getString("orderDate");
            printMenu.orderNumber =  order.getString("orderNumber");
            printMenu.storeName =  order.getString("storeName");
            if(printMenu.menuItems==null)
                printMenu.menuItems = new ArrayList<KioskOrderInfoForPrint.OrderMenuItem>();
            JSONArray jsonMenu =  order.getJSONArray("orderMenu");
            for(int j =0; j<jsonMenu.length(); j++)
            {
                JSONObject menu = jsonMenu.getJSONObject(j);
                KioskOrderInfoForPrint.OrderMenuItem item = new KioskOrderInfoForPrint.OrderMenuItem();

                item.orderPrice =  menu.getString("orderPrice");
                item.productName =  menu.getString("productName");
                item.productId =  menu.getString("productID");
                item.orderCount =  menu.getString("orderCount");
                printMenu.menuItems.add(item);
            }
            orderInfoList.add(printMenu);
        }
        return orderInfoList;

    }
    public DidOptionEnv parseDidAgentOptionXML(String fileName, DidOptionEnv didOption, Context c) {

        try {
            File fXmlFile = new File(fileName);

            Log.d(TAG, "parseAgentOptionXML fileName=" + fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder dBuilder = factory.newDocumentBuilder();

            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Option");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Log.d(TAG, "Current Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    didOption.serverHost = eElement.getElementsByTagName(c.getResources().getString(R.string.serverserverHost)).item(0).getTextContent();
                    didOption.serverPort = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverserverPort)).item(0).getTextContent());
                    didOption.serverUkid = eElement.getElementsByTagName(c.getResources().getString(R.string.serverserverUkid)).item(0).getTextContent();
                    didOption.stbServiceType = eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbServiceType)).item(0).getTextContent();
                    didOption.stbUdpPort = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbUdpPort)).item(0).getTextContent());
                    didOption.stbServiceType = eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbServiceType)).item(0).getTextContent();
                    didOption.storeId = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_id)).item(0).getTextContent();
                    didOption.deviceId = eElement.getElementsByTagName(c.getResources().getString(R.string.server_device_id)).item(0).getTextContent();


                    didOption.stbId = Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbId)).item(0).getTextContent());
                    didOption.stbName= eElement.getElementsByTagName(c.getResources().getString(R.string.serverstbName)).item(0).getTextContent();;

                    didOption.ftpActiveMode =Boolean.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.ftpActiveMode)).item(0).getTextContent());
                    didOption.ftpHost=eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpHost)).item(0).getTextContent();
                    didOption.ftpPort=Integer.valueOf(eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpPort)).item(0).getTextContent());
                    didOption.ftpUser=eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpUser)).item(0).getTextContent();
                    didOption.ftpPassword=eElement.getElementsByTagName(c.getResources().getString(R.string.serverftpPassword)).item(0).getTextContent();

                    didOption.storeName = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_name)).item(0).getTextContent(); //매장명
                    didOption.storeAddr = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_addr)).item(0).getTextContent();  //매장 주소
                    didOption.storeBusinessNum = eElement.getElementsByTagName(c.getResources().getString(R.string.server_business_num)).item(0).getTextContent(); //매장사업자 번호
                    didOption.storeTel = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_tel)).item(0).getTextContent();  //매장 전화번호
                    didOption.storeMerchantNum = eElement.getElementsByTagName(c.getResources().getString(R.string.server_merchant_num)).item(0).getTextContent(); //매장 가맹점 번호
                    didOption.storeCatId = eElement.getElementsByTagName(c.getResources().getString(R.string.server_store_catid)).item(0).getTextContent();  //kiosk 카드 단말기 cat id
                    didOption.storeRepresent = eElement.getElementsByTagName(c.getResources().getString(R.string.server_represent)).item(0).getTextContent();  //매장 대표자 명

                }
            }
        } catch (Exception e) {
            Log.d(TAG, "3. Error in ParseXML()", e);
            return didOption;
        }
        return didOption;
    }
}

