package com.example.securedatasharingfordtn.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "user_login_data_table")
data class LoginUserData(
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0,

    @ColumnInfo(name = "user_name")
    var username: String="",

    @ColumnInfo(name = "user_password")
    var password: String="",

    @ColumnInfo(name = "first_name")
    var firstname: String="",

    @ColumnInfo(name = "last_name")
    var lastname: String="",

    @ColumnInfo(name = "mission_id")
    var mission: Long = 0L,

    @ColumnInfo(name = "user_attribute")
    var attributes: String="",

    @ColumnInfo(name = "recent_login_time")
    var recentLoginTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "user_register_time")
    val registerationTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "user_expiration_time")
    var expirationDate: Long = 0L,

    @ColumnInfo(name = "members")
    var members: String="",

    @ColumnInfo(name = "keys")
    var keys: ByteArray = byteArrayOf()




) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginUserData

        if (!keys.contentEquals(other.keys)) return false

        return true
    }

    override fun hashCode(): Int {
        return keys.contentHashCode()
    }


}
