package com.cflint.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cflint.R;
import com.cflint.util.SharedPreferencesUtils;

public class DeleteButtonPreference extends Preference {
    private Context mContext;
    private ListPreference genderPref;
    private EditTextPreference firstNameEditTextPreference, lastNameEditTextPreference, phoneNumberEditTextPreference, emailEditTextPreference;
    public DeleteButtonPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public DeleteButtonPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.settings_view_delete_button);
    }
    public void setButtonInfo(Context context,EditTextPreference firstNameEditTextPreference,EditTextPreference lastNameEditTextPreference,EditTextPreference phoneNumberEditTextPreference,EditTextPreference emailEditTextPreference,ListPreference genderPref){
    	this.mContext = context;
    	this.firstNameEditTextPreference = firstNameEditTextPreference;
    	this.lastNameEditTextPreference = lastNameEditTextPreference;
    	this.phoneNumberEditTextPreference = phoneNumberEditTextPreference;
    	this.emailEditTextPreference = emailEditTextPreference;
    	this.genderPref = genderPref;
    }
    
    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        Button deleteButton = (Button) view.findViewById(R.id.btn_delete_info);
        deleteButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				
				final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(mContext.getString(R.string.settings_view_personal_info_delete_warning_title));
				builder.setMessage(mContext.getString(R.string.settings_view_personal_info_delete_warning_content));
				
				builder.setPositiveButton(mContext.getString(R.string.general_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});
				builder.setNegativeButton(mContext.getString(R.string.alert_continue),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Editor editor = SharedPreferencesUtils.initSharedPreferences(
										mContext).edit();
								editor.remove(SettingsActivity.PREFS_FIRST_NAME);
								editor.remove(SettingsActivity.PREFS_LAST_NAME);
								editor.remove(SettingsActivity.PREFS_EMAIL);
								editor.remove(SettingsActivity.PREFS_PHONE_NUMBER);
								editor.remove(SettingsActivity.PREFS_GENDER);
								editor.commit();
								firstNameEditTextPreference
										.setSummary(R.string.setting_view_first_name_alert);
								lastNameEditTextPreference
										.setSummary(R.string.setting_view_last_name_alert);
								phoneNumberEditTextPreference
										.setSummary(R.string.setting_view__mobile_number_alert);
								emailEditTextPreference
										.setSummary(R.string.setting_view_email_alert);
								genderPref.setSummary(R.string.setting_view_gender_alert);
						
							}
						});
				builder.show();
			}
		});
    }
}
