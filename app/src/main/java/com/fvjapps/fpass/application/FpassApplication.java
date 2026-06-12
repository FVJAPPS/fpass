package com.fvjapps.fpass.application;

import android.app.Application;

import com.fvjapps.fpass.db.AppDatabase;

public class FpassApplication extends Application {

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
