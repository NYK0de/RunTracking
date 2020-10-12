package com.curso.runtracking


import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunDatabase
import com.curso.runtracking.database.RunTracker
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RunDatabaseTest {

    private lateinit var runDao: RunDAO
    private lateinit var db: RunDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, RunDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        runDao = db.runDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    //@Throws(Exception::class)
    suspend fun insertAndGetRun() {
        val run = RunTracker()
        runDao.insert(run)
        val today = runDao.getRunToday()
        assertEquals(today?.runEvaluation, -1)
    }
}