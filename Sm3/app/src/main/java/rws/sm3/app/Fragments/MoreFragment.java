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
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.LinkedList;

import rws.sm3.app.Adapters.MoreListAdapter;
import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 10/16/2015.
 */
public class MoreFragment extends Fragment {
    View rootView;
    private Handler mHandler = new Handler();
    LinkedList<JSONObject> mlist;
    int page = 0;
    LoadMoreListView list_subcategory;
    MoreListAdapter moreListAdapter;
    boolean load = true, more = true;
    Context appContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more, container, false);
        appContext = getActivity();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Initialization();
            }
        }, 500);
        return rootView;
    }

    private void Initialization() {
        if (Utility.isConnectingToInternet(appContext)) {
            list_subcategory = (LoadMoreListView) rootView.findViewById(R.id.list_more);
            mlist = new LinkedList<JSONObject>();
            moreListAdapter = new MoreListAdapter(appContext, mlist);
            list_subcategory.setAdapter(moreListAdapter);
            new LoadMoreDataFromServer().execute();
            ((LoadMoreListView) rootView.findViewById(R.id.list_more)).setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (Utility.isConnectingToInternet(appContext)) {
                        if (more == true) {
                            if (load = true) {
                                page = page + 1;
                                new LoadMoreDataFromServer().execute();
                            }
                        } else {
                            list_subcategory.onLoadMoreComplete();
                        }
                    } else {
                        Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                        list_subcategory.onLoadMoreComplete();
                    }
                }
            });
        } else {
            Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
    }


    public class LoadMoreDataFromServer extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;
        ProgressHUD progress;

        @Override
        protected void onPreExecute() {
            if (page == 0) {
                progress = ProgressHUD.show(appContext, getResources().getString(R.string.wait), true,
                        false, null);
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String data;
                if (Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR).equals("all_followers_following")) {
                    JSONStringer putParameters = new JSONStringer()
                            .object()
                            .key(Constant.PAGE).value(page)
                            .key(Constant.METHOD).value(Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR))
                            .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
                            .endObject();
                    data = putRequest.executePostRequest(putParameters);
                    networkResponse = new JSONObject(data);
                } else if (Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR).equals("category_data")) {
                    JSONStringer putParameters = new JSONStringer()
                            .object()
                            .key(Constant.PAGE).value(page)
                            .key(Constant.METHOD).value(Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR))
                            .key(Constant.CAT_ID).value(Utility.getSharedPreferences(appContext, "myid"))
                            .endObject();
                    data = putRequest.executePostRequest(putParameters);
                    networkResponse = new JSONObject(data);
                } else if (Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR).equals("playlist_songs")) {
                    JSONStringer putParameters = new JSONStringer()
                            .object()
                            .key(Constant.PAGE).value(page)
                            .key(Constant.METHOD).value(Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR))
                            .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
                            .key("playlist_id").value(Utility.getSharedPreferences(appContext, "PLAYLIST_ID"))
                            .endObject();
                    data = putRequest.executePostRequest(putParameters);
                    networkResponse = new JSONObject(data);
                } else {
                    JSONStringer putParameters = new JSONStringer()
                            .object()
                            .key(Constant.PAGE).value(page)
                            .key(Constant.METHOD).value(Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR))
                            .endObject();
                    data = putRequest.executePostRequest(putParameters);
                    networkResponse = new JSONObject(data);
                }
                if (Utility.getSharedPreferences(appContext, Constant.FRAGMENT_OPENFOR).equals("playlist_songs")) {
                    if (networkResponse.getString("result").equalsIgnoreCase("success")) {
                        if (networkResponse.getJSONArray("users_songs").length() > 0) {
                            for (int i = 0; i < networkResponse.getJSONArray("users_songs").length(); i++) {
                                mlist.add(networkResponse.getJSONArray("users_songs").getJSONObject(i));
                            }
                        }
                        return "suc";
                    } else {
                        return "fail";
                    }
                } else {
                    if (networkResponse.getString("result").equalsIgnoreCase("success")) {
                        if (networkResponse.getJSONArray("tracklist").length() > 0) {
                            for (int i = 0; i < networkResponse.getJSONArray("tracklist").length(); i++) {
                                mlist.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                            }
                        }
                        return "suc";
                    } else {
                        return "fail";
                    }
                }
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (progress != null) {
                progress.cancel();
            }

            if (s.equals("")) {
                more = true;
                load = false;
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            } else if (s.equals("suc")) {
                moreListAdapter.myNotify(mlist);
                load = true;
                more = true;
            } else {
                Toast.makeText(appContext, getResources().getString(R.string.more_apps_unavail), Toast.LENGTH_SHORT).show();
                more = false;
                load = false;
            }
            super.onPostExecute(s);
            list_subcategory.onLoadMoreComplete();
        }

    }

}
