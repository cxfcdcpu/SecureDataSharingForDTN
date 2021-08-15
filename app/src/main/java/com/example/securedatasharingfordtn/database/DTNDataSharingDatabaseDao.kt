package com.example.securedatasharingfordtn.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DTNDataSharingDatabaseDao {
    @Insert
     fun insert(user: LoginUserData)

    @Update
     fun update(user: LoginUserData)

    @Query("SELECT * from user_login_data_table WHERE user_name = :username AND user_password = :password LIMIT 1" )
     fun tryLogin(username: String, password: String) : LoginUserData?

    @Query("DELETE FROM user_login_data_table")
     fun clear()



}