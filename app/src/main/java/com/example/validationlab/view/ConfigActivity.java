package com.example.validationlab.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.validationlab.MainActivity;
import com.example.validationlab.R;
import com.example.validationlab.utils.SPreferences;

public class ConfigActivity extends Activity implements View.OnClickListener {


    private ConfigActivity.ViewHolder mViewHolder = new ConfigActivity.ViewHolder();
    private SPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        this.sp = SPreferences.getInstance();

        this.mViewHolder.buttonSave = findViewById(R.id.button_save);
        this.mViewHolder.buttonSave.setOnClickListener(this);
        this.mViewHolder.buttonCancel = findViewById(R.id.button_cancel);
        this.mViewHolder.buttonCancel.setOnClickListener(this);

        this.mViewHolder.textH = findViewById(R.id.textH);
        this.mViewHolder.textS = findViewById(R.id.textS);
        this.mViewHolder.textV = findViewById(R.id.textV);
        this.mViewHolder.textA = findViewById(R.id.textA);

        this.mViewHolder.textH.setText(String.valueOf(sp.getVALUE_H()));
        this.mViewHolder.textS.setText(String.valueOf(sp.getVALUE_S()));
        this.mViewHolder.textV.setText(String.valueOf(sp.getVALUE_V()));
        this.mViewHolder.textA.setText(String.valueOf(sp.getVALUE_A()));

        this.mViewHolder.textR = findViewById(R.id.textR);
        this.mViewHolder.textG = findViewById(R.id.textG);
        this.mViewHolder.textB = findViewById(R.id.textB);
        this.mViewHolder.textRGBA = findViewById(R.id.textRGBA);

        this.mViewHolder.textR.setText(String.valueOf(sp.getVALUE_R()));
        this.mViewHolder.textG.setText(String.valueOf(sp.getVALUE_G()));
        this.mViewHolder.textB.setText(String.valueOf(sp.getVALUE_B()));
        this.mViewHolder.textRGBA.setText(String.valueOf(sp.getVALUE_RGB_A()));

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save) {
            sp.setVALUE_H(Long.parseLong(this.mViewHolder.textH.getText().toString()));
            sp.setVALUE_S(Long.parseLong(this.mViewHolder.textS.getText().toString()));
            sp.setVALUE_V(Long.parseLong(this.mViewHolder.textV.getText().toString()));
            sp.setVALUE_A(Long.parseLong(this.mViewHolder.textA.getText().toString()));

            sp.setVALUE_R(Long.parseLong(this.mViewHolder.textR.getText().toString()));
            sp.setVALUE_G(Long.parseLong(this.mViewHolder.textG.getText().toString()));
            sp.setVALUE_B(Long.parseLong(this.mViewHolder.textB.getText().toString()));
            sp.setVALUE_RGB_A(Long.parseLong(this.mViewHolder.textRGBA.getText().toString()));
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private static class ViewHolder {
        Button buttonSave;
        Button buttonCancel;
        EditText textH;
        EditText textS;
        EditText textV;
        EditText textA;

        EditText textR;
        EditText textG;
        EditText textB;
        EditText textRGBA;
    }

}