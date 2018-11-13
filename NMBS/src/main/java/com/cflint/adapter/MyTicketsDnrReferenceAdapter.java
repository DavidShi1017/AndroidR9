package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.DossierDetailActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.listeners.DeleteDossierListener;
import com.cflint.model.Connection;
import com.cflint.model.Dossier;
import com.cflint.model.DossierSummary;
import com.cflint.model.DossierTravelSegment;
import com.cflint.model.MyTicket;
import com.cflint.model.Order;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.services.impl.TestService;
import com.cflint.util.ComparatorConnectionDate;
import com.cflint.util.ComparatorMyTicketDate;
import com.cflint.util.ComparatorTravelSegmentDate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by David on 2/26/16.
 */
public class MyTicketsDnrReferenceAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private MyTicketsDnrReferenceChildAdapter myTicketsDnrReferenceChildAdapter;
    private DeleteDossierListener deleteDossierListener;
    public MyTicketsDnrReferenceAdapter(Activity activity, DeleteDossierListener deleteDossierListener){
        this.activity = activity;
        this.myTicketsDnrReferenceChildAdapter = new MyTicketsDnrReferenceChildAdapter(activity);
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.deleteDossierListener = deleteDossierListener;
    }

    public void getMyTicketsDnrReferenceView(final Dossier dossierDetailsResponse, LinearLayout linearLayout, final DossierSummary dossierSummary, boolean isActive) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_my_tickets_dnr_reference, null);
        TextView tvDnr = (TextView)convertView.findViewById(R.id.tv_dnr);
        TextView tvDnrLabel = (TextView)convertView.findViewById(R.id.tv_my_tickets_dnr_reference_item_label);
        tvDnrLabel.setText(activity.getResources().getString(R.string.general_booking_reference) + ": ");
        TextView tvDossierError = (TextView)convertView.findViewById(R.id.tv_dossier_error);
        TextView tvRefreshDossier = (TextView)convertView.findViewById(R.id.tv_refresh_dossier);
        TextView tvDossierFullfilmentFailed = (TextView)convertView.findViewById(R.id.tv_dossier_fullfilmentfailed);
        LinearLayout llMissingPdf = (LinearLayout)convertView.findViewById(R.id.ll_dossier_missing_pdf);
        LinearLayout llMissingPdfOrBarcode = (LinearLayout)convertView.findViewById(R.id.ll_dossier_missing_pdf_or_barcode);

            tvRefreshDossier.setText(activity.getResources().getString(R.string.stationboard_home_refresh).toUpperCase());
            LinearLayout tvDossierInProgress = (LinearLayout)convertView.findViewById(R.id.ll_dossier_inprogress);
            RelativeLayout rlDossier = (RelativeLayout) convertView.findViewById(R.id.rl_dossier);
            rlDossier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossier(null);
                    NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossierSummary(null);
                    activity.startActivity(DossierDetailActivity.createIntent(activity.getApplicationContext(), dossierDetailsResponse, dossierSummary));
                }
            });
        Button btn = (Button) convertView.findViewById(R.id.btn_delete_dossier);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NMBSApplication.getInstance().getDossierDetailsService().deleteDossier(dossierSummary);
                deleteDossierListener.deleted();
            }
        });
        if(TestService.isTestMode && !isActive){
            btn.setVisibility(View.GONE);
        } btn.setVisibility(View.GONE);
            if(dossierDetailsResponse != null){
                tvDnr.setText(dossierDetailsResponse.getDossierId());
                if(DossierDetailsService.DossierStateError.equalsIgnoreCase(dossierDetailsResponse.getDossierState())){
                    tvDossierError.setVisibility(View.VISIBLE);
                }else{
                    tvDossierError.setVisibility(View.GONE);
                }
                //Log.e("dossier", "getDossierState--->"+ dossierDetailsResponse.getDossierState());
                if(DossierDetailsService.DossierStateInProgress.equalsIgnoreCase(dossierDetailsResponse.getDossierState())){
                    RelativeLayout rlRefresh = (RelativeLayout)convertView.findViewById(R.id.rl_refresh);
                    tvDossierInProgress.setVisibility(View.VISIBLE);
                    rlRefresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Log.d("rlRefresh", "rlRefresh...");
                            refreshCallback.refresh(dossierDetailsResponse, dossierSummary);
                        }
                    });
                }else{
                    tvDossierInProgress.setVisibility(View.GONE);
                }
                if(DossierDetailsService.DossierStateConfirmed.equalsIgnoreCase(dossierDetailsResponse.getDossierState()) && dossierDetailsResponse.isFullfilmentFailed()){
                    tvDossierFullfilmentFailed.setVisibility(View.VISIBLE);
                }else{
                    tvDossierFullfilmentFailed.setVisibility(View.GONE);
                }
                if(dossierDetailsResponse.isMissingPDFs()){
                    llMissingPdf.setVisibility(View.VISIBLE);
                    RelativeLayout rlRefreshPdf = (RelativeLayout)convertView.findViewById(R.id.rl_refresh_pdf);
                    //tvDossierInProgress.setVisibility(View.VISIBLE);
                    rlRefreshPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refreshCallback.refresh(dossierDetailsResponse, dossierSummary);
                        }
                    });

                }else {
                    llMissingPdf.setVisibility(View.GONE);
                }

                //Log.e("dossier", "isBarcodeSuccessfully--->"+ dossierSummary.isBarcodeSuccessfully());
                //Log.e("dossier", "isPDFSuccessfully--->"+ dossierSummary.isPDFSuccessfully());
                if(!dossierSummary.isBarcodeSuccessfully() || !dossierSummary.isPDFSuccessfully()){
                    llMissingPdfOrBarcode.setVisibility(View.VISIBLE);
                    RelativeLayout rlRefreshPdf = (RelativeLayout)convertView.findViewById(R.id.rl_refresh_pdf_or_barcode);
                    //tvDossierInProgress.setVisibility(View.VISIBLE);
                    rlRefreshPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refreshCallback.refresh(dossierDetailsResponse, dossierSummary);
                        }
                    });

                }else {
                    llMissingPdfOrBarcode.setVisibility(View.GONE);
                }
            }
        
        List<MyTicket> myTickets = dossierDetailsResponse.getMyTicketData();
        //List<Connection> connections = dossierDetailsResponse.getConnections();
        //List<DossierTravelSegment> dtsList = dossierDetailsResponse.getTravelSegmentsForDossierList();
        LinearLayout childLayout = (LinearLayout)convertView.findViewById(R.id.ll_my_tickets_dnr_reference_item_ticket_list);
        //Log.e("isActive", "isActive..." + isActive);
        if(myTickets != null && myTickets.size() > 0){
            if (isActive){
                Comparator<MyTicket> comp = new ComparatorMyTicketDate(ComparatorMyTicketDate.ASC);
                Collections.sort(myTickets, comp);
            }else{
                Comparator<MyTicket> comp = new ComparatorMyTicketDate(ComparatorMyTicketDate.DESC);
                Collections.sort(myTickets, comp);
            }
        }

        for(int i = 0; i < myTickets.size(); i++){
            MyTicket myTicket = myTickets.get(i);
            if(myTicket != null){
                this.myTicketsDnrReferenceChildAdapter.getMyTicketsDnrReferenceChildView(myTicket, childLayout, dossierDetailsResponse, dossierSummary);
            }
        }
/*        if(dtsList != null && dtsList.size() > 0){
            Comparator<DossierTravelSegment> comp = new ComparatorTravelSegmentDate(dtsList);
            Collections.sort(dtsList, comp);
        }
        for(int i = 0; i < dtsList.size(); i++){
            this.myTicketsDnrReferenceChildAdapter.getMyTicketsDnrReferenceChildView(null, dtsList.get(i), childLayout, dossierDetailsResponse, dossierSummary);
        }*/
        linearLayout.addView(convertView);
    }
    private RefreshCallback refreshCallback;
    public void setRefreshCallback(RefreshCallback refreshCallback) {
        this.refreshCallback = refreshCallback;
    }
    public interface RefreshCallback {
        public void refresh(Dossier dossier, DossierSummary dossierSummary);
    }
}
