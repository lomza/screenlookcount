package com.totemsoft.screenlookcount.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.util.Log
import com.totemsoft.screenlookcount.utils.C
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

@Database(entities = [DayLookEntity::class], version = 2)
abstract class ScreenCounterDb : RoomDatabase() {

    private var dbClearFlag = false

    abstract fun dayLookDao(): DayLookDbDao

    companion object {

        @Volatile
        private var INSTANCE: ScreenCounterDb? = null
        private const val DB_NAME = "lookcount-db"

        private val FROM_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // tables haven't changed, we're just moving from
                // greenDAO impl to Room
            }
        }

        fun getDatabase(context: Context): ScreenCounterDb =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        ScreenCounterDb::class.java, DB_NAME)
                        .addMigrations(FROM_1_TO_2)
                        .build()
    }

    @Synchronized
    fun getDayLook(date: String): Maybe<DayLookEntity> {
        return if (dbClearFlag) {
            Maybe.just(DayLookEntity(null, date, null, null, null))
        } else {
            dayLookDao().getDayLookByDate(date)
        }
    }

    @Synchronized
    fun addDayLook(dayLook: DayLookEntity): Disposable {
        Log.d(C.TAG, "Insert/update daylook into the database.")

        return Completable.fromAction {
            dayLookDao().insert(dayLook)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onComplete = {
                            Log.d(C.TAG, "addDayLook() insert() completed.")
                        }, onError = {
                            Log.e(C.TAG, "addDayLook() error: ${it.printStackTrace()}")
                })
    }

    @Synchronized
    fun dropDb() {
        this.dbClearFlag = false

        Completable.fromAction {
            dayLookDao().deleteAll()
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onError = {
                            Log.e(C.TAG, "dropDb() error: ${it.printStackTrace()}")
                        }
                )
    }

    @Synchronized
    fun setDbClearFlag(dbClearFlag: Boolean) {
        this.dbClearFlag = dbClearFlag
    }
}
