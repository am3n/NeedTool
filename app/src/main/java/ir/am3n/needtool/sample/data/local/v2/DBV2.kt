package ir.am3n.needtool.sample.data.local.v2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        InvoiceV2::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DBV2 : RoomDatabase() {

    companion object {
        var invoiceDao: InvoiceV2Dao? = null
    }

    protected abstract fun createInvoiceDao(): InvoiceV2Dao

    fun invoiceDao(): InvoiceV2Dao {
        if (invoiceDao == null)
            invoiceDao = createInvoiceDao()
        invoiceDao!!.setDatabase(this)
        return invoiceDao!!
    }

}