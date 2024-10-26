package ir.am3n.needtool.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.am3n.needtool.webserver.HttpHandler
import ir.am3n.needtool.webserver.HttpServer
import kotlinx.android.synthetic.main.activity_webserver.serverButton
import kotlinx.android.synthetic.main.activity_webserver.serverTextView
import org.json.JSONObject
import java.io.InputStream
import java.net.InetSocketAddress
import java.util.Scanner
import java.util.concurrent.Executors

@SuppressLint("SetTextI18n")
class WebServerAct : AppCompatActivity() {

    private var serverUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webserver)

        serverButton.setOnClickListener {
            serverUp = if (!serverUp){
                startServer(port = 5000)
                true
            } else{
                stopServer()
                false
            }
        }

    }

    private fun streamToString(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    private var mHttpServer: HttpServer? = null


    private fun startServer(port: Int) {
        try {
            mHttpServer = HttpServer.create(InetSocketAddress(port), 0)
            mHttpServer!!.executor = Executors.newCachedThreadPool()
            mHttpServer!!.createContext("/", rootHandler)
            mHttpServer!!.createContext("/index", rootHandler)
            mHttpServer!!.createContext("/messages", messageHandler)
            mHttpServer!!.start()
            serverTextView.text = "server running"
            serverButton.text = "stop server"
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun stopServer() {
        if (mHttpServer != null) {
            mHttpServer!!.stop(0)
            serverTextView.text = "server down"
            serverButton.text = "start server"
        }
    }

    private val rootHandler = HttpHandler { exchange ->
        run {
            when (exchange!!.requestMethod) {
                "GET" -> {
                    exchange.sendUTF8Response("Welcome to سلام my server po")
                }
            }
        }
    }

    private val messageHandler = HttpHandler { exchange ->
        run {
            when (exchange!!.requestMethod) {
                "GET" -> {
                    exchange.sendUTF8Response("Would be all messages stringified json")
                }
                "POST" -> {
                    val inputStream = exchange.requestBody
                    val requestBody = streamToString(inputStream)
                    val jsonBody = JSONObject(requestBody)
                    exchange.sendUTF8JsonResponse(jsonBody)
                }
            }
        }
    }

}