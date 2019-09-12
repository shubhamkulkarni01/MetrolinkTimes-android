package failed.com.realistic.metrolinktimes;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Station_Display extends Fragment implements OnMapReadyCallback, FragmentCommunicator {
    Station station;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("onCreateView called", "Proceeding with layout inflation");
        View v = inflater.inflate(R.layout.activity_stationdisplay, container, false);
        station = (Station)getArguments().getSerializable("station");
        SupportMapFragment mapFragment = new com.google.android.gms.maps.SupportMapFragment();
        //northbound
        ListView nlv = ((ListView) v.findViewById(R.id.northlv));

        nlv.setAdapter(new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_list_item_1, station.northdatelisttostring()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (station.northdatelist.get(position) == station.northdatelist.get(station.north_position)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                    int color = Color.BLUE;
                    Date d = null;
                    Date now = new Date();
                    try {
                        d = sdf.parse(((TextView) super.getView(position, convertView, parent).findViewById(android.R.id.text1)).getText().toString());
                        d.setMonth(now.getMonth());
                        d.setYear(now.getYear());
                        d.setDate(now.getDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!d.after(now)) {
                        color = Color.RED;
                    }
                    View v = super.getView(position, convertView, parent);
                    TextView text = (TextView) v.findViewById(android.R.id.text1);
                    text.setTextColor(color);
                    Log.e("int", String.valueOf(position));
                    Log.e("text", text.getText().toString());
                    return text;
                }
                int color = Color.BLACK;
                View v = super.getView(position, convertView, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(color);
                return v;
            }
        });
        nlv.setSelection(station.north_position);
        Log.i("north is", String.valueOf(station.north_position));

        //southbound
        ListView slv = ((ListView) v.findViewById(R.id.southlv));
        slv.setAdapter(new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_list_item_1, station.southdatelisttostring()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (station.southdatelist.get(position) == station.southdatelist.get(station.southposition)) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                    int color = Color.BLUE;
                    Date d = null;
                    Date now = new Date();
                    try {
                        d = sdf.parse(((TextView) super.getView(position, convertView, parent).findViewById(android.R.id.text1)).getText().toString());
                        d.setMonth(now.getMonth());
                        d.setYear(now.getYear());
                        d.setDate(now.getDate());
                        Log.i("d", "is not null");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("date", d.toString());
                    if (!d.after(now)) {
                        color = Color.RED;
                    }
                    View v = super.getView(position, convertView, parent);
                    TextView text = (TextView) v.findViewById(android.R.id.text1);
                    text.setTextColor(color);
                    return text;
                }
                int color = Color.BLACK;
                View v = super.getView(position, convertView, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(color);
                return v;

            }
        });
        slv.setSelection(station.southposition);
        Log.v("changed color", "slv");
        Log.i("south is", String.valueOf(station.southposition));
        //map
        ((FragmentActivity)container.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
        Bundle b = new Bundle();
        b.putBoolean("toClose", false);
        ((FragmentCommunicator)container.getContext()).sendmsg(b);
        return v;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(station.latitude.doubleValue(), station.longitude.doubleValue())).title(station.name));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(station.latitude.doubleValue(), station.longitude.doubleValue())));

    }

    @Override
    public void sendmsg(Bundle b) {

    }
}
