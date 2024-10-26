package ir.am3n.needtool.sample.data.local.v1

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.am3n.needtool.db.BaseDao
import ir.am3n.needtool.sample.DatabaseAct

@Dao
abstract class InvoiceDao : BaseDao<Invoice>("Invoice", DatabaseAct.db) {


    @Query("SELECT * FROM Invoice WHERE UpdateOnServer = 0")
    abstract fun getAllNotUpdated(): LiveData<List<Invoice>>


    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insertSyncOrAbort(entity: Invoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSyncOrReplace(entity: Invoice): Long


    @Query("""UPDATE Invoice SET Status=:status WHERE BillNo=:billNo""")
    abstract fun changeStatusByBillNo(status: Int, billNo: Int): Int


    @Query("""UPDATE Invoice SET ResponseCode=:responseCode, UpdateOnServer=:updateOnServer WHERE BillNo=:billNo""")
    abstract fun updateOnServerByBillNo(
        responseCode: Int,
        updateOnServer: Boolean,
        billNo: Int
    ): Int


}
