package kr.co.bbmc.selforderutil;

import android.content.Context;
import android.os.Build;

import com.jcraft.jsch.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProductInfo {

    public static String PRODUCT_REG_URL = "http://auth.bbmc.co.kr/ext/agent/v2/authreq";  //제품등록

    public static String AUTH_VALIDATION_URL = "http://auth.bbmc.co.kr/ext/agent/v2/authexec"; //실행인증

    public static String GET_DEVICE_ID_URL = "http://auth.bbmc.co.kr/ext/agent/v2/deviceid"; //디바이스 ID 가졍기

    public static String REVOKE_AUTH_URL = "http://auth.bbmc.co.kr/ext/agent/v2/authrevoke"; //인증키 회수

    public static String AUTH_FCM_REG_URL = "http://auth.bbmc.co.kr/ext/agent/v2/token";    //FCM 토큰 등록


    public static final String authServer = PRODUCT_REG_URL;
    public static final String authValidationServer = AUTH_VALIDATION_URL;
    public static final String authRegFCMTokenServer = AUTH_FCM_REG_URL;


    //    public static String companyName;
    public static String userName;
    public static String authKey;
    public static String authMacAddress;
    public static int authVersion;
    public static Date effectiveEndDate;
    public static String deviceId;
    public static boolean fileExist;
    public static String fcmToken="";
    public static String osid;

    public ProductInfo(Context c, String name) {
        boolean inValid = false;

        deviceId = "?";
        userName = "?";
        authKey = "?";
        authMacAddress = "?";
        authVersion = 2;

        //authMacAddress = Utils.getMacAddress(c);

        String strThatDay = "21000101";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            effectiveEndDate = formatter.parse(strThatDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        File fd = new File(name);
        if (fd.exists()) {
            deviceId = readTextFile(name);
            fileExist = true;
        } else
            fileExist = false;
        if ((deviceId != null) && !deviceId.isEmpty() && deviceId.length() == 8) {
            File fi = new File(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + "PayCast.key");
            if (fi.exists()) {
                String authEncryptedStr = readTextFile(FileUtils.BBMC_PAYCAST_DATA_DIRECTORY + "PayCast.key");
                if (authEncryptedStr != null && !authEncryptedStr.isEmpty()) {
                    String authStr = null;
                    try {
                        authStr = AuthKeyFile.Base64Util.decode(authEncryptedStr);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String[] parts = authStr.split("\\|");

                    if (parts.length == 6) {
                        if (!deviceId.equals(parts[0])) {
                            inValid = true;
                        } else {
                            deviceId = parts[0];
                            userName = parts[1];
                            authKey = parts[2];
                            authMacAddress = parts[3];

                            int tmpVersion = 2;
                            tmpVersion = Integer.parseInt(parts[4]);
                            authVersion = tmpVersion;

                            if (!parts[5].equals("?") && parts[5].length() == 8) {
                                effectiveEndDate = new Date(
                                        Integer.parseInt(parts[5].substring(0, 4)),
                                        Integer.parseInt(parts[5].substring(4, 2)),
                                        Integer.parseInt(parts[5].substring(6, 2)));
                            }
                        }
                    }
                }
            }
        }
        if ((authMacAddress != null) && !authMacAddress.isEmpty() && !authMacAddress.equals("?")) {
            List<String> macList = Utils.getMacAddress(c);
            if((macList!=null)&&(macList.size()>0)) {
                inValid = true;
                for(int i = 0; i<macList.size(); i++) {
                    String macAddr = macList.get(i);
                    if (macAddr.equalsIgnoreCase(authMacAddress)) {
                        inValid = false;
                        break;
                    }
                }
            }
        }
        if (inValid == true) {
            AuthKeyFile.writeKeyFile("?", "?", "?", "?", 2, "?");
            userName = "?";
            authKey = "?";
            authMacAddress = "?";
            authVersion = 2;
            try {
                effectiveEndDate = formatter.parse(strThatDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isReg() {
        if (!deviceId.equals("?") && !userName.equals("?") && !authKey.equals("?") && !authMacAddress.equals("?")) {
            Date now = Utils.getCurrentDate();
            if (now.after(effectiveEndDate))
                return false;
            else
                return true;
        }
        return false;
    }

    public static String readTextFile(String path) {

        String str = "";
/*
        int data ;
        char ch ;
        FileReader fr = null ;
        File file = new File(path) ;



        try {
            // open file.
            fr = new FileReader(file) ;

            // read file.
            while ((data = fr.read()) != -1) {
                // TODO : use data
                ch = (char) data ;
                System.out.println("ch : " + ch) ;
            }

            fr.close() ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
*/


        File file = new File(path);
        StringBuffer sbuff = new StringBuffer();

        int readcount = 0;
        if (file != null && file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                if(fis==null)
                    return str;
                readcount = (int) file.length();
                byte[] buffer = new byte[4096];
                while ((readcount = fis.read(buffer)) != -1) {
                    sbuff.append(new String(buffer, 0, readcount));
                }
                fis.close();
                str = sbuff.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return str;
    }

    public String getAuthKeyParam(Context c, String majorVersion) {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
        String dueDate = sdfNow.format(effectiveEndDate);
        String mVersion = String.valueOf(Build.VERSION.SDK_INT);
        String deviceParam = "";

        if (deviceId != null) {
            if (deviceId.equals("?"))
                deviceParam = "";
            else
                deviceParam = deviceId;
        }
        if (authMacAddress != null) {
            if (authMacAddress.isEmpty() || authMacAddress.equals("?")) {
                authMacAddress = Utils.getMacAddrOnConnect(c);
            }
        }
        if ((userName != null) && (userName.equals("?")))
            userName = "";
        if ((authKey != null) && (authKey.equals("?")))
            authKey = "";


        String param = null;
        try {
/*
            param = "user=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                    "authkey=" + URLEncoder.encode(authKey, "UTF-8") + "&" +
                    "macaddress=" + URLEncoder.encode(authMacAddress, "UTF-8") + "&" +
                    "version=" + authVersion + "&" +
                    "duedate=" + dueDate + "&" +
                    "type=" + "P" ;
*/

            param = "user=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                    "authkey=" + URLEncoder.encode(authKey, "UTF-8") + "&" +
                    "deviceid=" + URLEncoder.encode(deviceParam, "UTF-8") + "&" +
                    "version=" + URLEncoder.encode(majorVersion, "UTF-8") + "&" +
                    "duedate=" + URLEncoder.encode(dueDate, "UTF-8") + "&" +
                    "ediver=" + URLEncoder.encode(c.getString(R.string.editionCode), "UTF-8") + "&" +
                    "os=" + URLEncoder.encode("A", "UTF-8") + "&" +
                    "osver=" + URLEncoder.encode(mVersion, "UTF-8") + "&" +
                    "osid=" + URLEncoder.encode(authMacAddress, "UTF-8") + "&" +
                    "type=" + URLEncoder.encode("A", "UTF-8");  //PAYCAST : A, Signcast Lite player : P


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param;
    }

    public String getRevokeAuthKeyParam(Context c) {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
        String dueDate = sdfNow.format(effectiveEndDate);

        String param = null;
        try {
            param = String.format("authkey=" + URLEncoder.encode(authKey, "UTF-8") + "&" +
                    "macaddress=" + URLEncoder.encode(authMacAddress, "UTF-8") + "&" +
                    "version=" + authVersion + "&" +
                    "type=" + "A");  //PAYCAST : A, Signcast Lite player : P
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param;
    }

    public String getAuthServer() {
        return authServer;
    }

    public String getAuthValidationServer() {
        return authValidationServer;
    }

    public String getAuthRevokeAuthServer() {
        return REVOKE_AUTH_URL;
    }

    public String getAuthMacAddress() {
        return authMacAddress;
    }

    public String getAuthDeviceId() {
        return deviceId;
    }

    public String getAuthRegFCMTokenServer() {
        return authRegFCMTokenServer;
    }

    public String getAuthTokenParam() {
        String param = null;
        try {
            param = "deviceId=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
                    "token=" + URLEncoder.encode(fcmToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return param;
    }

    public void setAuthFcmToken(String token)
    {
        fcmToken =token;
    }

    public String getFcmTokenParam() {
        String deviceParam = "";

        if (deviceId != null) {
            if (deviceId.equals("?"))
                deviceParam = "";
            else
                deviceParam = deviceId;
        }


        String param = null;
        if((fcmToken!=null)&&(!fcmToken.isEmpty())) {
            try {
                param = "deviceId=" + URLEncoder.encode(deviceParam, "UTF-8") + "&" +
                        "token=" + URLEncoder.encode(fcmToken, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return param;
    }
}
