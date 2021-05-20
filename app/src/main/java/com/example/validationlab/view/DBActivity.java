package com.example.validationlab.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.validationlab.MainActivity;
import com.example.validationlab.R;
import com.google.common.math.Stats;

import java.util.ArrayList;
import java.util.List;

public class DBActivity extends Activity implements View.OnClickListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private Stats stats;
    private boolean isLoading;
    private List<Long> analysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        isLoading = true;
        analysis = new ArrayList<Long>();

        this.mViewHolder.btnAdd = findViewById(R.id.btnAdd);
        this.mViewHolder.btnAdd.setOnClickListener(this);
        this.mViewHolder.btnCalculate = findViewById(R.id.btnCalculate);
        this.mViewHolder.btnCalculate.setOnClickListener(this);

        this.mViewHolder.add_analisys = (TextView) findViewById(R.id.add_analisys);
        this.mViewHolder.add_dilution = (TextView) findViewById(R.id.add_dilution);
        this.mViewHolder.lista_resultados = (TextView) findViewById(R.id.lista_resultados);

        this.mViewHolder.resultadon = (TextView) findViewById(R.id.resultadon);
        this.mViewHolder.resultadomedia = (TextView) findViewById(R.id.resultadomedia);
        this.mViewHolder.resultadomediana = (TextView) findViewById(R.id.resultadomediana);
        this.mViewHolder.resultadodesvio = (TextView) findViewById(R.id.resultadodesvio);
        this.mViewHolder.btnReturn = findViewById(R.id.btnReturn);
        this.mViewHolder.btnReturn.setOnClickListener(this);
        this.mViewHolder.limpar = findViewById(R.id.limpar);
        this.mViewHolder.limpar.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnReturn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.btnAdd) {
            analysis.add((Long.parseLong(this.mViewHolder.add_analisys.getText().toString()) *
                    Long.parseLong(this.mViewHolder.add_dilution.getText().toString())));

            if (this.mViewHolder.lista_resultados.getText().toString().length() > 0)
                this.mViewHolder.lista_resultados.setText(this.mViewHolder.lista_resultados.getText().toString() +
                    ", " + this.mViewHolder.add_analisys.getText().toString());
            else
                this.mViewHolder.lista_resultados.setText(this.mViewHolder.add_analisys.getText().toString());

            this.mViewHolder.add_analisys.setText("");
            this.mViewHolder.add_dilution.setText("");

        } else if (v.getId() == R.id.btnCalculate) {
            getAnalysisCulture();
        }
        else if (v.getId() == R.id.limpar) {
            this.mViewHolder.add_analisys.setText("");
            this.mViewHolder.add_dilution.setText("");
            this.mViewHolder.resultadon.setText("");
            this.mViewHolder.resultadomedia.setText("");
            this.mViewHolder.resultadomediana.setText("");
            this.mViewHolder.resultadodesvio.setText("");
            this.mViewHolder.lista_resultados.setText("");
            analysis = new ArrayList<Long>();
        }
    }

    private void getAnalysisCulture() {
        double total = 0;
        long[] ntotal = new long[analysis.size()];

        for (int i = 0; i < analysis.size(); i++) {
            total += analysis.get(i);
            ntotal[i] = analysis.get(i);
        }
        this.mViewHolder.resultadon.setText(Integer.toString(analysis.size()));
        this.mViewHolder.resultadomedia.setText(Double.toString(total / analysis.size()));
        this.mViewHolder.resultadomediana.setText(Long.toString(ntotal[ntotal.length / 2]));

        Double std = Stats.of(ntotal).sampleStandardDeviation();
        this.mViewHolder.resultadodesvio.setText(std.toString());
    }


    private static class ViewHolder {
        TextView resultadon;
        TextView resultadomedia;
        TextView resultadomediana;
        TextView resultadomoda;
        TextView resultadodesvio;
        TextView add_analisys;
        TextView add_dilution;
        TextView lista_resultados;
        Button btnAdd;
        Button btnCalculate;
        Button btnReturn;
        Button limpar;

    }

}
