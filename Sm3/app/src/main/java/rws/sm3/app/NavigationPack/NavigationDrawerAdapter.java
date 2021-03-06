package rws.sm3.app.NavigationPack;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import rws.sm3.app.R;

public class NavigationDrawerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private static String[] titles = null;
    private static int[] list_img = null;

    public NavigationDrawerAdapter(Context context, String[] titles, int[] list_img) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.titles = titles;
        this.list_img = list_img;
    }

    @Override
    public int getCount() {

        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            img_item = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        MyViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.single_list_layout, null);
            viewHolder = new MyViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) v.getTag();
        }
        viewHolder.title.setText(titles[position]);
        viewHolder.img_item.setImageResource(list_img[position]);
        return v;
    }

}