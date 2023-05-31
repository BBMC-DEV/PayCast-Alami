package kr.co.bbmc.selforderutil;

public class PaymentInfoData {
    public String payMethod;           //1: 신용카드, 2: 현금 영수증, c: 취소, other:선불카드
    public String tradingMedium;    //IC: IC 거래, MS : MS 거래, RF : RF 거래, Other : 현금영수증
    public String cardName;     //카드명 ex) 하나카드, 우리카드
    public String cardNumber;   //카드번호  ex) 123456******
    public String catId;        //CATID
    public String approvalNum;  //승인번호
    public String installmentMonth;     //할부개월
    public String tradingDate;      //거래일시  yyyy-MM-DD hh:mm:ss
    public String storeMerchantNum; //가맹점 번호
    public String payAmount;        //공급가
    public String payVat;           //부가세
    public String sumAmount;        //총결제금액
    public String transactionNumber;    //거래 고유번호
    public String issueer;              //발급사
    public String issueerName;              //발급사명
    public String acquirer;             //매입사
    public String acquirerName;              //매입사명

}
