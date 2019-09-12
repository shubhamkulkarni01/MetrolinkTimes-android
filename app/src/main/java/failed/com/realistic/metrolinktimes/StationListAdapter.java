package failed.com.realistic.metrolinktimes;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.MyViewHolder>{
    ArrayList<Station> stations;
    Activity mContext;
    Date now;
    public static final String north = "Northbound";
    public static final String south = "Southbound";
    public StationListAdapter(ArrayList<Station> stations, Context context) {
        this.stations = stations;
        this.mContext = (Activity) context;
        now = new Date();
    }
    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerviewlayout, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.container.setOnClickListener(((View.OnClickListener)mContext));
        viewHolder.favorite.setOnClickListener(((View.OnClickListener) mContext));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int i) {
        Station station = stations.get(i);
        viewHolder.name.setText(station.name);
        viewHolder.name.setTextSize(30);
        viewHolder.name.post(new Runnable() {
            @Override
            public void run() {
                viewHolder.name.setMaxLines(1);
                viewHolder.name.setMaxWidth(viewHolder.container.getWidth()-50);
            }
        });
        Map pair = Station.getDirection_Time(station, now);
        ArrayList<String> times = ((SuperPair) pair.get(StationListAdapter.north)).getName();
        ArrayList<String> directions = ((SuperPair) pair.get(StationListAdapter.north)).getValue();
        viewHolder.nextdirection.setText(directions.get(1));
        viewHolder.next1direction.setText(((SuperPair) pair.get(StationListAdapter.south)).getValue().get(1));
        viewHolder.next_time.setText(times.get(1));
        viewHolder.next_time_1.setText(((SuperPair) pair.get(StationListAdapter.south)).getName().get(1));
        viewHolder.container.setTag(station);
        if(station.favorite == true)viewHolder.favorite.setImageDrawable(mContext.getDrawable(R.drawable.heart_filled));
        else viewHolder.favorite.setImageDrawable(mContext.getDrawable(R.drawable.heart_unfilled));
        Object[] array = new Object[3];
        array[0] = Variables.FAVORITE_IMAGE;
        array[1] = station;
        array[2] = i;
        viewHolder.favorite.setTag(array);
    }

    public void setList(ArrayList<Station> stations){this.stations = stations;}

    @Override
    public int getItemCount() {
        return stations.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView next_time;
        TextView next_time_1;
        TextView nextdirection;
        TextView next1direction;
        View container;
        ImageView favorite;
        public MyViewHolder(View view) {
            super(view);
            this.container = view;
            this.favorite = ((ImageView) view.findViewById(R.id.favorite));
            this.name = (TextView) view.findViewById(R.id.name);
            this.next_time = (TextView) view.findViewById(R.id.nextTime);
            this.next_time_1 = (TextView) view.findViewById(R.id.nextTime1);
            this.nextdirection = (TextView) view.findViewById(R.id.nextdirection);
            this.next1direction = (TextView) view.findViewById(R.id.next1direction);
            name.setTag("name");
            next_time.setTag("nexttime");
            next_time_1.setTag("nexttime1");
            nextdirection.setTag("nextdirection");
            next1direction.setTag("next1direction");
        }
    }
}
