package kr.co.bbmc.selforderutil;

public class DidOptionEnv {
    public int stbUdpPort=11001;
    public int serverPort=8281;
    public String stbServiceType="S";
    public String serverHost="test.signcast.co.kr";
    public String serverUkid="signcast";
    public String storeId="";    //매장 번호
    public String deviceId="";     //deviceId;

    public int stbId;
    public String stbName="";

    public boolean ftpActiveMode;
    public String ftpHost="";
    public int ftpPort;
    public String ftpUser="";
    public String ftpPassword="";

    public int stbStatus;       //0: 미확인, 2 : 장비꺼짐, 3:모니터 꺼짐, 4:플레이어 꺼짐, 5:스케줄 미지정, 6: 정상방송

    public String storeName="";    //매장명
    public String storeAddr="";        //매장 주소
    public String storeBusinessNum=""; //매장사업자 번호
    public String storeTel="";         //매장 전화번호
    public String storeMerchantNum=""; //매장 가맹점 번호
    public String storeCatId="";       //kiosk 카드 단말기 cat id
    public String storeRepresent="";    //매장 대표자 명
    public String operatingTime="";    //매장 영업시간
    public String introMsg="";         //매장소개글


}
