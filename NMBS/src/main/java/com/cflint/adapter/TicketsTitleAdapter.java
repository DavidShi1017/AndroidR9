package com.cflint.adapter;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activity.AssistantNoConfirmedActivity;
import com.cflint.activity.LinearLayoutForListView;
import com.cflint.activity.TicketActivity;
import com.cflint.adapter.TicketsAdapter.ReloadCallback;
import com.cflint.model.DossierResponse.OrderItemStateType;
import com.cflint.model.Order;
import com.cflint.services.IAssistantService;
import com.cflint.util.TickesHelper;

import java.util.ArrayList;
import java.util.List;


public class TicketsTitleAdapter extends ArrayAdapter<TickesHelper> {
	//private static final String TAG = TicketsTitleAdapter.class.getSimpleName();
	private Context context;

	final static class ViewHolder {
		public TextView nameView;
		private TextView dnr;
		private ImageView refreshImageView;
		private LinearLayoutForListView listView;
	}

	private ReloadCallback reloadCallback;

	private List<View> listViews = new ArrayList<View>();
	private List<View> pnrViews = new ArrayList<View>();
	private TicketsAdapter ticketsAdapter;
	ViewHolder viewHolder;
	private LayoutInflater layoutInflater;
	private List<integer> listViewPositions = new ArrayList<integer>();
	private IAssistantService assistantService;
	private RefreshCallback refreshCallback;
	private boolean isNotHistory;
	private boolean isHasError;

	public TicketsTitleAdapter(Context context, int textViewResourceId,
			List<TickesHelper> objects, IAssistantService assistantService,
			boolean isNotHistory, boolean isHasError) {

		super(context, textViewResourceId, objects);

		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.assistantService = assistantService;
		this.isNotHistory = isNotHistory;
		this.isHasError = isHasError;
	}

	public void setReloadCallback(ReloadCallback reloadCallback) {
		this.reloadCallback = reloadCallback;
	}

	public void setRefreshCallback(RefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void controlAdapter(int position) {

		View listView = listViews.get(position);
		TextView pnrView = (TextView) pnrViews.get(position);
		switch (listView.getVisibility()) {
		case View.GONE:
			//Log.d(TAG, "View.GONE ...");
			this.getItem(position).setOpen(true);
			pnrView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_arrow, 0, 0, 0);
			listView.setVisibility(View.VISIBLE);
			break;
		default:
			this.getItem(position).setOpen(false);
			pnrView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_arrow, 0, 0, 0);
			listView.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.tickets_title_item,
					null);

			viewHolder.dnr = (TextView) convertView.findViewById(R.id.tickets_title_item_dnr_textview);
			viewHolder.listView = (LinearLayoutForListView) convertView.findViewById(R.id.tickets_title_item_listview);
			viewHolder.refreshImageView = (ImageView) convertView.findViewById(R.id.tickets_title_item_refresh_image);
			setTicketOnclickLinstener(this.getItem(position).getOrdersOfDnr());
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.dnr.setText(context
				.getString(R.string.assistant_view_bookingref) + " " + this.getItem(position).getDnr());
		ticketsAdapter = new TicketsAdapter(this.getContext(),
				R.layout.tickets_adapter, this.getItem(position).getOrdersOfDnr(), assistantService, false, isNotHistory, isHasError);
		viewHolder.listView.setId(position);
		viewHolder.listView.setAdapter(ticketsAdapter);
		//viewHolder.listView.setClickable(true);
		//viewHolder.listView.setFocusable(true);
		//viewHolder.listView.setFocusableInTouchMode(true);
		//viewHolder.listView.setDivider(sage);

		//viewHolder.listView.setDividerHeight(1);

		if (!listViews.contains(viewHolder.listView)
				&& !listViewPositions.contains(position)) {
			listViews.add(viewHolder.listView);
		}
		pnrViews.add(viewHolder.dnr);
		//Utils.setListViewHeightBasedOnChildren(viewHolder.listView);

		//restoOnItemClickListener(this.getItem(position).getOrdersOfDnr());
		//viewHolder.refreshImageView.setOnClickListener(refreshImageViewOnClickListener);

		refreshImageViewOnClickListener(this.getItem(position).getOrdersOfDnr());
		/*
		 * View listView = listViews.get(position); TextView pnrView =
		 * (TextView)pnrViews.get(position);
		 */
		
		if (this.getItem(position).isOpen()) {

			viewHolder.listView.setVisibility(View.VISIBLE);
			viewHolder.dnr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_arrow, 0, 0, 0);
		} else {
			viewHolder.dnr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_arrow, 0, 0, 0);
			viewHolder.listView.setVisibility(View.GONE);
		}

		if (this.getItem(position).getOrdersOfDnr().get(0).getOrderState() == OrderItemStateType.OrderItemStateTypeCancelled
				.ordinal()) {
			viewHolder.dnr.setTextColor(context.getResources().getColor(
					R.color.textcolor_error));
			ticketsAdapter.setReloadCallback(reloadCallback);
		}
		return convertView;
	}


	private void setTicketOnclickLinstener(final List<Order> orders){
		viewHolder.listView.setOnclickLinstener(new OnClickListener() {
			
			public void onClick(View v) {
				int position = v.getId();
				Log.e("orders", "orders..." + orders);
				Log.e("orders", "orders..." + orders.size());
				if (orders.get(position).getTravelSegmentID() != null
						&& !"".equalsIgnoreCase(orders.get(position).getTravelSegmentID())) {

					if (orders.get(position).getOrderState() == OrderItemStateType.OrderItemStateTypeConfirmed.ordinal()) {
						context.startActivity(TicketActivity.createIntent(context, orders.get(position)));

					} else if (orders.get(position).getOrderState() == OrderItemStateType.OrderItemStateTypeProvisional.ordinal()) {
						context.startActivity(AssistantNoConfirmedActivity.createConfirmationIntent(context, orders.get(position)));
					}
				} else {
					// Toast.makeText(context,
					// context.getString(R.string.alert_ticket_need_refresh),
					// Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	
	
	
	
	private void refreshImageViewOnClickListener(final List<Order> orders) {

		viewHolder.refreshImageView
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {

						// track Click the "DNR level manual refresh" button
						refreshCallback.refresh(orders);

					}
				});
	}
	
	

	public interface RefreshCallback {
		public void refresh(List<Order> orders);
	}



	public boolean isHasError() {
		return isHasError;
	}

	
}
