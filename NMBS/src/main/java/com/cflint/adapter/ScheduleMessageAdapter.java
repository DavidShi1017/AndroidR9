package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cflint.R;
import com.cflint.activities.AlertActivity;
import com.cflint.activities.WebViewActivity;
import com.cflint.activities.WebViewOverlayActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.AsyncImageLoader;
import com.cflint.model.UserMessage;
import com.cflint.util.ImageUtil;
import com.cflint.util.NetworkUtils;
import com.cflint.util.Utils;

import java.util.List;

/**
 * Created by Richard on 3/9/16.
 */
public class ScheduleMessageAdapter {
    private LayoutInflater layoutInflater;
    private List<UserMessage> userMessageList;
    private Activity activity;
    public ScheduleMessageAdapter(Activity activity, List<UserMessage> objects){
        this.activity = activity;
        this.userMessageList = objects;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getScheduleMessageView(int position, LinearLayout linearLayout) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_schedule_result_header_view, null);
        TextView desc = (TextView) convertView.findViewById(R.id.tv_schedule_message_desc);
        ImageView imageDesc = (ImageView)convertView.findViewById(R.id.iv_schedule_message_desc);
        TextView titleView = (TextView) convertView.findViewById(R.id.tv_schedule_message_title);
        ImageView titleIcon = (ImageView) convertView.findViewById(R.id.iv_schedule_message_head_icon);
        TextView time = (TextView) convertView.findViewById(R.id.tv_schedule_message_datetime);

        final ImageView imageButton = (ImageView) convertView.findViewById(R.id.iv_schedule_plus_action);
        final LinearLayout descLayout = (LinearLayout) convertView.findViewById(R.id.ll_schedule_message_desc_layou);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descLayout.getVisibility() == View.VISIBLE){
                    descLayout.setVisibility(View.GONE);
                    imageButton.setImageResource(R.drawable.ic_plus);
                }else{
                    descLayout.setVisibility(View.VISIBLE);
                    imageButton.setImageResource(R.drawable.ic_minus);

                }
            }
        });
        titleView.setText(userMessageList.get(position).getTitle());
        desc.setText(userMessageList.get(position).getDescription());
        time.setText(userMessageList.get(position).getValidity());

        Button btnMessage = (Button) convertView.findViewById(R.id.btn_schedule_message);
        final UserMessage userMessage = userMessageList.get(position);
        if (userMessageList.get(position).isIncludeActionButton()){
            btnMessage.setText(userMessageList.get(position).getActionButtonText());
            btnMessage.setVisibility(View.VISIBLE);
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userMessage!=null&&!"".equals(userMessage.getHyperlink())){
                        //Utils.openProwser(activity, userMessage.getHyperlink(), NMBSApplication.getInstance().getClickToCallService());
                        if(NetworkUtils.isOnline(activity)) {
                            activity.startActivity(WebViewOverlayActivity.createIntent(activity.getApplicationContext(),
                                    Utils.getUrl(userMessage.getHyperlink())));
                        }

                        /*Uri uri = Uri.parse(userMessage.getHyperlink());
                        Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);*/
                    }else {
                        Toast.makeText(activity,"the url is invalid！",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            btnMessage.setVisibility(View.GONE);
        }

        String imageIconUrl = ImageUtil.convertImageExtension(userMessageList.get(position).getIcon(Utils.getDeviceDensity(activity)));
        String fullIconImageUrl = activity.getString(R.string.server_url_host) + imageIconUrl;
        AsyncImageLoader.getInstance().loadDrawable(
                activity, fullIconImageUrl, imageIconUrl,
                titleIcon, null, new AsyncImageLoader.ImageCallback() {

                    public void imageLoaded(Bitmap imageDrawable,
                                            String imageUrl, View view) {
                        ((ImageView) view).setImageBitmap(imageDrawable);
                    }
                });

        if (userMessageList.get(position).getImage(Utils.getDeviceDensity(activity)).trim().equals("") || userMessageList.get(position).getImage(Utils.getDeviceDensity(activity)) == null) {
            imageDesc.setVisibility(View.GONE);
        } else {
            imageDesc.setVisibility(View.VISIBLE);
            String imageUrl = ImageUtil.convertImageExtension(userMessageList.get(position).getImage(Utils.getDeviceDensity(activity)));
            String fullImageUrl = activity.getString(R.string.server_url_host) + imageUrl;
            AsyncImageLoader.getInstance().loadDrawable(
                    activity, fullImageUrl, imageUrl,
                    imageDesc, null, new AsyncImageLoader.ImageCallback() {

                        public void imageLoaded(Bitmap imageDrawable,
                                                String imageUrl, View view) {
                            ((ImageView) view).setImageBitmap(imageDrawable);
                        }
                    });
        }
        linearLayout.addView(convertView);
    }
}
