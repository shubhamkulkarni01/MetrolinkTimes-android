package failed.com.realistic.metrolinktimes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/*class Station_Displayold extends ActionBarActivity implements OnMapReadyCallback {
    ArrayList<String> datearray = new ArrayList<String>();
    ArrayList<Date> dates;
    Date date;
    String dateformatted;
    Station station;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationdisplay);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        ListView lv = (ListView) findViewById(R.id.northlv);
        mapFragment.getMapAsync(this);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        Intent intent = getIntent();
        station = (Station) intent.getSerializableExtra("" +
                "station");
        ab.setTitle(station.name);
        dates = station.northdatelist;
        Collections.sort(dates);
        for (Date date : dates) {
            datearray.add(new SimpleDateFormat("hh:mm:ss aa", Locale.US).format(date));
            ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datearray);
            lv.setAdapter(arrayadapter);
            arrayadapter.notifyDataSetChanged();
            lv.smoothScrollToPosition(station.north_position);
        }
    }
        @Override
        public void onMapReady (GoogleMap googleMap){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(station.latitude.doubleValue(), station.longitude.doubleValue())).title(station.name));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(station.latitude.doubleValue(), station.longitude.doubleValue())));
            googleMap.moveCamera(CameraUpdateFactory.zoomIn());
        }
    }*/