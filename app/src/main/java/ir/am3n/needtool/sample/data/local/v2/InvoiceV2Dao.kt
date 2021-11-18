package ir.am3n.needtool.sample.data.local.v2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.am3n.needtool.db.BaseDao
import ir.am3n.needtool.db.BaseDaoV2
import ir.am3n.needtool.sample.DatabaseAct
import ir.am3n.needtool.sample.DatabaseV2Act

@Dao
abstract class InvoiceV2Dao : BaseDaoV2<InvoiceV2>(
    "InvoiceV2",
    true
) {


    @Query("SELECT * FROM InvoiceV2 WHERE UpdateOnServer = 0")
    abstract fun getAllNotUpdated(): LiveData<List<InvoiceV2>>?


    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insertSyncOrAbort(entity: InvoiceV2?): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSyncOrReplace(entity: InvoiceV2?): Long?


    @Query("""UPDATE InvoiceV2 SET Status=:status WHERE BillNo=:billNo""")
    abstract fun changeStatusByBillNo(status: Int, billNo: Int): Int


    @Query("""UPDATE InvoiceV2 SET ResponseCode=:responseCode, UpdateOnServer=:updateOnServer WHERE BillNo=:billNo""")
    abstract fun updateOnServerByBillNo(
        responseCode: Int,
        updateOnServer: Boolean,
        billNo: Int
    ): Int


}
