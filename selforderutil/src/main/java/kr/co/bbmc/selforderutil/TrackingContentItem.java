package kr.co.bbmc.selforderutil;

import android.net.Uri;

import java.sql.Time;
import java.util.Comparator;

public class TrackingContentItem {
    public int seq;
    public String name;
    public String type;
    public Uri thumbnail;
    public Time playTime;

    public boolean isSelected;
    public boolean isMultipleContent;

    // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
    public String scheduleName;

    public int getSeq() {
        return seq;
    }
    public String getName() {
        return name;
    }
    public String getType(){
        return type;
    }
    public Uri getThumbnail() {
        return thumbnail;
    }
    public Time getPlayTime(){
        return playTime;
    }
    public boolean getIsSelected()
    {
        return isSelected;
    }
    public boolean getIsMultipleContent()
    {
        return isMultipleContent;
    }

    // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
    public String getScheduleName()
    {
        return scheduleName;
    }


    public TrackingContentItem(int seq, String name)
    {
        this.seq = seq;
        this.name = name;
        // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
        this.scheduleName = "";
    }

    public TrackingContentItem(int seq, String name, String type, Uri thumbnail, Time playTime)
    {
        this.seq = seq;
        this.name = name;
        this.type = type;
        this.thumbnail = thumbnail;
        this.playTime = playTime;
        // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
        this.scheduleName = "";
    }

    // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
    public TrackingContentItem(int seq, String name, String schedule)
    {
        this.seq = seq;
        this.name = name;
        this.playTime = playTime;
        // hazel:adtrackschedulename: 광고추적 로그에 스케줄이름 추가(2013/05/22)
        this.scheduleName = schedule;
    }

    public TrackingContentItem(int seq, String name, String type, Uri thumbnail, Time playTime, String schedule)
    {
        this.seq = seq;
        this.name = name;
        this.type = type;
        this.thumbnail = thumbnail;
        this.playTime = playTime;
        this.scheduleName = schedule;
    }
    //-

    public String PlayTimeDisplay()
    {
        return Utils.simpleTimeFormat(playTime) + ((isMultipleContent) ? ", ..." : "");
    }

    public class TrackingContentItemComparer implements Comparator<TrackingContentItem>
    {
        @Override
        public int compare(TrackingContentItem o1, TrackingContentItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
