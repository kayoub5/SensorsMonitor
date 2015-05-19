package com.kaanich.sensorsmonitor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.kaanich.sensorsmonitor.models.*;
import com.kaanich.sensorsmonitor.reporters.BatteryLevelReporter;
import com.kaanich.sensorsmonitor.reporters.TelephonySignalStrengthReporter;

/**
 * Helper class which creates/updates our database and provides the DAOs.
 *
 * @author kevingalligan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "monitor.db";
    private static final int DATABASE_VERSION = 1;
    private final String LOG_NAME = getClass().getName();

    // private Dao<Metric, String> metricDao;
    private Map<String, Dao> daoRegistry;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        daoRegistry = new HashMap<>();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Metric.class);
            TableUtils.createTable(connectionSource, Sensor.class);
            TableUtils.createTable(connectionSource, Measurement.class);

            Dao<Metric, String> metricDao = getDao(Metric.class);
            Dao<Sensor, String> sensorDao = getDao(Sensor.class);

            metricDao.createOrUpdate(Metric.PERCENTAGE);

            sensorDao.createOrUpdate(TelephonySignalStrengthReporter.SENSOR);
            sensorDao.createOrUpdate(BatteryLevelReporter.SENSOR);

        } catch (SQLException e) {
            Log.e(LOG_NAME, "Could not create new table for Thing", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Metric.class, true);
            TableUtils.dropTable(connectionSource, Sensor.class, true);
            TableUtils.dropTable(connectionSource, Measurement.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(LOG_NAME, "Could not upgrade the table for Thing", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        String className = clazz.getName();
        D dao;
        if (daoRegistry.containsKey(className)) {
            dao = (D)daoRegistry.get(className);
        } else {
            dao = super.getDao(clazz);
            daoRegistry.put(className, dao);
        }
        return dao;
    }


    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}


