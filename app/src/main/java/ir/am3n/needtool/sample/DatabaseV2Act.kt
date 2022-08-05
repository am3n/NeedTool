package ir.am3n.needtool.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import ir.am3n.needtool.db.observeOnce
import ir.am3n.needtool.onIO
import ir.am3n.needtool.sample.data.local.v2.DBV2
import ir.am3n.needtool.sample.data.local.v2.InvoiceV2
import java.lang.Thread.sleep

class DatabaseV2Act : AppCompatActivity() {

    companion object {
        var db: DBV2? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(this, DBV2::class.java, "DBV2").build()

        db!!.invoiceDao().getAllLive().observeOnce(this) {
            Log.d("DatabaseAct", "getAll > observeOnce: ${it.toString()}")
        }

        db!!.invoiceDao().getAllLive().observe(this) {
            Log.d("DatabaseAct", "getAll > observe: ${it.toString()}")
        }

        db!!.invoiceDao().getByIdLive(2).observe(this) {
            Log.d("DatabaseAct", "getById > observe: $it")
        }

        onIO {
            sleep(3000)

            Log.d("DatabaseAct", db!!.invoiceDao().getAllSync().toString())

            db!!.invoiceDao().insert(InvoiceV2(billNo = 1), observer = {
                Log.d("DatabaseAct", it.toString())
            })

            try {
                val r = db!!.invoiceDao().insertSync(InvoiceV2(billNo = 1))
                Log.d("DatabaseAct", "r: $r")
            } catch (t: Throwable) {
                Log.e("DatabaseAct", "error", t)
            }

            sleep(1000)

            db!!.invoiceDao().insert(InvoiceV2(billNo = 2))
            db!!.invoiceDao().insert(InvoiceV2(billNo = 3))

            sleep(1000)

            val u = db!!.invoiceDao().updateOnServerByBillNo(8, true, 2)
            Log.d("DatabaseAct", u.toString())

            sleep(1000)

            db!!.invoiceDao().delete(InvoiceV2(billNo = 1)) {
                Log.d("DatabaseAct", it.toString())
            }
            sleep(1000)

            db!!.invoiceDao().getAllSync()?.firstOrNull()?.let { invoice ->
                db!!.invoiceDao().delete(invoice) {
                    Log.d("DatabaseAct", it.toString())
                }
            }
            sleep(1000)

            val dbi = db!!.invoiceDao().deleteByIdSync(2)
            Log.d("DatabaseAct", dbi.toString())
            sleep(1000)

            val da = db!!.invoiceDao().deleteAllSync()
            Log.d("DatabaseAct", da.toString())

            val ra = db!!.invoiceDao().restoreAllSync()
            Log.d("DatabaseAct", ra.toString())

        }

    }

}