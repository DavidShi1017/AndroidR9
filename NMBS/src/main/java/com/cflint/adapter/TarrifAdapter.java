package com.cflint.adapter;

import java.util.List;
import com.cflint.R;
import com.cflint.model.InfoText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
/**
 * Adapter used on the result view to show the base info that user selected 
 *
 */
public class TarrifAdapter extends ArrayAdapter<InfoText>{

	final static class ViewHolder {
		public TextView displayText;
		public TextView infoText;
	}
	
	private LayoutInflater layoutInflater;
	public TarrifAdapter(Context context, int textViewResourceId,
			List<InfoText> objects) {
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
			convertView = layoutInflater.inflate(R.layout.tarrif_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.displayText = (TextView) convertView.findViewById(R.id.tarrif_adapter_desc_title);	
			viewHolder.infoText = (TextView) convertView.findViewById(R.id.tarrif_adapter_desc_content);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.displayText.setText(this.getItem(position).getKey());	
		viewHolder.infoText.setText(this.getItem(position).getText());	
		        
        return convertView;
	}
}
