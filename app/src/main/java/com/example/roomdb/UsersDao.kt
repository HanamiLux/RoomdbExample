package com.example.roomdb

import androidx.room.*

@Dao
interface UsersDao {
    @Insert
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM  Users WHERE login = :login")
    fun getUser(login: String): User

    @Query("SELECT * FROM Users")
    fun getAllUsers(): List<User>


}