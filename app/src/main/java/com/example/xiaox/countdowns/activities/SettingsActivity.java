package com.example.xiaox.countdowns.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;


import com.example.xiaox.countdowns.R;
import com.example.xiaox.countdowns.basic.extension.XLog;

import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatPreferenceActivity  {
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            final ListPreference langPreference = (ListPreference)findPreference(getString(R.string.lang_pref_key_str));
            Preference skipHomePreference = findPreference(getString(R.string.skip_home_key));
            final SwitchPreference debugSwitch = (SwitchPreference) findPreference(getString(R.string.debug_switch_key));
            final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(getString(R.string.sharedPref_stored_key), MODE_PRIVATE);

            //Get stored language
            String storedLang = sharedPreferences.getString(getString(R.string.lang_pref_key_str), getString(R.string.not_set));
            //Store system language
            if(storedLang.equals(getString(R.string.not_set))){
                Locale locale = this.getResources().getConfiguration().locale;
                String sysLang = locale.getLanguage().endsWith("zh") ? getString(R.string.lang_cn) : getString(R.string.lang_en);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.lang_pref_key_str), sysLang);
                editor.apply();
                storedLang = sysLang;
            }
            //Set value of preference
            int langIndex = langPreference.findIndexOfValue(storedLang);
            if(langIndex < 0) langIndex = 0;
            final int onStartLangIndex = langIndex;
            langPreference.setSummary(String.valueOf(langPreference.getEntries()[onStartLangIndex]));
            langPreference.setValueIndex(onStartLangIndex);
            //Restore debug switch
            debugSwitch.setChecked(sharedPreferences.getBoolean(getString(R.string.debug_switch_key), false));
            debugSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isChecked = (boolean)newValue;
                    XLog.logEnable = isChecked;
                    System.out.println("XLog log enable " + isChecked);
                    debugSwitch.setChecked(isChecked);
                    //Save changes
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.debug_switch_key), isChecked);
                    editor.apply();
                    return false;
                }
            });
            skipHomePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final Activity activity = GeneralPreferenceFragment.this.getActivity();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    dialogBuilder.setTitle(R.string.skip_home).setMessage(R.string.hum).setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                    return false;
                }
            });
            langPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    //Get value
                    String oldValue = langPreference.getValue();
                    final String currentValue = (String)newValue;
                    if(oldValue.equals(currentValue)) return false;
                    XLog.logLine(currentValue);
                    //Get index
                    int index = langPreference.findIndexOfValue(currentValue);
                    langPreference.setValueIndex(index);
                    XLog.logLine("setValueIndex " + index);
                    if(index < 0) return false;
                    preference.setSummary(String.valueOf(langPreference.getEntries()[index]));
                    if(index == onStartLangIndex){
                        //Save changes
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.lang_pref_key_str), currentValue);
                        editor.apply();
                    }else{
                        //Require restart main activity
                        final Activity activity = GeneralPreferenceFragment.this.getActivity();
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                        dialogBuilder.setTitle(R.string.requires_restart)
                                .setMessage(R.string.ensure_change_lang).setPositiveButton(
                                R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //Save changes
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(getString(R.string.lang_pref_key_str), currentValue);
                                        editor.apply();
                                        //Call restart
                                        MainActivity.callSetLangRestartHome(activity);
                                    }
                                }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
                    return false;
                }
            });
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.lang_pref_key_str)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
