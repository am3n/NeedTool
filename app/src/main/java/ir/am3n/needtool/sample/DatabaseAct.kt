package ir.am3n.needtool.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import ir.am3n.needtool.db.observeOnce
import ir.am3n.needtool.onIO
import ir.am3n.needtool.sample.data.local.v1.DB
import ir.am3n.needtool.sample.data.local.v1.Invoice
import java.lang.Thread.sleep

class DatabaseAct : AppCompatActivity() {

    companion object {
        var db: DB? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(this, DB::class.java, "DB").build()

        db!!.invoiceDao().getAll().observeOnce(this) {
            Log.d("DatabaseAct", "observeOnce: ${it.toString()}")
        }

        db!!.invoiceDao().getAll().observe(this) {
            Log.d("DatabaseAct", "observe: ${it.toString()}")
        }

        onIO {
            sleep(3000)

            Log.d("DatabaseAct", db!!.invoiceDao().getAllSync().toString())

            db!!.invoiceDao().insert(Invoice(billNo = 1), observer = Observer<Long> {
                Log.d("DatabaseAct", it.toString())
            })

            try {
                val r = db!!.invoiceDao().insertSync(Invoice(billNo = 1))
                Log.d("DatabaseAct", "r: $r")
            } catch (t: Throwable) {
                Log.e("DatabaseAct", "error", t)
            }

            sleep(1000)

            db!!.invoiceDao().insert(Invoice(billNo = 2))

            sleep(1000)

            val u = db!!.invoiceDao().updateOnServerByBillNo(8, true, 2)
            Log.d("DatabaseAct", u.toString())

            sleep(1000)

            db!!.invoiceDao().delete(Invoice(billNo = 1)) {
                Log.d("DatabaseAct", it.toString())
            }
            sleep(1000)

            db!!.invoiceDao().getAllSync()?.firstOrNull()?.let { invoice ->
                db!!.invoiceDao().delete(invoice) {
                    Log.d("DatabaseAct", it.toString())
                }
            }
            sleep(1000)

            val da = db!!.invoiceDao().deleteAllSync()
            Log.d("DatabaseAct", da.toString())

        }

    }

}