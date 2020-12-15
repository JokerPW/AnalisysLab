package com.example.validationlab.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.validationlab.MainActivity;
import com.example.validationlab.R;
import com.example.validationlab.model.Analysis;
import com.example.validationlab.utils.DatabaseAccess;
import com.google.common.math.Stats;

import java.util.ArrayList;
import java.util.List;

public class DBActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private Stats stats;
    private boolean isLoading;
    private List<Integer> analysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        isLoading = true;
        analysis = new ArrayList<Integer>();

        //get the spinner from the xml.
        this.mViewHolder.listaanalises = (Spinner) findViewById(R.id.lista_analises);

        this.mViewHolder.resultadon = (TextView) findViewById(R.id.resultadon);
        this.mViewHolder.resultadomedia = (TextView) findViewById(R.id.resultadomedia);
        this.mViewHolder.resultadomediana = (TextView) findViewById(R.id.resultadomediana);
        //this.mViewHolder.resultadomoda = (TextView) findViewById(R.id.resultadomoda);
        this.mViewHolder.resultadodesvio = (TextView) findViewById(R.id.resultadodesvio);
        this.mViewHolder.btnReturn = findViewById(R.id.btnReturn);
        this.mViewHolder.btnReturn.setOnClickListener(this);

//create a list of items for the spinner.
        String[] items = DatabaseAccess.getInstance(this).getAnalysisGroups();
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        if (items != null && items.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
            this.mViewHolder.listaanalises.setAdapter(adapter);
            this.mViewHolder.listaanalises.setOnItemSelectedListener(this);
        } else {
            this.mViewHolder.resultadon.setText("Sem dados para analisar");
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnReturn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        System.out.println("++++++++++ Item selected: " + parent.getItemAtPosition(pos));
        System.out.println("isLoading: " + isLoading);
        if (isLoading) {
            isLoading = false;
            System.out.println("$$$$$$ RETURNING HERE");
            return;
        }

        getAnalysisCulture((String) parent.getItemAtPosition(pos));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    private void getAnalysisCulture(String group) {
        System.out.println("@@@@@@@@ ANALYSIS SELECTED");
        List<Analysis> analysis = DatabaseAccess.getInstance(this).getAnalysisGroup(group);
        double total = 0;
        int[] ntotal = new int[analysis.size()];

        for (int i = 0; i < analysis.size(); i++) {
            total += analysis.get(i).getNumCultures();
            ntotal[i] = analysis.get(i).getNumCultures();

            total += analysis.get(i).getNumCultures();
            ntotal[i] = analysis.get(i).getNumCultures();
        }
        this.mViewHolder.resultadon.setText(Integer.toString(analysis.size()));
        this.mViewHolder.resultadomedia.setText(Double.toString(total / analysis.size()));
        this.mViewHolder.resultadomediana.setText(Integer.toString(ntotal[ntotal.length / 2]));

        Double std = Stats.of(ntotal).populationStandardDeviation();
        this.mViewHolder.resultadodesvio.setText(std.toString());
    }


    private static class ViewHolder {
        Spinner listaanalises;
        TextView resultadon;
        TextView resultadomedia;
        TextView resultadomediana;
        TextView resultadomoda;
        TextView resultadodesvio;
        Button btnReturn;
    }

}
