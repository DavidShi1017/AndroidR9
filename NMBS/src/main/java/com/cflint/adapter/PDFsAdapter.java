package com.cflint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.model.HomePrintTicket;
import com.cflint.model.PDF;
import com.cflint.util.DateUtils;
import com.cflint.util.FileManager;
import com.cflint.util.Utils;

import java.util.List;

/**
 * Adapter used on the result view to show the base info that user selected 
 *
 */
public class PDFsAdapter extends ArrayAdapter<PDF>{

	final static class ViewHolder {
		public TextView nameView;
		public ImageView cityIcon;
	}

	private LayoutInflater layoutInflater;
	private Context context;
	public PDFsAdapter(Context context, int textViewResourceId, List<PDF> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public long getItemId(int position) {
		return position;
	}
	


	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.li_pdf, null);
			viewHolder = new ViewHolder();
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.tv_pdf);
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.nameView.setText(DateUtils.dateTimeToString(this.getItem(position).getCreationTimeStamp(), "dd MMM yyyy - HH:mm"));
		return convertView;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.alert_dialog_data_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.alert_dialog_data_adapter_text);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.nameView.setText(DateUtils.dateTimeToString(this.getItem(position).getCreationTimeStamp(), "dd MMM yyyy - HH:mm"));

		return convertView;
	}

}
