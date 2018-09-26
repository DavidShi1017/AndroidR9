package com.nmbs.model;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.nmbs.dataaccess.database.FavoriteDatabaseService;

/**
 * Data model implementation record the all Favorites' data.
 *
 */
public class AllFavorites {

	private static AllFavorites instance;
	
	private AllFavorites(){}
	
	public static AllFavorites getInstance(){
		if (instance == null){
			instance = new AllFavorites();
		}
		return instance;
	}
	
	private List<Favorite> listAllFavorites =  new ArrayList<Favorite>();
	
	public void addFavorite(Favorite favorite){
		if (favorite != null) {
			this.listAllFavorites.add(favorite);
		}
	}
		
	public List<Favorite> getAllFavorites(){
		return listAllFavorites;
	}
	
	/**
	 * Create all Favorites from the cursor, convert Cursor to the model
	 * @param cursor
	 */
	public void createAllFavoritesByCusor(Cursor cursor) {
		AllFavorites allFavorites = AllFavorites.getInstance();
		if (allFavorites.getAllFavorites() != null) {
			allFavorites.getAllFavorites().clear();
		}
		Favorite favorite = null;
		int cursorNum = cursor.getCount();
		for (int i = 0; i < cursorNum; i++) {			
			cursor.moveToPosition(i);
			int proId = cursor.getInt(cursor
					.getColumnIndexOrThrow(FavoriteDatabaseService.FAVORITE_ID));			
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(FavoriteDatabaseService.FAVORITE_NAME));
			
			favorite = new Favorite(proId, name);
			allFavorites.addFavorite(favorite);
		}
	}
}
