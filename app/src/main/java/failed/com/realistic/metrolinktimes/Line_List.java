package failed.com.realistic.metrolinktimes;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Line_List extends Fragment{
    RecyclerView.Adapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        RecyclerView linelist = ((RecyclerView) view.findViewById(R.id.my_recycler_view));
        adapter = new Line_Adapter(container.getContext());
        linelist.setAdapter(adapter);
        linelist.setLayoutManager(new LinearLayoutManager(container.getContext()));
        return view;
    }
}

