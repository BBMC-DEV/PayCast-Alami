package kr.co.bbmc.paycastdid.model;

import android.view.animation.Animation;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.bbmc.paycastdid.MainActivity;

public class BlinkAlarmData {
    public String blinkFlag = "N";
    public String orderSeq = "";
    public String alarmDate ="";
    public Timer  blinkTimer;
    public Animation animation;
    public MainActivity.OnBlinkTimerTask blinkTimerTask;
}
