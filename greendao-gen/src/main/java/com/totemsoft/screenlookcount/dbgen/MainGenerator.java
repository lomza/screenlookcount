package com.totemsoft.screenlookcount.dbgen;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates all database tables with their corresponding properties. Is based on a schema.
 *
 * @author antonina.tkachuk
 */
public class MainGenerator {

    private static final int DATABASE_VERSION = 1;
    private static final String PROJECT_DIR = System.getProperty("user.dir");

    private static final String TABLE_NAME = "DayLook";
    private static final String COLUMN_NAME_DATE = "date";
    private static final String COLUMN_NAME_SCREEN_ON = "screenon";
    private static final String COLUMN_NAME_SCREEN_OFF = "screenoff";
    private static final String COLUMN_NAME_UNLOCK = "screenunlock";

    public static void main(String[] args) {
        Schema schema = new Schema(DATABASE_VERSION, "com.totemsoft.screenlookcount.db");
        schema.enableKeepSectionsByDefault();
        addTables(schema);
        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "\\app\\src\\main\\java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        addDayLookTable(schema);
    }

    private static Entity addDayLookTable(final Schema schema) {
        Entity dayLookEntry = schema.addEntity(TABLE_NAME);
        dayLookEntry.addIdProperty().primaryKey().autoincrement();
        dayLookEntry.addStringProperty(COLUMN_NAME_DATE).notNull();
        dayLookEntry.addIntProperty(COLUMN_NAME_SCREEN_ON);
        dayLookEntry.addIntProperty(COLUMN_NAME_SCREEN_OFF);
        dayLookEntry.addIntProperty(COLUMN_NAME_UNLOCK);

        return dayLookEntry;
    }
}
