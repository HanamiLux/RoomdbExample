package com.example.roomdb

import android.content.Context
import androidx.room.*

@Database(entities = [User::class, MoneyFlow::class], version = 1, exportSchema = false )
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun moneyFlowsDao() : MoneyFlowsDao

    companion object{
        private var INSTANCE: AppDatabase? = null
        fun getDbInstance(context: Context): AppDatabase{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "mpt3.db")
                    .allowMainThreadQueries().build()
            }
            return INSTANCE!!
        }
    }

}