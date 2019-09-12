package failed.com.realistic.metrolinktimes;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements FragmentCommunicator, View.OnClickListener, MyResultReceiver.Receiver, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public static String TITLE;
    public static int position;
    public static final String invalidateDataSet = "invalidateDataSet";
    public static boolean toClose = true;
    public static ArrayList<Station> m_station_list;
    public boolean DISTANCE_AVAILIBLE = false;
    public static Variables comparator = Variables.NORTHTOSOUTH;
    Location mLastLocation;
    Intent intent;
    GoogleApiClient mGoogleApiClient;
    @SuppressWarnings({"deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getIntent().getIntExtra(Variables.POSITION.string, 0);
        TITLE = getResources().getStringArray(R.array.line_name)[position];
        getSupportActionBar().setDisplayHomeAsUpEnabled(toClose);
        Log.d(""+position, TITLE);
        String url = null;
        MyResultReceiver resultReceiver = new MyResultReceiver(new Handler());
        resultReceiver.setReceiver(this);
        if (position != 0) url = getResources().getStringArray(R.array.line_urls)[position-1];
        else {
            resultReceiver.send(50, null);
        }
        buildGoogleApiClient();
        intent = new Intent(this, SyncIntentService.class);
        Log.i("built", "intent");
        intent.putExtra(Variables.JOB.string, Variables.FETCH);
        intent.putExtra(Variables.URL.string, url);
        intent.putExtra("line", position);
        intent.putExtra(Variables.RESULT_RECEIVER.string, resultReceiver);
        Log.i("inserted", "all but location");
        setContentView(R.layout.fragmentlayout);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("11A31E65FE177E34A66ED8277AAD0326")  // An example device ID
                .build();
        mAdView.loadAd(adRequest);
    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        getSupportActionBar().setTitle(TITLE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.comparator_toggle);
        switch(MainActivity.comparator){
            case NORTHTOSOUTH:
                menuItem.setIcon(R.drawable.distance);
                break;
            case SOUTHTONORTH:
                menuItem.setIcon(R.drawable.northtosouth);
                break;
            case DISTANCE:
                menuItem.setIcon(R.drawable.southtonorth);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if(!toClose)returning();
                else finish();
                break;
            case R.id.export_data:
                Intent export = new Intent(this, SyncIntentService.class);
                export.putExtra(Variables.JOB.string, Variables.EXPORT);
                export.putExtra(Variables.URL.string, getResources().getStringArray(R.array.line_urls)[position]);
                export.putExtra(Variables.POSITION.string, position);
                startService(export);
                Toast.makeText(this, "Successfully exported " + getResources().getStringArray(R.array.line_name)[position] + " data", Toast.LENGTH_SHORT).show();
                break;
            case R.id.comparator_toggle:
                Bundle b = new Bundle();
                b.putBoolean(MainActivity.invalidateDataSet, true);
                switch(comparator){
                    case NORTHTOSOUTH:
                        Collections.sort(m_station_list, new NorthtoSouthComparator());
                        MainActivity.comparator = Variables.SOUTHTONORTH;
                        invalidateOptionsMenu();
                        break;
                    case SOUTHTONORTH:
                        Collections.sort(m_station_list, new SouthtoNorthComparator());
                        if (DISTANCE_AVAILIBLE == true)MainActivity.comparator = Variables.DISTANCE;
                        else MainActivity.comparator = Variables.NORTHTOSOUTH;
                        invalidateOptionsMenu();
                        break;
                    case DISTANCE:
                        Collections.sort(m_station_list, new DistanceComparator());
                        MainActivity.comparator = Variables.NORTHTOSOUTH;
                        invalidateOptionsMenu();
                        break;
                    default:
                        Collections.sort(m_station_list, new NorthtoSouthComparator());
                        MainActivity.comparator = Variables.SOUTHTONORTH;
                        invalidateOptionsMenu();
                        break;
                }
                //DEBUG
                b.putSerializable("list", m_station_list);
                ((FragmentCommunicator) getFragmentManager().findFragmentById(R.id.fragment)).sendmsg(b);
        }
        return super.onOptionsItemSelected(item);

    }
    public void returning(){
        Fade fade = new Fade(Fade.OUT);
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.fragment), fade);
        getFragmentManager().popBackStack();
        toClose = true;
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(TITLE);
        invalidateOptionsMenu();
    }
    @Override
    public void onBackPressed(){
        if(!toClose)returning();
        else finish();
    }
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("" + resultCode, "check the status");
        switch(resultCode){
            case 100:
                Log.i("Message Received", "Message Received");
                m_station_list = (ArrayList<Station>) resultData.getSerializable("result");
                try{
                    for(Station station: m_station_list){
                        float[] array = new float[1];
                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), station.latitude.doubleValue(), station.longitude.doubleValue(), array);
                        station.distance=array[0];
                        DISTANCE_AVAILIBLE = true;
                    }}
                catch (NullPointerException e){
                    e.printStackTrace();
                }
                Collections.sort(m_station_list, new StationListComparator());
                ArrayList<String> favorites = null;
                try {
                    favorites = new ArrayList<>(getPreferences(MODE_PRIVATE).getStringSet(Variables.FAVORITES.string, null));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
                if(fragment == null){
                    fragment = new Station_List();
                    Log.i("Successfully", "created fragment");
                    Bundle b = new Bundle();
                    b.putSerializable("list", m_station_list);
                    b.putSerializable("favorites", favorites);
                    fragment.setArguments(b);
                    Log.i("Successfully", "set fragment args");
                    getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
                    Log.i("Successfully", "replaced framelayout with fragment");}
                break;
            case 50:
                getPreferences(MODE_PRIVATE).getStringSet(Variables.FAVORITES.string, null);
                Fragment station_list = new Station_List();
                Log.i("Successfully", "created fragment");
                Bundle bundle = new Bundle();
                bundle.putBoolean("favorites_only", true);
                ArrayList<String> favorite = null;
                try {
                    favorite= new ArrayList<>(getPreferences(MODE_PRIVATE).getStringSet(Variables.FAVORITES.string, null));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                bundle.putSerializable("favorites", favorite);
                station_list.setArguments(bundle);
                Log.i("Successfully", "set fragment args");
                getFragmentManager().beginTransaction().replace(R.id.fragment, station_list).commit();
                Log.i("Successfully", "replaced framelayout with fragment");
                break;
            case 200:
                break;
            case 0:
                Toast.makeText(this, "Data is corrupted. Please try again later.", Toast.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i("updated", "location");
        try{
            Log.i("LOCATION", String.valueOf(mLastLocation.getLatitude())+", "+String.valueOf(mLastLocation.getLongitude()));
            intent.putExtra(Variables.LOCATION.string, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
        catch (NullPointerException e){
            e.printStackTrace();
            Log.i("null", "probably the location");
            Toast.makeText(this, "Location not found.", Toast.LENGTH_SHORT).show();
        }
        startService(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("FAILED", "sumthing be broken");
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("FAILED", "mapconnect");
        Toast.makeText(this, "Failed to get location. Proceeding without it.", Toast.LENGTH_SHORT).show();
        startService(intent);
    }
    @Override
    public void onClick(View v) {
        Object[] array = null;
        /*if(position ==0){
            array = ((Object[]) v.getTag());
            Bundle b = new Bundle();
            b.putBoolean("inFavoritesMenu", true);
            Log.e(array[0].toString(), "0");
            Log.e(((Station) array[1]).name, "1");
            Log.e(array[2].toString(), "2");
            ((FragmentCommunicator) getFragmentManager().findFragmentById(R.id.fragment)).sendmsg(b);
        }*/
        try{
            array = ((Object[]) v.getTag());
            final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            final Station s = ((Station) ((Object[]) v.getTag())[1]);
            Runnable addFavorite = new Runnable(){
                @Override
                public void run() {
                    Set<String> string = sharedPreferences.getStringSet(Variables.FAVORITES.string, null);
                    ArrayList<String> string_editable;
                    try{
                        string_editable = new ArrayList<String>(string);}
                    catch (Exception e){
                        string_editable = new ArrayList<String>();
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(string_editable.contains(s.toString())){
                        string_editable.remove(s.toString());
                    }
                    else {
                        string_editable.add(s.toString());
                    }
                    editor.putStringSet(Variables.FAVORITES.string, new HashSet<>(string_editable));
                    editor.apply();
                }
            };
            new Thread(addFavorite).start();
            Bundle b = new Bundle();
            b.putBoolean("heart_pressed", true);
            b.putInt("station", (int) array[2]);
            ((FragmentCommunicator) getFragmentManager().findFragmentById(R.id.fragment)).sendmsg(b);
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Fade fade = new Fade(Fade.IN);
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.fragment), fade);
        Log.i("click received", "proceeding with fragment replacement");
        Bundle b = new Bundle();
        b.putSerializable("station", (Station) v.getTag());
        Fragment fragment = new Station_Display();
        fragment.setArguments(b);
        Log.i("Fragment replacement", "Args set");
        this.getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();
        getSupportActionBar().setIcon(android.R.color.transparent);
        getSupportActionBar().setTitle(((Station) v.getTag()).name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void sendmsg(Bundle bundle) {
        toClose = bundle.getBoolean("toClose");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        comparator = Variables.NORTHTOSOUTH;
    }
}
