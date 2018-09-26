package com.nmbs.util;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import com.nmbs.activity.SettingsActivity;

public class EncryptEditTextPreference extends EditTextPreference {

	public EncryptEditTextPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public EncryptEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EncryptEditTextPreference(Context context) {
		super(context);
	}

	@Override
	public String getText() {
		if (getKey().equals(SettingsActivity.PREFS_FIRST_NAME)) {
			return SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_FIRST_NAME, getContext());
		}
		if (getKey().equals(SettingsActivity.PREFS_LAST_NAME)) {
			return SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_LAST_NAME, getContext());
		}
		if (getKey().equals(SettingsActivity.PREFS_EMAIL)) {
			return SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_EMAIL, getContext());
		}
		if (getKey().equals(SettingsActivity.PREFS_PHONE_NUMBER)) {
			return SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_PHONE_NUMBER, getContext());
		}
		return super.getText();
	}
	
	@Override
	protected boolean persistString(String value) {
		if (shouldPersist()) {
			// Shouldn't store null
			if (value == getPersistedString(null)) {
				// It's already there, so the same as persisting
				return true;
			}
		}
		return false;
	}

}
