package com.codewif.service.da.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codewif.service.models.UITestInfo
import com.codewif.shared.App

@Database(entities = [UITestInfo::class], version = 1, exportSchema = true)
abstract class RoomDB : RoomDatabase() {
    abstract fun roomDao(): RoomDao

    companion object {
        var roomDB = Room.databaseBuilder(App.ctx, RoomDB::class.java, "CodewifService").build()
    }
}