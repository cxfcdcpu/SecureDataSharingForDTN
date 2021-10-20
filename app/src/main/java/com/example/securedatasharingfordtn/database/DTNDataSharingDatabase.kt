package com.example.securedatasharingfordtn.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LoginUserData::class], version = 2, exportSchema = false)
abstract class DTNDataSharingDatabase : RoomDatabase() {
    abstract val dataSharingDatabaseDao: DTNDataSharingDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: DTNDataSharingDatabase? = null

        fun getInstance(context: Context) : DTNDataSharingDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DTNDataSharingDatabase::class.java,
                        "dtn_data_sharing_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                }
                return instance
            }

        }
    }


}