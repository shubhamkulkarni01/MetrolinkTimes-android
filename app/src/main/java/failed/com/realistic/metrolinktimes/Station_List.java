package failed.com.realistic.metrolinktimes;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class Station_List extends Fragment implements FragmentCommunicator{
    StationListAdapter adapter;
    ArrayList<Station> stations;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        Log.i("Successfully", "inflated view");
        stations = (ArrayList) getArguments().getSerializable("list");
        if(stations == null) stations = new ArrayList<>();
        ArrayList<String> favorites = (ArrayList) getArguments().getSerializable("favorites");
        boolean favoritesonly = getArguments().getBoolean("favorites_only");
        if(favorites != null){
            for(String s:favorites) {
                Station station = null;
                try {
                    station = new Station();
                    JSONObject jsonObject = new JSONObject(s);
                    station.name = jsonObject.optString("name");
                    station.line = Variables.getbyInt(jsonObject.optInt("line"));
                    station.longitude = BigDecimal.valueOf(Double.valueOf(jsonObject.optString("longitude")));
                    station.latitude = BigDecimal.valueOf(Double.valueOf(jsonObject.optString("latitude")));
                    station.route_name = jsonObject.optString("route_id");
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
                    JSONArray stationnorthtimes = jsonObject.getJSONArray("stationnorthtimes");
                    JSONArray stationnorthtrainid = jsonObject.getJSONArray("stationnorthtrainid");
                    for (int i = 0; i < stationnorthtimes.length(); i++){
                        station.northdatelist.add(sdf.parse(stationnorthtimes.getString(i)));
                        station.northtrain_id.add(stationnorthtrainid.getInt(i));
                    }
                    JSONArray stationsouthtimes = jsonObject.getJSONArray("stationsouthtimes");
                    JSONArray stationsouthtrainid = jsonObject.getJSONArray("stationsouthtrainid");
                    for (int j = 0; j< stationsouthtimes.length(); j++){
                        station.southdatelist.add(sdf.parse(stationsouthtimes.getString(j)));
                        station.southtrain_id.add(stationsouthtrainid.getInt(j));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                if (favoritesonly){
                    station.favorite = true;
                    stations.add(station);
                }
                else{
                    try{
                        stations.get(stations.indexOf(station)).favorite = true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }
        if(stations.size() == 0){
            Toast.makeText(container.getContext(), "No Favorites! Tap the heart next to a station to add it to Favorites.", Toast.LENGTH_LONG).show();
            v = new LinearLayout(container.getContext());
            return v;
        }
        else{
            adapter = new StationListAdapter(stations, container.getContext());
            adapter.setList(stations);
            Log.i("Successfully", "got args");
            RecyclerView rv = (RecyclerView)v.findViewById(R.id.my_recycler_view);
            rv.setLayoutManager(new LinearLayoutManager(container.getContext()));
            rv.setAdapter(adapter);
            Log.i("Successfully", "set rv manager and adapter");
            Bundle b = new Bundle();
            b.putBoolean("toClose", true);
            ((FragmentCommunicator)container.getContext()).sendmsg(b);
            return v;}

    }

    @Override
    public void sendmsg(Bundle b) {
        if(b.getBoolean(MainActivity.invalidateDataSet)){
            adapter.setList(((ArrayList<Station>) b.getSerializable("list")));
            adapter.notifyDataSetChanged();
        }
        if(b.getBoolean("heart_pressed")){
            stations.get(b.getInt("station")).favorite = !stations.get(b.getInt("station")).favorite;
            adapter.setList(stations);
            adapter.notifyItemChanged(b.getInt("station"));
        }
    }
}
