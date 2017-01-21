package rws.sm3.app.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

import rws.sm3.app.Adapters.HomeGridAdapter;
import rws.sm3.app.Adapters.HomeGridAdapter1;
import rws.sm3.app.Adapters.HomeGridAdapter2;
import rws.sm3.app.Adapters.HomeListAdapter;
import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.Helper;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.Home_Activity;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 10/16/2015.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    View rootView;
    private Handler mHandler = new Handler();
    ArrayList<JSONObject> j1, j2, j3;
    Context appContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        appContext = getActivity();
        if (rootView != null) {
            rootView.findViewById(R.id.list_top).setVisibility(View.GONE);
            rootView.findViewById(R.id.list_layout).setVisibility(View.GONE);
        }
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Initialization();
            }
        }, 100);
        return rootView;
    }

    private void Initialization() {
        try {
            ((ListView) rootView.findViewById(R.id.cat_list)).setAdapter(new HomeListAdapter(appContext, getResources().getStringArray(R.array.Categories), 4));
            Helper.getListViewSize((ListView) rootView.findViewById(R.id.cat_list));
            (rootView.findViewById(R.id.btn_more_1)).setOnClickListener(this);
            (rootView.findViewById(R.id.btn_more_2)).setOnClickListener(this);
            (rootView.findViewById(R.id.btn_more_3)).setOnClickListener(this);
            (rootView.findViewById(R.id.btn_more_4)).setOnClickListener(this);
            if (Utility.isConnectingToInternet(appContext)) {
                new LoadDataFromServer().execute();
            } else {
                Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    private void loadData() {
        try {
            if (j1.size() != 0 && j1 != null) {
                (rootView.findViewById(R.id.layout_1)).setVisibility(View.VISIBLE);
                ((GridView) rootView.findViewById(R.id.first_grid)).setAdapter(new HomeGridAdapter(appContext, j1));
                (rootView.findViewById(R.id.first_grid)).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
        try {
            if (j2.size() != 0 && j2 != null) {
                (rootView.findViewById(R.id.layout_2)).setVisibility(View.VISIBLE);
                ((GridView) rootView.findViewById(R.id.second_grid)).setAdapter(new HomeGridAdapter1(appContext, j2));
                (rootView.findViewById(R.id.second_grid)).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
        try {
            if (j3.size() != 0 && j3 != null) {
                (rootView.findViewById(R.id.layout_3)).setVisibility(View.VISIBLE);
                ((GridView) rootView.findViewById(R.id.third_grid)).setAdapter(new HomeGridAdapter2(appContext, j3));
                (rootView.findViewById(R.id.third_grid)).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more_1:
                Utility.setSharedPreference(appContext, Constant.FRAGMENT_OPENFOR, "all_audio");
                ((Home_Activity) appContext).openActivites(9);
                break;
            case R.id.btn_more_2:
                Utility.setSharedPreference(appContext, Constant.FRAGMENT_OPENFOR, "most_played_audio");
                ((Home_Activity) appContext).openActivites(9);
                break;
            case R.id.btn_more_3:
                Utility.setSharedPreference(appContext, Constant.FRAGMENT_OPENFOR, "all_followers_following");
                ((Home_Activity) appContext).openActivites(9);
                break;
            case R.id.btn_more_4:
                ((Home_Activity) appContext).openActivites(2);
                break;
        }
    }

    public class LoadDataFromServer extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;
        ProgressHUD progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressHUD.show(appContext, getResources().getString(R.string.wait), true,
                    false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String data;
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.PAGE).value("0")
                        .key(Constant.METHOD).value("all_audio")
                        .endObject();
                data = putRequest.executePostRequest(putParameters);
//                System.out.println("data"+data);
                networkResponse = new JSONObject(data);
                if (networkResponse.getString("result").equalsIgnoreCase("success")) {
                    j1 = new ArrayList<>();
                    if (networkResponse.getJSONArray("tracklist").length() > 3) {
                        for (int i = 0; i < 3; i++) {
                            j1.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                        }
                    } else {
                        for (int i = 0; i < networkResponse.getJSONArray("tracklist").length(); i++) {
                            j1.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                        }
                    }
                }
                JSONStringer putParameters1 = new JSONStringer()
                        .object()
                        .key(Constant.PAGE).value("0")
                        .key(Constant.METHOD).value("most_played_audio")
                        .endObject();
                data = putRequest.executePostRequest(putParameters1);
//                System.out.println("data1"+data);
                networkResponse = new JSONObject(data);
                if (networkResponse.getString("result").equalsIgnoreCase("success")) {
                    j2 = new ArrayList<>();
                    if (networkResponse.getJSONArray("tracklist").length() > 3) {
                        for (int i = 0; i < 3; i++) {
                            j2.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                        }
                    } else {
                        for (int i = 0; i < networkResponse.getJSONArray("tracklist").length(); i++) {
                            j2.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                        }
                    }
                }
                JSONStringer putParameters2 = new JSONStringer()
                        .object()
                        .key(Constant.PAGE).value("0")
                        .key(Constant.METHOD).value("all_followers_following")
                        .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
                        .endObject();
                data = putRequest.executePostRequest(putParameters2);
//                System.out.println("data2"+data);
                networkResponse = new JSONObject(data);
                if (networkResponse.getString("result").equalsIgnoreCase("success")) {
                    j3 = new ArrayList<>();
                    if (networkResponse.getJSONArray("tracklist").length() > 3) {
                        for (int i = 0; i < 3; i++) {
                            j3.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                        }
                    } else {
                        for (int i = 0; i < networkResponse.getJSONArray("tracklist").length(); i++) {
                            j3.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                        }
                    }
                }
                JSONStringer putParameters3 = new JSONStringer()
                        .object()
                        .key(Constant.METHOD).value("count_details")
                        .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
                        .endObject();
                data = putRequest.executePostRequest(putParameters3);
                networkResponse = new JSONObject(data);
                if (networkResponse.getString("result").equalsIgnoreCase("success")) {
                    JSONObject djson = networkResponse.getJSONObject("count_details");
                    Utility.setSharedPreference(appContext, Constant.COUNT_PLAYLIST, djson.getString(Constant.COUNT_PLAYLIST));
                    Utility.setSharedPreference(appContext, Constant.COUNT_FOLLOWER, djson.getString(Constant.COUNT_FOLLOWER));
                    Utility.setSharedPreference(appContext, Constant.COUNT_FOLLOWING, djson.getString(Constant.COUNT_FOLLOWING));
                    Utility.setSharedPreference(appContext, Constant.COUNT_UPLOADS, djson.getString(Constant.COUNT_UPLOADS));
                }
                return "suc";
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (getView() != null) {
                getView().findViewById(R.id.list_top).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.list_layout).setVisibility(View.VISIBLE);
            }
            if (progress != null) {
                progress.cancel();
            }
            if (s.equals("")) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            } else {
                ((Home_Activity) appContext).SetLike();
                loadData();
            }
            super.onPostExecute(s);
        }

    }

}
