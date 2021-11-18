package ir.am3n.needtool.sample.data.local.v2

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.am3n.needtool.KParcelable
import ir.am3n.needtool.db.BaseModelV2
import ir.am3n.needtool.parcelableCreator
import ir.am3n.needtool.readBool
import ir.am3n.needtool.writeBool

@Entity(
    indices = [
        Index(value = ["BillNo"], unique = true)
    ]
)
class InvoiceV2 : BaseModelV2, KParcelable {

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


    constructor()

    @Ignore
    constructor(billNo: Int) : super() {
        this.billNo = billNo
    }

    @Ignore
    constructor(
        id: Long,
        createdAt: Long,
        modifiedAt: Long,
        deletedAt: Long,
        responseCode: Int,
        chanelPay: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        billNo: Int,
        status: Int,
        updateOnServer: Boolean = false
    ) : super() {
        this.id = id
        this.createdAt = createdAt
        this.modifiedAt = modifiedAt
        this.deletedAt = deletedAt
        this.responseCode = responseCode
        this.chanelPay = chanelPay
        this.customerName = customerName
        this.customerPhone = customerPhone
        this.customerAddress = customerAddress
        this.billNo = billNo
        this.status = status
        this.updateOnServer = updateOnServer
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<InvoiceV2> = parcelableCreator(::InvoiceV2)
    }

    @Ignore
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readBool(),
    )

    @Ignore
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeLong(createdAt)
        dest.writeLong(modifiedAt)
        dest.writeLong(deletedAt)
        dest.writeInt(responseCode)
        dest.writeString(chanelPay)
        dest.writeString(customerName)
        dest.writeString(customerPhone)
        dest.writeString(customerAddress)
        dest.writeInt(billNo)
        dest.writeInt(status)
        dest.writeBool(updateOnServer)
    }

}
