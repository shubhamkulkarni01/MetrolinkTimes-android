package failed.com.realistic.metrolinktimes;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.ResultReceiver;

public class MyResultReceiver extends ResultReceiver{
    public Receiver mReceiver;
    public MyResultReceiver(Handler handler) {
        super(handler);
    }
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }
    public void setReceiver(Receiver receiver){
        mReceiver = receiver;
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null){
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }
}

