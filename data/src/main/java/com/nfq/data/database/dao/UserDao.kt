package com.nfq.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nfq.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_entity")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT id FROM user_entity")
    fun getRegistrantId(): String?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM user_entity")
    suspend fun deleteUser()
}