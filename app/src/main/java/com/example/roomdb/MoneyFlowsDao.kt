package com.example.roomdb

import androidx.room.*

@Dao
interface MoneyFlowsDao {
    @Insert
    fun insertFlow(moneyFlow: MoneyFlow)

    @Update
    fun updateFlow(moneyFlow: MoneyFlow)

    @Delete
    fun deleteFlow(moneyFlow: MoneyFlow)

    @Query("SELECT * FROM MoneyFlow WHERE user_id = :id")
    fun getFlowByUser(id: String?): List<MoneyFlow>

}