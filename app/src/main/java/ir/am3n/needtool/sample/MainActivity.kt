package ir.am3n.needtool.sample

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.am3n.needtool.*
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


        toolbar.setMenu(listOf(
            A3Toolbar.Menu.create(R.id.sawtooth, "", R.drawable.ic_baseline_arrow_back_ios_new_24, null, 24.iDp2Px)
        ))

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
            snack(actMain, "سلام, ${device["appVersion"]}", actionText = "بزن بریم")
        }, 1000)

        onUI({
            snack(actMain, "سلام, ${device["appVersion"]}", font = SnackFont.AUTO, actionText = "بزن بریم")
        }, 3000)



        val plainText = "salllom :)"
        val key = "12345678901234567890123456789012"
        val ciphered = encrypt(plainText, key) ?:""
        Log.d("Meeeeee", ciphered)
        val plain = decrypt(ciphered, key) ?:""
        Log.d("Meeeeee", plain)




        scanWifi({
            onUI {
                toast("پیدا شد: ${it?.size}")
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
        adapter = CustomAdapter(arrayOf(
            "Helo", "aliiii", "dastamo", "sofre", "khoda"
        ))
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
                    if (progress>=1000)
                        break
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
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