package ir.am3n.needtool

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.AccessController
import java.security.PrivilegedAction
import java.security.Provider
import java.security.Security
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class GMailSender : Authenticator() {

    private var multipart: Multipart = MimeMultipart()
    private var session: Session? = null

    private val mailhost = "smtp.gmail.com"
    private var user: String? = null
    private var password: String? = null
    private var recipients: String? = null
    private var subject: String? = null
    private var body: String? = null

    fun sender(user: String?, password: String?): GMailSender {
        this.user = user
        this.password = password
        return this
    }

    fun recipients(recipients: String?): GMailSender {
        this.recipients = recipients
        return this
    }

    fun subject(subject: String?): GMailSender {
        this.subject = subject
        return this
    }

    fun body(body: String?): GMailSender {
        this.body = body
        return this
    }

    fun attach(filepath: String?, filename: String = ""): GMailSender {
        try {
            val messageBodyPart: BodyPart = MimeBodyPart()
            val source: DataSource = FileDataSource(filepath)
            messageBodyPart.dataHandler = DataHandler(source)
            messageBodyPart.fileName = if (filename.isNotEmpty()) filename else source.name
            multipart.addBodyPart(messageBodyPart)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return this
    }

    @Synchronized
    fun send(debug: Boolean = false, callback: (Boolean) -> Unit): GMailSender {
        try {
            val props = Properties()
            props.setProperty("mail.transport.protocol", "smtp")
            props.setProperty("mail.host", mailhost)
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "465"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.socketFactory.fallback"] = "false"
            props.setProperty("mail.smtp.quitwait", "false")
            session = Session.getDefaultInstance(props, this)
            session?.debug = debug
            val message = MimeMessage(session)
            val handler = DataHandler(ByteArrayDataSource(body!!.toByteArray(), "text/plain"))
            message.sender = InternetAddress(user)
            message.subject = subject
            message.dataHandler = handler
            val messageBodyPart: BodyPart = MimeBodyPart()
            messageBodyPart.setText(body)
            multipart.addBodyPart(messageBodyPart)
            message.setContent(multipart)
            if (recipients!!.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients)
            ) else
                message.setRecipient(Message.RecipientType.TO, InternetAddress(recipients))

            Transport.send(message)

            callback(true)

        } catch (e: Exception) {
            callback(false)
            e.printStackTrace()
        }
        return this
    }

    internal inner class ByteArrayDataSource : DataSource {

        private var data: ByteArray
        private var type: String? = null

        constructor(data: ByteArray, type: String?) : super() {
            this.data = data
            this.type = type
        }

        constructor(data: ByteArray) : super() {
            this.data = data
        }

        fun setType(type: String?) {
            this.type = type
        }

        override fun getContentType(): String? {
            return if (type == null) "application/octet-stream" else type
        }

        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        override fun getName(): String {
            return "ByteArrayDataSource"
        }

        override fun getOutputStream(): OutputStream? {
            return null
        }
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }

    internal inner class JSSEProvider : Provider("HarmonyJSSE", 1.0, "Harmony JSSE Provider") {
        init {
            AccessController.doPrivileged<Void>(PrivilegedAction<Void?> {
                put("SSLContext.TLS", "org.apache.harmony.xnet.provider.jsse.SSLContextImpl")
                put("Alg.Alias.SSLContext.TLSv1", "TLS")
                put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl")
                put("TrustManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl")
                return@PrivilegedAction null
            })
        }
    }

    init {
        Security.addProvider(JSSEProvider())
    }

}