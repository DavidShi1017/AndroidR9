package com.cflint.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.model.ReleaseNote;

import java.util.List;

public class CheckUpdateVersioinDescAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<ReleaseNote> releaseNotes;


	public CheckUpdateVersioinDescAdapter(Context context,List<ReleaseNote> releaseNotes) {
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.releaseNotes = releaseNotes;
	}

	public void getDescItem(int position, LinearLayout linearLayout) {
		View convertView = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.dialog_check_update_desc_item, null);
		}
		TextView desc = (TextView) convertView.findViewById(R.id.tv_check_update_desc_item_content);
		String descContent = releaseNotes.get(position).getDescription().replaceAll("\\n", "<br />") + "<br />";
		desc.setText(Html.fromHtml(descContent)+"");
		linearLayout.addView(convertView);
	}
}
