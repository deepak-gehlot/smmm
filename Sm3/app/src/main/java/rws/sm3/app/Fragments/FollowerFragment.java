package rws.sm3.app.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CircleImageView;
import rws.sm3.app.CustomWidget.CustomEditText;
import rws.sm3.app.CustomWidget.CustomTextView;
import rws.sm3.app.Home_Activity;
import rws.sm3.app.R;
import rws.sm3.app.SqureImageViewPack.RoundedImageView;

/**
 * Created by Android-2 on 11/2/2015.
 */
public class FollowerFragment extends Fragment implements View.OnClickListener {
    View rootView;
    private Handler mHandler = new Handler();
    Context appContext;
    int page = 0;
    LinkedList<JSONObject> mlist;
    LoadMoreListView list_subcategory;
    boolean load = true, more = true;
    String key = "";
    int key1 = 0;
    JSONObject j = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        appContext = getActivity();
        try {
            j = new JSONObject(Utility.getSharedPreferences(appContext, "USER_DATA"));
        } catch (JSONException e) {

        }
        (rootView.findViewById(R.id.edt_profile)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.upload_lay)).setBackgroundResource(R.drawable.bg_follow_edit_btn);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Initialization();
            }
        }, 500);
        return rootView;
    }

    private void Initialization() {
        (rootView.findViewById(R.id.edt_follow)).setOnClickListener(this);
        (rootView.findViewById(R.id.edt_profile)).setOnClickListener(this);
        try {
//            System.out.println("userdata"+j);
            if (!j.getString(Constant.LOG_U_PRO_PIC).equals("")) {
                Picasso.with(appContext)
                        .load(Constant.PRO_PIC_IMG_URL + j.getString(Constant.LOG_U_PRO_PIC))
                        .placeholder(R.drawable.user_default).error(R.drawable.user_default)
                        .into(((CircleImageView) rootView.findViewById(R.id.profile_pic)));
            }
            if (!j.getString(Constant.FIRST_NAME).equals("")) {
                ((CustomTextView) rootView.findViewById(R.id.user_name)).setText(j.getString(Constant.FIRST_NAME) + " " + j.getString(Constant.LAST_NAME));
            }
            if (!j.getString(Constant.STATUS).equals("")) {
                ((CustomTextView) rootView.findViewById(R.id.user_status)).setText(j.getString(Constant.STATUS));
            }
        } catch (JSONException e) {

        }
        key = "uploaded";
        method_for_uploaded();
        (rootView.findViewById(R.id.upload_lay)).setOnClickListener(this);
        (rootView.findViewById(R.id.playlist_lay)).setOnClickListener(this);
        (rootView.findViewById(R.id.spekar_lay)).setOnClickListener(this);
        (rootView.findViewById(R.id.audiance_lay)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_profile:
                ((Home_Activity) appContext).CallEditProfile();
                break;
            case R.id.upload_lay:
                if (!key.equals("uploaded")) {
                    key = "uploaded";
                    method_for_uploaded();
                }
                break;
            case R.id.playlist_lay:
                if (!key.equals("playlist")) {
                    key = "playlist";
                    method_for_playlist();
                }

                break;
            case R.id.spekar_lay:
                if (!key.equals("spekar")) {
                    key = "spekar";
                    method_for_spekar();
                }
                break;
            case R.id.audiance_lay:
                if (!key.equals("audiance")) {
                    key = "audiance";
                    method_for_audience();
                }
                break;
            case R.id.edt_follow:
                new UserFollow().execute();
                break;
        }
    }

    private void method_for_uploaded() {
        (rootView.findViewById(R.id.upload_lay)).setBackgroundResource(R.drawable.bg_follow_edit_btn);
        (rootView.findViewById(R.id.playlist_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.audiance_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.spekar_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        load_and_list("get_uploadedaudio");
    }

    private void method_for_playlist() {
        (rootView.findViewById(R.id.upload_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.playlist_lay)).setBackgroundResource(R.drawable.bg_follow_edit_btn);
        (rootView.findViewById(R.id.audiance_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.spekar_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        load_and_list("get_playlistaudio");
    }

    private void method_for_spekar() {
        (rootView.findViewById(R.id.upload_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.playlist_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.audiance_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.spekar_lay)).setBackgroundResource(R.drawable.bg_follow_edit_btn);
        load_and_list("get_speakers");
    }

    private void method_for_audience() {
        (rootView.findViewById(R.id.upload_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.playlist_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        (rootView.findViewById(R.id.audiance_lay)).setBackgroundResource(R.drawable.bg_follow_edit_btn);
        (rootView.findViewById(R.id.spekar_lay)).setBackgroundResource(R.drawable.pro_lay_bg);
        load_and_list("get_audience");
    }

    AdapterForProfileData moreListAdapter;

    private void load_and_list(final String method) {
        load = true;
        more = true;
        page = 0;
        if (Utility.isConnectingToInternet(appContext)) {
            list_subcategory = (LoadMoreListView) rootView.findViewById(R.id.list_content);
            mlist = new LinkedList<JSONObject>();
            moreListAdapter = new AdapterForProfileData(appContext, mlist, method);
            list_subcategory.setAdapter(moreListAdapter);
            new LoadMoreDataFromServer().execute(method);
            ((LoadMoreListView) rootView.findViewById(R.id.list_content)).setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (Utility.isConnectingToInternet(appContext)) {
                        if (more == true) {
                            if (load = true) {
                                page = page + 1;
                                new LoadMoreDataFromServer().execute(method);
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
        JSONObject jsonObjectnew;

        @Override
        protected void onPreExecute() {
            if (key1 == 0) {
                progress = ProgressHUD.show(appContext, getResources().getString(R.string.wait), true,
                        false, null);
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String data;
                if(page==0){
                    JSONStringer putParameters0 = new JSONStringer()
                            .object()
                            .key("follow_userid").value(j.getString("u_id"))
                            .key(Constant.METHOD).value("chk_follow")
                            .key(Constant.ID).value(Utility.getSharedPreferences(appContext,Constant.ID))
                            .endObject();
                    String data1 = putRequest.executePostRequest(putParameters0);
                    JSONObject j=new JSONObject(data1);
                    if(j.getString("result").equals("success")) {
                        Utility.setSharedPreference(appContext, "TYPE", "1");
                    }else {
                        Utility.setSharedPreference(appContext, "TYPE", "2");
                    }
                }

                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.PAGE).value(page)
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.ID).value(j.getString("u_id"))
                        .endObject();
                data = putRequest.executePostRequest(putParameters);

                if (key1 == 0) {
                    JSONStringer putParameters3 = new JSONStringer()
                            .object()
                            .key(Constant.METHOD).value("count_details")
                            .key(Constant.ID).value(j.getString("u_id"))
                            .endObject();
                    jsonObjectnew = new JSONObject(putRequest.executePostRequest(putParameters3)).getJSONObject("count_details");
                }
                networkResponse = new JSONObject(data);
                if (networkResponse.getString("result").equalsIgnoreCase("success")) {
                    if (params[0].equals("get_uploadedaudio")) {
                        if (networkResponse.getJSONArray("users_songs").length() > 0) {
                            for (int i = 0; i < networkResponse.getJSONArray("users_songs").length(); i++) {
                                mlist.add(networkResponse.getJSONArray("users_songs").getJSONObject(i));
                            }
                        }
                    }
                    if (params[0].equals("get_speakers")) {
                        if (networkResponse.getJSONArray("audience").length() > 0) {
                            for (int i = 0; i < networkResponse.getJSONArray("audience").length(); i++) {
                                mlist.add(networkResponse.getJSONArray("audience").getJSONObject(i));
                            }
                        }

                    }
                    if (params[0].equals("get_audience")) {
                        if (networkResponse.getJSONArray("audience").length() > 0) {
                            for (int i = 0; i < networkResponse.getJSONArray("audience").length(); i++) {
                                mlist.add(networkResponse.getJSONArray("audience").getJSONObject(i));
                            }
                        }

                    }
                    if (params[0].equals("get_playlistaudio")) {
                        if (networkResponse.getJSONArray("playlist").length() > 0) {
                            for (int i = 0; i < networkResponse.getJSONArray("playlist").length(); i++) {
                                mlist.add(networkResponse.getJSONArray("playlist").getJSONObject(i));
                            }
                        }

                    }
                    return "suc";
                } else {
                    return "fail";
                }

            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (progress != null) {
                progress.cancel();
            }try{

            if (key1 == 0) {

                setData(jsonObjectnew);
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

        }catch (Exception e){

        }
        }

    }

    private void setData(JSONObject j2) {
        try {
            if (!j2.getString(Constant.COUNT_PLAYLIST).equals("")) {
                ((CustomTextView) rootView.findViewById(R.id.ply_count_edit)).setText(j2.getString(Constant.COUNT_PLAYLIST));
            }
            if (!j2.getString(Constant.COUNT_FOLLOWER).equals("")) {
                ((CustomTextView) rootView.findViewById(R.id.aud_count_edit)).setText(j2.getString(Constant.COUNT_FOLLOWER));
            }
            if (!j2.getString(Constant.COUNT_FOLLOWING).equals("")) {
                ((CustomTextView) rootView.findViewById(R.id.speker_count_edit)).setText(j2.getString(Constant.COUNT_FOLLOWING));
            }
            if (!j2.getString(Constant.COUNT_UPLOADS).equals("")) {
                ((CustomTextView) rootView.findViewById(R.id.upl_count_edit)).setText(j2.getString(Constant.COUNT_UPLOADS));
            }
            if( Utility.getSharedPreferences(appContext, "TYPE").equals("2")){
                (rootView.findViewById(R.id.edt_follow)).setVisibility(View.VISIBLE);
            }
            key1 = 1;
        } catch (JSONException e) {

        }
    }

    public class UserFollow extends AsyncTask<String, String, String> {
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
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.METHOD).value("follow_users")
                        .key("user_id").value(Utility.getSharedPreferences(appContext, Constant.ID))
                        .key("follow_userid").value(j.getString("u_id"))
                        .endObject();
                return putRequest.executePostRequest(putParameters);
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        if (networkResponse.getString("responseMessage").equals("you have successfully followed this users")) {
                            int fo=Integer.parseInt(Utility.getSharedPreferences(appContext, Constant.COUNT_FOLLOWING));
                            Utility.setSharedPreference(appContext, Constant.COUNT_FOLLOWING, "" + (fo+1));
                            ((Home_Activity)appContext).SetLike();
                            (rootView.findViewById(R.id.edt_follow)).setVisibility(View.GONE);
                        }

                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }

    public class AdapterForProfileData extends BaseAdapter {
        Context appContext;
        ArrayList<JSONObject> jsonObjects;
        LinkedList<JSONObject> jsonObjectLinkedList;
        String key;

        public AdapterForProfileData(Context appContext, LinkedList<JSONObject> jsonArray, String key) {
            this.appContext = appContext;
            this.jsonObjects = new ArrayList<>();
            this.jsonObjects.addAll(jsonArray);
            this.key = key;
        }

        @Override
        public int getCount() {
            return jsonObjects.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            SingleGridViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) appContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.single_content_small, null);
                viewHolder = new SingleGridViewHolder(v);
                v.setTag(viewHolder);
            } else {
                viewHolder = (SingleGridViewHolder) v.getTag();
            }
            JSONObject j = jsonObjects.get(position);
            try {
                if (key.equals("get_uploadedaudio")) {
                    viewHolder.book_name.setText(j.getString(Constant.BOOK_NAME));
                    viewHolder.auth_name.setText(j.getString(Constant.AUTHOR_NAME));
                    viewHolder.play_btn.setVisibility(View.VISIBLE);
                    Picasso.with(appContext).load(Constant.SERVER_IMG_URL + j.getString(Constant.IMAGE_PATH)).into(viewHolder.audio_book);
                    if (jsonObjects.get(position).getString("user_id").equals(Utility.getSharedPreferences(appContext, Constant.ID))) {
                        viewHolder.del_btn.setVisibility(View.VISIBLE);
                        viewHolder.del_btn.setOnClickListener(new OnItemClickListener5(position));
                    }
                    v.setOnClickListener(new OnItemClickListener(position));
                }
                if (key.equals("get_speakers")) {
                    viewHolder.book_name.setText(j.getString("first_name") + " " + j.getString("last_name"));
                    viewHolder.auth_name.setText(j.getString("description"));
                    Picasso.with(appContext).load(Constant.PRO_PIC_IMG_URL + j.getString("profile_pic")).placeholder(R.drawable.user_default).error(R.drawable.user_default).into(viewHolder.audio_book);
                    v.setOnClickListener(new OnItemClickListener2(position));
                }
                if (key.equals("get_audience")) {
                    viewHolder.book_name.setText(j.getString("first_name") + " " + j.getString("last_name"));
                    viewHolder.auth_name.setText(j.getString("description"));
                    Picasso.with(appContext).load(Constant.PRO_PIC_IMG_URL + j.getString("profile_pic")).placeholder(R.drawable.user_default).error(R.drawable.user_default).into(viewHolder.audio_book);
                    v.setOnClickListener(new OnItemClickListener1(position));
                }
                if (key.equals("get_playlistaudio")) {
                    viewHolder.book_name.setText(j.getString("playlist_category"));
                    viewHolder.auth_name.setVisibility(View.GONE);
                    viewHolder.audio_book.setVisibility(View.GONE);
                    if (jsonObjects.get(position).getString("playlist_userid").equals(Utility.getSharedPreferences(appContext, Constant.ID))) {
                        viewHolder.play_btn.setVisibility(View.VISIBLE);
                        viewHolder.play_btn.setImageResource(R.drawable.btn_edt);
                        viewHolder.play_btn.setOnClickListener(new OnItemClickListener4(position));
                    }
                    v.setOnClickListener(new OnItemClickListener3(position));
                }

            } catch (JSONException e) {

            }
            return v;
        }

        class SingleGridViewHolder {
            public CustomTextView book_name, auth_name;
            ImageView play_btn, del_btn;
            public RoundedImageView audio_book;

            public SingleGridViewHolder(View base) {
                audio_book = (RoundedImageView) base.findViewById(R.id.audio_book_img);
                book_name = (CustomTextView) base.findViewById(R.id.book_name);
                auth_name = (CustomTextView) base.findViewById(R.id.auth_name);
                play_btn = (ImageView) base.findViewById(R.id.play_bt);
                del_btn = (ImageView) base.findViewById(R.id.del);
            }
        }

        private class OnItemClickListener implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {
                try {
                    JSONArray j = new JSONArray();
                    for (int i = 0; i < jsonObjects.size(); i++) {
                        j.put(i, jsonObjects.get(i));
                    }
                    ((Home_Activity) appContext).PlaySongs(mPosition);
                    Utility.setSharedPreference(appContext, Constant.PLAY_LIST, "" + j);
                } catch (JSONException e) {

                }
            }
        }

        private class OnItemClickListener1 implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener1(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {
                try {
                    if (jsonObjects.get(mPosition).getString("u_id").equals(Utility.getSharedPreferences(appContext, Constant.ID))) {
                        ((Home_Activity) appContext).openActivites(3);
                    } else {
                        ((Home_Activity) appContext).openActivites(10);
                        Utility.setSharedPreference(appContext, "USER_DATA", "" + jsonObjects.get(mPosition));
                        Utility.setSharedPreference(appContext, "TYPE", "2");
                    }
                } catch (JSONException e) {

                }
            }
        }

        private class OnItemClickListener5 implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener5(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {
                try {
                    new DeleteSong().execute("deletesongs", jsonObjects.get(mPosition).getString("s_id"), Utility.getSharedPreferences(appContext, Constant.ID), "" + mPosition);
                } catch (JSONException e) {

                }
            }
        }

        private class OnItemClickListener3 implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener3(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {
                try {
                    Utility.setSharedPreference(appContext, Constant.FRAGMENT_OPENFOR, "playlist_songs");
                    Utility.setSharedPreference(appContext, "PLAYLIST_ID", jsonObjects.get(mPosition).getString("playlist_ctegoryid"));
                    ((Home_Activity) appContext).openActivites(9);
                } catch (JSONException e) {

                }
            }
        }

        private class OnItemClickListener4 implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener4(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {
                try {
                    ShowSendComment(jsonObjects.get(mPosition).getString("playlist_ctegoryid"), mPosition);
                } catch (JSONException e) {

                }
            }
        }

        private void ShowSendComment(final String id, final int pos) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);
            LayoutInflater li = (LayoutInflater) appContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View convertView = li.inflate(R.layout.dialog_change_name, null);
            alertDialogBuilder.setView(convertView);
            alertDialogBuilder.setCancelable(false);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.setTitle(appContext.getResources().getString(R.string.send_comment));
            (convertView.findViewById(R.id.submit_comment)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((((CustomEditText) convertView.findViewById(R.id.edt_for_com)).getText().toString()).equals("")) {
                        Toast.makeText(appContext, appContext.getResources().getString(R.string.emptyFiled), Toast.LENGTH_SHORT).show();
                    } else {
                        alertDialog.cancel();
                        new SendCommentServer().execute("update_playlist", id, Utility.getSharedPreferences(appContext, Constant.ID), ((CustomEditText) convertView.findViewById(R.id.edt_for_com)).getText().toString(), "" + pos);
                    }
                }
            });
            (convertView.findViewById(R.id.cancel_comment)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
            alertDialog.show();
        }

        public class SendCommentServer extends AsyncTask<String, String, String> {
            NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
            JSONObject networkResponse = null;
            ProgressHUD progress;
            int pos;
            String name;
            String cat_id;

            @Override
            protected void onPreExecute() {
                progress = ProgressHUD.show(appContext, appContext.getResources().getString(R.string.wait), true,
                        false, null);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                pos = Integer.parseInt(params[4]);
                name = params[3];
                cat_id = params[1];
                try {
                    JSONStringer putParameters = new JSONStringer()
                            .object()
                            .key(Constant.METHOD).value(params[0])
                            .key("playlist_id").value(params[1])
                            .key("user_id").value(params[2])
                            .key("playlist").value(URLEncoder.encode(params[3], "utf-8").trim())
                            .endObject();
                    return putRequest.executePostRequest(putParameters);
                } catch (Exception e) {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (progress != null) {
                    progress.cancel();
                }
                try {
                    networkResponse = new JSONObject(s);
                    if (networkResponse.equals(null) || networkResponse.equals("")) {
                        Toast.makeText(appContext, appContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    } else {
                        if (networkResponse.getString("result").equals("success")) {
                            try {
//    {"playlist_ctegoryid":"3","playlist_userid":"1","playlist_category":"newplayist","playlist_Date":"2015-10-30 07:20:04"},
                                JSONObject j = new JSONObject();
                                j.put("playlist_ctegoryid", cat_id);
                                j.put("playlist_userid", Utility.getSharedPreferences(appContext, Constant.ID));
                                j.put("playlist_category", name);
                                j.put("playlist_Date", "");
                                jsonObjects.remove(pos);
                                jsonObjects.add(pos, j);
                                jsonObjectLinkedList = new LinkedList<JSONObject>();
                                jsonObjectLinkedList.addAll(jsonObjects);
                                myNotify(jsonObjectLinkedList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(appContext, appContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(s);
            }

        }

        private class OnItemClickListener2 implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener2(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {
                try {
                    if (jsonObjects.get(mPosition).getString("u_id").equals(Utility.getSharedPreferences(appContext, Constant.ID))) {
                        ((Home_Activity) appContext).openActivites(3);
                    } else {
                        ((Home_Activity) appContext).openActivites(10);
                        Utility.setSharedPreference(appContext, "USER_DATA", "" + jsonObjects.get(mPosition));
                        Utility.setSharedPreference(appContext, "TYPE", "1");

                    }
                } catch (JSONException e) {

                }
            }
        }

        public class DeleteSong extends AsyncTask<String, String, String> {
            NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
            JSONObject networkResponse = null;
            ProgressHUD progress;
            int pos;

            @Override
            protected void onPreExecute() {
                progress = ProgressHUD.show(appContext, appContext.getResources().getString(R.string.wait), true,
                        false, null);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                pos = Integer.parseInt(params[3]);
                try {
                    JSONStringer putParameters = new JSONStringer()
                            .object()
                            .key(Constant.METHOD).value(params[0])
                            .key("song_id").value(params[1])
                            .key("user_id").value(params[2])
                            .endObject();
                    return putRequest.executePostRequest(putParameters);
                } catch (Exception e) {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (progress != null) {
                    progress.cancel();
                }
                try {
                    networkResponse = new JSONObject(s);
                    if (networkResponse.equals(null) || networkResponse.equals("")) {
                        Toast.makeText(appContext, appContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    } else {
                        if (networkResponse.getString("result").equals("success")) {

//        {"playlist_ctegoryid":"3","playlist_userid":"1","playlist_category":"newplayist","playlist_Date":"2015-10-30 07:20:04"},
                            jsonObjects.remove(pos);
                            mlist.remove(pos);
                            jsonObjectLinkedList = new LinkedList<JSONObject>();
                            jsonObjectLinkedList.addAll(jsonObjects);
                            myNotify(jsonObjectLinkedList);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(appContext, appContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(s);
            }

        }

        public void myNotify(LinkedList<JSONObject> data) {
            jsonObjects.clear();
            jsonObjects.addAll(data);
            notifyDataSetChanged();
        }
    }
}
