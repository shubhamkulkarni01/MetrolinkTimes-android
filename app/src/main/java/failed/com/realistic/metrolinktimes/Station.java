package failed.com.realistic.metrolinktimes;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Station extends Object implements Serializable {
    public static final String NAME = "name";
    public static final String TIME = "time";
    public int north_position;
    public String route_name;
    public boolean favorite = false;
    public int southposition;
    public String name;
    public Variables line;
    public ArrayList<Date> northdatelist = new ArrayList<Date>();
    public ArrayList<Date> southdatelist = new ArrayList<Date>();
    public ArrayList<Integer> northtrain_id = new ArrayList<Integer>();
    public ArrayList<Integer> southtrain_id = new ArrayList<Integer>();
    public BigDecimal latitude;
    public BigDecimal longitude;
    public float distance;

    public static Map getDirection_Time(Station station, Date now) {
        ArrayList<Date> datelist = station.northdatelist;
        ArrayList<Integer> trainid = station.northtrain_id;
        SuperPair a = helpermethod(datelist, trainid);
        station.north_position = a.getK();
        datelist = station.southdatelist;
        trainid = station.southtrain_id;
        SuperPair b =helpermethod(datelist, trainid);
        station.southposition = b.getK();
        Map h = new LinkedHashMap<String, SuperPair>();
        h.put(StationListAdapter.north, a);
        h.put(StationListAdapter.south, b);
        return h;
    }
    private static SuperPair helpermethod(ArrayList<Date> datelist, ArrayList<Integer> trainid){
        Collections.sort(datelist);
        Collections.sort(trainid);
        ArrayList<String> times = new ArrayList<String>();
        ArrayList<String> directions = new ArrayList<String>();
        int k = 0;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
        for (int i = 0; i < datelist.size(); i++) {
            Date possible = datelist.get(i);
            if (possible.after(now)) {
                k = i;
                break;
            }
            else if (i == datelist.size()-1) {
                k=i;
            }
        }
        //getting the times
        int l = k+1;
        int j = k-1;
        if (l>= datelist.size()) l=0;
        if(j<0) j=0;
        String before = sdf.format(datelist.get(j));
        String next = sdf.format(datelist.get(k));
        String one_after = sdf.format(datelist.get(l));
        //adding the times
        if (!before.equals(next)) times.add(before);
        else times.add("No trains.");
        times.add(next);
        if (!one_after.equals(next)) times.add(one_after);
        else times.add("No trains.");
        ArrayList<Integer> intarray = new ArrayList<Integer>();
        intarray.add(trainid.get(j));
        intarray.add(trainid.get(k));
        intarray.add(trainid.get(l));
        String direction = null;
        for(Integer i:intarray) {
            if (i%2 == 1) direction = StationListAdapter.north;
            else direction= StationListAdapter.south;
            directions.add(direction);
        }
        return new SuperPair(times, directions, k);
    }

    @Override
    public boolean equals(Object o) {
        Station station;
        Boolean matches = false;
        if (o instanceof Station) {
            station = (Station) o;
            matches = this.name.equals(station.name);
        }
        return matches;
    }
    @Override
    public String toString() {
        String json;
        try{
            JSONObject stationjson = new JSONObject();
            stationjson.put("name", name);
            stationjson.put("line", line.i);
            stationjson.put("latitude", latitude);
            stationjson.put("longitude", longitude);
            stationjson.put("route_id", route_name);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            JSONArray stationnorthtimes = new JSONArray();
            for (Date d: northdatelist){
                stationnorthtimes.put(sdf.format(d));
            }
            stationjson.put("stationnorthtimes", stationnorthtimes);
            JSONArray stationsouthtimes = new JSONArray();
            for (Date d: southdatelist){
                stationsouthtimes.put(sdf.format(d));
            }
            stationjson.put("stationsouthtimes", stationsouthtimes);
            JSONArray stationnorthdirection = new JSONArray();
            for (Integer d: northtrain_id){
                stationnorthdirection.put(d);
            }
            stationjson.put("stationnorthtrainid", stationnorthdirection);
            JSONArray stationsouthdirection = new JSONArray();
            for (Integer d: southtrain_id){
                stationsouthdirection.put(d);
            }
            stationjson.put("stationsouthtrainid", stationsouthdirection);
            json = stationjson.toString();}
        catch (Exception e){
            e.printStackTrace();
            return name;
        }
        return json;
    }

    public ArrayList<String> northdatelisttostring() {
        ArrayList<String> string = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        for(Date d : northdatelist){
            string.add(sdf.format(d));
        }
        return string;
    }
    public ArrayList<String> southdatelisttostring() {
        ArrayList<String> string = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        for(Date d : southdatelist){
            string.add(sdf.format(d));
        }
        return string;
    }
}