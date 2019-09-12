package failed.com.realistic.metrolinktimes;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Result;

public class SyncIntentService extends IntentService {
    public static final String quote = "\"";
    public ArrayList<Station> station_list;
    public SyncIntentService() {
        super("syncservice");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        switch (((Variables) intent.getSerializableExtra(Variables.JOB.string))) {
            case FETCH:
                fetch(intent);
                break;
            case UPDATE:
                update(intent);
                break;
            case EXPORT:
                export(intent);
                break;
        }
    }
    protected void fetch(Intent intent) {
        String urlunparsed= intent.getStringExtra(Variables.URL.string);
        String json = null;
        try{
            if(urlunparsed == null){
                ((ResultReceiver) intent.getParcelableExtra((Variables.RESULT_RECEIVER.string))).send(200, null);
                return;
            }
            URL url = new URL(urlunparsed);
            File file = new File(getFilesDir(), MainActivity.TITLE+".txt");
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
            if (networkinfo != null || file.exists()){
                if (file.exists()){
                    Log.d("using", "file");
                    json = open(new FileInputStream(file));
                }
                else if (networkinfo.isConnected()) {
                    Log.d("using", "network");
                    Document doc = Jsoup.parse(open(url.openStream()));
                    Elements scripts = doc.select("#container > script:eq(2)");
                    Element script = scripts.first();
                    String script_element = script.toString();
                    Integer i = script_element.indexOf("[");
                    Integer j = script_element.indexOf("]") + 1;
                    json = script_element.substring(i, j);
                    cache(json, file);
                }
            }
            else {
                String name = file.getName();
                json = open(getResources().getAssets().open(name));
            }
            if(!json.isEmpty()){
                ArrayList<Station> station = parse(json, intent);
                ResultReceiver receiver = intent.getParcelableExtra(Variables.RESULT_RECEIVER.string);
                if(station == null)receiver.send(0, new Bundle());
                else{
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("result", station);
                    receiver.send(100, bundle);}
            }
            else{
                ResultReceiver receiver = intent.getParcelableExtra(Variables.RESULT_RECEIVER.string);
                receiver.send(0, new Bundle());
                throw new Exception();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            ResultReceiver receiver = intent.getParcelableExtra(Variables.RESULT_RECEIVER.string);
            receiver.send(0, new Bundle());
        }
    }
    protected String getJSON(String string){
        String urlunparsed=string;
        String json = null;
        try {
            URL url = new URL(urlunparsed);
            File file = new File(getFilesDir(), Variables.JSON.string);
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
            if (networkinfo.isConnected()) {
                Document doc = Jsoup.parse(open(url.openStream()));
                Elements scripts = doc.select("#container > script:eq(2)");
                Element script = scripts.first();
                String script_element = script.toString();
                Integer i = script_element.indexOf("[");
                Integer j = script_element.indexOf("]") + 1;
                json = script_element.substring(i, j);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
    protected void update (Intent intent){
        try {
            String date = intent.getStringExtra(Variables.DATE.string);
            Station station = (Station) intent.getSerializableExtra(Variables.STATION.string);
            createjson(station, date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void export (Intent intent){
        try {
            String s = createfulljson(parse(getJSON(intent.getStringExtra(Variables.URL.string)), null));
            cache(s, new File(getFilesDir(), getResources().getStringArray(R.array.line_name)[intent.getIntExtra(Variables.POSITION.string,0)].replace(" ","_")+"_json.txt"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected ArrayList<Station> parse(String json, Intent intent) throws Exception {

        JSONArray jsonArray = new JSONArray(json);
        ArrayList<Station> sstation_list = new ArrayList<Station>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.optJSONObject(i);
            String stationname = obj.optString("stop_name");
            String trainid = obj.optString("trip_short_name");
            String timing = obj.optString("arrival_time");
            String latitude = obj.optString("stop_lat");
            String longitude = obj.optString("stop_lon");
            String route_id = obj.optString("route_id");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            Date realtime = sdf.parse(timing);
            realtime.setYear(new Date().getYear());
            realtime.setDate(new Date().getDate());
            realtime.setMonth(new Date().getMonth());
            Station station = new Station();
            stationname = stationname.substring(0, stationname.indexOf("Metrolink"));
            station.name = stationname;
            station.route_name = route_id;
            try{
                station.line = Variables.getbyInt(intent.getIntExtra("line", 0));
                if (Integer.parseInt(trainid)%2==1){station.northdatelist.add(realtime);
                    station.northtrain_id.add(Integer.parseInt(trainid));}
                else{station.southdatelist.add(realtime);
                    station.southtrain_id.add(Integer.parseInt(trainid));}}
            catch(NumberFormatException e){
                e.printStackTrace();
                continue;
            }
            station.latitude = BigDecimal.valueOf(Double.parseDouble(latitude));
            station.longitude = BigDecimal.valueOf(Double.parseDouble(longitude));
            if (sstation_list.contains(station)) {
                Station s = sstation_list.get(sstation_list.indexOf(station));
                if (Integer.parseInt(trainid)%2==1) {s.northdatelist.add(realtime);
                    s.northtrain_id.add(Integer.parseInt(trainid));}
                else{s.southdatelist.add(realtime);
                    s.southtrain_id.add(Integer.parseInt(trainid));}
            }
            else sstation_list.add(station);
        }
        return sstation_list;
    }
    protected String createjson (Station station, String date) {
        String start = "{";
        String name = quote+station.NAME+quote+":"+quote+station.name+quote+"\n";
        String time = quote+station.TIME+quote+":"+quote+date+quote+"\n";
        String end = "}";
        return start+name+time+end;
    }
    protected void cache (String json, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected String open(InputStream inputStream) {
        String json_trial = null;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            json_trial = sb.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return json_trial;
    }
    protected String createfulljson (ArrayList<Station> station_list) throws Exception {
        JSONArray jsonArray = new JSONArray();
        for(Station station:station_list){
            JSONObject stationjson = new JSONObject();
            stationjson.put("name", station.name);
            stationjson.put("latitude", station.latitude);
            stationjson.put("longitude", station.longitude);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            JSONArray stationnorthtimes = new JSONArray();
            for (Date d: station.northdatelist){
                stationnorthtimes.put(sdf.format(d));
            }
            stationjson.put("stationnorthtimes", stationnorthtimes);
            JSONArray stationsouthtimes = new JSONArray();
            for (Date d: station.southdatelist){
                stationsouthtimes.put(sdf.format(d));
            }
            stationjson.put("stationsouthtimes", stationsouthtimes);
            JSONArray stationnorthdirection = new JSONArray();
            for (Integer d: station.northtrain_id){
                stationnorthdirection.put(d);
            }
            stationjson.put("stationnorthtrainid", stationnorthdirection);
            JSONArray stationsouthdirection = new JSONArray();
            for (Integer d: station.southtrain_id){
                stationsouthdirection.put(d);
            }
            stationjson.put("stationsouthtrainid", stationsouthdirection);
            jsonArray.put(stationjson);
        }
        return jsonArray.toString();
    }
}
