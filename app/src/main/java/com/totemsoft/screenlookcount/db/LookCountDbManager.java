package com.totemsoft.screenlookcount.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import de.greenrobot.dao.async.AsyncSession;

/**
 * The manager of the database which provides methods to insert, update, and get screen looks and unlocks,
 * as well as manage DB connection or drop all tables.
 *
 * @author antonina.tkachuk
 */
public class LookCountDbManager {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private AsyncSession asyncSession;
    private OnDatabaseListener databaseListener;
    private volatile DaoMaster.DevOpenHelper helper;
    private static LookCountDbManager manager;

    private boolean dbClearedFlag = false;

    private LookCountDbManager(@NonNull final Context context) {
        if (helper == null) {
            synchronized (this) {
                if (helper == null) {
                    helper = new DaoMaster.DevOpenHelper(context, "lookcount-db", null);
                }
            }
        }
    }

    public static synchronized LookCountDbManager getManager(@NonNull final Context context) {
        if (manager == null) {
            manager = new LookCountDbManager(context);
        }

        return manager;
    }

    private synchronized void openReadableDbConnection() throws SQLiteException {
        db = helper.getReadableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
    }

    private synchronized void openWritableDbConnection() throws SQLiteException {
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
    }

    private synchronized void closeDbConnection() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }

        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }

        if (helper != null) {
            helper.close();
        }
    }

    public synchronized void setDbListener(final OnDatabaseListener databaseListener) {
        this.databaseListener = databaseListener;
    }

    public synchronized void setDbClearedFlag(final boolean dbClearedFlag) {
        this.dbClearedFlag = dbClearedFlag;
    }

    public synchronized void dropDatabase() {
        openWritableDbConnection();
        DaoMaster.dropAllTables(db, true);
        helper.onCreate(db);
        asyncSession.deleteAll(DayLook.class);
        if (databaseListener != null) {
            databaseListener.onDatabaseDataCleared();
        }
    }

    public synchronized DayLook insertDayLook(@NonNull final DayLook dayLook) {
        openWritableDbConnection();
        DayLookDao dayLookDao = daoSession.getDayLookDao();
        dayLookDao.insertOrReplace(dayLook);
        closeDbConnection();

        return dayLook;
    }

    public synchronized DayLook getDayLookByDate(final String date) {
        if (dbClearedFlag) {
            return null;
        }

        openReadableDbConnection();
        DayLookDao dayLookDao = daoSession.getDayLookDao();
        DayLook dayLook = dayLookDao.queryBuilder().where(DayLookDao.Properties.Date.eq(date)).unique();
        closeDbConnection();

        return dayLook;
    }
}
