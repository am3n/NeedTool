package ir.am3n.needtool.sample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.am3n.needtool.SnackFont
import ir.am3n.needtool.device
import ir.am3n.needtool.onUI
import ir.am3n.needtool.snack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Me-MainAct", "onCreate()")

        val device = device()
        Log.d("Me-MainAct", "onCreate() > device: $device")

        onUI({
            snack(activity_main, "سلام, ${device["appVersion"]}", actionText = "بزن بریم")
        }, 1000)

        onUI({
            snack(activity_main, "سلام, ${device["appVersion"]}", font = SnackFont.AUTO, actionText = "بزن بریم")
        }, 3000)

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

}