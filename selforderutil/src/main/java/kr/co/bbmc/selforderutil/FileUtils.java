package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static final int Manager = 0;
    public static final int Player = 1;
    public static final int Agent = 2;
    public static final int None = 3;
    public static final int SearchKeyword = 4;
    public static final int Holiday = 5;
    public static final int PayCastAgent = 6;
    public static final int PayCastPlayer = 7;
    public static final int PayCastDid = 8;
    public static final int PayCastPad = 9;

    public static String BBMC_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//DATA//";
    public static String BBMC_SCH_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//Schedule//";
    public static String BBMC_DEFAULT_DIR = Environment.getExternalStorageDirectory()+"//BBMC//";
    public static String BBMC_REPORT_DIR = Environment.getExternalStorageDirectory()+"//BBMC//Report//";
    public static String BBMC_LOG_DIR = Environment.getExternalStorageDirectory()+"//BBMC//Log//";
    public static String BBMC_DEBUG_DIR = Environment.getExternalStorageDirectory()+"//BBMC//Debug//";
    public static String ScheduleRootFolder = BBMC_DEFAULT_DIR + "Schedule/";
    public static String BBMC_CONTENT_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//Content//";
    public static String BBMC_COMMAND_DIR = Environment.getExternalStorageDirectory()+"//BBMC//Command//";
    public static String BBMC_TEMP_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//Temp//";

    public static String BBMC_AGENTPRINT_DIR = Environment.getExternalStorageDirectory()+"//BBMC//PAYCAST//AgentPrt//";
    public static String BBMC_PAYCAST_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//PAYCAST//";
    public static String BBMC_PAYCAST_DATA_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//PAYCAST//DATA//";
    public static String BBMC_PAYCAST_MENU_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//PAYCAST//Menu//";
    public static String BBMC_PAYCAST_CONTENT_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//PAYCAST//Content//";
    public static String BBMC_PAYCAST_BG_DIRECTORY = Environment.getExternalStorageDirectory()+"//BBMC//PAYCAST//background//";

    public static String AGENT_OPT_FILE = "PayCastAgentOpt.xml";
    public static String PLAYER_OPT_FILE = "PlayerOptData.xml";


    public static File makeDirectory(String dir_path){
        File dir = new File(dir_path);
        if (!dir.exists())
        {
            dir.mkdirs();
            Log.i( TAG , "!dir.exists" );
        }else{
            Log.i( TAG , "dir.exists" );
        }

        return dir;
    }
    /**
     * 파일 생성
     * @param dir
     * @return file
     */
    public static File makeDidAgentOptionFile(File dir, String file_path, Context c, DidOptionEnv option){
        File file = null;
        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(file_path);
            if(file!=null&&!file.exists())
            {
                Log.i( TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                    FileOutputStream fileos = null;
                    try{
                        fileos = new FileOutputStream(file);

                        writeXmlDidAgentOptionData(fileos, c, option);

                    }catch(FileNotFoundException e)
                    {
                        Log.e("FileNotFoundException",e.toString());
                    }
                }
            }else{
                Log.i( TAG , "file.exists" );
            }
        }
        return file;
    }

    /**
     * 파일 생성
     * @param dir
     * @return file
     */
    public static File makeAgentOptionFile(File dir, String file_path, Context c, StbOptionEnv option){
        File file = null;
        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(file_path);
            if(file!=null&&!file.exists())
            {
                Log.i( TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                    FileOutputStream fileos = null;
                    try{
                        fileos = new FileOutputStream(file);

                        writeXmlAgentOptionData(fileos, c, option);

                    }catch(FileNotFoundException e)
                    {
                        Log.e("FileNotFoundException",e.toString());
                    }
                }
            }else{
                Log.i( TAG , "file.exists" );
            }
        }
        return file;
    }
    public static File makePlayerOptionFile(File dir, String file_path, Context c, PlayerOptionEnv option){
        File file = null;
        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(file_path);
            if(file!=null&&!file.exists())
            {
                Log.i( TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                    FileOutputStream fileos = null;
                    try{
                        if(isSuccess==false)
                            file = new File(file_path);
                        fileos = new FileOutputStream(file);
                        writeXmlPlayerOptionData(fileos, c, option);

                    }catch(FileNotFoundException e)
                    {
                        Log.e("FileNotFoundException",e.toString());
                    }
                }
            }else{
                Log.i( TAG , "file.exists" );
            }
        }
        return file;
    }
    public static File updatePlayerOptionFile(File dir, String file_path, Context c, PlayerOptionEnv option){
        File file = null;
        FileOutputStream fileos = null;
        if(dir.isDirectory()){
            file = new File(file_path);
            try {
                fileos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writeXmlPlayerOptionData(fileos, c, option);
        }
        return file;
    }
    public File makeHolidayDataFile(File dir , String file_path, Context c, PlayerOptionEnv option){
        File file = null;
        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(file_path);
            if(file!=null&&!file.exists())
            {
                Log.i( TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                    FileOutputStream fileos = null;
                    try{
                        fileos = new FileOutputStream(file);

                        writeXmlHolidayData(fileos, c, option);

                    }catch(FileNotFoundException e)
                    {
                        Log.e("FileNotFoundException",e.toString());
                    }
                }
            }else{
                Log.i( TAG , "file.exists" );
            }
        }
        return file;
    }
    public static void updateFile(String file_path, Context c, DidOptionEnv option) throws FileNotFoundException {
        File file = null;
        file = new File(file_path);
        if(file!=null&&file.exists())
        {
            Log.d( TAG , "file.exists" );
            FileOutputStream fileos = null;
            fileos = new FileOutputStream(file);

            writeXmlDidAgentOptionData(fileos, c, option);

        }else{
            Log.d( TAG , "file not exists" );
        }
    }

    public static void updateFile(String file_path, Context c, StbOptionEnv option) throws FileNotFoundException {
        File file = null;
        file = new File(file_path);
        if(file!=null&&file.exists())
        {
            Log.d( TAG , "file.exists" );
            FileOutputStream fileos = null;
            fileos = new FileOutputStream(file);

            writeXmlAgentOptionData(fileos, c, option);

        }else{
            Log.d( TAG , "file not exists" );
        }
    }
    public static void writeXmlDidAgentOptionData (FileOutputStream fs, Context c, DidOptionEnv option)
    {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTimeStr = String.format(sdfNow.format(new Date(System.currentTimeMillis())));
        try
        {
            FileOutputStream fileos = fs;
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startTag(null, "SignCast");
            xmlSerializer.attribute(null, "generated", nowTimeStr);
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Option");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.ftpActiveMode));
            xmlSerializer.text(String.valueOf(option.ftpActiveMode));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.ftpActiveMode));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpHost));
            xmlSerializer.text(option.ftpHost);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpHost));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpPassword));
            xmlSerializer.text(option.ftpPassword);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpPassword));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpPort));
            xmlSerializer.text(String.valueOf(option.ftpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpPort));
            xmlSerializer.text("\n");

            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpUser));
            xmlSerializer.text(option.ftpUser);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpUser));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverserverHost));
            xmlSerializer.text(option.serverHost);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverserverHost));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverserverPort));
            xmlSerializer.text(String.valueOf(option.serverPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverserverPort));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverserverUkid));
            xmlSerializer.text(option.serverUkid);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverserverUkid));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbId));
            xmlSerializer.text(String.valueOf(option.stbId));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbId));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbName));
            xmlSerializer.text(option.stbName);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbName));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbServiceType));
            xmlSerializer.text(option.stbServiceType);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbServiceType));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbUdpPort));
            xmlSerializer.text(String.valueOf(option.stbUdpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbUdpPort));
            xmlSerializer.text("\n");

            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_name));
            xmlSerializer.text(option.storeName);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_name));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_addr));
            xmlSerializer.text(option.storeAddr);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_addr));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_business_num));
            xmlSerializer.text(option.storeBusinessNum);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_business_num));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_tel));
            xmlSerializer.text(option.storeTel );
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_tel));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_merchant_num));
            xmlSerializer.text(option.storeMerchantNum);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_merchant_num));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_catid));
            xmlSerializer.text(option.storeCatId);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_catid));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_represent));
            xmlSerializer.text(option.storeRepresent);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_represent));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_id));
            xmlSerializer.text(option.storeId);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_id));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.store_operating_time));
            xmlSerializer.text(option.operatingTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.store_operating_time));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.store_introduction_msg));
            xmlSerializer.text(option.introMsg);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.store_introduction_msg));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_device_id));
            xmlSerializer.text(option.deviceId);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_device_id));

            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "Option");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "SignCast");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeXmlAgentOptionData (FileOutputStream fs, Context c, StbOptionEnv option)
    {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTimeStr = String.format(sdfNow.format(new Date(System.currentTimeMillis())));
        try
        {
            FileOutputStream fileos = fs;
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            //xmlSerializer.startDocument("UTF-8", true);
//            xmlSerializer.startTag("SignCast", " generated=\""+nowTimeStr+"\"");
            xmlSerializer.startTag(null, "SignCast");
            xmlSerializer.attribute(null, "generated", nowTimeStr);
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Option");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.ftpActiveMode));
            xmlSerializer.text(String.valueOf(option.ftpActiveMode));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.ftpActiveMode));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.monitorMins));
            xmlSerializer.text(String.valueOf(option.monitorMins));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.monitorMins));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerStart));
            xmlSerializer.text(String.valueOf(option.playerStart));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerStart));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpHost));
            xmlSerializer.text(option.ftpHost);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpHost));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpPassword));
            xmlSerializer.text(option.ftpPassword);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpPassword));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpPort));
            xmlSerializer.text(String.valueOf(option.ftpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpPort));
            xmlSerializer.text("\n");

            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverftpUser));
            xmlSerializer.text(option.ftpUser);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverftpUser));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverserverHost));
            xmlSerializer.text(option.serverHost);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverserverHost));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverserverPort));
            xmlSerializer.text(String.valueOf(option.serverPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverserverPort));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverserverUkid));
            xmlSerializer.text(option.serverUkid);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverserverUkid));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbId));
            xmlSerializer.text(String.valueOf(option.stbId));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbId));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbName));
            xmlSerializer.text(option.stbName);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbName));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbServiceType));
            xmlSerializer.text(option.stbServiceType);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbServiceType));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.serverstbUdpPort));
            xmlSerializer.text(String.valueOf(option.stbUdpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.serverstbUdpPort));
            xmlSerializer.text("\n");

            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_name));
            xmlSerializer.text(option.storeName);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_name));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_addr));
            xmlSerializer.text(option.storeAddr);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_addr));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_business_num));
            xmlSerializer.text(option.storeBusinessNum);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_business_num));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_tel));
            xmlSerializer.text(option.storeTel );
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_tel));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_merchant_num));
            xmlSerializer.text(option.storeMerchantNum);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_merchant_num));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_catid));
            xmlSerializer.text(option.storeCatId);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_catid));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_represent));
            xmlSerializer.text(option.storeRepresent);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_represent));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_store_id));
            xmlSerializer.text(option.storeId);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_store_id));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.store_operating_time));
            xmlSerializer.text(option.operatingTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.store_operating_time));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.store_introduction_msg));
            xmlSerializer.text(option.introMsg);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.store_introduction_msg));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.server_device_id));
            xmlSerializer.text(option.deviceId);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.server_device_id));

            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "Option");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "SignCast");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*  Holiday write   */
    public static void writeXmlHolidayData (FileOutputStream fs, Context c, PlayerOptionEnv option)
    {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTimeStr = String.format(sdfNow.format(new Date(System.currentTimeMillis())));
        try
        {
            FileOutputStream fileos = fs;
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
//            xmlSerializer.startTag(null, "SignCast generated=\""+nowTimeStr+"\"");
            xmlSerializer.startTag(null, "SignCast");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Holidays");
            xmlSerializer.attribute("Holiday", "date", "2018-03-31");
            xmlSerializer.endTag(null, "Holidays");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "SignCast");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeXmlMenuOrderReceipt (FileOutputStream fs, Context c, ArrayList list, String tprice, ArrayList sellerInfo, PaymentInfoData payInfo, String orderNum)
    {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTimeStr = null;
        nowTimeStr = sdfNow.format(payInfo.tradingDate);
        try
        {
            FileOutputStream fileos = fs;
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            //xmlSerializer.startDocument("UTF-8", true);
//            xmlSerializer.startTag("SignCast", " generated=\""+nowTimeStr+"\"");
            xmlSerializer.startTag(null, "PayCast");
            xmlSerializer.attribute(null, "generated", nowTimeStr);
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Print");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "CardName");
            xmlSerializer.text(String.format("%s %s", payInfo.issueerName, payInfo.acquirerName) );
            xmlSerializer.endTag(null,"CardName");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "CardNumber");
            xmlSerializer.text(payInfo.cardNumber);
            xmlSerializer.endTag(null, "CardNumber");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "CATID");
            xmlSerializer.text(payInfo.catId);
            xmlSerializer.endTag(null, "CATID");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "ApprovealNum");
            xmlSerializer.text(payInfo.approvalNum);
            xmlSerializer.endTag(null, "ApprovealNum");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "InstallmentMonth");
            xmlSerializer.text(payInfo.installmentMonth);
            xmlSerializer.endTag(null, "InstallmentMonth");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "TradingDate");
            xmlSerializer.text(nowTimeStr);
            xmlSerializer.endTag(null, "TradingDate");
            xmlSerializer.text("\n");

            xmlSerializer.startTag(null, "SupplyPrice");
            xmlSerializer.text(String.valueOf(Integer.valueOf(tprice) - Integer.valueOf(payInfo.payVat)));
            xmlSerializer.endTag(null, "SupplyPrice");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "VatPrice");
            xmlSerializer.text(payInfo.payVat);
            xmlSerializer.endTag(null, "VatPrice");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "TotalPrice");
            xmlSerializer.text(tprice);
            xmlSerializer.endTag(null, "TotalPrice");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "OrderNumber");
            xmlSerializer.text(orderNum);
            xmlSerializer.endTag(null, "OrderNumber");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "OrderItmes");
            for(int i = 0; i<list.size(); i++){
                DataModel order = (DataModel) list.get(i);
                String count= String.valueOf(order.count);

                xmlSerializer.attribute(null, "menuname", order.text);
                xmlSerializer.attribute(null, "count", count);
                xmlSerializer.attribute(null, "eachPrice", order.itemprice);
                xmlSerializer.attribute(null, "price", order.price);
            }
            xmlSerializer.endTag(null, "OrderItmes");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "Print");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "PayCast");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeXmlPlayerOptionData (FileOutputStream fs, Context c, PlayerOptionEnv option)
    {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTimeStr = String.format(sdfNow.format(new Date(System.currentTimeMillis())));
        try
        {
            FileOutputStream fileos = fs;
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
//            xmlSerializer.startTag(null, "SignCast generated=\""+nowTimeStr+"\"");
            xmlSerializer.startTag(null, "SignCast");
            xmlSerializer.attribute(null, "generated", nowTimeStr);
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Option");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTcpPort));
            xmlSerializer.text(String.valueOf(option.optionManagerTcpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTcpPort));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerUdpport));
            xmlSerializer.text(String.valueOf(option.optionStbUdpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerUdpport));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerLang));
            xmlSerializer.text(String.valueOf(option.optionDefaultLang));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerLang));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerFriBegin));
            xmlSerializer.text(option.timeFriBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerFriBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerFriEnd));
            xmlSerializer.text(option.timeFriEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerFriEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSatBegin));
            xmlSerializer.text(option.timeSatBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSatBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSatEnd));
            xmlSerializer.text(option.timeSatEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSatEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSunBegin));
            xmlSerializer.text(option.timeSunBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSunBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSunEnd));
            xmlSerializer.text(option.timeSunEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSunEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerMonBegin));
            xmlSerializer.text(option.timeMonBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerMonBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerMonEnd));
            xmlSerializer.text(option.timeMonEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerMonEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTueBegin));
            xmlSerializer.text(option.timeTueBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTueBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTueEnd));
            xmlSerializer.text(option.timeTueEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTueEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerWedBegin));
            xmlSerializer.text(option.timeWedBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerWedBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerWedEnd));
            xmlSerializer.text(option.timeWedEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerWedEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerThuBegin));
            xmlSerializer.text(option.timeThuBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerThuBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerThuEnd));
            xmlSerializer.text(option.timeThuEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerThuEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerRegion));
            xmlSerializer.text(option.optionDefaultRegion);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerRegion));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerAfterClosingAction));
            xmlSerializer.text(String.valueOf(option.optionAfterClosingAction));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerAfterClosingAction));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerAllSchedulePlayMode));
            xmlSerializer.text(String.valueOf(option.optionAllSchedulePlayMode));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerAllSchedulePlayMode));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerAutoDelSchedDays));
            xmlSerializer.text(String.valueOf(option.optionAutoDelSchedDays));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerAutoDelSchedDays));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerClosingDelaySecs));
            xmlSerializer.text(String.valueOf(option.optionClosingDelaySecs));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerClosingDelaySecs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerConnectionCheck));
            xmlSerializer.text(String.valueOf(option.optionConnectionCheckingRequired));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerConnectionCheck));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playervConnectionErrorDisplayed));
            xmlSerializer.text(String.valueOf(option.optionDisplayConnectionError));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playervConnectionErrorDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerCustomArgs));
            xmlSerializer.text(option.optionCustomExeArgs);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerCustomArgs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerCustomExec));
            xmlSerializer.text(option.optionCustomExeFile);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerCustomExec));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDefaultFadeApplied));
            xmlSerializer.text(String.valueOf(option.optionSlideShowFadeInOutApplied));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDefaultFadeApplied));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDefaultFadeTimeType));
            xmlSerializer.text(String.valueOf(option.optionSlideShowDefaultFadeTimeType));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDefaultFadeTimeType));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDisplayAfterDuration));
            xmlSerializer.text(String.valueOf(option.optionSlideShowDisplayAfterDuration));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDisplayAfterDuration));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerGuidImgFile));
            xmlSerializer.text(String.valueOf(option.optionIntroductionImgFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerGuidImgFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerKioskAutoStartSecs));
            xmlSerializer.text(String.valueOf(option.optionKioskAutoStartSecs));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerKioskAutoStartSecs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerKioskStartOnPlayerStartUp));
            xmlSerializer.text(String.valueOf(option.optionKioskStartOnPlayerStartUp));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerKioskStartOnPlayerStartUp));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerMonitorAction));
            xmlSerializer.text(String.valueOf(option.optionMonitorAction));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerMonitorAction));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerOperationInfoDebugged));
            xmlSerializer.text(String.valueOf(option.optionOperationInfoDebugged));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerOperationInfoDebugged));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerPptViewFile));
            xmlSerializer.text(String.valueOf(option.optionPowerPointViewerFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerPptViewFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerRestartHours));
            xmlSerializer.text(String.valueOf(option.optionRestartHours));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerRestartHours));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerRoutineSchedPrefix));
            xmlSerializer.text(String.valueOf(option.optionRoutineSchedPrefix));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerRoutineSchedPrefix));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerScheduleFile));
            xmlSerializer.text(String.valueOf(option.optionDefaultScheduleFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerScheduleFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerScreenExtMode));
            xmlSerializer.text(String.valueOf(option.optionScreenExtMode));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerScreenExtMode));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayed));
            xmlSerializer.text(String.valueOf(option.optionSimpleClockDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayedOnlyOntheHour));
            xmlSerializer.text(String.valueOf(option.optionSimpleClockDisplayedOnlyOntheHour));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayedOnlyOntheHour));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSleepSecs));
            xmlSerializer.text(String.valueOf(option.optionSleepSecs));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSleepSecs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSlideProgressBarColor));
            xmlSerializer.text(String.valueOf(option.optionSlideProgressBarColor));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSlideProgressBarColor));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSlideProgressBarDisplayed));
            xmlSerializer.text(String.valueOf(option.optionSlideProgressBarDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSlideProgressBarDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTestNoticeDisplayed));
            xmlSerializer.text(String.valueOf(option.optionTestNoticeDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTestNoticeDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTestNoticeText));
            xmlSerializer.text(String.valueOf(option.optionTestNoticeText));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTestNoticeText));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTimeSyncRequired));
            xmlSerializer.text(String.valueOf(option.optionTimeSyncRequired));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTimeSyncRequired));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTopmostDisplayed));
            xmlSerializer.text(String.valueOf(option.optionTopmostDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTopmostDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTouchInfoLogged));
            xmlSerializer.text(String.valueOf(option.optionTouchInfoLogged));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTouchInfoLogged));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTransparentBGApplied));
            xmlSerializer.text(String.valueOf(option.optionSlideShowTransparentBGApplied));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTransparentBGApplied));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerWebBrowserFile));
            xmlSerializer.text(String.valueOf(option.optionWebBrowserFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerWebBrowserFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDebugDueTime));
            xmlSerializer.text(String.valueOf(option.optionDebugDueTime));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDebugDueTime));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerVolume));
            xmlSerializer.text(String.valueOf(option.optionVolume));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerVolume));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherApiKey));
            xmlSerializer.text(String.valueOf(option.optionWeatherApiKey));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherApiKey));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherCity));
            xmlSerializer.text(String.valueOf(option.optionWeatherCity));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherCity));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherDisp));
            xmlSerializer.text(String.valueOf(option.optionWeatherCityDisplay));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherDisp));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherUnit));
            xmlSerializer.text(String.valueOf(option.optionWeatherUnit));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherUnit));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.option_card_server));
            xmlSerializer.text(String.valueOf(option.optionCardServer));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.option_card_server));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.option_card_port));
            xmlSerializer.text(String.valueOf(option.optionCardport));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.option_card_port));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerStoreMenuFile));
            xmlSerializer.text(String.valueOf(option.optionDefaultMenuFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerStoreMenuFile));
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "Option");
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "SignCast");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 파일에 내용 쓰기
     * @param file
     * @param file_content
     * @return
     */
    public boolean writeFile(File file , byte[] file_content){
        boolean result;
        FileOutputStream fos;
        if(file!=null&&file.exists()&&file_content!=null){
            try {
                fos = new FileOutputStream(file);
                try {
                    fos.write(file_content);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }
    public static String getFilename(int currentOptType)
    {
        String filename = "";

        if (currentOptType == Manager)
        {
            filename = "ManagerOptData.xml";
        }
        else if (currentOptType == Player)
        {
            filename = "PlayerOptData.xml";
        }
        else if (currentOptType == Agent)
        {
            filename = "AgentOptData.xml";
        }
        else if (currentOptType == SearchKeyword)
        {
            filename = "SearchKeywordData.xml";
        }
        else if (currentOptType == Holiday)
        {
            filename = "HolidayData.xml";
        }
        else if (currentOptType == PayCastAgent)
        {
            filename = "PayCastAgentOpt.xml";
        }
        else if (currentOptType == PayCastPlayer)
        {
            filename = "PlayerOptData.xml";
        }
        else if (currentOptType == PayCastDid)
        {
            filename = "DidAgent.xml";
        }

        if (filename==null)
        {
            return "";
        }
        return filename;
    }
    public String getResourceTagReportStr()
    {
        String ret = "";
/*
        String checkItems = PropUtil.customConfigValue("ResourceCheckItem");
        if ((checkItems!=null)&& !checkItems.isEmpty())
        {
            for (int i = 0; i < checkItems.length(); i++)
            {
                String code = checkItems.substring(i, 1);
                String result = "";

                // 표준 리소스 점검
                switch (code)
                {
                    case "B":
                        result = checkBackgroundImageOnWindows();
                        break;
                }
                // 표준 리소스 점검 완료

                // For Edition Customization
                //-

                if (!code.Equals(result))
                {
                    if (String.IsNullOrEmpty(result))
                    {
                        result = code;
                    }

                    ret += (String.IsNullOrEmpty(ret) ? "" : "|") + result;
                }
            }
        }
*/
        return ret.equals("") ? "_NO_" : ret;
    }
    public static String getOnlyExtension(String dirName) {
        int fileIndex = dirName.lastIndexOf(".");
        return dirName.substring(fileIndex+1, dirName.length());
    }
    //파일 & 폴더 삭제
    public static void removeFile(String dirName) {

        String mRootPath = dirName;

        int separator = dirName.lastIndexOf(".");


        if(dirName.substring(separator-1, separator).equals("*"))
        {
            int index = dirName.lastIndexOf("//");
            File file = new File(dirName.substring(0, index));
            File[] childFileList = file.listFiles();
            String extension = getOnlyExtension(dirName);
            if(childFileList!=null) {
                Log.d(TAG, "childFileList.length=" + childFileList.length);
                for (File childFile : childFileList) {
                    if (childFile.isFile()) {
                        if(extension.equals("*"))
                        {
                            Log.d(TAG, "delete extension=" + extension + " FileName=" + childFile.getName());
                            childFile.delete();
                        }
                        else if (getOnlyExtension(childFile.getName()).equals(extension)) {
                            Log.d(TAG, "delete extension=" + extension + " FileName=" + childFile.getName() + " DELET = " + childFile.delete());
                            //                        childFile.delete();
                        }
                    }
                }
            }
        }
        else
        {
            File file = new File(dirName);
            if(file.isFile())
                file.delete();
            else
            {
                Log.d(TAG, "file="+dirName+" IS NOT FILE");
            }
        }
    }
    public static boolean makeCommandFile(String command)
    {
        return true;
    }

    public static String NextCommandFilePrefix()
    {
        File dInfo = new File(BBMC_DEFAULT_DIR + "/Command/");
//        Directory dInfo = new Directory(BBMC_DEFAULT_DIR + "/Command/");
//        File filelist[] = dInfo.listFiles("???_????????????_??.xml");
        File filelist[] = dInfo.listFiles();
        int tmp = 0;

        if (filelist.length > 0)
        {
            for (File fInfo : filelist)
            {
                if((fInfo.getName().endsWith(".xml"))&&(fInfo.getName().substring(3).startsWith("_")==true)&&(fInfo.getName().substring(16).startsWith("_")==true))
                {
                    int seq = Integer.valueOf(fInfo.getName().substring(0, 3));

                    if (seq > tmp) {
                        tmp = seq;
                    }
                    {
                        tmp = 0;
                    }
                }
            }
        }
        String retValue = String.format("%03d", ++tmp);
        return retValue;
    }

    public static boolean saveRemotePlayerConfigData (String fs, Context c)
    {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String nowTimeStr = String.format(sdfNow.format(new Date(System.currentTimeMillis())));

        // 2. 스케줄 목록
        ArrayList<String> galleryFiles = new ArrayList<>();
        File dInfo = new File(ScheduleRootFolder);
        if (dInfo.exists())
        {
            List<String> fInfo = FileUtils.searchByFilefilter(ScheduleRootFolder, ".scd");

            for(String fileInfo:fInfo)
                galleryFiles.add(fileInfo);
        }
        PlayerOptionEnv playerOptionEnv = SettingPersister.getPlayerOptionEnv();
        try
        {
            FileOutputStream fileos = new FileOutputStream(FileUtils.BBMC_DEFAULT_DIR+"//Temp//"+fs);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
//        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
//        xmlSerializer.startTag(null, "SignCast generated=\""+nowTimeStr+"\"");
            xmlSerializer.startTag(null, "SignCast");
            xmlSerializer.attribute(null, "generated", nowTimeStr);
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Option");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTcpPort));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionManagerTcpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTcpPort));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerUdpport));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionStbUdpPort));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerUdpport));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerLang));
            xmlSerializer.text(playerOptionEnv.optionDefaultLang);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerLang));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerFriBegin));
            xmlSerializer.text(playerOptionEnv.timeFriBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerFriBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerFriEnd));
            xmlSerializer.text(playerOptionEnv.timeFriEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerFriEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerMonBegin));
            xmlSerializer.text(playerOptionEnv.timeMonBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerMonBegin));
            xmlSerializer.text("\n");

            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerMonEnd));
            xmlSerializer.text(playerOptionEnv.timeMonEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerMonEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSatBegin));
            xmlSerializer.text(playerOptionEnv.timeSatBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSatBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSatEnd));
            xmlSerializer.text(playerOptionEnv.timeSatEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSatEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSunBegin));
            xmlSerializer.text(playerOptionEnv.timeSunBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSunBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSunEnd));
            xmlSerializer.text(playerOptionEnv.timeSunEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSunEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerThuBegin));
            xmlSerializer.text(playerOptionEnv.timeThuBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerThuBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerThuEnd));
            xmlSerializer.text(playerOptionEnv.timeThuEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerThuEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTueBegin));
            xmlSerializer.text(playerOptionEnv.timeTueBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTueBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTueEnd));
            xmlSerializer.text(playerOptionEnv.timeThuEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTueEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerWedBegin));
            xmlSerializer.text(playerOptionEnv.timeWedBeginTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerWedBegin));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerWedEnd));
            xmlSerializer.text(playerOptionEnv.timeWedEndTime);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerWedEnd));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerRegion));
            xmlSerializer.text(playerOptionEnv.optionDefaultRegion);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerRegion));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerAfterClosingAction));
            xmlSerializer.text(playerOptionEnv.optionAfterClosingAction);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerAfterClosingAction));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerAllSchedulePlayMode));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionAllSchedulePlayMode));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerAllSchedulePlayMode));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerAutoDelSchedDays));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionAutoDelSchedDays));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerAutoDelSchedDays));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerClosingDelaySecs));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionClosingDelaySecs));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerClosingDelaySecs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerConnectionCheck));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionConnectionCheckingRequired));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerConnectionCheck));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playervConnectionErrorDisplayed));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionDisplayConnectionError));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playervConnectionErrorDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerCustomArgs));
            xmlSerializer.text(playerOptionEnv.optionCustomExeArgs);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerCustomArgs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerCustomExec));
            xmlSerializer.text(playerOptionEnv.optionCustomExeFile);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerCustomExec));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDefaultFadeApplied));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSlideShowFadeInOutApplied));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDefaultFadeApplied));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDefaultFadeTimeType));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSlideShowDefaultFadeTimeType));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDefaultFadeTimeType));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDisplayAfterDuration));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSlideShowDisplayAfterDuration));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDisplayAfterDuration));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerGuidImgFile));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionIntroductionImgFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerGuidImgFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerKioskAutoStartSecs));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionKioskAutoStartSecs));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerKioskAutoStartSecs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerKioskStartOnPlayerStartUp));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionKioskStartOnPlayerStartUp));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerKioskStartOnPlayerStartUp));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerMonitorAction));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionMonitorAction));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerMonitorAction));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerOperationInfoDebugged));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionOperationInfoDebugged));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerOperationInfoDebugged));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerPptViewFile));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionPowerPointViewerFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerPptViewFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerRestartHours));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionRestartHours));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerRestartHours));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerRoutineSchedPrefix));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionRoutineSchedPrefix));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerRoutineSchedPrefix));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerScheduleFile));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionDefaultScheduleFile));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerScheduleFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerScreenExtMode));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionScreenExtMode));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerScreenExtMode));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayed));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSimpleClockDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayedOnlyOntheHour));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSimpleClockDisplayedOnlyOntheHour));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSimpleClockDisplayedOnlyOntheHour));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSleepSecs));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSleepSecs));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSleepSecs));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSlideProgressBarColor));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSlideProgressBarColor));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSlideProgressBarColor));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerSlideProgressBarDisplayed));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSlideProgressBarDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerSlideProgressBarDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTestNoticeDisplayed));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionTestNoticeDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTestNoticeDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTestNoticeText));
            xmlSerializer.text(playerOptionEnv.optionTestNoticeText);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTestNoticeText));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTimeSyncRequired));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionTimeSyncRequired));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTimeSyncRequired));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTopmostDisplayed));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionTopmostDisplayed));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTopmostDisplayed));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTouchInfoLogged));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionTouchInfoLogged));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTouchInfoLogged));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerTransparentBGApplied));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionSlideShowTransparentBGApplied));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerTransparentBGApplied));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerWebBrowserFile));
            xmlSerializer.text(playerOptionEnv.optionWebBrowserFile);
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerWebBrowserFile));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerDebugDueTime));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionDebugDueTime));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerDebugDueTime));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.playerVolume));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionVolume));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.playerVolume));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherApiKey));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionWeatherApiKey));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherApiKey));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherCity));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionWeatherCity));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherCity));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherDisp));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionWeatherCityDisplay));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherDisp));
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, c.getResources().getString(R.string.weatherUnit));
            xmlSerializer.text(String.valueOf(playerOptionEnv.optionWeatherUnit));
            xmlSerializer.endTag(null, c.getResources().getString(R.string.weatherUnit));
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "Option");
            xmlSerializer.text("\n");

            xmlSerializer.startTag(null, "Holidays");
            List<Date> holidayLists = OptUtil.ReadOtherDateTimeOptions(FileUtils.Holiday);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if((holidayLists!=null)&&(holidayLists.size()>0)) {
                for (Date hDay : holidayLists) {
                    xmlSerializer.startTag(null, "Holiday");
                    xmlSerializer.attribute(null, "date", sdf.format(hDay));
                    xmlSerializer.endTag(null, "Holiday");
                    xmlSerializer.text("\n");
                }
            }
            xmlSerializer.endTag(null, "Holidays");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "Schedules");
            xmlSerializer.text("\n");

            if(galleryFiles.size()>0) {
                for (String scdfile : galleryFiles) {
                    String hasAllFile = String.valueOf(hasScheduleAllFiles(scdfile));
                    String newSch = scdfile.substring(scdfile.lastIndexOf('/') + 1, scdfile.lastIndexOf('.'))+".scd";
                    Log.d(TAG, "newSch="+newSch);
                    xmlSerializer.startTag(null, "Schedule");
                    xmlSerializer.attribute(null, "name", newSch);
                    xmlSerializer.attribute(null, "hasallfiles", hasAllFile);
                    xmlSerializer.attribute(null,"mountdir", "");
                    xmlSerializer.endTag(null, "Schedule");
                    xmlSerializer.text("\n");
                }
            }

            xmlSerializer.endTag(null, "Schedules");
            xmlSerializer.text("\n");
            xmlSerializer.startTag(null, "System");
            xmlSerializer.attribute(null, "root", String.valueOf(Environment.getExternalStorageDirectory()));
            xmlSerializer.endTag(null, "System");
