package kr.co.bbmc.paycastdid;

import android.view.animation.Animation;

import java.util.Timer;
import java.util.TimerTask;

public class BlinkAlarmData {
    String blinkFlag = "N";
    String orderSeq = "";
    String alarmDate ="";
    Timer  blinkTimer;
    Animation animation;
    MainActivity.OnBlinkTimerTask blinkTimerTask;
}
