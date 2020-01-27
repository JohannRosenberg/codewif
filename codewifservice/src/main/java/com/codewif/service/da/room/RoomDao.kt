package com.codewif.service.da.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.codewif.service.models.UITestInfo

@Dao
interface RoomDao {

    // UITests
    @Query("SELECT * FROM UITests WHERE projectId = :projectId")
    suspend fun getUITests(projectId: String): List<UITestInfo>

    @Insert(onConflict = REPLACE)
    suspend fun storeUITests(tests: List<UITestInfo>)
}
