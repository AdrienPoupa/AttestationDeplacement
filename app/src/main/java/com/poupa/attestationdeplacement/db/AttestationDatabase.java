package com.poupa.attestationdeplacement.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AttestationEntity.class}, version = 2, exportSchema = false)
public abstract class AttestationDatabase extends RoomDatabase {
    public abstract AttestationDao daoAccess();

    private static final String DB_NAME = "db_attestation";

    private static volatile AttestationDatabase attestationDatabase;

    public static synchronized AttestationDatabase getInstance(Context context){
        if (attestationDatabase == null) attestationDatabase = create(context);
        return attestationDatabase;
    }

    private static AttestationDatabase create(final Context context) {
        return Room.databaseBuilder(context, AttestationDatabase.class, DB_NAME)
                .build();
    }
}