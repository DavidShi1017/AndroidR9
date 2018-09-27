package com.cfl.adapter;

import java.util.List;
import com.cfl.R;

import com.cfl.model.StationTextBlock;

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
public class TextBlockAdapter extends ArrayAdapter<StationTextBlock>{

	final static class ViewHolder {
		private TextView title;
		private TextView content;
		
	}
	
	private LayoutInflater layoutInflater;
	public TextBlockAdapter(Context context, int textViewResourceId,
			List<StationTextBlock> objects) {
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
			convertView = layoutInflater.inflate(R.layout.textblock_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.textblock_adapter_title);	
			viewHolder.content = (TextView) convertView.findViewById(R.id.textblock_adapter_content);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.title.setText(this.getItem(position).getTitle());	
		viewHolder.content.setText(Html.fromHtml(this.getItem(position).getContent()));

		return convertView;
	}

}
