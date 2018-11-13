package com.cflint.adapter;

import java.util.List;
import com.cflint.R;
import com.cflint.model.Parking;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
/**
 * Adapter used on the result view to show the base info that user selected 
 *
 */
public class ParkingAdapter extends ArrayAdapter<Parking>{

	final static class ViewHolder {
		private TextView name;
		private TextView description;
		
	}
	
	private LayoutInflater layoutInflater;
	public ParkingAdapter(Context context, int textViewResourceId,
			List<Parking> objects) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
    /**
     * {@inheritDoc}
     */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.parking_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.parking_adapter_name);	
			viewHolder.description = (TextView) convertView.findViewById(R.id.parking_adapter_description);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		/*viewHolder.name.setText(this.getItem(position).getName());
		viewHolder.description.setText(Html.fromHtml(this.getItem(position).getDescription()));*/

		return convertView;
	}

}
