package rws.sm3.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomTextView;
import rws.sm3.app.Home_Activity;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 10/16/2015.
 */
public class HomeListAdapter extends BaseAdapter {
    Context appContext;
    String[]cat;
    int no;
    public HomeListAdapter(Context appContext,String[]cat,int length){
        this.appContext=appContext;
        this.cat=cat;
        this.no=length;
    }
    @Override
    public int getCount() {
        return no;
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
        SingleListViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) appContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.single_list_content, null);
            viewHolder = new SingleListViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (SingleListViewHolder) v.getTag();
        }
        viewHolder.mTVItem.setText(cat[position]);
        v.setOnClickListener(new OnItemClickListener(position));
        return v;
    }
    class SingleListViewHolder {
        public CustomTextView mTVItem;
        public SingleListViewHolder(View base) {
        mTVItem=(CustomTextView)base.findViewById(R.id.title);
        }
    }
    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            if(no!=4) {
                Utility.setSharedPreference(appContext, "PREVPOS", "2");
            }
            if(mPosition==12){
                Utility.setSharedPreference(appContext, Constant.FRAGMENT_OPENFOR, "category_data");
                Utility.setSharedPreference(appContext, "myid", "all");
                ((Home_Activity) appContext).openActivites(9);
            }else
                if(mPosition==13){
                    Utility.setSharedPreference(appContext, Constant.FRAGMENT_OPENFOR, "category_data");
                    Utility.setSharedPreference(appContext, "myid", "0");
                    ((Home_Activity) appContext).openActivites(9);
                }
            else {
                    Utility.setSharedPreference(appContext, Constant.FRAGMENT_OPENFOR, "category_data");
                    Utility.setSharedPreference(appContext, "myid", ""+(mPosition+1));
                    ((Home_Activity) appContext).openActivites(9);
                }
        }
    }
}