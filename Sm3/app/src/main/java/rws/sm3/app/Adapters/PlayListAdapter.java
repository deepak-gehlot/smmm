package rws.sm3.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 10/31/2015.
 */
public class PlayListAdapter extends BaseAdapter {
    Context appContext;
    ArrayList<JSONObject> jsonObjects;
    private int mSelectedPosition = -1;
    private RadioButton mSelectedRB;
    private String mUserApllication = "";
    public PlayListAdapter(Context appContext, ArrayList<JSONObject> jsonArray) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        SingleGridViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) appContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.single_playlist_content, null);
            viewHolder = new SingleGridViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (SingleGridViewHolder) v.getTag();
        }
        JSONObject j = jsonObjects.get(position);
        try {
            viewHolder.book_name.setText(j.getString("playlist_category"));
            viewHolder.book_name.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position != mSelectedPosition && mSelectedRB != null){
                        mSelectedRB.setChecked(false);
                    }
                    mUserApllication ="";
                    mSelectedPosition = position;
                    try {
                        Utility.setSharedPreference(appContext, "cat_id_for_send", jsonObjects.get(position).getString("playlist_ctegoryid"));
                    }
                   catch (JSONException e){

                   }
                    mSelectedRB = (RadioButton) view;
                }
            });
            String userApp = jsonObjects.get(position).getString("playlist_ctegoryid");
            if(mUserApllication.equals(userApp)) {
                mSelectedPosition = position;
            }

            if (mSelectedPosition != position) {
                viewHolder.book_name.setChecked(false);
            } else {
                viewHolder.book_name.setChecked(true);
                mSelectedRB =  viewHolder.book_name;
            }
        } catch (JSONException e) {

        }
//        v.setOnClickListener(new OnItemClickListener(position));
        return v;
    }

    class SingleGridViewHolder {
        public RadioButton book_name;

        public SingleGridViewHolder(View base) {
            book_name = (RadioButton) base.findViewById(R.id.book_name);
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
                Utility.setSharedPreference(appContext, "cat_id_for_send", jsonObjects.get(mPosition).getString("playlist_category"));
            }catch (JSONException e){

            }
               notifyDataSetChanged();
        }
    }
}
