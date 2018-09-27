package com.cfl.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.adapter.AlertDialogDataAdapter;
import com.cfl.adapter.CollectionItemAdapter;
import com.cfl.model.CollectionItem;
import com.cfl.model.OfferQuery;
import com.cfl.model.PartyMember;
import com.cfl.model.ReductionCard;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AlertDialogUtils {
	//private static final String TAG = AlertDialogUtils.class.getSimpleName();
	public static Dialog createAlertDialog(Context context,final String[] mArray, final TextView textView
			, AlertDialogDataAdapter alertDialogDataAdapter, int dialogTitle, int dialogDataAdapterLayout){
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(context.getString(dialogTitle));		
		List<String> list = java.util.Arrays.asList(mArray);
		alertDialogDataAdapter = new AlertDialogDataAdapter( context, dialogDataAdapterLayout, list);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int which) {
				textView.setText(mArray[which]);
			}
		};
		builder.setAdapter(alertDialogDataAdapter, listener);
		return builder.create();
	}
	
	public static Dialog createCollectionItemAlertDialog(Context context,final OfferQuery offerQuery, final int position, final List<CollectionItem> list, final TextView textView
			, CollectionItemAdapter collectionItemAdapter, int dialogTitle, int dialogDataAdapterLayout){
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(context.getString(dialogTitle));		
		collectionItemAdapter = new CollectionItemAdapter(context, dialogDataAdapterLayout, list);
				
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialogInterface, int which) {
				textView.setText(list.get(which).getLable());
				if (StringUtils.isEmpty(list.get(which).getKey())) {
					List<ReductionCard> reductionCards = new ArrayList<ReductionCard>();
					reductionCards.add(new ReductionCard("",""));
					offerQuery.getTravelPartyMembers().get(position).setReductionCards(reductionCards);
				}else {
					
					if (offerQuery != null && offerQuery.getTravelPartyMembers() != null ){
						if (offerQuery.getTravelPartyMembers().get(position).getReductionCards()== null) {
							List<ReductionCard> reductionCards = new ArrayList<ReductionCard>();
							reductionCards.add(new ReductionCard("",""));
							offerQuery.getTravelPartyMembers().get(position).setReductionCards(reductionCards);
						}
						offerQuery.getTravelPartyMembers().get(position).getReductionCards().get(0).setCardNumber(list.get(which).getLable());
						offerQuery.getTravelPartyMembers().get(position).getReductionCards().get(0).setType(list.get(which).getKey());					
					}																									
				}				
			}
		};
		builder.setAdapter(collectionItemAdapter, listener);
		return builder.create();
	}
	
	
	
	public static Dialog createAlertDialogForCollectionItem(Context context, ArrayAdapter<CollectionItem> adapter, 
			String dialogTitle, DialogInterface.OnClickListener listener){
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(dialogTitle);		
		
		builder.setAdapter(adapter, listener);
		return builder.create();
	}
	
	public static Dialog createAlertDialogForPartMember(Context context, ArrayAdapter<PartyMember> adapter, 
			String dialogTitle, DialogInterface.OnClickListener listener){
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(dialogTitle);		
		
		builder.setAdapter(adapter, listener);
		return builder.create();
	}
	
	public static Dialog createAlertDialogForTrainCategory(Context context, ArrayAdapter<String> adapter, 
			String dialogTitle, DialogInterface.OnClickListener listener){
		Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(dialogTitle);		
		
		builder.setAdapter(adapter, listener);
		return builder.create();
	}
	
	public static AlertDialog createCheckUpdateDialog(String title, String message,Context context,View.OnClickListener negativeListener,View.OnClickListener positiveListener,IBackPressListener backPressListener) {
		CustomAlertDialogBuilder customAlertDialogBuilder = getBuilder(context);
		customAlertDialogBuilder.setTitle(title);
		customAlertDialogBuilder.setIcon(null);
		customAlertDialogBuilder.setMessage(message);
		customAlertDialogBuilder.setNeutralButtonListener(negativeListener);
		customAlertDialogBuilder.setPositiveButtonListener(positiveListener);
		customAlertDialogBuilder.setDialogBackListener(backPressListener);
		return customAlertDialogBuilder;
	}
	
	public static CustomAlertDialogBuilder getBuilder(Context ctx){
		return new CustomAlertDialogBuilder(ctx);
	}
	
	private static class CustomAlertDialogBuilder extends AlertDialog {
		// context
		private IBackPressListener backPressListener = null;
	    private final Context mContext;
	    private final TextView descText;
	    private final Button uploadApp,notNowUpdate;
		/**
	     * Constructor
	     * @param context
	     */
	    public CustomAlertDialogBuilder(Context context) {
	        // super(context, R.style.mDialog);
	    	super(context);
	        mContext = context; 
	        View customMessage = null;
			customMessage = View.inflate(mContext, R.layout.check_app_update_view, null);
			descText = (TextView) customMessage.findViewById(R.id.desc);
	        uploadApp = (Button) customMessage.findViewById(R.id.check_updata_download_btn);
	        notNowUpdate = (Button) customMessage.findViewById(R.id.check_updata_not_now_btn);
	       // webView.setBackgroundColor(0);  
	       // webView.setHorizontalScrollBarEnabled(false);
	        // set the color depend you set
	        
	        setView(customMessage);
	    }

		public void setNeutralButtonListener(View.OnClickListener listener) {
	    	notNowUpdate.setOnClickListener(listener);
		}


		public void setPositiveButtonListener(View.OnClickListener listener) {
			uploadApp.setOnClickListener(listener);
		}

	    public void setMessage(int textResId) {
	        descText.setText(textResId);
	    }

	    public void setMessage(CharSequence text) {
	    	descText.setText(text);
	    }
	    
	    @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (KeyEvent.KEYCODE_BACK == keyCode) {
				backPressListener.onBackPress();
			}
			return super.onKeyDown(keyCode, event);
		}
	    
		
		
		public void setDialogBackListener(IBackPressListener backPressListener) {
			this.backPressListener = backPressListener;
		}

	}
	
	public interface IBackPressListener {
		void onBackPress();
	}
	
}
