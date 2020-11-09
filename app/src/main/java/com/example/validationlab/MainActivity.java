package com.example.validationlab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.validationlab.utils.SPreferences;
import com.example.validationlab.view.ConfigActivity;
import com.example.validationlab.view.DBActivity;
import com.example.validationlab.view.OpenCamera;
import com.example.validationlab.view.OpenFile;

public class MainActivity extends Activity implements View.OnClickListener{

    private ViewHolder mViewHolder = new ViewHolder();
    private SPreferences sp;

    public MainActivity() {

    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sp = SPreferences.getInstance();

        this.mViewHolder.buttonFile = findViewById(R.id.button_file);
        this.mViewHolder.buttonFile.setOnClickListener(this);
        this.mViewHolder.buttonCamera = findViewById(R.id.button_camera);
        this.mViewHolder.buttonCamera.setOnClickListener(this);
        this.mViewHolder.buttonConfig = findViewById(R.id.button_config);
        this.mViewHolder.buttonConfig.setOnClickListener(this);
        this.mViewHolder.buttonDB = findViewById(R.id.button_db);
        this.mViewHolder.buttonDB.setOnClickListener(this);

//        this.mViewHolder.rg = findViewById(R.id.radioAlgoritmo);
//        this.mViewHolder.rColor = findViewById(R.id.radioColor);
//        this.mViewHolder.rArea = findViewById(R.id.radioArea);
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        if (view.getId() == R.id.button_camera) {
            intent = new Intent(this, OpenCamera.class);
        } else if (view.getId() == R.id.button_file) {
            intent = new Intent(this, OpenFile.class);
        } else if (view.getId() == R.id.button_config) {
            intent = new Intent(this, ConfigActivity.class);
        } else { //if (view.getId() == R.id.button_db) {
            intent = new Intent(this, DBActivity.class);
        }

        intent.putExtra(SPreferences.ANALYSIS_TYPE, SPreferences.TYPE_COLOR);
//                (this.mViewHolder.rg.getCheckedRadioButtonId() == R.id.radioColor) ?
//                        SPreferences.TYPE_COLOR : SPreferences.TYPE_AREA);
        startActivity(intent);
    }


    private static class ViewHolder {
        Button buttonCamera;
        Button buttonFile;
        Button buttonConfig;
        Button buttonDB;
       // RadioGroup rg;
       // RadioButton rColor;
       // RadioButton rArea;
    }

}