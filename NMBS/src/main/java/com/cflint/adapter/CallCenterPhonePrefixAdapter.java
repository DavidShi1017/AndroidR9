package com.cflint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;

/**
 * Created by Richard on 2/22/16.
 */
public class CallCenterPhonePrefixAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private String prefixArray[];
    private int logoArray[];
    private Context context;
    public CallCenterPhonePrefixAdapter(Context context, String prefixArray[],int logoArray[]){
        this.context = context;
        this.logoArray = logoArray;
        this.prefixArray = prefixArray;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return prefixArray.length;
    }

    public String getItem(int position) {
        return prefixArray[position];
    }

    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout ll = new LinearLayout(this.context);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        ImageView iv = new ImageView(this.context);
        iv.setImageResource(R.drawable.ic_belgium_number_prefix);
        ll.addView(iv);

        TextView tv=new TextView(this.context);
        tv.setText(prefixArray[position]);
        ll.addView(tv);
        return ll;
    }
}
