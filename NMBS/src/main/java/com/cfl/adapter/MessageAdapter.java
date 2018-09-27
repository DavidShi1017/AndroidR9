package com.cfl.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activities.AlertActivity;
import com.cfl.activities.WebViewActivity;
import com.cfl.activities.WebViewOverlayActivity;
import com.cfl.activities.WizardActivity;
import com.cfl.activities.view.FitImageView;
import com.cfl.application.NMBSApplication;
import com.cfl.async.AsyncImageLoader;
import com.cfl.async.AsyncImageLoader.ImageCallback;
import com.cfl.model.MobileMessage;
import com.cfl.services.IClickToCallService;
import com.cfl.util.ImageUtil;
import com.cfl.util.NetworkUtils;
import com.cfl.util.Utils;

public class MessageAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater layoutInflater;
    public static Map<View, Boolean> map = new HashMap<View, Boolean>();
    private List<MobileMessage> mobileMessages;
    private IClickToCallService clickToCallService;
    public MessageAdapter(Activity activity, List<MobileMessage> mobileMessages) {
        this.activity = activity;
        this.mobileMessages = mobileMessages;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clickToCallService = ((NMBSApplication) activity.getApplication()).getClickToCallService();
    }


    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_messages, null);
        }
        final RelativeLayout groupLayout = (RelativeLayout) convertView.findViewById(R.id.rl_message_title);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expand);
        final RelativeLayout linearLayout = (RelativeLayout) convertView.findViewById(R.id.rl_message_detail);
        final ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
        final FitImageView imageDesc = (FitImageView) convertView.findViewById(R.id.iv_message_image);


        TextView tvMessageTitle = (TextView) convertView.findViewById(R.id.tv_message_title);
        TextView tvMessageDescription = (TextView) convertView.findViewById(R.id.tv_message_description);
        TextView tvMessageValidity = (TextView) convertView.findViewById(R.id.tv_validity);
        Button btnMessage = (Button) convertView.findViewById(R.id.btn_message);
        if (mobileMessages.get(position).isIncludeActionButton()){
            String btnText = mobileMessages.get(position).getActionButtonText();
            if(btnText != null){
                btnText = btnText.toUpperCase();
            }
            btnMessage.setText(btnText);
        }else{
            btnMessage.setVisibility(View.GONE);
        }

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mobileMessages.get(position).getHyperlink();
                if (url != null && !url.isEmpty()) {
                    //Utils.openProwser(activity, url, clickToCallService);
                    Utils.openWebView(activity, url, WebViewActivity.NORMAL_FLOW, "", mobileMessages.get(position).isNavigationInNormalWebView());
                    /*if(NetworkUtils.isOnline(activity)) {
                        if (url.contains("Navigation=WebView")) {
                            activity.startActivity(WebViewActivity.createIntent(activity.getApplicationContext(), Utils.getUrl(activity.getApplicationContext(), url, clickToCallService)));
                        } else {
                            activity.startActivity(WebViewOverlayActivity.createIntent(activity.getApplicationContext(), Utils.getUrl(activity.getApplicationContext(), url, clickToCallService)));
                        }
                    }*/
                }
            }
        });
        tvMessageTitle.setText(mobileMessages.get(position).getTitle());
        tvMessageDescription.setText(mobileMessages.get(position).getDescription());
        if(mobileMessages.get(position).getValidity() != null && !mobileMessages.get(position).getValidity().isEmpty()){
            tvMessageValidity.setVisibility(View.VISIBLE);
            tvMessageValidity.setText(mobileMessages.get(position).getValidity());
        }else{
            tvMessageValidity.setVisibility(View.GONE);
        }
        groupLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!map.containsKey(v)) {
                    map.put(v, true);
                    imageView.setImageResource(R.drawable.ic_unexpanded);
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    if (map.get(v) == true) {
                        imageView.setImageResource(R.drawable.ic_expand);
                        linearLayout.setVisibility(View.GONE);
                        map.put(v, false);
                    } else {
                        imageView.setImageResource(R.drawable.ic_unexpanded);
                        linearLayout.setVisibility(View.VISIBLE);
                        map.put(v, true);
                    }
                }

            }

        });

        if (mobileMessages.get(position).getImage(Utils.getDeviceDensity(activity)).trim().equals("") || mobileMessages.get(position).getImage(Utils.getDeviceDensity(activity)) == null) {
            imageDesc.setVisibility(View.GONE);
        } else {
            imageDesc.setVisibility(View.VISIBLE);
            String imageUrl = ImageUtil.convertImageExtension(mobileMessages.get(position).getImage(Utils.getDeviceDensity(activity)));
            String fullImageUrl = activity.getString(R.string.server_url_host) + imageUrl;
            AsyncImageLoader.getInstance().loadDrawable(
                    activity, fullImageUrl, imageUrl,
                    imageDesc, null, new ImageCallback() {

                        public void imageLoaded(Bitmap imageDrawable,
                                                String imageUrl, View view) {

                            ((ImageView) view).setImageBitmap(imageDrawable);
                        }
                    });
        }


        if (mobileMessages.get(position).getIcon(Utils.getDeviceDensity(activity)).trim().equals("") || mobileMessages.get(position).getIcon(Utils.getDeviceDensity(activity)) == null) {
            ivIcon.setVisibility(View.INVISIBLE);
        } else {
            ivIcon.setVisibility(View.VISIBLE);
            String imageUrl = ImageUtil.convertImageExtension(mobileMessages.get(position).getIcon(Utils.getDeviceDensity(activity)));
            String fullImageUrl = activity.getString(R.string.server_url_host) + imageUrl;
            AsyncImageLoader.getInstance().loadDrawable(
                    activity, fullImageUrl, imageUrl,
                    ivIcon, null, new ImageCallback() {
                        public void imageLoaded(Bitmap imageDrawable,
                                                String imageUrl, View view) {
                            ((ImageView) view).setImageBitmap(imageDrawable);
                        }
                    });
        }
        return convertView;
    }


    public int getCount() {

        return mobileMessages.size();
    }

}
