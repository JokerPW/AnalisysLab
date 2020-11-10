package com.example.validationlab.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.validationlab.MainActivity;
import com.example.validationlab.R;
import com.example.validationlab.model.Analysis;
import com.example.validationlab.utils.DatabaseAccess;
import com.example.validationlab.utils.ImageTools;
import com.example.validationlab.utils.SPreferences;

import java.util.Calendar;

public class Results extends Activity implements View.OnClickListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private ImageTools imgTools;
    private DatabaseAccess dba;
    private String currPath;
    private int currResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        this.imgTools = ImageTools.getInstance();
        this.imgTools.verifyStoragePermissions(this);
        this.dba = DatabaseAccess.getInstance(this);

        this.mViewHolder.textBlobs = findViewById(R.id.count_blobs);
        this.mViewHolder.textAnalysis = findViewById(R.id.text_analysis);
        this.mViewHolder.imgView = findViewById(R.id.imgvw);
        this.mViewHolder.btnSave = findViewById(R.id.btn_save);
        this.mViewHolder.btnSave.setOnClickListener(this);
        this.mViewHolder.btnReturn = findViewById(R.id.btnReturn);
        this.mViewHolder.btnReturn.setOnClickListener(this);

        this.loadDataFromActivity();
    }


    private void loadDataFromActivity() {
        //this.mViewHolder.btnSave.setEnabled(true);
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            currPath = extras.getString(SPreferences.FINAL_IMG);
            currResult = extras.getInt(SPreferences.RESULT);
            this.mViewHolder.textBlobs.setText (String.valueOf(currResult));
            this.mViewHolder.imgView.setImageBitmap (this.imgTools.loadImgByPath(currPath));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            Analysis a = new Analysis();
            a.setAnalysis(this.mViewHolder.textAnalysis.getText().toString());
            a.setAnalysisDate(Calendar.getInstance().getTime());
            a.setImgPath(currPath);
            a.setNumCultures(currResult);
            this.mViewHolder.btnSave.setEnabled(!dba.insertAnalysis(a));

        } else if (v.getId() == R.id.btnReturn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    private static class ViewHolder {
        TextView textBlobs;
        ImageView imgView;
        EditText textAnalysis;
        Button btnSave;
        Button btnReturn;
    }

}
