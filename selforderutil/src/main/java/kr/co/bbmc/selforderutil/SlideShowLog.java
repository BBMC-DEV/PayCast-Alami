package kr.co.bbmc.selforderutil;

import java.util.Date;

public class SlideShowLog {
    public int seq;
    public String startTime;
    public double sec;
    public String scheduleName;
    public Date playDate;

    public int getSeq()
    {
        return seq;
    }
    public void setSeq(int a)
    {
        seq = a;
    }
    public String getStartTime()
    {
        return startTime;
    }
    public void setStartTime(String t) {
        startTime = t;
    }
    public double getSec () {
        return sec;
    }
    public void setSec (double d) {
        sec = d;
    }

    // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
    public String getScheduleName()
    {
        return scheduleName;
    }
    public void setScheduleName(String n)
    {
        scheduleName = n;
    }

    // jason:yesterdayadlog: 멤버 추가
    public Date getPlayDate()
    {
        return new Date(Integer.valueOf(startTime.substring(0, 4)), Integer.valueOf(startTime.substring(4, 2)), Integer.valueOf(startTime.substring(6, 2)));
    }

    public SlideShowLog(int seq, String startTime, double sec)
    {
        this.seq = seq;
        this.startTime = startTime;
        this.sec = sec;
    }

    // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
    public SlideShowLog(int seq, String startTime, double sec, String schedule)
    {
        this.seq = seq;
        this.startTime = startTime;
        this.sec = sec;
        this.scheduleName = schedule;
    }
    //-
}
