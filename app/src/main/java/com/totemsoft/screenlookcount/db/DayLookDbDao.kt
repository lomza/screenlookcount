package com.totemsoft.screenlookcount.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface DayLookDbDao {

    @Query("SELECT * FROM DAY_LOOK WHERE DATE = :dateToSearch LIMIT 1")
    fun getDayLookByDate(dateToSearch: String?): Maybe<DayLookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dayLook: DayLookEntity)

    @Query("SELECT * FROM DAY_LOOK")
    fun getAllDayLooks(): Flowable<List<DayLookEntity>>

    @Query("DELETE FROM DAY_LOOK")
    fun deleteAll()
}
