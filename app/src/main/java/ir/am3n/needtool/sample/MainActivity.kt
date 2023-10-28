package ir.am3n.needtool.sample

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.am3n.needtool.*
import ir.am3n.needtool.polygon.Point
import ir.am3n.needtool.polygon.Polygon
import ir.am3n.needtool.views.A3Toolbar
import ir.am3n.needtool.views.recyclerview.RtlStaggeredLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity(), LifecycleObserver {

    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Me-MainAct", "onCreate() isDataEnabled: $isDataEnabled  -  isSimCardExists: $isSimCardExists")


        toolbar.setMenu(
            listOf(
                A3Toolbar.Menu.create(R.id.menuItem0, "", R.drawable.img_logout, null, 4.iDp2Px),
                A3Toolbar.Menu.create(R.id.menuItem1, "", R.drawable.calendar, null, 4.iDp2Px)
            )
        )

        onUI(3000) {
            toolbar?.direction = LayoutDirection.LTR
        }

        mhrlv.direction = View.LAYOUT_DIRECTION_RTL

        btnTestVeiws?.setSafeOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.actMain, A3ViewsFrg(), "A3ViewsFrg")
                .addToBackStack("A3ViewsFrg")
                .commit()
        }


        txt.delayOnLifecycle(4000) {
            txt.text = "aaaaaaaaaaa"
            Log.d("Meeeeeeeee", "delay on lifecycle")
        }
        txt.delayOnLifecycleSuspended(4000) {
            txt.text = "aaaaaaaaaaa"
            Log.d("Meeeeeeeee", "delay on lifecycle suspended")
        }
        onUI(4000) {
            Log.d("Meeeeeeeee", "delay on ui thread")
        }


        val device = device()
        Log.d("Me-MainAct", "onCreate() > device: $device")

        onUI({
            snack(actMain, text = "سلام, ${device["appVersion"]}", actionText = "بزن بریم")
        }, 1000)

        onUI({
            snack(actMain, rtl = true, text = "سلام, ${device["appVersion"]}", font = SnackFont.AUTO, actionText = "بزن بریم")
        }, 6000)


        val plainText = "salllom :)"
        val key = "12345678901234567890123456789012"
        val ciphered = encrypt(plainText, key) ?: ""
        Log.d("Meeeeee", ciphered)
        val plain = decrypt(ciphered, key) ?: ""
        Log.d("Meeeeee", plain)




        scanWifi({ success, results ->
            onUI {
                toast("پیدا شد: ${results?.size}")
            }
        }, {
            onUI {
                toast("نیازمند")
            }
        })


        ProcessLifecycleOwner.get().lifecycle.addObserver(this)



        actMain?.waitForLayout {
            toast("صبر برای ساخته شدن صفحه")
        }




        rcl?.layoutManager = RtlStaggeredLayoutManager(2, LinearLayout.VERTICAL, {
            false
        }, {
            2
        })
        adapter = CustomAdapter(
            arrayOf(
                "Helo", "aliiii", "dastamo", "sofre", "khoda"
            )
        )
        rcl?.adapter = adapter




        btnDatabaseAct?.setSafeOnClickListener {
            startActivity(Intent(this, DatabaseAct::class.java))
        }
        btnDatabaseV2Act?.setSafeOnClickListener {
            startActivity(Intent(this, DatabaseV2Act::class.java))
        }


        onIO {
            sleep(1000)
            var progress = 0
            while (true) {
                try {
                    sleep(1)
                    sb?.post { sb?.progress = progress++ }
                    if (progress >= 1000)
                        break
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }

        ping({ Log.d("Me-MainAct", "ping  telegram.org  $it") }, "telegram.org")
        ping({ Log.d("Me-MainAct", "ping  google.com  $it") }, "google.com")


        try {
            val p = Polygon.Builder()
                .addVertex(Point(0.0, 1.0))
                .build()
            p.sides.forEach {
                // draw
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d("Me-MainAct", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Me-MainAct", "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Me-MainAct", "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Me-MainAct", "onStop()")
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("Me-MainAct", "in foreground")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("Me-MainAct", "in background")
    }

}