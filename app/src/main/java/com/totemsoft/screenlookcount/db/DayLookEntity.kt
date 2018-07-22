package com.totemsoft.screenlookcount.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "DAY_LOOK")
data class DayLookEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long? = null,

        @ColumnInfo(name = "DATE")
        var date: String,

        @ColumnInfo(name = "SCREENON")
        var screenon: Int? = null,

        @ColumnInfo(name = "SCREENOFF")
        var screenoff: Int? = null,

        @ColumnInfo(name = "SCREENUNLOCK")
        var screenunlock: Int? = null

) {
    constructor() : this(
            id = null,
            date = "",
            screenon = null,
            screenoff = null,
            screenunlock = null
    )
}
