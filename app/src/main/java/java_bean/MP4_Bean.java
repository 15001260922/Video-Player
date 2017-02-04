package java_bean;

import java.io.Serializable;

/**
 * Created by previous_off_yuan on 2016/12/14.
 */
public class MP4_Bean implements java.io.Serializable{

    private String mp4Name;
    private long mp4Time;
    private long mp4Size;
    private String mp4Address;
    private String mp4Artist;
    private int mp4State;

    public MP4_Bean() {
    }

    public MP4_Bean(String mp4Name, long mp4Time, long mp4Size, String mp4Address, String mp4Artist, int mp4State) {
        this.mp4Name = mp4Name;
        this.mp4Time = mp4Time;
        this.mp4Size = mp4Size;
        this.mp4Address = mp4Address;
        this.mp4Artist = mp4Artist;
        this.mp4State = mp4State;
    }

    public String getMp4Name() {
        return mp4Name;
    }

    public void setMp4Name(String mp4Name) {
        this.mp4Name = mp4Name;
    }

    public long getMp4Time() {
        return mp4Time;
    }

    public void setMp4Time(long mp4Time) {
        this.mp4Time = mp4Time;
    }

    public long getMp4Size() {
        return mp4Size;
    }

    public void setMp4Size(long mp4Size) {
        this.mp4Size = mp4Size;
    }

    public String getMp4Address() {
        return mp4Address;
    }

    public void setMp4Address(String mp4Address) {
        this.mp4Address = mp4Address;
    }

    public String getMp4Artist() {
        return mp4Artist;
    }

    public void setMp4Artist(String mp4Artist) {
        this.mp4Artist = mp4Artist;
    }

    public int getMp4State() {
        return mp4State;
    }

    public void setMp4State(int mp4State) {
        this.mp4State = mp4State;
    }

    @Override
    public String toString() {
        return "MP4_Bean{" +
                "mp4Name='" + mp4Name + '\'' +
                ", mp4Time=" + mp4Time +
                ", mp4Size=" + mp4Size +
                ", mp4Address='" + mp4Address + '\'' +
                ", mp4Artist='" + mp4Artist + '\'' +
                ", mp4State=" + mp4State +
                '}';
    }
}
