package rws.sm3.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomTextView;
import rws.sm3.app.Home_Activity;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 10/29/2015.
 */
public class AdapterForUsers extends BaseAdapter {
    Context appContext;
    ArrayList<JSONObject> jsonObjects;

    public AdapterForUsers(Context appContext, ArrayList<JSONObject> jsonArray) {
        this.appContext = appContext;
        this.jsonObjects = new ArrayList<>();
        this.jsonObjects.addAll(jsonArray);
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
            v = li.inflate(R.layout.dialog_list_single, null);
            viewHolder = new SingleGridViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (SingleGridViewHolder) v.getTag();
        }
        JSONObject j = jsonObjects.get(position);
        try {
            viewHolder.book_name.setText(j.getString("first_name")+" "+j.getString("last_name"));
            viewHolder.auth_name.setText(j.getString("email"));
            Picasso.with(appContext).load(Constant.PRO_PIC_IMG_URL + j.getString("profile_pic")).placeholder(R.drawable.user_default).error(R.drawable.user_default).into(viewHolder.audio_book);
        } catch (JSONException e) {

        }
//        v.setOnClickListener(new OnItemClickListener(position));
        return v;
    }

    class SingleGridViewHolder {
        public CustomTextView book_name, auth_name;
        public ImageView audio_book;

        public SingleGridViewHolder(View base) {
            audio_book = (ImageView) base.findViewById(R.id.audio_book_img);
            book_name = (CustomTextView) base.findViewById(R.id.book_name);
            auth_name = (CustomTextView) base.findViewById(R.id.auth_name);
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
}
