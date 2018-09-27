package com.cfl.adapter;

import java.util.List;
import com.cfl.R;


import com.cfl.model.HomePrintTicket;
import com.cfl.util.FileManager;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Adapter used on the result view to show the base info that user selected 
 *
 */
public class PDFAdapter extends ArrayAdapter<HomePrintTicket>{

	final static class ViewHolder {
		public TextView nameView;
		public ImageView cityIcon;
	}
	
	private LayoutInflater layoutInflater;
	private String DNR;
	private Context context;
	public PDFAdapter(Context context, int textViewResourceId,
			List<HomePrintTicket> objects, String DNR) {
		super(context, textViewResourceId, objects);
		this.DNR = DNR;
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
			convertView = layoutInflater.inflate(R.layout.alert_dialog_data_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.alert_dialog_data_adapter_text);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.nameView.setText(this.getItem(position).getDisplayName(this.DNR) + "-" + position + 1);	
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
		String fileName = "";				
		fileName = this.DNR + "-" + this.getItem(position).getPdfId() + ".pdf";
		
		boolean has = FileManager.getInstance().hasExternalStoragePrivateFile(this.context, this.DNR, fileName);
		
		if (has) {
			viewHolder.nameView.setVisibility(View.VISIBLE);
			viewHolder.nameView.setText(this.getItem(position).getDisplayName(this.DNR) + "-" + (position + 1));	
		}else {
			viewHolder.nameView.setVisibility(View.GONE);
		}		
		
		return convertView;
	}

}
