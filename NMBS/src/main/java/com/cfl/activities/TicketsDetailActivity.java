package com.cfl.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activities.view.DialogError;
import com.cfl.activities.view.uk.co.senab.photoview.PhotoViewAttacher;
import com.cfl.activity.BaseActivity;
import com.cfl.adapter.PagerViewAdapter;
import com.cfl.adapter.SeatAdapter;
import com.cfl.adapter.TariffAdapter;
import com.cfl.application.NMBSApplication;
import com.cfl.async.AsyncImageLoader;
import com.cfl.async.RealTimeInfoAsyncTask;
import com.cfl.exceptions.NetworkError;
import com.cfl.log.LogUtils;
import com.cfl.model.Dossier;
import com.cfl.model.DossierDetailParameter;
import com.cfl.model.DossierDetailsResponse;
import com.cfl.model.DossierSummary;
import com.cfl.model.DossierTravelSegment;
import com.cfl.model.PDF;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.model.SeatLocation;
import com.cfl.model.Tariff;
import com.cfl.model.TariffCondition;
import com.cfl.model.Ticket;
import com.cfl.model.TrainIcon;
import com.cfl.services.IAssistantService;
import com.cfl.services.impl.AsyncDossierAfterSaleResponse;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.SettingService;
import com.cfl.util.DateUtils;
import com.cfl.util.FileManager;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.ImageUtil;
import com.cfl.util.TrackerConstant;
import com.cfl.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class TicketsDetailActivity extends BaseActivity {

    private LayoutInflater layoutInflater;
    private PagerViewAdapter pagerViewAdapter;
    private com.cfl.activities.view.MyViewPager viewPager;
    private static final String Intent_Key_DossierTravelSegment = "DossierTravelSegment";
    private static final String Intent_Key_Dossier = "Dossier";
    private static final String Intent_Key_DossierSummary = "DossierSummary";
    private static final String Intent_Key_RealTimeInfoRequestParameter = "RealTimeInfoRequestParameter";

    private SettingService settingService;
    private DossierTravelSegment dossierTravelSegment;
    private IAssistantService assistantService;
    private Dossier dossier;
    private DossierSummary dossierSummary;
    private TextView tvTecietCount;
    private int ticketCount;
    boolean isExpandTicket = true, isExpandBarcode = true, isExpandDetailInfo = true;
    private DossierDetailsService dossierDetailsService;
    private List<ImageView> ivTicketsExpand = new ArrayList<>();
    private List<ImageView> ivBarcodesExpand = new ArrayList<>();
    private List<ImageView> ivDetailInfosExpand = new ArrayList<>();
    private List<LinearLayout> llTicketsContent = new ArrayList<>();
    private List<LinearLayout> llBarcodesContent = new ArrayList<>();
    private List<LinearLayout> llDetailInfosContent = new ArrayList<>();
    private int pagePosition;
    private List<PDF> pdfs = new ArrayList<>();
    private boolean isDisplayedPdf;
    private String pdfId;
    private final static String TAG = UploadDossierActivity.class.getSimpleName();
    private MyState myState;
    private ProgressDialog progressDialog;
    private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
    private int screenBrightness;
    private LinearLayout btnPdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setToolBarStyle(this);
        settingService = ((NMBSApplication) getApplication()).getSettingService();
        //settingService.initLanguageSettings();
        assistantService = ((NMBSApplication) getApplication()).getAssistantService();
        dossierDetailsService = ((NMBSApplication) getApplication()).getDossierDetailsService();
        setContentView(R.layout.activity_tickkets_detail);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getIntentValues();
        //bindAllViewElements();
        GoogleAnalyticsUtil.getInstance().sendScreen(TicketsDetailActivity.this, TrackerConstant.DOSSIER_TICKETDETAILS);
    }

    private void bindAllViewElements(){

        this.viewPager = (com.cfl.activities.view.MyViewPager)findViewById(R.id.vf_ticket_detail_view_pager);
        btnPdf = (LinearLayout) findViewById(R.id.ll_tickets_footer);


        tvTecietCount = (TextView) findViewById(R.id.tv_ticket_count);
        tvTecietCount.setText(getResources().getString(R.string.general_ticket) + " " + 1 + " " + getResources().getString(R.string.general_of) + " " + ticketCount);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagePosition = position;
                int index = position + 1;
                tvTecietCount.setText(getResources().getString(R.string.general_ticket) + " " + index + " " + getResources().getString(R.string.general_of) + " " + ticketCount);
                isExpandTicket = true;
                isExpandBarcode = true;
                isExpandDetailInfo = false;
                initTicketView();
                initBarcoidtView();
                initDetailView();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        List<View> viewList = new ArrayList<View>();
        String dossierId = "";
        if(dossier != null){
            dossierId = dossier.getDossierId();
        }
        ivTicketsExpand.clear();
        ivBarcodesExpand.clear();
        ivDetailInfosExpand.clear();
        llTicketsContent.clear();
        llBarcodesContent.clear();
        llDetailInfosContent.clear();

        if(dossierTravelSegment != null && dossierTravelSegment.getTickets() != null){

            List<Ticket> tickets = dossierTravelSegment.getTickets();
            for(int i = 0;i < tickets.size(); i++){

                Ticket ticket = tickets.get(i);
                View convertView = layoutInflater.inflate(R.layout.li_ticket_detail, null);

                TextView tvHeaderTitle = (TextView) convertView.findViewById(R.id.tv_tickets_detail_header_title);
                TextView tvTrainNr = (TextView) convertView.findViewById(R.id.tv_train_nr);
                TextView tvClass = (TextView) convertView.findViewById(R.id.tv_class);
                TextView tvTicketStation = (TextView) convertView.findViewById(R.id.tv_ticket_station);
                TextView tvTicketDepartureDate = (TextView) convertView.findViewById(R.id.tv_ticket_departure_date);
                TextView tvTicketArrivalDate = (TextView) convertView.findViewById(R.id.tv_ticket_arrival_date);

                TextView tvTicketTrainType = (TextView) convertView.findViewById(R.id.iv_li_ticket_detail_train_type_number);
                //TextView tvTicketTrainNr = (TextView) convertView.findViewById(R.id.iv_li_ticket_detail_train_number);
                TextView tvProvisionally = (TextView) convertView.findViewById(R.id.tv_provisionally);
                TextView tvReservation = (TextView) convertView.findViewById(R.id.tv_reservation);
                ImageView ivBarcode = (ImageView) convertView.findViewById(R.id.iv_barcode);
                TextView tvPdfStatus = (TextView) convertView.findViewById(R.id.tv_pdf_status);
                TextView tvBarcodeStutes = (TextView) convertView.findViewById(R.id.tv_barcode_stutes);
                TextView tvTariff = (TextView) convertView.findViewById(R.id.tv_tariff);
                LinearLayout llTariffs = (LinearLayout) convertView.findViewById(R.id.ll_tariffs);
                TextView tvSwipes = (TextView) convertView.findViewById(R.id.tv_tickets_detail_swipe_to_next);
                ImageView ivTrainIcon = (ImageView) convertView.findViewById(R.id.iv_li_ticket_detail_train_icon);
                //ivBarcode.setDisplayType(ImageViewTouchBase.DisplayType.NONE);
                //ivBarcode.setDoubleTapEnabled(true);
                //ivBarcode.setQuickScaleEnabled(true);
                //ivBarcode.setAdjustViewBounds(true);
                //ivBarcode.setScaleType(ImageView.ScaleType.FIT_CENTER);
                LinearLayout llTrainInfo = (LinearLayout) convertView.findViewById(R.id.ll_train_info);

                LinearLayout llSeat = (LinearLayout) convertView.findViewById(R.id.ll_seatlocation);
                TextView tvSeatTypeLabel = (TextView) convertView.findViewById(R.id.tv_li_ticket_detail_type_label);
                TextView tvSeatTypeValue = (TextView) convertView.findViewById(R.id.tv_li_ticket_detail_type_value);



                if(ticketCount == 1){
                    tvSwipes.setVisibility(View.GONE);
                }else{
                    tvSwipes.setVisibility(View.VISIBLE);
                }

                final RelativeLayout rlBarcoid = (RelativeLayout) convertView.findViewById(R.id.rl_barcode);
                final RelativeLayout rlTicket = (RelativeLayout) convertView.findViewById(R.id.rl_ticket);
                final RelativeLayout rlDetail = (RelativeLayout) convertView.findViewById(R.id.rl_detail_info);

                LinearLayout llTicketContent = (LinearLayout) convertView.findViewById(R.id.ll_ticket_content);
                ImageView ivTicketExpand = (ImageView) convertView.findViewById(R.id.iv_li_tickets_detail_ticketNr_expand_action);
                rlTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTicket();
                    }
                });
                ivTicketsExpand.add(ivTicketExpand);
                llTicketsContent.add(llTicketContent);

                LinearLayout llBarcodeContent = (LinearLayout) convertView.findViewById(R.id.ll_barcode_content);
                ImageView ivBarcodeExpand = (ImageView) convertView.findViewById(R.id.iv_li_tickets_detail_barcode_expand_action);
                rlBarcoid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBarcode();
                    }
                });

                llBarcodesContent.add(llBarcodeContent);
                ivBarcodesExpand.add(ivBarcodeExpand);

                LinearLayout llDetailInfoContent = (LinearLayout) convertView.findViewById(R.id.ll_detail_info_content);
                ImageView ivDetailInfoExpand = (ImageView) convertView.findViewById(R.id.iv_li_tickets_detail_expand_action);
                rlDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailInfo();
                    }
                });
                //llDetailInfosContent.add(llDetailInfoContent);
               // ivDetailInfosExpand.add(ivDetailInfoExpand);

                initTicketView();
                initBarcoidtView();
                initDetailView();

                if(ticket != null){

                    if(ticket.isBarcodeTicket()){
                        rlBarcoid.setVisibility(View.VISIBLE);
                        llBarcodeContent.setVisibility(View.VISIBLE);
                    }else{
                        rlBarcoid.setVisibility(View.GONE);
                        llBarcodeContent.setVisibility(View.GONE);
                    }

                    PDF pdf = dossier.getPdf(ticket.getPdfId());
                    pdfs.add(pdf);
                    showBarcodeZone(ticket, dossierId, ivBarcode, tvBarcodeStutes);

                    if(dossierSummary != null && dossierSummary.isPDFSuccessfully()){
                        if(dossier.getPdfs() != null && dossier.getPdfs().size() > 0){
                            btnPdf.setVisibility(View.VISIBLE);
                        }else{
                            btnPdf.setVisibility(View.GONE);
                        }
                        tvPdfStatus.setVisibility(View.GONE);

                    }else{
                        tvPdfStatus.setVisibility(View.VISIBLE);
                        btnPdf.setVisibility(View.GONE);
                        if(ticket.isPDFTicket() && (ticket.getPdfId() == null || ticket.getPdfId().isEmpty()) && dossier.isFullfilmentFailed()){
                            tvPdfStatus.setVisibility(View.VISIBLE);
                            tvPdfStatus.setBackgroundColor(getResources().getColor(R.color.background_error));
                            tvPdfStatus.setText(getString(R.string.pdf_not_available));
                        }
                        if(ticket.isPDFTicket() && (ticket.getPdfId() == null || ticket.getPdfId().isEmpty()) && !dossier.isFullfilmentFailed()){
                            tvPdfStatus.setVisibility(View.VISIBLE);
                            tvPdfStatus.setText(getString(R.string.pdf_not_yet_available));
                            tvPdfStatus.setBackgroundColor(getResources().getColor(R.color.general_light_orange));
                        }
                        if(dossier.getPdfs() == null || dossier.getPdfs().size() == 0){
                            tvPdfStatus.setVisibility(View.GONE);
                        }
                    }



                    tvHeaderTitle.setText(getResources().getString(R.string.general_ticket_for) + " " + Utils.arrayToString(dossierTravelSegment.getPassegersName(ticket)));
                    tvTrainNr.setText(ticket.getTicketNumber());
                    tvClass.setText(dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                    tvTicketStation.setText(dossierTravelSegment.getOriginStationName() + " - " + dossierTravelSegment.getDestinationStationName());

                    tvTicketDepartureDate.setText(getResources().getString(R.string.general_departure) + " " + DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "dd MMMM yyyy - HH:mm a"));
                    if(dossierTravelSegment.getArrivalDateTime() == null){
                        tvTicketArrivalDate.setVisibility(View.GONE);
                    }else{
                        tvTicketArrivalDate.setText(getResources().getString(R.string.general_arrival) + " " + DateUtils.dateTimeToString(dossierTravelSegment.getArrivalDateTime(), "dd MMMM yyyy - HH:mm a"));
                    }

                    if(DossierDetailsService.SegmentType_Admission.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
                        llTrainInfo.setVisibility(View.GONE);
                        if(dossierTravelSegment.getDepartureDate() != null && dossierTravelSegment.getValidityEndDate() != null){
                            //Log.e(TAG, "DepartureDate is...." + dossierTravelSegment.getDepartureDate());
                            //Log.e(TAG, "ValidityEndDate is...." + dossierTravelSegment.getValidityEndDate());
                            if(dossierTravelSegment.getDepartureDate().equals(dossierTravelSegment.getValidityEndDate())){

                                tvTicketDepartureDate.setText(getResources().getString(R.string.barcode_valid_date) + " " + DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "dd MMMM yyyy"));
                            }else{
                                tvTicketDepartureDate.setText(getResources().getString(R.string.barcode_valid_from) + " " + DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "dd MMMM yyyy"));
                            }
                        }
                        if(dossierTravelSegment.getValidityEndDate() != null){
                            if(!dossierTravelSegment.getDepartureDate().equals(dossierTravelSegment.getValidityEndDate())){
                                tvTicketArrivalDate.setVisibility(View.VISIBLE);
                                tvTicketArrivalDate.setText(getResources().getString(R.string.barcode_valid_until) + " " + DateUtils.dateTimeToString(dossierTravelSegment.getValidityEndDate(), "dd MMMM yyyy"));
                            }
                        }

                    }else{
                        //SeatLocation seatLocation = dossierTravelSegment.getSeatLocation(ticket.getSegmentPassengerIds());
                        SeatAdapter seatAdapter = new SeatAdapter(this);
                        for(SeatLocation seatLocation : dossierTravelSegment.getSeatLocations(ticket)){
                            seatAdapter.getSeatLocationView(seatLocation, llSeat);
                        }

                        String seatType = dossierTravelSegment.getAccommodationType();
                        if(seatType != null && !seatType.isEmpty()){
                            tvSeatTypeLabel.setVisibility(View.VISIBLE);
                            tvSeatTypeLabel.setText(getResources().getString(R.string.general_type) + ": ");
                            if("seat".equalsIgnoreCase(seatType)){
                                tvSeatTypeLabel.setVisibility(View.GONE);
                                tvSeatTypeValue.setText(getResources().getString(R.string.general_seat));
                                tvSeatTypeValue.setVisibility(View.GONE);
                            }else if("berth".equalsIgnoreCase(seatType)){
                                tvSeatTypeValue.setText(getResources().getString(R.string.general_berth));
                            }else{
                                tvSeatTypeValue.setText(getResources().getString(R.string.general_bed));
                            }

                        }else{
                            tvSeatTypeLabel.setVisibility(View.GONE);
                        }

                        addTrainIcon(ivTrainIcon);
                        String trainType = dossierTravelSegment.getTrainType();
                        TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
                        if(trainIcon != null){
                            if(trainIcon.getBrandName() != null && !trainIcon.getBrandName().isEmpty()){
                                trainType = trainIcon.getBrandName();
                            }
                        }

                        tvTicketTrainType.setText(trainType + " " + dossierTravelSegment.getTrainNumber());
                        //tvTicketTrainNr.setText(dossierTravelSegment.getTrainNumber());

                    }
                    if(ticket.getProvisionallyCancelledText() != null && !ticket.getProvisionallyCancelledText().isEmpty()){
                        tvProvisionally.setVisibility(View.VISIBLE);
                    }else {
                        tvProvisionally.setVisibility(View.GONE);
                    }
                    if(ticket.getReservationCancelledText() != null && !ticket.getReservationCancelledText().isEmpty()){
                        tvReservation.setVisibility(View.VISIBLE);
                    }else{
                        tvReservation.setVisibility(View.GONE);
                    }
                    if(dossier != null ){
                        Tariff tariff = dossier.getTariff(ticket.getTariffId());
                        tvTariff.setText(tariff.getTariffName());
                        List<TariffCondition> conditions = tariff.getTariffConditions();
                        TariffAdapter tariffAdapter = new TariffAdapter(this);
                        for(TariffCondition tariffCondition : conditions){
                            tariffAdapter.getTariffView(llTariffs, tariffCondition);
                        }
                    }

                    addExchangebleInfo(ticket, convertView);
                    addRefundInfo(ticket, convertView);
                    addTickTypeInfo(ticket, convertView);
                    addDeliveryInfo(ticket, convertView);
                }

                bindAllViewElements(convertView);
                viewList.add(convertView);
            }
        }

        this.pagerViewAdapter = new PagerViewAdapter(viewList);
        viewPager.setAdapter(pagerViewAdapter);
    }

    private void barcodeView(Ticket ticket, TextView tvBarcodeStutes, ImageView ivBarcode, String dossierId){
        if(ticket.isBarcodeTicket() && (dossierTravelSegment.getSegmentType().equalsIgnoreCase(DossierDetailsService.SegmentType_MARKETPRICE)
                || dossierTravelSegment.getSegmentType().equalsIgnoreCase(DossierDetailsService.SegmentType_Admission))
                && (ticket.getBarcodeId() == null || ticket.getBarcodeId().isEmpty()) && dossier.isFullfilmentFailed()){
            tvBarcodeStutes.setVisibility(View.VISIBLE);
            tvBarcodeStutes.setText(getString(R.string.barcode_not_available));
            tvBarcodeStutes.setBackgroundColor(getResources().getColor(R.color.background_error));
        }
        if(ticket.isBarcodeTicket() && (dossierTravelSegment.getSegmentType().equalsIgnoreCase(DossierDetailsService.SegmentType_MARKETPRICE)
                || dossierTravelSegment.getSegmentType().equalsIgnoreCase(DossierDetailsService.SegmentType_Admission))
                && (ticket.getBarcodeId() == null || ticket.getBarcodeId().isEmpty()) && !dossier.isFullfilmentFailed()){
            tvBarcodeStutes.setVisibility(View.VISIBLE);
            tvBarcodeStutes.setText(getString(R.string.barcode_not_yet_available));
            tvBarcodeStutes.setBackgroundColor(getResources().getColor(R.color.general_light_orange));
        }
        if(ticket.isBarcodeTicket() && dossierTravelSegment.getSegmentType().equalsIgnoreCase(DossierDetailsService.SegmentType_Reservation)
                && (dossierTravelSegment.getParentTravelSegmentId() == null || dossierTravelSegment.getParentTravelSegmentId().isEmpty())
                && (ticket.getBarcodeId() == null || ticket.getBarcodeId().isEmpty())){
            tvBarcodeStutes.setVisibility(View.VISIBLE);
            tvBarcodeStutes.setText(getString(R.string.barcode_not_yet_applicable_or_available ));
            tvBarcodeStutes.setBackgroundColor(getResources().getColor(R.color.background_group_title));
        }
        if(ticket.isBarcodeTicket() && dossierTravelSegment.getSegmentType().equalsIgnoreCase(DossierDetailsService.SegmentType_Reservation)
                && (dossierTravelSegment.getParentTravelSegmentId() != null && !dossierTravelSegment.getParentTravelSegmentId().isEmpty())
                && (ticket.getBarcodeId() == null || ticket.getBarcodeId().isEmpty())){
            DossierTravelSegment travelSegment = dossier.getParentTravelSegment(dossierTravelSegment);
            //Log.e("ticketParent", "ticketParent size ===" + travelSegment.getTickets().size());
            if(travelSegment.getTickets() != null && travelSegment.getTickets().size() > 0){
                Ticket ticketParent = travelSegment.getTickets().get(0) ;
                //Log.e("ticketParent", "ticketParent id ===" + ticketParent);
                showBarcodeZone(ticketParent, dossierId, ivBarcode, tvBarcodeStutes);
            }


            /*tvBarcodeStutes.setVisibility(View.VISIBLE);
            tvBarcodeStutes.setText(getString(R.string.barcode_not_applicable));
            tvBarcodeStutes.setBackgroundColor(getResources().getColor(R.color.background_group_title));*/
        }
        if(!ticket.isBarcodeTicket()){
            tvBarcodeStutes.setVisibility(View.GONE);
        }
    }

    private void showBarcodeZone(Ticket ticket, String dossierId, ImageView ivBarcode, TextView tvBarcodeStutes){
        Bitmap mBitmap = null;
        try {
            String name = dossierId + "-" + ticket.getBarcodeId() + ".png";
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inTempStorage = new byte[16 * 1024];
            options.inSampleSize = 1;

            mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(this, dossierId, "." + name), options);
            //mBitmap = assistantService.getStreamFromEncryptFile(dossierId, ticket.getBarcodeId());

        } catch (Exception e) {
            ivBarcode.setVisibility(View.GONE);
            tvBarcodeStutes.setBackgroundColor(getResources().getColor(R.color.background_error));
            tvBarcodeStutes.setVisibility(View.VISIBLE);
            tvBarcodeStutes.setText(getString(R.string.barcode_download_failed));
        }
        if(mBitmap != null){
            ivBarcode.setVisibility(View.VISIBLE);
            PhotoViewAttacher mAttacher;
            mAttacher = new PhotoViewAttacher(ivBarcode);mAttacher.update();
            tvBarcodeStutes.setVisibility(View.GONE);
            ivBarcode.setImageBitmap(mBitmap);
        }else{
            if(ticket.getBarcodeId() != null && !ticket.getBarcodeId().isEmpty()){
                ivBarcode.setVisibility(View.GONE);
                tvBarcodeStutes.setBackgroundColor(getResources().getColor(R.color.background_error));
                tvBarcodeStutes.setVisibility(View.VISIBLE);
                tvBarcodeStutes.setText(getString(R.string.barcode_download_failed));
                if(!ticket.isBarcodeTicket()){
                    tvBarcodeStutes.setVisibility(View.GONE);
                }

            }else{
                ivBarcode.setVisibility(View.GONE);
                barcodeView(ticket, tvBarcodeStutes, ivBarcode, dossierId);
            }
        }
    }

    private void addTrainIcon(ImageView ivTrainIcon){

        if(ImageUtil.getTrainTypeImageId(dossierTravelSegment.getTrainType()) == R.drawable.icon_train){
            TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
            //Log.e("TrainType", "TrainType====" + dossierTravelSegment.getTrainType());
            //Log.e("TrainType", "trainIcon====" + trainIcon);
            if(trainIcon != null){
                String imageUrl = ImageUtil.convertImageExtension(trainIcon.getIcon(Utils.getDeviceDensity(this)));
                String fullImageUrl = getString(R.string.server_url_host) + imageUrl;
                //Log.e("TrainType", "imageUrl====" + imageUrl);
                if(imageUrl == null || imageUrl.isEmpty()){
                    //Log.e("TrainType", "TrainType is null====");
                    //Log.e("TrainType", "TrainType is id====" + ImageUtil.getTrainTypeImageId(dossierTravelSegment.getTrainType()));
                    ivTrainIcon.setImageResource(ImageUtil.getTrainTypeImageId(dossierTravelSegment.getTrainType()));
                }else{
                    AsyncImageLoader.getInstance().loadDrawable(this, fullImageUrl, imageUrl,
                            ivTrainIcon, null, new AsyncImageLoader.ImageCallback() {
                                public void imageLoaded(Bitmap imageDrawable, String imageUrl, View view) {
                                    if(imageDrawable == null){
                                        ((ImageView) view).setImageResource(ImageUtil.getTrainTypeImageId(dossierTravelSegment.getTrainType()));
                                    }else{
                                        ((ImageView) view).setImageBitmap(imageDrawable);
                                    }
                                }});
                }
            }else{
                ivTrainIcon.setImageResource(R.drawable.icon_train);
            }
        }else{
            ivTrainIcon.setImageResource(ImageUtil.getTrainTypeImageId(dossierTravelSegment.getTrainType()));
        }

    }

    private void addExchangebleInfo(Ticket ticket, View convertView){
        TextView tvIsExchangeable = (TextView) convertView.findViewById(R.id.tv_is_exchangeable);
        TextView tvExchangeableReason = (TextView) convertView.findViewById(R.id.tv_exchangeable_reason);
        TextView tvExchangeableExtrainfo = (TextView) convertView.findViewById(R.id.tv_exchangeable_extrainfo);
        //Log.d(TAG, "isExchangeable=====" + ticket.isExchangeable());
        if(ticket.isExchangeable()){
            tvIsExchangeable.setText(getResources().getString(R.string.general_yes).toUpperCase());
        }else{
            tvIsExchangeable.setText(getResources().getString(R.string.general_no).toUpperCase());
        }
        if(ticket.getNotExchangeableReasonText() != null && !ticket.getNotExchangeableReasonText().isEmpty()){
            tvExchangeableReason.setVisibility(View.VISIBLE);
            tvExchangeableReason.setText(ticket.getNotExchangeableReasonText());
        }else{
            tvExchangeableReason.setVisibility(View.GONE);
        }
        if(ticket.getExchangeableExtraInfoText() != null && !ticket.getExchangeableExtraInfoText().isEmpty()){
            tvExchangeableExtrainfo.setVisibility(View.VISIBLE);
            tvExchangeableExtrainfo.setText(ticket.getExchangeableExtraInfoText());
        }else{
            tvExchangeableExtrainfo.setVisibility(View.GONE);
        }
    }

    private void addRefundInfo(Ticket ticket, View convertView){
        TextView tvIsRefund = (TextView) convertView.findViewById(R.id.tv_is_refund);
        TextView tvRefundRefundableReason = (TextView) convertView.findViewById(R.id.tv_refund_refundable_reason);
        TextView tvRefundRefundableExtrainfo = (TextView) convertView.findViewById(R.id.tv_refund_refundable_extrainfo);
        if(ticket.isRefundable()){
            tvIsRefund.setText(getResources().getString(R.string.general_yes).toUpperCase());
        }else{
            tvIsRefund.setText(getResources().getString(R.string.general_no).toUpperCase());
        }
        if(ticket.getNotRefundableReasonText() != null && !ticket.getNotRefundableReasonText().isEmpty()){
            tvRefundRefundableReason.setVisibility(View.VISIBLE);
            tvRefundRefundableReason.setText(ticket.getNotRefundableReasonText());
        }else{
            tvRefundRefundableReason.setVisibility(View.GONE);
        }
        if(ticket.getRefundableExtraInfoText() != null && !ticket.getRefundableExtraInfoText().isEmpty()){
            tvRefundRefundableExtrainfo.setVisibility(View.VISIBLE);
            tvRefundRefundableExtrainfo.setText(ticket.getRefundableExtraInfoText());
        }else{
            tvRefundRefundableExtrainfo.setVisibility(View.GONE);
        }
    }

    private void addTickTypeInfo(Ticket ticket, View convertView){

        TextView tvTicketType = (TextView) convertView.findViewById(R.id.tv_ticket_type);
        if(dossierTravelSegment != null){
            if(DossierDetailsService.SegmentType_MARKETPRICE.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
                tvTicketType.setText(getResources().getString(R.string.dossier_detail_ticket_marketprice));
            }else if(DossierDetailsService.SegmentType_Reservation.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
                tvTicketType.setText(getResources().getString(R.string.dossier_detail_ticket_reservation));
            }else if(dossierTravelSegment.isNVSAdmission() && DossierDetailsService.SegmentType_Admission.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
                tvTicketType.setText(getResources().getString(R.string.dossier_detail_ticket_nvsadmission));
            }else {
                tvTicketType.setText(getResources().getString(R.string.dossier_detail_ticket_otheradmission));
            }
        }
    }

    private void addDeliveryInfo(Ticket ticket, View convertView){
        LinearLayout lDeliveryMethod = (LinearLayout) convertView.findViewById(R.id.ll_delivery_method);
        TextView tvDeliveryMethodType = (TextView) convertView.findViewById(R.id.tv_delivery_method_type);
        TextView tvDeliveryMethodTypeExtra = (TextView) convertView.findViewById(R.id.tv_delivery_method_type_extra);
        tvDeliveryMethodType.setText(ticket.getProductionType());
        tvDeliveryMethodTypeExtra.setText(ticket.getProductionTypeExtraInfo());
        if((ticket.getProductionType() == null || ticket.getProductionType().isEmpty())
                && (ticket.getProductionTypeExtraInfo() == null || ticket.getProductionTypeExtraInfo().isEmpty())){
            lDeliveryMethod.setVisibility(View.GONE);
        }else{
            lDeliveryMethod.setVisibility(View.VISIBLE);
        }
    }


    private void getIntentValues(){
        dossierTravelSegment = (DossierTravelSegment) getIntent().getSerializableExtra(Intent_Key_DossierTravelSegment);
        dossier = (Dossier) getIntent().getSerializableExtra(Intent_Key_Dossier);
        dossierSummary = (DossierSummary) getIntent().getSerializableExtra(Intent_Key_DossierSummary);

        realTimeInfoRequestParameter = (RealTimeInfoRequestParameter) getIntent().getSerializableExtra(Intent_Key_RealTimeInfoRequestParameter);
    }



    private void initData(int index){

    }
    private void bindAllViewElements(View view){


    }

    public static Intent createIntent(Context context, DossierTravelSegment dossierTravelSegment, Dossier dossier,
                                      DossierSummary dossierSummary, RealTimeInfoRequestParameter realTimeInfoRequestParameter) {
        Intent intent = new Intent(context, TicketsDetailActivity.class);
        intent.putExtra(Intent_Key_DossierTravelSegment, dossierTravelSegment);
        intent.putExtra(Intent_Key_Dossier, dossier);
        intent.putExtra(Intent_Key_DossierSummary, dossierSummary);
        intent.putExtra(Intent_Key_RealTimeInfoRequestParameter, realTimeInfoRequestParameter);
        return intent;
    }

    public void back(View view) {
        finish();
    }

    public void showWizard(View view){
        startActivity(WizardActivity.createIntent(this, WizardActivity.Wizard_MyTickets));
    }

    public void refresh(View view){
        reEnableState();
    }


    private class MyState {

        public AsyncDossierAfterSaleResponse asyncDossierAfterSaleResponse;
        public DossierDetailsResponse dossier;
        private boolean isRefreshed;
        public void unRegisterHandler(){
            if (asyncDossierAfterSaleResponse != null){
                asyncDossierAfterSaleResponse.unregisterHandler();
            }
        }

        public void registerHandler(Handler handler){
            if (asyncDossierAfterSaleResponse != null){
                asyncDossierAfterSaleResponse.registerHandler(handler);
            }
        }
    }

    private void reEnableState(){
        if (myState == null){
            //Initial call
            //Log.i(TAG, "myState is null");
            myState = new MyState();
            //getDossierData("MXMYQWZ"); Testing

        }
        getDossierData();
        //Log.i(TAG, "Is Refreshed? " + myState.isRefreshed);

    }

    // refresh Data
    private void getDossierData(){
        if(this.dossier != null){
            DossierDetailParameter dossier = new DossierDetailParameter(this.dossier.getDossierId(), null);
            List<DossierDetailParameter> dossiers = new ArrayList<DossierDetailParameter>();
            dossiers.add(dossier);
            myState.asyncDossierAfterSaleResponse = NMBSApplication.getInstance().getAssistantService().refrshDossierDetail(dossiers, settingService, false, true);
            myState.asyncDossierAfterSaleResponse.registerHandler(mHandler);
            myState.registerHandler(mHandler);
            showWaitDialog();
        }
    }

    private void refreshRealTime(){
        RealTimeInfoAsyncTask asyncTask = new RealTimeInfoAsyncTask(getApplicationContext(), settingService, dossierDetailsService,
                realTimeHandler, realTimeInfoRequestParameter);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private Handler realTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RealTimeInfoAsyncTask.REALTIMEMESSAGE:
                    //Log.d(TAG, "realTimeHandler...Finished RealTime..");
                    break;
                case ServiceConstant.MESSAGE_WHAT_ERROR:
                    break;
            }
        }
    };

    private void responseReceived(DossierDetailsResponse dossierResponse) {
        //Log.e(TAG, "responseReceived....");
        hideWaitDialog();
        refreshRealTime();
        if(dossierResponse != null && dossierResponse.getDossier() != null){
            dossierDetailsService.setCurrentDossier(dossierResponse.getDossier());
            DossierSummary dossierSummary = dossierDetailsService.getDossier(dossierResponse.getDossier().getDossierId());
            dossierDetailsService.setCurrentDossierSummary(dossierSummary);
            this.dossierSummary = dossierDetailsService.getCurrentDossierSummary();
            this.dossier = dossierDetailsService.getCurrentDossier();
            dossierDetailsService.setCurrentDossierTravelSegment(this.dossier.getDossierTravelSegment(dossierTravelSegment.getTravelSegmentId()));
            dossierTravelSegment = dossierDetailsService.getCurrentDossierTravelSegment();
            if(dossierSummary != null){
                if(dossierSummary.isDossierPushEnabled()){
                    dossierDetailsService.autoEnableSubscription(dossierSummary, dossier, null, settingService.getCurrentLanguagesKey());
                    dossierDetailsService.deleteSubscription(dossier);
                }else {
                    dossierDetailsService.disableSubscription(dossier, dossierSummary, false);
                }
            }
        }else{
            dossierDetailsService.deleteDossierSubscription(dossierSummary, dossier);
        }
        bindAllViewElements();
        myState.dossier = dossierResponse;
        myState.unRegisterHandler();
        myState.isRefreshed = true;

        //Log.d(TAG, "show response");
    }

    //Handler which is called when the App is refreshed.
    // The what parameter of the message decides if there was an error or not.
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServiceConstant.MESSAGE_WHAT_OK:
                    responseReceived(myState.asyncDossierAfterSaleResponse.getDossierAftersalesResponse());
                    // showView();

                    break;
                case ServiceConstant.MESSAGE_WHAT_ERROR:
                    hideWaitDialog();
                    Bundle bundle = msg.getData();
                    NetworkError error = (NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
                    String responseErrorMessage = bundle.getString(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
                    switch (error) {
                        case TIMEOUT:
                            responseErrorMessage = getResources().getString(R.string.general_server_unavailable);
                            // finish();
                            break;
                        case wrongCombination:
                            responseErrorMessage = getResources().getString(R.string.upload_tickets_failure_parameter_mismatch);

                            // finish();
                            break;
                        case donotContainTicke:
                            responseErrorMessage = getResources().getString(R.string.upload_tickets_failure_notravelsegment);

                            // finish();
                            break;
                        case journeyPast:
                            responseErrorMessage = getResources().getString(R.string.alert_add_existing_ticket_journey_past_description);
                            // finish();
                            break;
                        case CustomError:
                            hideWaitDialog();
                            if (responseErrorMessage == null) {
                                responseErrorMessage = getString(R.string.general_server_unavailable);
                            }
                            break;
                        default:
                            break;
                    }
                    DialogError dialogError = new DialogError(TicketsDetailActivity.this,
                            getResources().getString(R.string.alert_mytickets_refresh_failed), responseErrorMessage);
                    dialogError.show();
                    //Log.d(TAG, "Upload dossier failure, the error msg is.........." + responseErrorMessage);
                    break;
            }
        }
    };

    // show progressDialog.
    private void showWaitDialog() {

        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this,
                    getString(R.string.alert_loading),
                    getString(R.string.alert_waiting), true);
        }
    }

    // hide progressDialog
    private void hideWaitDialog() {

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public void openPDF(View view){

        if(pdfs != null && pdfs.size() > 0 && pagePosition < pdfs.size()){
            PDF pdf = pdfs.get(pagePosition);
            if(pdf != null){
                isDisplayedPdf = true;
                pdfId = pdf.getPdfId();
                dossierDetailsService.openPDF(dossier.getDossierId(), pdf.getPdfId());
            }
        }
    }

    private void initTicketView(){
        LogUtils.e("TicketsDetailActivity", "isExpandTicket--------->" + isExpandTicket);
        if(isExpandTicket){
            if(ivTicketsExpand.size() > 0 && pagePosition < ivTicketsExpand.size()){
                ivTicketsExpand.get(pagePosition).setImageResource(R.drawable.ic_minus);
            }
            if(llTicketsContent.size() > 0 && pagePosition < llTicketsContent.size()){
                llTicketsContent.get(pagePosition).setVisibility(View.VISIBLE);
            }
        }else{
            if(ivTicketsExpand.size() > 0 && pagePosition < ivTicketsExpand.size()){
                llTicketsContent.get(pagePosition).setVisibility(View.GONE);
            }
            if(llTicketsContent.size() > 0 && pagePosition < llTicketsContent.size()){
                ivTicketsExpand.get(pagePosition).setImageResource(R.drawable.ic_plus);
            }
        }
    }
    private void initBarcoidtView(){
        LogUtils.e("TicketsDetailActivity", "isExpandBarcode--------->" + isExpandBarcode);
        if(isExpandBarcode){
            if(ivBarcodesExpand.size() > 0 && pagePosition < ivBarcodesExpand.size()){
                ivBarcodesExpand.get(pagePosition).setImageResource(R.drawable.ic_minus);
            }
            if(llBarcodesContent.size() > 0 && pagePosition < llBarcodesContent.size()){
                llBarcodesContent.get(pagePosition).setVisibility(View.VISIBLE);
            }

        }else{
            if(ivBarcodesExpand.size() > 0 && pagePosition < ivBarcodesExpand.size()){
                ivBarcodesExpand.get(pagePosition).setImageResource(R.drawable.ic_plus);
            }
            if(llBarcodesContent.size() > 0 && pagePosition < llBarcodesContent.size()){
                llBarcodesContent.get(pagePosition).setVisibility(View.GONE);
            }
        }
    }

    private void initDetailView(){
        LogUtils.e("TicketsDetailActivity", "isExpandDetailInfo--------->" + isExpandDetailInfo);
        if(isExpandDetailInfo){
            if(llDetailInfosContent.size() > 0 && pagePosition < llDetailInfosContent.size()){
                llDetailInfosContent.get(pagePosition).setVisibility(View.VISIBLE);
            }
            if(ivDetailInfosExpand.size() > 0 && pagePosition < ivDetailInfosExpand.size()){
                ivDetailInfosExpand.get(pagePosition).setImageResource(R.drawable.ic_minus);
            }
        }else{
            if(llDetailInfosContent.size() > 0 && pagePosition < llDetailInfosContent.size()){
                llDetailInfosContent.get(pagePosition).setVisibility(View.GONE);
            }
            if(ivDetailInfosExpand.size() > 0 && pagePosition < ivDetailInfosExpand.size()){
                ivDetailInfosExpand.get(pagePosition).setImageResource(R.drawable.ic_plus);
            }
        }
    }

    public void showTicket(){
        LogUtils.e("TicketsDetailActivity", "isExpandTicket--------->" + isExpandTicket);
        if(isExpandTicket){
            isExpandTicket = false;
            if(ivTicketsExpand.size() > 0 && pagePosition < ivTicketsExpand.size()){
                ivTicketsExpand.get(pagePosition).setImageResource(R.drawable.ic_plus);
            }
            if(llTicketsContent.size() > 0 && pagePosition < llTicketsContent.size()){
                llTicketsContent.get(pagePosition).setVisibility(View.GONE);
            }
        }else{
            isExpandTicket = true;
            if(ivTicketsExpand.size() > 0 && pagePosition < ivTicketsExpand.size()){
                ivTicketsExpand.get(pagePosition).setImageResource(R.drawable.ic_minus);
            }
            if(llTicketsContent.size() > 0 && pagePosition < llTicketsContent.size()){
                llTicketsContent.get(pagePosition).setVisibility(View.VISIBLE);
            }
        }
    }

    public void showBarcode(){
        LogUtils.e("TicketsDetailActivity", "isExpandBarcode--------->" + isExpandBarcode);
        if(isExpandBarcode){
            isExpandBarcode = false;
            if(ivBarcodesExpand.size() > 0 && pagePosition < ivBarcodesExpand.size()){
                ivBarcodesExpand.get(pagePosition).setImageResource(R.drawable.ic_plus);
            }
            if(llBarcodesContent.size() > 0 && pagePosition < llBarcodesContent.size()){
                llBarcodesContent.get(pagePosition).setVisibility(View.GONE);
            }
        }else{
            isExpandBarcode = true;
            if(ivBarcodesExpand.size() > 0 && pagePosition < ivBarcodesExpand.size()){
                ivBarcodesExpand.get(pagePosition).setImageResource(R.drawable.ic_minus);
            }
            if(llBarcodesContent.size() > 0 && pagePosition < llBarcodesContent.size()){
                llBarcodesContent.get(pagePosition).setVisibility(View.VISIBLE);
            }
        }
    }

    public void showDetailInfo(){
        LogUtils.e("TicketsDetailActivity", "isExpandDetailInfo--------->" + isExpandDetailInfo);
        if(isExpandDetailInfo){
            isExpandDetailInfo = false;
            if(ivDetailInfosExpand.size() > 0 && pagePosition < ivDetailInfosExpand.size()){
                ivDetailInfosExpand.get(pagePosition).setImageResource(R.drawable.ic_plus);
            }
            if(llDetailInfosContent.size() > 0 && pagePosition < llDetailInfosContent.size()){
                llDetailInfosContent.get(pagePosition).setVisibility(View.GONE);
            }
        }else{
            isExpandDetailInfo = true;
            if(ivDetailInfosExpand.size() > 0 && pagePosition < ivDetailInfosExpand.size()){
                ivDetailInfosExpand.get(pagePosition).setImageResource(R.drawable.ic_minus);
            }
            if(llDetailInfosContent.size() > 0 && pagePosition < llDetailInfosContent.size()){
                llDetailInfosContent.get(pagePosition).setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    protected void onResume() {
        // Log.d(TAG, "onResume");
        super.onResume();
        if (isDisplayedPdf) {
            isDisplayedPdf = false;
            dossierDetailsService.deleteUndecryptPdfFile(dossier.getDossierId(), pdfId);
        }
        if(dossierDetailsService.getCurrentDossier() != null){
            dossier = dossierDetailsService.getCurrentDossier();
        }
        if(dossierDetailsService.getCurrentDossierSummary() != null){
            dossierSummary = dossierDetailsService.getCurrentDossierSummary();
        }
        if(dossierDetailsService.getCurrentDossierTravelSegment() != null){
            dossierTravelSegment = dossierDetailsService.getCurrentDossierTravelSegment();
        }

        if(dossierTravelSegment != null && dossierTravelSegment.getTickets() != null) {
            ticketCount = dossierTravelSegment.getTickets().size();
        }
        bindAllViewElements();
        screenBrightness = getScreenBrightness();
        setScreenBrightness(255);
    }

    @Override
    protected void onStop() {
        setScreenBrightness(screenBrightness);
        super.onStop();
    }

    private int getScreenBrightness(){
        int screenBrightness=255;
        try{
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Exception localException){

        }
        return screenBrightness;
    }
    private void setScreenBrightness(int paramInt){
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }
}
