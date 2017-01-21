package rws.sm3.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 11/3/2015.
 */
public class AboutUsFragment extends Fragment {
    View rootView;
    Context appContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
        appContext = getActivity();
        return rootView;
    }
}
