package com.sultanapp.moralstories.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.app.MyApplication;
import com.sultanapp.moralstories.helper.ApppreferenceManager;


public class SettingsActivity extends BaseActivity {
    TextView tvChangeTheme;
    private static final String MYPREFERENCES = "nightModePref";
    private static final String KEY_ISNIGHTMODE = "isNightMode";
    SharedPreferences sharedPreferences;
    ApppreferenceManager apppreferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_settings);
        tvChangeTheme = findViewById(R.id.tvChangeTheme);
        apppreferenceManager = new ApppreferenceManager(this);
        if(apppreferenceManager.getDarkModeState()){
            setTheme(R.style.AppTheme);

        }else {
            setTheme(R.style.AppThemeDark);
        }


        sharedPreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        // replace linear layout by preference screen
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();


        tvChangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (apppreferenceManager.getDarkModeState()) {

                    darkMode(false);
                } else {
                    darkMode(true);
                }
            }
        });

        initToolbar(true);
        setToolbarTitle(getString(R.string.settings));
        enableUpButton();
    }

    private void darkMode(boolean b) {
        apppreferenceManager.setDarkModeState(b);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        },1000);
    }



    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}