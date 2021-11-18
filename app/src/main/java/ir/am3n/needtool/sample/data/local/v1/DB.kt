package ir.am3n.needtool.sample.data.local.v1

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Invoice::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DB : RoomDatabase() {

    abstract fun invoiceDao(): InvoiceDao

}