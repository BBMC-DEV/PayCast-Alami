package kr.co.bbmc.selforderutil;

import java.util.Date;

public class SlideShowTouchLog {
    private String name;
    private String ukid;
    private String scheduleName;
    private String touchTime;
    private Date touchDate;

    public String getName() {
        return name;
    }
    public String getUkid() {
        return ukid;
    }
    public String getScheduleName()
    {
        return scheduleName;
    }
    public String getTouchTime() {
        return touchTime;
    }
    public void setName(String n) {
        this.name = n;
    }
    public void setUkid(String id) {
        this.ukid = id;
    }
    public void setScheduleName(String schName)
    {
        this.scheduleName = schName;
    }
    public void setTouchTime(String touch) {
        this.touchTime = touch;
    }

    public Date getTouchDate()
    {
        return new Date(Integer.valueOf(touchTime.substring(0, 4)), Integer.valueOf(touchTime.substring(4, 2)), Integer.valueOf(touchTime.substring(6, 2)));
    }

    public SlideShowTouchLog(String name, String ukid, String touchTime, String schedule)
    {
        this.name = name;
        this.ukid = ukid;
        this.touchTime = touchTime;
        this.scheduleName = schedule;
    }
}
