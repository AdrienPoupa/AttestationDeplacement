package com.poupa.attestationdeplacement.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static android.content.Context.MODE_PRIVATE;

@Database(entities = {AttestationEntity.class, ProfileEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AttestationDao attestationDao();

    public abstract ProfileDao profileDao();

    private static final String DB_NAME = "db_attestation";

    private static volatile AppDatabase attestationDatabase;

    private static Context context;

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            try {
                SharedPreferences userDetails = context.getSharedPreferences("userDetails", MODE_PRIVATE);

                database.execSQL("INSERT INTO profiles (firstname, lastname, birthdate, birthplace, address, postalcode, city) " +
                        "VALUES (" +
                        "'" + userDetails.getString("surname", "") + "', " +
                        "'" + userDetails.getString("lastName", "") + "', " +
                        "'" + userDetails.getString("birthDate", "") + "', " +
                        "'" + userDetails.getString("birthPlace", "") + "', " +
                        "'" + userDetails.getString("address", "") + "', " +
                        "'" + userDetails.getString("postalCode", "") + "', " +
                        "'" + userDetails.getString("city", "")+ "'"
                        + ")");
            } catch (SQLException sqlException) {
                // Ignore the exception and move on
            }
        }
    };

    public static synchronized AppDatabase getInstance(Context appContext){
        if (attestationDatabase == null) {
            context = appContext;
            attestationDatabase = create(context);
        }
        return attestationDatabase;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .addMigrations(MIGRATION_2_3)
                .build();
    }
}