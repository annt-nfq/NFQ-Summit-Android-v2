package com.nfq.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nfq.data.database.entity.AttractionEntity
import com.nfq.data.database.entity.VoucherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VouchersDao {
    @Query("SELECT * FROM voucher_entity")
    fun getVouchers(): Flow<List<VoucherEntity>>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertVouchers(vouchers: List<VoucherEntity>)

    @Query("DELETE FROM voucher_entity")
    suspend fun deleteAllVouchers()
}