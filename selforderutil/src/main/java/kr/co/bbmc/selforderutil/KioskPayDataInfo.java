package kr.co.bbmc.selforderutil;

import java.util.ArrayList;

public class KioskPayDataInfo {
        public String tid;      //거래고유번호
        public String mid;      //가맹점 번호
        public String fnCd;     //발급사 코드
        public String fnCd1;    //매입사 코드
        public String fnName1;  //매입사명

        public String storeIdpay;    //매장 ID
        public String totalindex;      //주문상품 개수
        public String goodsAmt;        //메뉴 주문 총 금액
        public String goodsTotal;     //메뉴 주문 총 수량
        public String orderNumber;     //주문번호
        public String orderDate;       //주문날짜
        public String authCode;        //승인번호
        public String fnName;          //결제카드사명",  (예 : 삼성 / 우리)
        public String catID;           //cat id

        public ArrayList<KioskOrderMenuItem> orderMenuList = new ArrayList<KioskOrderMenuItem>();

}
