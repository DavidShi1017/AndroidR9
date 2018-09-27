package com.cfl.async;

import java.util.List;
import com.cfl.R;
import com.cfl.activity.MyTicketsActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.model.GeneralSetting;
import com.cfl.model.Order;
import com.cfl.services.IAssistantService;
import com.cfl.services.IMasterService;
import com.cfl.services.impl.ServiceConstant;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.Toast;

public class OrderAsyncTask extends AsyncTask<IAssistantService, Void, List<Order>>{
	private static final String TAG = OrderAsyncTask.class.getSimpleName();
	private IAssistantService assistantService;
	private IMasterService masterService;
	private int flag;
	private List<Order> listOrders;
	private List<Order> listHistoryOrders;
	private List<Order> listCanceledOrders;
	private ExceptionReceiver exceptionReceiver;
	private Handler mHandler;
	private GeneralSetting generalSetting;
	public OrderAsyncTask(Handler handler, int flag, IMasterService masterService){
		this.flag = flag;
		this.masterService = masterService;
		this.mHandler = handler;
	}
	
	@Override
	protected void onPostExecute(List<Order> result) {
		//hideProgressDialog(this.progressDialog);		
		if (exceptionReceiver != null && exceptionReceiver.excp != null) {
			Toast.makeText(NMBSApplication.getContext(), NMBSApplication.getContext().getString(R.string.general_server_unavailable), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPreExecute() {
		//showProgressDialog();
		super.onPreExecute();
	}

	@Override
	protected List<Order> doInBackground(IAssistantService... params) {
		Log.e(TAG, "handler receive: doInBackground...!");
		assistantService = params[0];
		generalSetting = masterService.loadGeneralSetting();
		assistantService.setGeneralSetting(generalSetting);
		String dossierAftersalesLifetime = "";
		if(generalSetting != null){
			dossierAftersalesLifetime = generalSetting.getDossierAftersalesLifetime() + "";
		}
		try {
			listOrders = assistantService.searchOrders(flag, dossierAftersalesLifetime);

			if (flag == MyTicketsActivity.FLAG_FIND_ORDER) {
				listHistoryOrders = assistantService.searchOrders(MyTicketsActivity.FLAG_FIND_ORDER_HISTORY, dossierAftersalesLifetime);
				listCanceledOrders = assistantService.searchOrders(MyTicketsActivity.FLAG_FIND_ORDER_CANCELED, dossierAftersalesLifetime);								
			}
			assistantService.setOrderList(listOrders);
			assistantService.setOrderHistoryList(listHistoryOrders);
			assistantService.setOrderCanceledList(listCanceledOrders);
			
			sendMessageByWhat(ServiceConstant.MESSAGE_WHAT_OK);
		} catch (Exception e) {
			e.printStackTrace();
			//
			sendMessageByWhat(ServiceConstant.MESSAGE_WHAT_ERROR);
			exceptionReceiver = new ExceptionReceiver();
			exceptionReceiver.excp= e;
			//Log.e(TAG, "ExceptionReceiver when searchOrders.", e);
			throw new RuntimeException();
		}
		return listOrders;
	}
	
/*	// show progressDialog.
	private void showProgressDialog() {
		if(this.progressDialog == null){			
			this.progressDialog = new ProgressDialog(c);
			this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.progressDialog.setMessage(c.getString(R.string.alert_loading));
			this.progressDialog.show();
		}		
	}
	//hide progressDialog
	private void hideProgressDialog(ProgressDialog progressDialog) {
		if(progressDialog != null){			
			progressDialog.hide();
			progressDialog.dismiss();
			progressDialog = null;
		}
	}*/
	//send handler message
	private void sendMessageByWhat(int messageWhat){
		Log.e(TAG, "handler receive: sendMessageByWhat...!");
		Message message = new Message();
		message.what = messageWhat;
		mHandler.sendMessage(message);		
	}
	
	public List<Order> getListOrders(){
		return listOrders;
	}
	public List<Order> getListHistoryOrders(){
		return listHistoryOrders;
	}
	public List<Order> getListCanceledOrders(){
		return listCanceledOrders;
	}
	/**
	 * 	It's a Exception class, used for receiving exception.
	 */
	private class ExceptionReceiver {
		private Exception excp;
	}

}
