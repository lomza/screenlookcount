package com.totemsoft.screenlookcount.db;

/**
 * Database listener with the DB callback methods.
 *
 * @author antonina.tkachuk
 */
public interface OnDatabaseListener {
    /**
     * Data cleared callback (dropped tables).
     */
    void onDatabaseDataCleared();
}
