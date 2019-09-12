package failed.com.realistic.metrolinktimes;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InputActivity extends ActionBarActivity implements FragmentCommunicator{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentlayout);
        Log.d("set", "contentview");
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
        if(fragment == null){
            getFragmentManager().beginTransaction().add(R.id.fragment, new Line_List()).commit();
        }
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("11A31E65FE177E34A66ED8277AAD0326")  // An example device ID
                .build();
        mAdView.loadAd(adRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void sendmsg(Bundle b) {
        Log.d((String.valueOf(b.getInt(Variables.POSITION.string))), "success?");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Variables.POSITION.string, b.getInt(Variables.POSITION.string));
        startActivity(intent);
    }
}
