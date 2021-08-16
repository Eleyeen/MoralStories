package com.sultanapp.moralstories.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.app.MyApplication;
import com.sultanapp.moralstories.helper.ApppreferenceManager;

public class AboutDevActivity extends BaseActivity {
    ApppreferenceManager apppreferenceManager;
    ScrollView svAboutDev;
    TextView tvName,tvNAmedes,tvDes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();



    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        setContentView(R.layout.activity_about_dev);
        svAboutDev=findViewById(R.id.svAboutDev);
        tvName=findViewById(R.id.tvName);
        tvNAmedes=findViewById(R.id.tvNamedes);

        apppreferenceManager = new ApppreferenceManager(this);

        if(apppreferenceManager.getDarkModeState()){
            setTheme(R.style.AppTheme);

        }else {
            setTheme(R.style.AppThemeDark);

        }
        initToolbar(true);
        setToolbarTitle(getString(R.string.about_dev));
        enableUpButton();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

