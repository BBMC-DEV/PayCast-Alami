package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.util.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthKeyFile {
//    private static String companyName;
    private static String userName;
    private static String authKey;
    private static String authMacAddress;
    private static int authVersion;
    private static Date effectiveEndDate;
    private static String deviceId;
    private static ProductInfo regInfo;

    public static ProductInfo getProductInfo() {
        return regInfo;
    }

    public static String getAuthValidationServer() {
        return regInfo.getAuthValidationServer();
    }
    public static String getAuthKeyParam(Context c, String majVer) {
        return regInfo.getAuthKeyParam(c, majVer);
    }

    public static String getAuthRegFCMTokenServer() {
        return regInfo.getAuthRegFCMTokenServer();
    }
    public static String getAuthTokenParam() {
        return regInfo.getAuthTokenParam();
    }

    public static boolean readKeyFile(Context c, String fname)
    {

        regInfo = new ProductInfo(c, fname);
        if(regInfo.fileExist==false)
            return false;

//        companyName = regInfo.companyName;
        deviceId = regInfo.deviceId;
        userName = regInfo.userName;
        authKey = regInfo.authKey;
        authMacAddress = regInfo.authMacAddress;
        authVersion = regInfo.authVersion;
        effectiveEndDate = regInfo.effectiveEndDate;
        if(deviceId.length()==8)
            return true;
        return false;
    }
    public static void readKeyFile(ProductInfo regInfo)
    {
//        companyName = regInfo.companyName;
        userName = regInfo.userName;
        authKey = regInfo.authKey;
        authMacAddress = regInfo.authMacAddress;
        authVersion = regInfo.authVersion;
        effectiveEndDate = regInfo.effectiveEndDate;
    }
    public static void onSetFcmToken(String token) {
        regInfo.setAuthFcmToken(token);
    }

    public static boolean writeKeyFile(String endDate)
    {
        return writeKeyFile(deviceId, userName, authKey, authMacAddress, authVersion, endDate);
    }
    public static boolean writeKeyFile(String dId, String user, String aKey,
                                       String macaddr, int version, String endDate)
    {

        String str = String.format(dId+"|"+ user+"|"+ aKey+"|"+macaddr+"|"+version+"|"+endDate);

        deviceId = dId;
        userName = user;
        authKey = aKey;
        authMacAddress = macaddr;
        authVersion = version;

        if (endDate.equals("?"))
        {
            String strThatDay = "21000101";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            try {
                effectiveEndDate = formatter.parse(strThatDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
        {
            effectiveEndDate = new Date(
                    Integer.parseInt(endDate.substring(0, 4)),
                    Integer.parseInt(endDate.substring(4, 2)),
                    Integer.parseInt(endDate.substring(6, 2)));
        }
        File file = new File(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + "PayCast.key");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(Base64Util.encode(str));
            writer.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static class Base64Util {

        /**
         * Encode txt
         * @param txt
         * @return
         * @throws UnsupportedEncodingException
         */
        public static String encode(String txt) throws UnsupportedEncodingException {
            byte[] data = txt.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        }

        /**
         * Decode txt
         * @param txt
         * @return
         * @throws UnsupportedEncodingException
         */
        public static String decode(String txt) throws UnsupportedEncodingException {
            return new String(Base64.decode(txt, Base64.DEFAULT), "UTF-8");
        }
    }
    public static String getFcmTokenParam() {
        return regInfo.getFcmTokenParam();
    }

}
