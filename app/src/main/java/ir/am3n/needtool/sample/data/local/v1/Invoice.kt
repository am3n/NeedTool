package ir.am3n.needtool.sample.data.local.v1

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.am3n.needtool.*
import ir.am3n.needtool.db.BaseModel

@Entity(
    indices = [
        Index(value = ["BillNo"], unique = true)
    ]
)
class Invoice : BaseModel<Int>, KParcelable {

    @SerializedName(value = "ResponseCode")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "ResponseCode")
    var responseCode: Int = 0

    @SerializedName(value = "ChanelPay")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "ChanelPay")
    var chanelPay: String = ""

    @SerializedName(value = "CustomerName")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "CustomerName")
    var customerName: String = ""

    @SerializedName(value = "CustomerPhone")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "CustomerPhone")
    var customerPhone: String = ""

    @SerializedName(value = "CustomerAddress")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "CustomerAddress")
    var customerAddress: String = ""



    @SerializedName(value = "BillNo")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "BillNo")
    var billNo: Int = 0

    @SerializedName(value = "Status")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "Status")
    var status: Int = 0


    @SerializedName(value = "UpdateOnServer")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "UpdateOnServer", defaultValue = "false")
    var updateOnServer: Boolean = false


    @SerializedName(value = "CreatedAt")
    @Expose(serialize = true, deserialize = true)
    @ColumnInfo(name = "CreatedAt")
    var createdAt: Long = System.currentTimeMillis()



    constructor()

    @Ignore
    constructor(billNo: Int) : super() {
        this.billNo = billNo
    }

    @Ignore
    constructor(
        responseCode: Int,
        chanelPay: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        billNo: Int,
        status: Int,
        updateOnServer: Boolean = false,
        createdAt: Long = System.currentTimeMillis()
    ) : super() {
        this.responseCode = responseCode
        this.chanelPay = chanelPay
        this.customerName = customerName
        this.customerPhone = customerPhone
        this.customerAddress = customerAddress
        this.billNo = billNo
        this.status = status
        this.updateOnServer = updateOnServer
        this.createdAt = createdAt
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Invoice> = parcelableCreator(::Invoice)
    }

    @Ignore
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readBool(),
        parcel.readLong()
    )

    @Ignore
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(responseCode)
        dest.writeString(chanelPay)
        dest.writeString(customerName)
        dest.writeString(customerPhone)
        dest.writeString(customerAddress)
        dest.writeInt(billNo)
        dest.writeInt(status)
        dest.writeBool(updateOnServer)
        dest.writeLong(createdAt)
    }

}