//        xmlSerializer.text("/<System root ="+String.valueOf(Environment.getExternalStorageDirectory()+"///>"));
            xmlSerializer.text("\n");
            xmlSerializer.endTag(null, "SignCast");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


/*
        XElement schedulesEl = new XElement("Schedules");
        foreach (string schFileName in galleryFiles)
        {
            XAttribute[] scheduleAttrs = {
                    new XAttribute("name", schFileName),
                    new XAttribute("hasallfiles", hasScheduleAllFiles(schFileName)),
                    // jason:multictntmount: 다중 스케줄 탑재(2013/01/21)
                    new XAttribute("mountdir", SignCastModel.MountPointFromSchedule(ScheduleRootFolder + schFileName)),
            };

            schedulesEl.Add(new XElement("Schedule", scheduleAttrs));
        }
        additionalEls.Add(schedulesEl);

        // 3. 루트 디렉토리
        XAttribute[] systemAttrs = {
                new XAttribute("root", LocalStrings.RootDirectory),
        };
        XElement systemEl = new XElement("System", systemAttrs);
        additionalEls.Add(systemEl);
        // 추가 XElement 생성 종료

        TempConfigFileName = SignCastModel.GetTempFileName("xml");

        OptUtil.PrepareOptions(OptUtil.Options.Player);

        setOptionValues();

        OptUtil.SaveOptions(additionalEls, System.IO.Path.Combine(
                System.IO.Path.Combine(System.IO.Path.GetDirectoryName(
                        Assembly.GetExecutingAssembly().Location), "Temp"), TempConfigFileName));
*/
        //Log.d(TAG, "saveRemotePlayerConfigData() 함수 구현 필요");
        return true;
    }
    public static Boolean hasScheduleAllFiles(String fileName)
    {
        FileInputStream fis = null;
        XmlOptionParser xmlOptUtil = new XmlOptionParser();
        ArrayList<String> contentlist = new ArrayList<>();
        String mountPoint = XmlOptionParser.MountPointFromSchedule(fileName);


        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XmlPullParserFactory parserFactory = null;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        XmlPullParser parser = null;
        try {
            parser = parserFactory.newPullParser();
            int no = -1;
            String name = null;

            parser.setInput(fis, null);
            //          parser.setInput(str, null) ;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    // XML 데이터 시작
                } else if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();

                    if (startTag.equals("Area")) {
                        ;
                    } else if (startTag.equals("Page")) {
//                        PageSlideShowObject page = new PageSlideShowObject();
//                        page = xmlOptUtil.getSchedulePage(parser, page);
                        String bgImgFile = parser.getAttributeValue(null, "file");
                        if((bgImgFile!=null)&&(!bgImgFile.isEmpty())) {
                            if(!contentlist.contains(bgImgFile))
                                contentlist.add(bgImgFile);
                        }
                    }
/*
                    else if (startTag.equals("Frame")) {
                        FrameSlideShowObject frame = new FrameSlideShowObject();
                        frame = xmlOptUtil.getScheduleFrame(parser, frame);

                    }
*/
                    else if (startTag.equals("Content")) {
//                        ContentSlideShowObject content = new ContentSlideShowObject();
                        String file = parser.getAttributeValue(null, "file");
//                        content = xmlOptUtil.getScheduleContent(parser, content);
                        if((file!=null)&&(!file.isEmpty())) {
//                            Log.e(TAG, "hasScheduleAllFiles file ="+file+" content.file="+content.file);
                            if(!contentlist.contains(file))
                                contentlist.add(file);
                        }
                    } else if (startTag.equals("Schedule")) {
                        ;
                    } else if (startTag.equals("IntegrationSchedule")) {
                        ;
                    } else if (startTag.equals("AuxiliarySchedule")) {
                        ;
                    } else if (startTag.equalsIgnoreCase("/SignCast/IntegrationSchedule/Base/Schedule")) {
                        ;
                    }
                }
