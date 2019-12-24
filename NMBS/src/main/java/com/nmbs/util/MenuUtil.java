package com.nmbs.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.AlertActivity;
import com.nmbs.activities.LoginActivity;
import com.nmbs.activities.MainActivity;
import com.nmbs.activities.MyTicketsActivity;
import com.nmbs.activities.ScheduleSearchActivity;
import com.nmbs.activities.SettingsActivity;
import com.nmbs.activities.StationBoardActivity;
import com.nmbs.activities.TestActivity;
import com.nmbs.activities.UploadDossierActivity;
import com.nmbs.activities.WebViewActivity;
import com.nmbs.activities.WebViewCreateActivity;
import com.nmbs.activities.WizardActivity;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.model.GeneralSetting;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;

public class MenuUtil {

    public static void clickMenu(Activity activity) {
        //startActivity(MenuActivity.createIntent(this, ticketCount, realtimeCount, messateCount));
        TextView tvMenuTicketCount = (TextView) activity.findViewById(R.id.tv_menu_ticket_count);
        TextView tvMenuRealtimeCount = (TextView) activity.findViewById(R.id.tv_menu_realtime_count);
        TextView tvMenuMessageCount = (TextView) activity.findViewById(R.id.tv_menu_message_count);
        tvMenuTicketCount.setText("(" + MainActivity.ticketCount + ")");
        tvMenuRealtimeCount.setText("(" + MainActivity.realtimeCount + ")");
        tvMenuMessageCount.setText("(" + MainActivity.messateCount + ")");

        RelativeLayout rlMenuMyOption = (RelativeLayout) activity.findViewById(R.id.rl_menu_traintickets_content_myoptions);
        TextView tvMenuOptionCount = (TextView) activity.findViewById(R.id.tv_menu_option_count);
        tvMenuOptionCount.setText("(" + MainActivity.optionCount + ")");
        RelativeLayout rlMenuAlerts = (RelativeLayout) activity.findViewById(R.id.rl_menu_traintickets_content_realtimealerts);
        if(FunctionConfig.kFunManagePush){
            rlMenuAlerts.setVisibility(View.VISIBLE);
        }else {
            rlMenuAlerts.setVisibility(View.GONE);
        }

        TextView tvMenuLogon = (TextView) activity.findViewById(R.id.tv_menu_logon);
        RelativeLayout rlMenuLogin = (RelativeLayout) activity.findViewById(R.id.rl_menu_traintickets_content_login);
        if(!NMBSApplication.getInstance().getLoginService().isLogon()){
            rlMenuMyOption.setAlpha(0.3f);
            tvMenuOptionCount.setText("");
            tvMenuLogon.setText(activity.getResources().getString(R.string.menu_content_loginorcreateprofile));
            if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
                    && NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl() != null
                    && !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl().isEmpty()){
                rlMenuLogin.setVisibility(View.VISIBLE);
            }else {
                rlMenuLogin.setVisibility(View.GONE);
            }
        }else {
            tvMenuLogon.setText(activity.getResources().getString(R.string.menu_content_manageprofile));
        }
        if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
                && NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl() != null
                && !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl().isEmpty()){
            rlMenuMyOption.setVisibility(View.VISIBLE);
        }else {
            rlMenuMyOption.setVisibility(View.GONE);
        }

    }
    static boolean isGoto;
    public static void messages(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList,
                         final IMessageService messageService) {
        System.out.println("------->" + activity.getLocalClassName());

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        System.out.println("drawerOpen------->" + drawerOpen);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(com.nmbs.activities.MessageActivity.createIntent(activity,
                                        messageService.getMessageResponse()));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(com.nmbs.activities.MessageActivity.createIntent(activity,
                    messageService.getMessageResponse()));
        }
    }


    public static void stationBoard(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(com.nmbs.activities.StationBoardActivity.createIntent(activity,null));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(com.nmbs.activities.StationBoardActivity.createIntent(activity,null));
        }

    }

    public static void stations(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(com.nmbs.activities.StationInfoActivity.createIntent(activity));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(com.nmbs.activities.StationInfoActivity.createIntent(activity));
        }
        //startActivity(com.nmbs.activities.CallCenterActivity.createIntent(MainActivity.this,0,null,null));
    }

    public static void realtimeAlerts(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(com.nmbs.activities.AlertActivity.createIntent(activity));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(com.nmbs.activities.AlertActivity.createIntent(activity));
        }
    }

    public static void bookTicktes(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList,
                            IMasterService masterService){
        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_BOOK_YOUR_TICKET,TrackerConstant.HOME_BOOK_YOUR_TICKET_LABEL);
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(generalSetting != null && generalSetting.getBookingUrl() != null && !generalSetting.getBookingUrl().isEmpty()){
                                    //Utils.openProwser(StationBoardActivity.this, generalSetting.getBookingUrl(), clickToCallService);
                                    if(NetworkUtils.isOnline(activity)) {
                                        GoogleAnalyticsUtil.getInstance().sendScreen(activity, TrackerConstant.BOOKING);
                                        activity.startActivity(WebViewActivity.createIntent(activity,
                                                Utils.getUrl(generalSetting.getBookingUrl()), WebViewActivity.BOOKING_FLOW, ""));
                                    }
                                }
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(WebViewActivity.createIntent(activity,
                    Utils.getUrl(generalSetting.getBookingUrl()), WebViewActivity.BOOKING_FLOW, ""));
        }
    }

    public static void lowestFares(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList,
                            IMasterService masterService){
        GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_SELECT_LOWESTFARES,TrackerConstant.HOME_SELECT_LOWESTFARES_LABEL);
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(generalSetting != null && generalSetting.getLffUrl()!= null && !generalSetting.getLffUrl().isEmpty()){
                                    //Utils.openProwser(StationBoardActivity.this, generalSetting.getLffUrl(), clickToCallService);
                                    if(NetworkUtils.isOnline(activity)) {
                                        GoogleAnalyticsUtil.getInstance().sendScreen(activity, TrackerConstant.LLF);
                                        activity.startActivity(WebViewActivity.createIntent(activity,
                                                Utils.getUrl(generalSetting.getLffUrl()), WebViewActivity.BOOKING_FLOW, ""));
                                    }
                                }
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(WebViewActivity.createIntent(activity,
                    Utils.getUrl(generalSetting.getLffUrl()), WebViewActivity.BOOKING_FLOW, ""));
        }
    }
    public static void trainschedules(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(ScheduleSearchActivity.createIntent(activity));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(ScheduleSearchActivity.createIntent(activity));
        }

    }

    public static void settings(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(SettingsActivity.createIntent(activity));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(SettingsActivity.createIntent(activity));
        }
    }

    public static void about(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(WizardActivity.createIntent(activity, WizardActivity.Wizard_Home));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(WizardActivity.createIntent(activity, WizardActivity.Wizard_Home));
        }
    }
    public static void myTickets(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){

        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(MyTicketsActivity.createIntent(activity));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(MyTicketsActivity.createIntent(activity));
        }
    }

    public static void uploadTickets(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList){
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                activity.startActivity(UploadDossierActivity.createUploadDossierIntent(activity, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
                                isGoto = false;
                                if(!activity.getLocalClassName().equalsIgnoreCase("activities.MainActivity")){
                                    activity.finish();
                                }
                            }
                        }
                    });
        }else{
            activity.startActivity(UploadDossierActivity.createUploadDossierIntent(activity, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
        }
    }
    public static void myOption(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList,
                         IMasterService masterService){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(NMBSApplication.getInstance().getLoginService().isLogon()){
                                    if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
                                        activity.startActivity(WebViewActivity.createIntent(activity,
                                                Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
                                        GoogleAnalyticsUtil.getInstance().sendScreen(activity, TrackerConstant.CommercialTTLListUrl);
                                    }
                                }else{
                                    if(!activity.isFinishing()){
                                        DialogMyOptions dialogMyOptions = new DialogMyOptions(activity);
                                        dialogMyOptions.show();
                                    }

                                }
                                isGoto = false;
                            }
                        }
                    });
        }else{
            if(NMBSApplication.getInstance().getLoginService().isLogon()){
                if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
                    activity.startActivity(WebViewActivity.createIntent(activity,
                            Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
                    GoogleAnalyticsUtil.getInstance().sendScreen(activity, TrackerConstant.CommercialTTLListUrl);
                }
            }else{
                if(!activity.isFinishing()){
                    DialogMyOptions dialogMyOptions = new DialogMyOptions(activity);
                    dialogMyOptions.show();
                }
            }
        }


    }

    public static void loginOrManage(final Activity activity, DrawerLayout mDrawerLayout, LinearLayout mDrawerList,
                              IMasterService masterService){
        //startActivity(LoginActivity.createIntent(MainActivity.this));
        final GeneralSetting generalSetting = masterService.loadGeneralSetting();
        isGoto = true;
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(GravityCompat.END);
            mDrawerLayout.setDrawerListener(
                    new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            if(isGoto){
                                if(NMBSApplication.getInstance().getLoginService().isLogon()){
                                    ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(activity, null);
                                    profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    activity.startActivity(WebViewCreateActivity.createIntent(activity, Utils.getUrl(generalSetting.getProfileOverviewUrl())));
                                    GoogleAnalyticsUtil.getInstance().sendScreen(activity, TrackerConstant.ProfileOverviewUrl);
                                }else{
                                    activity.startActivity(LoginActivity.createIntent(activity, ""));
                                }
                                isGoto = false;
                            }
                        }
                    });
        }else{
            if(NMBSApplication.getInstance().getLoginService().isLogon()){
                ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(activity, null);
                profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                activity.startActivity(WebViewCreateActivity.createIntent(activity, Utils.getUrl(generalSetting.getProfileOverviewUrl())));
                GoogleAnalyticsUtil.getInstance().sendScreen(activity, TrackerConstant.ProfileOverviewUrl);
            }else{
                activity.startActivity(LoginActivity.createIntent(activity, ""));
            }
        }
    }
}
