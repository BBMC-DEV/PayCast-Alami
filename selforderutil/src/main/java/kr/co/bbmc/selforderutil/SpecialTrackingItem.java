package kr.co.bbmc.selforderutil;

public class SpecialTrackingItem {
    public int seq;
    public String name;
    public String type;
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
    public String getScheduleName(){
        return scheduleName;
    }
    public void setSeq(int s) {
        seq = s;
    }
    public void setName(String n) {
        name = n;
    }
    public void setType(String t){
        type = t;
    }
    public void setScheduleName(String n){
        scheduleName = n;
    }

    public SpecialTrackingItem(String type, int seq, String name, String scheduleName)
    {
        this.type = type;
        this.seq = seq;
        this.name = name;
        this.scheduleName = scheduleName;
    }
}
