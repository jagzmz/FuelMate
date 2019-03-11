package com.example.fuelmate;

import com.google.firebase.database.FirebaseDatabase;

public class DbUtils {

    private static FirebaseDatabase mDatabase;

    public  static void check() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

//        return mDatabase;

    }
}
