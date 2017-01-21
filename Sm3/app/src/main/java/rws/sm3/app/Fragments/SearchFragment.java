package rws.sm3.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import rws.sm3.app.Adapters.HomeListAdapter;
import rws.sm3.app.R;
/**
 * Created by Android-2 on 10/21/2015.
 */
public class SearchFragment extends Fragment {
    View rootView;
    private Handler mHandler = new Handler();
    Context appContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container,false);
        appContext=getActivity();
        Initialization();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Initialization();
            }
        }, 500);
        return rootView;
    }
    private void Initialization(){
        ((ListView)rootView.findViewById(R.id.cat_list)).setAdapter(new HomeListAdapter(appContext,getResources().getStringArray(R.array.Categories),14));
    }
}