/*
                else if (eventType == XmlPullParser.END_TAG) {
                    String endTag = parser.getName();
                    Log.d(TAG, "endTag = " + endTag);
                } else if (eventType == XmlPullParser.TEXT) {
                    String text = parser.getText();
                    Log.d(TAG, "TEXT = " + text);
                }
*/
                try {
                    eventType = parser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        boolean result = true;
        if((contentlist!=null)&&(contentlist.size()>0))
        {
            for(String fn:contentlist)
            {
                if((fn!=null)&&(!fn.isEmpty())) {
                    File contFile = new File(FileUtils.BBMC_DEFAULT_DIR + fn);
                    if ((contFile == null) || !(contFile.exists())) {
                        result = false;
                        break;
                    }
                }
            }
        }
        contentlist.clear();
        return result;
    }

    public static String getCurrentDateTimeCommandFileName(String command)
    {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
//ss 삭제        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
        String strNow = sdfNow.format(date);

        return String.valueOf(FileUtils.NextCommandFilePrefix()+"_"+
                strNow+"_"+command+".xml");
    }
    // jason:newoptionfile: 프로그램 옵션 파일 기능 단순화(2015/02/02)
    public void readRequestedStbConfigFileByServer(Context c)
    {
        File dirName = new File(BBMC_DEFAULT_DIR + "Temp//");
        String fileName = "PlayerOptData.xml";
        HashMap<String, String> dic;
        if (!dirName.exists()) {
            dirName.mkdirs();
        }

        File file = new File(BBMC_DEFAULT_DIR + "Temp//"+fileName);
        List<Date>  holidays = new ArrayList<Date>();
        if(file.exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            Document doc = null;
            try {
                doc = dBuilder.parse(file);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Holidays");

            if ((nList != null) && nList.getLength() > 0) {
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    Log.d(TAG, "Current Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String tmp = eElement.getAttribute("date");
                        if (((tmp != null) && (!tmp.isEmpty()) && tmp.length() == 10)) {
                            String day = tmp.replace("-", "").replace("/", "");
                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");

                            Date dt = null;
                            try {
                                dt = sdfNow.parse(day);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            holidays.add(dt);
                        }
                    }

                }
                OptUtil.SaveHolidayOptions(FileUtils.Holiday, holidays);
            }
            dic = OptUtil.GetCurrentOptionDic();
            HashMap<String, String> tempDic = new HashMap<>();
            for(String k:dic.keySet()) {
                tempDic.put(k, dic.get(k));
            }
            Log.d(TAG, "dic.key="+dic.keySet()+" value="+dic.values());
            if (dic != null && dic.size() > 0) {
                OptUtil.ReadAndSaveOptions(FileUtils.Player, tempDic, c);

                // jason:monitoroffclosingaction: 방송 종료 후 액션에 모니터 끄기 추가(2015/09/22)
                String StbStatus = SettingPersister.getPlayerStatus();
                Log.d(TAG, "StbStatus = "+StbStatus);
                if (StbStatus.equals("5") || StbStatus.equals("6")) {
                    Log.d(TAG, "restart()  구현해야 함.");
/*
                    System.Windows.Application.Current.Dispatcher.BeginInvoke(
                            System.Windows.Threading.DispatcherPriority.Normal, new Action(delegate {
                                restart(false);
                            }));
*/
                }
                //-
            }
        }

    }
    //-
    // jason:newoptionfile: 프로그램 옵션 파일 기능 단순화(2015/02/02)
    private static DocumentBuilder dBuilder = null;
    private static Document doc = null;
    private static NodeList nList = null;
    private static NodeList nScheduleList = null;

    public static void readRequestedStbConfigFile(XmlOptionParser optUtil, String tempSavedConfigFile, PlayerOptionEnv option, Context c)
    {
        String fileName = BBMC_TEMP_DIRECTORY + tempSavedConfigFile+".xml";
        option = optUtil.parsePlayerOptionXML(fileName, option, c);
        File file = new File(fileName);
        if (file.exists())
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            //Log.d(TAG, "Holidays Current Element tempSavedConfigFile=" + fileName);
            try {
                dBuilder = factory.newDocumentBuilder();
                try {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();
                    nList = doc.getElementsByTagName("Holidays");
                    nScheduleList = doc.getElementsByTagName("Schedules");
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            List<Date> holidays = new ArrayList<Date>();


            if (nList != null) {
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    //Log.d(TAG, "Holidays Current Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        NodeList n = eElement.getChildNodes();
                        for (int i = 0; i < n.getLength(); i++) {
                            Node node = n.item(i);
                            if (node.getNodeType() != Node.TEXT_NODE) {
                                if (node.getNodeName().equals("date")) {
                                    String hDay = String.valueOf(node.getAttributes());
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                    hDay.replace("-", "").replace("/", "");
                                    Date date = null;
                                    try {
                                        date = sdf.parse(hDay);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    holidays.add(date);
                                    //Log.e(TAG, "1 hDay=" + hDay + " getTextContent()=" + node.getTextContent());
                                }
                                else if (node.getNodeName().equals("Holiday")) {
                                    NamedNodeMap attr = node.getAttributes();
                                    for(int j = 0; j<attr.getLength(); j++) {
                                        Node nodeAttr = attr.item(j);
                                        String hDay = nodeAttr.getNodeValue();
                                        Log.e(TAG, "Holiday hDay=" + hDay);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        hDay.replace("-", "").replace("/", "");
                                        Date date = null;
                                        try {
                                            date = sdf.parse(hDay);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        holidays.add(date);
                                        //Log.e(TAG, "2 hDay=" + hDay + " getTextContent()=" + node.getTextContent());
                                    }
                                } else {
                                    //Log.e(TAG, "else node.getNodeName()=" + node.getNodeName());
                                    ;
                                }
                            }

                        }
                    }
                }
                OptUtil.SaveHolidayOptions(FileUtils.Holiday, holidays);

                // 2. 스케줄 목록
                ArrayList<String> galleryFiles = new ArrayList<>();
                File dInfo = new File(ScheduleRootFolder);
                if (dInfo.exists())
                {
                    List<String> fInfo = FileUtils.searchByFilefilter(ScheduleRootFolder, ".scd");

                    for(String fileInfo:fInfo) {
                        String filename=fileInfo.substring(fileInfo.lastIndexOf("/")+1);
                        galleryFiles.add(filename);
                        //Log.d(TAG, "galleryFiles list filename="+filename);
                    }
                }

                List<String> scheduleList = new ArrayList<String>();

                if(nScheduleList!=null)
                {
                    for (int temp = 0; temp < nScheduleList.getLength(); temp++) {
                        Node nNode = nScheduleList.item(temp);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            NodeList n = eElement.getChildNodes();
                            for (int i = 0; i < n.getLength(); i++) {
                                Node node = n.item(i);
                                if (node.getNodeType() != Node.TEXT_NODE) {
                                    if (node.getNodeName().equals("Schedule")) {
                                        NamedNodeMap attrs = node.getAttributes();

                                        for(int a = 0; a<attrs.getLength(); a++) {
                                            Node nodeAttr = attrs.item(a);
                                            if (nodeAttr.getNodeName().equals("name")) {
                                                String shedulefile = nodeAttr.getNodeValue();
                                                scheduleList.add(shedulefile);
                                                //galleryFiles.remove(shedulefile);
                                                //Log.e(TAG, "1 shedulefile=" + ScheduleRootFolder+shedulefile );
                                            }
                                        }
                                    } else {
                                        //Log.d(TAG, "node.getNodeName()=" + node.getNodeName());
                                        ;
                                    }
                                }

                            }
                        }
                    }
                    if(scheduleList.size()>0) {
                        if(galleryFiles.size() > 0) {
                            for (String schFile : scheduleList) {
                                galleryFiles.remove(schFile);
                            }
                        }
                    }
                    else {
                        File bbmcDir = new File(FileUtils.BBMC_PAYCAST_CONTENT_DIRECTORY+"Image");
                        if (bbmcDir.exists())
                        {
                            File[] fInfo = bbmcDir.listFiles();

                            if((fInfo!=null)&&(fInfo.length>0)) {
                                for (File fileInfo : fInfo) {
                                    fileInfo.delete();
                                }
                            }
                        }
                        bbmcDir = new File(FileUtils.BBMC_PAYCAST_CONTENT_DIRECTORY+"Video");
                        if (bbmcDir.exists())
                        {
                            File[] fInfo = bbmcDir.listFiles();

                            if((fInfo!=null)&&(fInfo.length>0)) {
                                for (File fileInfo : fInfo) {
                                    fileInfo.delete();
                                }
                            }
                        }
                        bbmcDir = new File(FileUtils.BBMC_PAYCAST_CONTENT_DIRECTORY+"Text");
                        if (bbmcDir.exists())
                        {
                            File[] fInfo = bbmcDir.listFiles();

                            if((fInfo!=null)&&(fInfo.length>0)) {
                                for (File fileInfo : fInfo) {
                                    fileInfo.delete();
                                }
                            }
                        }
                        bbmcDir = new File(FileUtils.BBMC_PAYCAST_CONTENT_DIRECTORY+"Flash");
                        if (bbmcDir.exists())
                        {
                            File[] fInfo = bbmcDir.listFiles();

                            if((fInfo!=null)&&(fInfo.length>0)) {
                                for (File fileInfo : fInfo) {
                                    fileInfo.delete();
                                }
                            }
                        }
                        bbmcDir = new File(FileUtils.BBMC_PAYCAST_CONTENT_DIRECTORY+"Audio");
                        if (bbmcDir.exists())
                        {
                            File[] fInfo = bbmcDir.listFiles();

                            if((fInfo!=null)&&(fInfo.length>0)) {
                                for (File fileInfo : fInfo) {
                                    fileInfo.delete();
                                }
                            }
                        }
                        bbmcDir = new File(FileUtils.BBMC_PAYCAST_CONTENT_DIRECTORY+"PowerPoint");
                        if (bbmcDir.exists())
                        {
                            File[] fInfo = bbmcDir.listFiles();

                            if((fInfo!=null)&&(fInfo.length>0)) {
                                for (File fileInfo : fInfo) {
                                    fileInfo.delete();
                                }
                            }
                        }
                    }
                    if(galleryFiles.size() > 0) {
                        for (String schFile : galleryFiles) {
                            File f = new File(ScheduleRootFolder + schFile);
                            if (f.exists()) {
                                //Log.e(TAG, "delete schedule file =" + f.getPath()+f.getName());
                                boolean result = f.delete();
                                //Log.e(TAG, "delete result =" + result);
                            }
                        }
                    }
                    if(galleryFiles!=null)
                        galleryFiles.clear();
                    if(scheduleList!=null)
                        scheduleList.clear();
                }
            }

        }
    }
    //-
    public static void deleteAllFiles(String folderName)
    {
        File dInfo = new File(folderName);
        if (dInfo.exists())
        {
            File[] fInfos = dInfo.listFiles();
            for (File fi : fInfos)
            {
                if (fi.exists())
                {
                    fi.delete();
                }
            }
        }
    }
    // jason:multictntmount: 다중 스케줄 탑재(2013/01/21)
    public static String MountedRelativeFilename(String mountDir, String filename)
    {
        if ((mountDir==null)|| mountDir.isEmpty() || (filename==null)||filename.isEmpty())
        {
            return filename;
        }

        if (filename.indexOf("/") < 0 && filename.indexOf("\\") < 0)
        {
            return "["+mountDir+"]"+filename;
        }
        else
        {
            if (filename.indexOf("/") > -1)
            {
                return filename.substring(0, filename.lastIndexOf("/") + 1)+"["+mountDir+"]"+
                        filename.substring(filename.lastIndexOf("/") + 1);
            }
            else
            {
                return filename.substring(0, filename.lastIndexOf("\\") + 1)+"["+ mountDir+"]"+
                        filename.substring(filename.lastIndexOf("\\") + 1);
            }
        }
    }
    // -
    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    // jason:yesterdayadlog: 이전날의 광고추적로그를 해당일자 광고추적파일로 저장(2012/11/27)
    public static String currentTrackFileName(Date dstDate)
    {
        String ret = "";
        String folderPathName = BBMC_DEFAULT_DIR + "/Report/";
        File dInfo = new File(folderPathName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayStr = String.format(sdf.format(dstDate));

        if (dInfo.exists())
        {
            File[] fInfos = dInfo.listFiles();

            int maxValue = 0;
            for (File fInfo : fInfos)
            {
                int tmp = 0;
                if (fInfo.getName().startsWith(todayStr) && fInfo.getName().length() == 17 && fInfo.getName().substring(8, 1).equals("_"))
                {
                    tmp = Integer.parseInt(fInfo.getName().substring(9, 4));
                }

                if (tmp > 0)
                {
                    maxValue = tmp;
                }
            }

            return folderPathName + String.format(sdf.format(dstDate)+"_"+(maxValue+1)+".xml");
        }

        return ret;
    }
    public static String findFirstRoutinedSchedule(String prefix)
    {
        List<String> routineSchedules = new ArrayList<String>();
        List<String> routineScheduleTests = new ArrayList<String>();
        File dInfo = new File(BBMC_SCH_DIRECTORY);
        if (dInfo.exists())
        {
            File[] fInfo = dInfo.listFiles();
            for (File fileInfo : fInfo)
            {
                if (fileInfo.getName().endsWith(".scd") &&
                        isRoutineScheduleFormat(fileInfo.getName(), prefix))
                {
                    String datePart = fileInfo.getName().substring(prefix.length());
                    if (datePart.length() > 10)
                    {
                        datePart = datePart.substring(0, 10);
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(Utils.getCurrentDate());
                    cal.add(Calendar.DATE, 1); //minus number would decrement the days
                    Date dt = cal.getTime();
                    SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyyMMdd");
                    String date  = datePart.replace("-", "").replace("/", "").replace(".", "");

                    try {
                        dt = simpleDateformat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (dt.compareTo(Utils.getCurrentDate()) <= 0)
                    {
                        String tmp = fileInfo.getName();
                        routineSchedules.add(tmp);

                        tmp = tmp.substring(0, tmp.length() - 4).substring(prefix.length());
                        tmp = tmp.substring(0, 4) + tmp.substring(5, 2) + tmp.substring(8);

                        routineScheduleTests.add(tmp);
                    }
                }
            }
        }

        if (routineSchedules.size() > 0)
        {
            List<String> routineScheduleSortTests = routineScheduleTests;
            Utils.NameAscCompare ascendingString = new Utils.NameAscCompare();
            Collections.sort(routineScheduleSortTests, ascendingString);

            return routineSchedules.get(routineScheduleSortTests.indexOf(
                    routineScheduleSortTests.get(routineScheduleSortTests.size() - 1)));
        }

        return "";
    }
    // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
    public static boolean isRoutineScheduleFormat(String schedule, String prefix)
    {
        if ((schedule==null || schedule.isEmpty()) && (prefix!=null&&!prefix.isEmpty()))
        {
            return true;
        }

        if ((schedule==null || schedule.isEmpty()) || (prefix==null||prefix.isEmpty()))
        {
            return false;
        }
        else if (!schedule.startsWith(prefix))
        {
            return false;
        }

        if (schedule.endsWith(".scd"))
        {
            schedule = schedule.substring(0, schedule.length() - 4);
        }

        String datePart = schedule.substring(prefix.length());
        if (datePart.length() < 10)
        {
            return false;
        }
        else if (datePart.length() > 10)
        {
            datePart = datePart.substring(0, 10);
        }
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyyMMdd");
        String date  = datePart.replace("-", "").replace("/", "").replace(".", "");

        try {
            simpleDateformat.parse(date);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }

/*
        try
        {
            DateTime.ParseExact(datePart.Replace("-", "").Replace("/", "").Replace(".", ""),
                    "yyyyMMdd", CultureInfo.InvariantCulture);

            return true;
        }
        catch { }
*/
        return false;
    }
    public static List<String> searchByFilefilter(String folder, final String extension){
        List<String> templist = new ArrayList<>();
        File f = new File(folder);

        if((f==null)&&(!f.exists()))
            return templist;

        File[] list = f.listFiles(new FilenameFilter(){

            @Override

            public boolean accept(File dir, String name){
                if((extension==null)||(extension.isEmpty())||extension.equals("*")||extension.equals(".*"))
                    return true;
                return name.endsWith(extension);
            }

        });
        if((list!=null)&&(list.length>0)) {
            for (int i = 0; i < list.length; i++)
                templist.add(list[i].toString());
        }
        return templist;
    }
    public static ArrayList<String> searchByFilefilter(String folder, final String prefix, final String extension){
        ArrayList<String> templist = new ArrayList<>();
        File f = new File(folder);

        File[] list = f.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name){
                boolean result = false;
                if(name.startsWith(prefix) &&name.endsWith(extension))
                    result = true;
                else
                    result = false;
                return result;
            }

        });
        for(int i = 0; i<list.length; i++)
            templist.add(list[i].toString());
        return templist;
    }
    public static boolean checkMandatoryDirectory()
    {
        File dir = new File(BBMC_DEFAULT_DIR);
        boolean result = true;
        if(dir==null || !dir.exists())
        {
            if(dir.mkdir()) {
                Log.d(TAG, dir.getName() + " is created");
                result = true;
            }
            else {
                Log.d(TAG, dir.getName() + " is fail creat");
                result = false;
            }
        }

        dir = new File(BBMC_DIRECTORY);
        if(dir==null || !dir.exists())
        {
            if(dir.mkdir()) {
                Log.d(TAG, dir.getName() + " is created");
                result = true;
            }
            else
            {
                result = false;
                Log.d(TAG, dir.getName() + " is fail creat");
            }
        }
        dir = new File(BBMC_SCH_DIRECTORY);
        if(dir==null || !dir.exists())
        {
            if(dir.mkdir()) {
                result = true;
                Log.d(TAG, dir.getName() + " is created");
            }
            else
            {
                result = false;
                Log.d(TAG, dir.getName() + " is fail creat");
            }
        }
        dir = new File(BBMC_REPORT_DIR);
        if(dir==null || !dir.exists())
        {
            if(dir.mkdir()) {
                result = true;
                Log.d(TAG, dir.getName() + " is created");
            }
            else
            {
                result = false;
                Log.d(TAG, dir.getName() + " is fail creat");
            }
        }
        dir = new File(BBMC_DEBUG_DIR);
        if(dir==null || !dir.exists())
        {
            if(dir.mkdir()) {
                result = true;
                Log.d(TAG, dir.getName() + " is created");
            }
            else
            {
                result = false;
                Log.d(TAG, dir.getName() + " is fail creat");
            }
        }
        dir = new File(BBMC_LOG_DIR);
        if(dir==null || !dir.exists())
        {
            if(dir.mkdir()) {
                result = true;
                Log.d(TAG, dir.getName() + " is created");
            }
            else
            {
                result = false;
                Log.d(TAG, dir.getName() + " is fail creat");
            }
        }
        return result;
    }
    public static String[] getPlayerUpdateVersion()
    {
        File dInfo = new File(FileUtils.BBMC_DEFAULT_DIR + "/Temp/");
        if(dInfo.exists())
        {
            if(dInfo.isDirectory())
            {
                String[] list = dInfo.list(new FilenameFilter(){

                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.endsWith(".apk"))
                        {
                            String tempName[] = name.split("_");

                            if(tempName.length == 3)
                            {
                                if(tempName[1].equals("Player"))
                                    return true;
                            }
                        }
                        return false;
                    }
                });
                if(list.length > 0)
                    return list;
            }

        }
        return null;
    }
    /** 사용가능한 외장 메모리 크기를 가져온다 */

    public static long getExternalMemorySize() {

        if (isStorage(true) == true) {

            File path = Environment.getExternalStorageDirectory();

            StatFs stat = new StatFs(path.getPath());

            long blockSize = stat.getBlockSize();

            long availableBlocks = stat.getAvailableBlocks();

            return availableBlocks * blockSize;

        } else {

            return -1;

        }

    }
    /** 외장메모리 sdcard 사용가능한지에 대한 여부 판단 */

    private static boolean isStorage(boolean requireWriteAccess) {

        String state = Environment.getExternalStorageState();



        if (Environment.MEDIA_MOUNTED.equals(state)) {

            return true;

        } else if (!requireWriteAccess &&

                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            return true;

        }

        return false;

    }
    public static void savePlayerConfigData(boolean isCheckMode) {
        // jason:initplayerconfig: 잘못된 플레이어 설정파일 초기화 방지(2013/03/07)
/*
        if (isCheckMode)
        {
            if (string.IsNullOrEmpty(DefaultScheduleFile) && string.IsNullOrEmpty(MonBeginTime) && string.IsNullOrEmpty(MonEndTime)
                    && string.IsNullOrEmpty(TueBeginTime) && string.IsNullOrEmpty(TueEndTime)
                    && string.IsNullOrEmpty(WedBeginTime) && string.IsNullOrEmpty(WedEndTime)
                    && string.IsNullOrEmpty(ThuBeginTime) && string.IsNullOrEmpty(ThuEndTime)
                    && string.IsNullOrEmpty(FriBeginTime) && string.IsNullOrEmpty(FriEndTime)
                    && string.IsNullOrEmpty(SatBeginTime) && string.IsNullOrEmpty(SatEndTime)
                    && string.IsNullOrEmpty(SunBeginTime) && string.IsNullOrEmpty(SunEndTime))
            {
                return;
            }
        }
        // -
        OptUtil.PrepareOptions(FileUtils.Player);

        setOptionValues();

        OptUtil.SaveOptions();
        OptUtil.SaveOptions(FileUtils.Holiday, Holidays);
*/
    }
    public void readRequestedStbConfigFile(Context c)
    {
/*
        String fileName = BBMC_TEMP_DIRECTORY +model.TempSavedConfigFileName;
        Context c = this.getApplicationContext();
        File tempFile = new File(fileName);
        if ((tempFile!=null) && (tempFile.exists()))
        {
            OptUtil.ReadOptions(FileUtils.Player, false, c);

            model.DefaultLang = OptUtil.GetValue("Lang", SignCastManager.Models.Environment.CurrentLang());
            model.DefaultRegion = OptUtil.GetValue("Region", SignCastManager.Models.Environment.CurrentRegion());

            model.IntroductionImgFile = OptUtil.GetValue("SlideShow.GuideImageFile");
            model.PowerPointViewerFile = OptUtil.GetValue("SlideShow.PptViewerFile");
            model.DefaultScheduleFile = OptUtil.GetValue("SlideShow.ScheduleFile");
            model.ConnectionCheckingRequired = OptUtil.GetValue("SlideShow.ConnectionCheck", false);
            model.DisplayConnectionError = OptUtil.GetValue("SlideShow.ConnectionErrorDisplayed", false);
            model.CustomExeFile = OptUtil.GetValue("SlideShow.CustomExec");
            model.CustomExeArgs = OptUtil.GetValue("SlideShow.CustomArgs");
            model.SleepSecs = OptUtil.GetValue("SlideShow.SleepSecs", 0);
            model.KioskStartOnPlayerStartUp = OptUtil.GetValue("SlideShow.KioskStartOnPlayerStartUp", false);
            model.KioskAutoStartSecs = OptUtil.GetValue("SlideShow.KioskAutoStartSecs", 0);
            model.AfterClosingAction = OptUtil.GetValue("SlideShow.AfterClosingAction", "PF");
            model.ClosingDelaySecs = OptUtil.GetValue("SlideShow.ClosingDelaySecs", 60);
            model.MonitorAction = OptUtil.GetValue("SlideShow.MonitorAction", "Monitor");
            model.RestartHours = OptUtil.GetValue("SlideShow.RestartHours", "3");
            model.ScreenExtMode = OptUtil.GetValue("SlideShow.ScreenExtMode", "0");
            model.TestNoticeDisplayed = OptUtil.GetValue("SlideShow.TestNoticeDisplayed", false);
            model.TestNoticeText = OptUtil.GetValue("SlideShow.TestNoticeText");
            model.TopmostDisplayed = OptUtil.GetValue("SlideShow.TopmostDisplayed", false);
            model.TimeSyncRequired = OptUtil.GetValue("SlideShow.TimeSyncRequired", false);
            model.AllSchedulePlayMode = OptUtil.GetValue("SlideShow.AllSchedulePlayMode", false);
            model.WebBrowserFile = OptUtil.GetValue("SlideShow.WebBrowserFile");
            model.SimpleClockDisplayed = OptUtil.GetValue("SlideShow.SimpleClockDisplayed", false);
            model.SimpleClockDisplayedOnlyOntheHour = OptUtil.GetValue("SlideShow.SimpleClockDisplayedOnlyOntheHour", true);
            model.SlideProgressBarDisplayed = OptUtil.GetValue("SlideShow.SlideProgressBarDisplayed", false);
            model.SlideProgressBarColor = OptUtil.GetValue("SlideShow.SlideProgressBarColor", "Red");
            model.OperationInfoDebugged = OptUtil.GetValue("SlideShow.OperationInfoDebugged", false);
            model.TouchInfoLogged = OptUtil.GetValue("SlideShow.TouchInfoLogged", false);

            // jason:transitionfinal: 전환 효과 최종(2015/02/12)
            model.SlideShowFadeInOutApplied = OptUtil.GetValue("SlideShow.DefaultFadeApplied", true);
            model.SlideShowDefaultFadeTimeType = OptUtil.GetValue("SlideShow.DefaultFadeTimeType", "M");
            model.SlideShowTransparentBGApplied = OptUtil.GetValue("SlideShow.TransparentBGApplied", false);
            model.SlideShowDisplayAfterDuration = OptUtil.GetValue("SlideShow.DisplayAfterDuration", true);
            //-

            // jason:scheduleautomation: 스케줄 자동 관리 기능(2015/04/02)
            model.RoutineSchedPrefix = OptUtil.GetValue("SlideShow.RoutineSchedPrefix");
            model.AutoDelSchedDays = OptUtil.GetValue("SlideShow.AutoDelSchedDays", 0);
            //-

            model.MonBeginTime = OptUtil.GetValue("Playtime.MonBeginTime");
            model.MonEndTime = OptUtil.GetValue("Playtime.MonEndTime");
            model.TueBeginTime = OptUtil.GetValue("Playtime.TueBeginTime");
            model.TueEndTime = OptUtil.GetValue("Playtime.TueEndTime");
            model.WedBeginTime = OptUtil.GetValue("Playtime.WedBeginTime");
            model.WedEndTime = OptUtil.GetValue("Playtime.WedEndTime");
            model.ThuBeginTime = OptUtil.GetValue("Playtime.ThuBeginTime");
            model.ThuEndTime = OptUtil.GetValue("Playtime.ThuEndTime");
            model.FriBeginTime = OptUtil.GetValue("Playtime.FriBeginTime");
            model.FriEndTime = OptUtil.GetValue("Playtime.FriEndTime");
            model.SatBeginTime = OptUtil.GetValue("Playtime.SatBeginTime");
            model.SatEndTime = OptUtil.GetValue("Playtime.SatEndTime");
            model.SunBeginTime = OptUtil.GetValue("Playtime.SunBeginTime");
            model.SunEndTime = OptUtil.GetValue("Playtime.SunEndTime");

            model.ManagerTcpPort = OptUtil.GetValue("Communication.ManagerTcpPort", 11002);
            model.StbUdpPort = OptUtil.GetValue("Communication.StbUdpPort", 11001);

            model.WeatherCity = OptUtil.GetValue("Weather.City");
            model.WeatherCityDisplay = OptUtil.GetValue("Weather.Display");
            model.WeatherUnit = OptUtil.GetValue("Weather.Unit", "C");
            model.WeatherApiKey = OptUtil.GetValue("Weather.ApiKey");

            // 일반 플레이어 옵션을 제외한 원격 제어용 잔여 속성 읽기
            XDocument xmlData = XDocument.Load(fileName);

            model.Holidays.Clear();
            foreach (XElement holidayEl in xmlData.XPathSelectElements("//SignCast/Holidays/Holiday"))
            {
                string tmp = holidayEl.Attribute("date").Value;
                if (!string.IsNullOrEmpty(tmp) && tmp.Length == 10)
                {
                    try
                    {
                        DateTime dt = DateTime.ParseExact(tmp.Replace("-", "").Replace("/", ""),
                                "yyyyMMdd", CultureInfo.InvariantCulture);
                        model.Holidays.Add(dt);
                    }
                    catch { }
                }
            }

            List<string> scheduleList = new List<string>();
            foreach (XElement scheduleEl in xmlData.XPathSelectElements("//SignCast/Schedules/Schedule"))
            {
                scheduleList.Add(scheduleEl.Attribute("name").Value);
            }

            if (scheduleList.Count == 0)
            {
                model.DefaultScheduleFile = "";

                deleteAllFiles(model.ScheduleRootFolder);
                deleteAllFiles(LocalStrings.RootDirectory + LocalStrings.Separator + "Content\\Audio");
                deleteAllFiles(LocalStrings.RootDirectory + LocalStrings.Separator + "Content\\Component");
                deleteAllFiles(LocalStrings.RootDirectory + LocalStrings.Separator + "Content\\Flash");
                deleteAllFiles(LocalStrings.RootDirectory + LocalStrings.Separator + "Content\\Image");
                deleteAllFiles(LocalStrings.RootDirectory + LocalStrings.Separator + "Content\\PowerPoint");
                deleteAllFiles(LocalStrings.RootDirectory + LocalStrings.Separator + "Content\\Text");
                deleteAllFiles(LocalStrings.RootDirectory + LocalStrings.Separator + "Content\\Video");
            }
            else
            {
                List<string> tmpScheduleList = new List<string>();
                DirectoryInfo dInfo = new DirectoryInfo(model.ScheduleRootFolder);
                if (dInfo.Exists)
                {
                    FileInfo[] fInfo = dInfo.GetFiles();
                    foreach (FileInfo fileInfo in fInfo)
                    {
                        tmpScheduleList.Add(fileInfo.Name);
                    }
                }

                foreach (string file in scheduleList)
                {
                    tmpScheduleList.Remove(file);
                }

                foreach (string fname in tmpScheduleList)
                {
                    FileInfo fInfo = new FileInfo(model.ScheduleRootFolder + fname);
                    if (fInfo.Exists)
                    {
                        fInfo.Delete();
                    }
                }
            }

            model.savePlayerConfigData();

            // jason:testnotice: 플레이어 점검중 공지 기능 옵션(2013/01/29)
            if (model.TestNoticeDisplayed)
            {
                floatingNotice.SetTextAndStart(string.IsNullOrEmpty(model.TestNoticeText) ? testNoticeText : model.TestNoticeText);
            }
            else
            {
                floatingNotice.StopAnimation();
            }

            // jason:topmostoption: 플레이어 최상위 표시 옵션(2013/04/03)
            System.Windows.Application.Current.Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, new Action(delegate
            {
                    this.Topmost = model.TopmostDisplayed;
            }));
        }
*/
    }
    /**
     * 파일 생성
     * @param dir
     * @return file
     */
    public static File makeMenuOrderReceiptFile(File dir, String file_path, Context c, ArrayList list, String tprice, ArrayList sellerInfo, PaymentInfoData payInfo, String orderNum){
        File file = null;
        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(file_path);
            if(file!=null&&!file.exists())
            {
                Log.i( TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                    FileOutputStream fileos = null;
                    try{
                        fileos = new FileOutputStream(file);

                        writeXmlMenuOrderReceipt(fileos, c, list, tprice, sellerInfo, payInfo, orderNum);

                    }catch(FileNotFoundException e)
                    {
                        Log.e("FileNotFoundException",e.toString());
                    }
                }
            }else{
                Log.i( TAG , "file.exists" );
            }
        }
        return file;
    }
    public static void writeDebug(String errlog, String appName)
    {
        String path = BBMC_DEBUG_DIR;
        File dir = new File(path);
        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm:ss:SSS");

        String getTime = sdfdate.format(date);
        String logTime = sdftime.format(date)+"\r\n";


        if(dir==null || !dir.exists())
        {
            dir.mkdir();
        }

        if(dir.exists())
        {
            File file = new File(path+appName+"Debug"+getTime+".txt");
            //if(file!=null&&!file.exists())
            {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //Log.i(TAG, "파일생성 여부 = " + isSuccess);
                    FileOutputStream fileos = null;
                    try {
                        fileos = new FileOutputStream(file, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fileos.write(logTime.getBytes());
                        fileos.write(errlog.getBytes());
                        fileos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fileos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }}
