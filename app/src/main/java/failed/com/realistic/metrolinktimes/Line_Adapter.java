package failed.com.realistic.metrolinktimes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Line_Adapter extends RecyclerView.Adapter<Line_Adapter.Line_ViewHolder> implements View.OnClickListener {
    Context context;
    public Line_Adapter(Context context){
        this.context = context;
    }

    @Override
    public Line_ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.line_select, viewGroup, false);
        return new Line_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Line_ViewHolder holder, int position) {
        holder.textView.setText(context.getResources().getStringArray(R.array.line_name)[position]);
        switch(position){
            case 0:
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.heart_filled));
                holder.textView.setTextColor(Color.parseColor("#524895"));
            case 1:
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.orange_line));
                holder.textView.setTextColor(Color.parseColor("#FF6D10"));
                break;
            case 2:
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.red_line));
                holder.textView.setTextColor(Color.parseColor("#c81e53"));
                break;
            case 3:
                holder.textView.setTextColor(Color.parseColor("#6db33f"));
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.green_line));
                break;
            case 4:
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.purple_line));
                holder.textView.setTextColor(Color.parseColor("#652d89"));
                break;
            case 5:
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.brown_line));
                holder.textView.setTextColor(Color.parseColor("#893101"));
                break;
            case 6:
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.yellow_line));
                holder.textView.setTextColor(Color.parseColor("#fdb913"));
                break;
            case 7:
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.blue_line));
                holder.textView.setTextColor(Color.parseColor("#569bbe"));
                break;
        }
        holder.container.setTag(position);
        holder.container.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Bundle b = new Bundle();
        b.putInt(Variables.POSITION.string, ((int) v.getTag()));
        ((FragmentCommunicator) context).sendmsg(b);
    }

    @Override
    public int getItemCount() {
        return context.getResources().getStringArray(R.array.line_name).length;
    }
    public static class Line_ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        View container;
        public Line_ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            this.imageView = ((ImageView) itemView.findViewById(R.id.linePic));
            this.textView = ((TextView) itemView.findViewById(R.id.lineName));
        }
    }
}
