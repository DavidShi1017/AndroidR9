package com.nmbs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.StationsActivity;
import com.nmbs.model.FavoriteStation;
import com.nmbs.model.Station;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.impl.StationService;
import com.nmbs.util.ComparatorStationName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoriteAndNormalStationsAdapter extends BaseExpandableListAdapter implements Filterable{
	private List<List<Station>> allStations;
	private StationsActivity stationsActivity;
	private Context context;
	private LayoutInflater inflater;
	private ExpandableListView listView;
	private List<List<Station>> originStations;
	private List<Station> favoriteStations = null;
	private List<Station> normalStations = null;
	private List<Station> originFavoriteStations ;
	private List<Station> originNormalStations;
	private IAssistantService assitantService;
	private StationService stationService;
	private Comparator<Station> comp = new ComparatorStationName();
	private boolean flag = false;
	private int noFavoriteStringId;
	private boolean isFromSchedule = false;
	public FavoriteAndNormalStationsAdapter(StationsActivity stationsActivity,
											List<List<Station>> allStations, List<Station> originFavoriteStations, List<Station> originNormalStations,
											IAssistantService assitantService, StationService stationService, ExpandableListView listView, int noFavoriteStringId) {
		context = (Context)stationsActivity;
		this.stationsActivity = stationsActivity;
		this.allStations = allStations;
		this.listView = listView;
		this.stationService = stationService;
		this.assitantService = assitantService;
		this.originFavoriteStations = originFavoriteStations;
		this.originNormalStations = originNormalStations;
		this.noFavoriteStringId = noFavoriteStringId;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isFromSchedule = true;
	}

	public int getGroupCount() {
		return 2;
	}

	public int getChildrenCount(int groupPosition) {

		if (allStations == null) {
			allStations = assitantService.getAllStations();
		}
		if (allStations.get(groupPosition).size() == 0) {
			if(!flag)
			return 1;
			else
			return 0;
		} else {
			return allStations.get(groupPosition).size();
		}
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.favorite_station_group_view, null);
		TextView title = (TextView) convertView.findViewById(R.id.tv_title);
		if (groupPosition == 0) {
			title.setText(R.string.station_favorite);
		} else {
			title.setText(R.string.station_all);
		}
		if(flag){
			if(allStations.get(groupPosition).size() == 0){
				convertView = inflater.inflate(R.layout.station_null_group_view, null);
				return convertView;
			}
		}
		return convertView;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = inflater.inflate(R.layout.station_item_view, null);
		ImageView addFavorite = (ImageView) convertView.findViewById(R.id.station_adapter_controll_button);
		TextView stationName = (TextView) convertView.findViewById(R.id.station_adapter_station_name_textview);
		TextView stationSynoniem = (TextView) convertView.findViewById(R.id.station_adapter_synoniem_textview);
		
		RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.station_board_item_view);
		TextView noFavoriteView = (TextView) convertView.findViewById(R.id.station_adapter_view_favorite_noticket_TextView);
		if(allStations == null || allStations.size() == 0){
			return convertView;
		}
		if (groupPosition == 0 && allStations.get(groupPosition).size() == 0) {
			noFavoriteView.setVisibility(View.VISIBLE);
			relativeLayout.setVisibility(View.GONE);
			noFavoriteView.setText(noFavoriteStringId);
			
			
		} else {
			noFavoriteView.setVisibility(View.GONE);
			relativeLayout.setVisibility(View.VISIBLE);
			if (groupPosition == 0) {
				addFavorite.setImageResource(R.drawable.ic_add_favorite_cancel);
			} else {
				addFavorite.setImageResource(R.drawable.ic_add_favorite);
			}
			if (groupPosition == 1) {
				addFavorite.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(!flag)
							addOriginFavoriteStation(allStations.get(groupPosition).get(childPosition));
						else
							addFavoriteStation(allStations.get(groupPosition).get(childPosition));
						
					}
				});
			} else {
				addFavorite.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(!flag)
							deleteOriginFavoriteStation(allStations.get(groupPosition).get(childPosition));
						else
							deleteFavoriteStation(allStations.get(groupPosition).get(childPosition));
					}
				});
			}
			//Log.e("FavoriteStation", "allStations..." + allStations.size() +"...groupPosition..." + groupPosition);
			//Log.e("FavoriteStation", "childPosition..." + allStations.size() +"...groupPosition..." + groupPosition);
			stationName.setText(allStations.get(groupPosition).get(childPosition).getName().toUpperCase());
			
			String synoniem = allStations.get(groupPosition).get(childPosition).getSynoniem();
			if (synoniem != null && !synoniem.isEmpty()) {
				stationSynoniem.setVisibility(View.VISIBLE);
				stationSynoniem.setText(allStations.get(groupPosition).get(childPosition).getSynoniem());
			}else {
				stationSynoniem.setVisibility(View.INVISIBLE);
			}
			
		}
		return convertView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public Object getGroup(int groupPosition) {
		return null;
	}

	public Object getChild(int groupPosition, int childPosition) {
		if (allStations.size() > 0 && allStations.get(groupPosition).size() > 0) {
			return allStations.get(groupPosition).get(childPosition);
		}
		return null;
	}

	public long getGroupId(int groupPosition) {
		return 0;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public boolean hasStableIds() {
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
		this.listView.expandGroup(groupPosition);
	}

	public Filter getFilter() {
		Filter filter = new Filter() {
	        @SuppressWarnings("unchecked")
			@Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {        	
	        	allStations = (List<List<Station>>) results.values; // has the filtered values
	            notifyDataSetChanged();  // notifies the data with new filtered values
	            expandListView();
	        }
	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	        	
	        	// Holds the results of a filtering operation in values
	            FilterResults results = new FilterResults();        
	            List<List<Station>> filtereStationList = new ArrayList<List<Station>>();
	            if (originStations == null) {       
	            	originStations = new ArrayList<List<Station>>(allStations); 
	            }
	            if (constraint == null || constraint.length() == 0) {
	            	flag = false;
	                results.values = originStations;
	            } else {
	            	flag = true;
	            		favoriteStations = new ArrayList<Station>();
	            		normalStations = new ArrayList<Station>();
	                constraint = constraint.toString().toLowerCase();
	                for(Station station:originStations.get(0)){
						if (station != null) {
							if (station.getQuery().toLowerCase()
									.contains(constraint.toString())) {
								favoriteStations.add(station);
							}
						}  
	                }
	                
	                for(Station station:originStations.get(1)){
	                	if (station != null) {
							if (station.getQuery().toLowerCase()
									.contains(constraint.toString())) {
								normalStations.add(station);
							}
						}  
	                }
				    Collections.sort(favoriteStations, comp);
				    Collections.sort(normalStations, comp);
	                filtereStationList.add(favoriteStations);
	                filtereStationList.add(normalStations);
	                results.values = filtereStationList;
	            }
	            return results;
	        }
        };
        return filter;   
	}
	
	public void expandListView(){
		for(int i=0;i<getGroupCount();i++){
			this.listView.expandGroup(i);
		}
	}
	
	public void addFavoriteStation(Station station){
		favoriteStations.add(station);
		normalStations.remove(station);
		addOriginFavoriteStation(station);
		Collections.sort(favoriteStations, comp);
	    Collections.sort(normalStations, comp);
		notifyDataSetChanged();
	}
	
	public void deleteFavoriteStation(Station station){
		favoriteStations.remove(station);
		normalStations.add(station);
		deleteOriginFavoriteStation(station);
		Collections.sort(favoriteStations, comp);
	    Collections.sort(normalStations, comp);
		notifyDataSetChanged();
	}
	
	public void addOriginFavoriteStation(Station station){
		originFavoriteStations.add(station);
		if(stationService != null && station != null)
			stationService.addStationFavorite(station.getCode());
		//assitantService.insertStationCode(new FavoriteStation(station.getCode(),station.getStationBoardEnabled()), context);
		/*if(masterservice != null)
		masterservice.insertStationCode(new FavoriteStation(station.getCode(),station.getStationBoardEnabled()), context);*/
		originNormalStations.remove(station);
		Collections.sort(originFavoriteStations, comp);
	    Collections.sort(originNormalStations, comp);
		notifyDataSetChanged();
		this.stationsActivity.selectedStationValue(station);
	}
	
	public void deleteOriginFavoriteStation(Station station){
		originFavoriteStations.remove(station);
		if(assitantService != null && station != null)
			stationService.deleteStationFavorite(station.getCode());
		assitantService.deleteStationCode(station.getCode(), context);
		/*if(masterservice != null)
		masterservice.deleteStationCode(station.getCode(), context);*/
		originNormalStations.add(station);
		Collections.sort(originFavoriteStations, comp);
	    Collections.sort(originNormalStations, comp);
		notifyDataSetChanged();
	}
}
