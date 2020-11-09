package com.example.validationlab.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.validationlab.model.Analysis;

import java.io.InvalidClassException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess extends SQLiteOpenHelper {

    private static DatabaseAccess myDatabaseAccess;
    private static boolean canInstantiate = false;
    public static DatabaseAccess getInstance(Context context) {
        if (myDatabaseAccess == null) {
            canInstantiate = true;
            try {
                synchronized(DatabaseAccess.class) {
                    myDatabaseAccess = new DatabaseAccess(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            canInstantiate = false;
        }
        return myDatabaseAccess;
    }

    public static final String DATABASE_NAME = "db-analysis";
    private static final int DATABASE_VERSION = 1;

//    public AnalysisDAO analysisDao;
    private SQLiteDatabase mydatabase;
    private Context refContext;


    public DatabaseAccess(Context context) throws InvalidClassException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (!canInstantiate)
            throw new InvalidClassException("This class is a Singleton. Use its getInstance method");

        refContext = context;
        mydatabase = this.getWritableDatabase();
//        mydatabase = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        mydatabase.execSQL(Analysis.CREATE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            mydatabase.execSQL(Analysis.CREATE_TABLE);
        } catch (Exception er) {
            System.out.println(er);
        }
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Analysis.TABLE_NAME);

        // Create a new one.
        onCreate(db);
    }

    public void close() {
        mydatabase.close();
    }

    public boolean insertAnalysis(Analysis analysis) {
        long result = 0;

        try {
            ContentValues newValues = new ContentValues();
            // Assign values for each column.
            newValues.put(Analysis.COLUMN_IMG_PATH, analysis.getImgPath());
            newValues.put(Analysis.COLUMN_NUM_CULTURES, analysis.getNumCultures());
            newValues.put(Analysis.COLUMN_ANALYSIS_DATE, analysis.getAnalysisDate().toString());
            newValues.put(Analysis.COLUMN_ANALYSIS, analysis.getAnalysis());
            // Insert the row into your table
            result = mydatabase.insert(Analysis.TABLE_NAME, null, newValues);
            System.out.print(result);
            Toast.makeText(refContext, "Analysis Saved", Toast.LENGTH_LONG).show();

        } catch(Exception ex) {
            System.out.println("Exceptions " + ex);
            return false;
        }
        return result > 0;
    }

    public Analysis getSinlgeEntry (int analysisID) {
        mydatabase = this.getReadableDatabase();
        Cursor cursor = mydatabase.query(Analysis.TABLE_NAME, null, Analysis.COLUMN_ID + "=?", new String[]{Integer.toString(analysisID)}, null, null, null);
        if(cursor.getCount() < 1)
            return null;

        cursor.moveToFirst();
        Analysis anl = new Analysis(
                cursor.getInt(cursor.getColumnIndex(Analysis.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Analysis.COLUMN_IMG_PATH)),
                cursor.getInt(cursor.getColumnIndex(Analysis.COLUMN_NUM_CULTURES)),
                new Date (cursor.getLong(cursor.getColumnIndex(Analysis.COLUMN_ANALYSIS_DATE))),
                cursor.getString(cursor.getColumnIndex(Analysis.COLUMN_ANALYSIS))
        );

        return anl;
    }

    public List<Analysis> getAnalysisGroup (String analysis) {
        mydatabase = this.getReadableDatabase();
        Cursor cursor = mydatabase.query(Analysis.TABLE_NAME, null, Analysis.COLUMN_ANALYSIS + "=?", new String[]{analysis}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
            return null;
        cursor.moveToFirst();
        ArrayList<Analysis> ala = new ArrayList<Analysis>();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            ala.add(new Analysis(
                    cursor.getInt(cursor.getColumnIndex(Analysis.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Analysis.COLUMN_IMG_PATH)),
                    cursor.getInt(cursor.getColumnIndex(Analysis.COLUMN_NUM_CULTURES)),
                    new Date(cursor.getLong(cursor.getColumnIndex(Analysis.COLUMN_ANALYSIS_DATE))),
                    cursor.getString(cursor.getColumnIndex(Analysis.COLUMN_ANALYSIS))
            ));

            if (i <= cursor.getColumnCount())
                cursor.moveToNext();

        }
        return ala;
    }
//get: puxa a lista de grupos de analises //
public String[] getAnalysisGroups() {
    mydatabase = this.getReadableDatabase();
    String[] columns = new String[]{Analysis.COLUMN_ANALYSIS};

    Cursor cursor = mydatabase.query(true, Analysis.TABLE_NAME, columns, null, null, null, null, null,null);
    if(cursor.getCount() < 1)
        return null;

    cursor.moveToFirst();
    String[] als = new String[cursor.getCount()];
    for (int i = 0; i < cursor.getColumnCount(); i++) {
        als[i] = (cursor.getString(cursor.getColumnIndex(Analysis.COLUMN_ANALYSIS)));

        if (i <= cursor.getColumnCount())
            cursor.moveToNext();

    }

    return als;
}
}
