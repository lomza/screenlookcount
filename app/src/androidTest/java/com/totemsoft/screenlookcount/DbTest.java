package com.totemsoft.screenlookcount;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.totemsoft.screenlookcount.db.DayLookEntity;
import com.totemsoft.screenlookcount.db.ScreenCounterDb;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DbTest {
    private ScreenCounterDb db;
    private static String DATE = "05-05-2018";
    private static String DATE_TWO = "06-05-2018";
    private static String DATE_THREE = "07-05-2018";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, ScreenCounterDb.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void addDayLookCountAndReadByDate() {
        DayLookEntity dayLookEntity = getDayLookForTest();
        db.addDayLook(dayLookEntity);
        db.getDayLook(DATE)
                .test()
                .assertValue(dayLookEntity);
    }

    @Test
    public void replaceDayLookCountAndReadByDate() {
        DayLookEntity dayLookEntity = getDayLookForTest();
        db.addDayLook(dayLookEntity);

        db.dayLookDao().getAllDayLooks()
                .test()
                .assertValueCount(1);

        DayLookEntity dayLookTwoEntity = getDayLookForTest();
        dayLookTwoEntity.setScreenon(2);
        dayLookTwoEntity.setScreenoff(3);
        dayLookTwoEntity.setScreenunlock(1);
        db.addDayLook(dayLookTwoEntity);

        db.dayLookDao().getAllDayLooks()
                .test()
                .assertValueCount(1);

        db.getDayLook(DATE)
                .test()
                .assertValue(dayLookTwoEntity);
    }

    private DayLookEntity getDayLookForTest() {
        DayLookEntity dayLookEntity = new DayLookEntity();
        dayLookEntity.setId(0L);
        dayLookEntity.setScreenon(1);
        dayLookEntity.setScreenoff(1);
        dayLookEntity.setScreenunlock(1);
        dayLookEntity.setDate(DATE);

        return dayLookEntity;
    }
}