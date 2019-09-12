package failed.com.realistic.metrolinktimes;

import java.io.Serializable;

public enum Variables implements Serializable{
    URL("url"), EXPORT("export"),JSON("json"),RESULT_RECEIVER("result"),JOB("job"),FETCH("fetch"),UPDATE("update"),DATE("date"),POSITION("position"), FAVORITES("failed.com.realistic.metrolinktimes.sharedprefs.favorites"),
    orange_line(1), red_line(2), green_line(3), purple_line(4), brown_line(5), yellow_line(6), blue_line(7),
    STATION,LOCATION,NORTHTOSOUTH,SOUTHTONORTH,DISTANCE, FAVORITE_IMAGE;
    public String string;
    public int i;
    Variables(){
        string = null;
        i = 0;
    }
    Variables(String s){
        string = s;
    }
    Variables(int i){
        string = null;
        this.i = i;
    }
    public static Variables getbyInt(int i){
        switch(i){
            case 1:
                return orange_line;
            case 2:
                return red_line;
            case 3:
                return green_line;
            case 4:
                return purple_line;
            case 5:
                return brown_line;
            case 6:
                return yellow_line;
            case 7:
                return blue_line;
            default:
                return null;
        }
    }
}
