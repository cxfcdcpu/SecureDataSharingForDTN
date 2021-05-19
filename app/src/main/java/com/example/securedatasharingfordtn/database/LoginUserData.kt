package com.example.securedatasharingfordtn.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "user_login_data_table")
data class LoginUserData(
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L,

    @ColumnInfo(name = "user_name")
    var userName: String="",

    @ColumnInfo(name = "user_password")
    var userPassword: String="",

    @ColumnInfo(name = "mission_id")
    var mission: Long = 0L,

    @ColumnInfo(name = "user_attribute")
    var userAttribute: String="",

    @ColumnInfo(name = "user_public_key")
    var userMasterKey: String="",

    @ColumnInfo(name = "user_private_key")
    var userPrivateKey: String="",

    @ColumnInfo(name = "recent_login_time")
    val recentLoginTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "user_register_time")
    val userRegisterTimeMilli: Long = 0L,

    @ColumnInfo(name = "user_expiration_time")
    val userExpirationTimeMilli: Long = 0L,

    @ColumnInfo(name = "is_revoked")
    var isRevoked: Boolean=false



)