package com.example.validationlab.model;

import java.util.Date;

public class Analysis {

    public static final String DB_NAME = "validation_lab";
    public static final String TABLE_NAME = "analysis";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMG_PATH = "imgpath";
    public static final String COLUMN_NUM_CULTURES = "numcultures";
    public static final String COLUMN_ANALYSIS_DATE = "analysisdate";
    public static final String COLUMN_ANALYSIS = "analysis";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_IMG_PATH + " VARCHAR(255),"
                    + COLUMN_NUM_CULTURES + " INTEGER,"
                    + COLUMN_ANALYSIS_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_ANALYSIS + " VARCHAR(255)"
                    + ")";

    public static final String QUERY_DISTINCT_ANALYSIS = "SELECT DISTINCT(" + COLUMN_ANALYSIS + ") FROM " + TABLE_NAME;
    public static final String QUERY_ANALYSIS_BY_ANALISYS = "SELECT "
            + ", " + COLUMN_ANALYSIS
            + ", " + COLUMN_IMG_PATH
            + ", " + COLUMN_NUM_CULTURES
            + ", " + COLUMN_ANALYSIS_DATE
            + ", " + COLUMN_ANALYSIS
            + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ANALYSIS + " = ";

    private int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    private String imgPath;
    public String getImgPath() { return imgPath; }
    public void setImgPath(String imgPath) { this.imgPath = imgPath; }

    private int numCultures;
    public int getNumCultures() { return numCultures; }
    public void setNumCultures(int numCultures) { this.numCultures = numCultures; }

    private Date analysisDate;
    public Date getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(Date analysisDate) { this.analysisDate = analysisDate; }

    private String analysis;
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }


    public Analysis(int id, String imgPath, int numCultures, Date analysisDate, String analysis) {
        this.id = id;
        this.imgPath = imgPath;
        this.numCultures = numCultures;
        this.analysisDate = analysisDate;
        this.analysis = analysis;
    }

    public Analysis() {
    }

    public Analysis(String imgPath, int numCultures, Date analysisDate, String analysis) {
        this.imgPath = imgPath;
        this.numCultures = numCultures;
        this.analysisDate = analysisDate;
        this.analysis = analysis;
    }

}
