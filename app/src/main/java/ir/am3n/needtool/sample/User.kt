package ir.am3n.needtool.sample

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.am3n.needtool.*

data class User(

    @SerializedName(value = "cid") // ComputerID
    @Expose(serialize = true, deserialize = true)
    var cId: String? = null,

    @SerializedName(value = "regdate") // RegisterDate
    @Expose(serialize = true, deserialize = true)
    var regDate: String? = null,

    @SerializedName(value = "ncode") // NationalCode
    @Expose(serialize = true, deserialize = true)
    var nCode: String? = null,

    @SerializedName(value = "name")
    @Expose(serialize = true, deserialize = true)
    var name: String? = null,

    @SerializedName(value = "family")
    @Expose(serialize = true, deserialize = true)
    var family: String? = null,

    @SerializedName(value = "gender")
    @Expose(serialize = true, deserialize = true)
    var gender: Gender? = null,

    @SerializedName(value = "birthdate")
    @Expose(serialize = true, deserialize = true)
    var birthDate: String? = null,

    @SerializedName(value = "tell")
    @Expose(serialize = true, deserialize = true)
    var tell: String? = null,

    @SerializedName(value = "phone")
    @Expose(serialize = true, deserialize = true)
    var phone: String? = null,

    @SerializedName(value = "email")
    @Expose(serialize = true, deserialize = true)
    var email: String? = null,

    @SerializedName(value = "city")
    @Expose(serialize = true, deserialize = true)
    var city: Int? = null,

    @SerializedName(value = "address")
    @Expose(serialize = true, deserialize = true)
    var address: String? = null,

    @SerializedName(value = "postalcode")
    @Expose(serialize = true, deserialize = true)
    var postalCode: String? = null,

    @SerializedName(value = "serialcard")
    @Expose(serialize = true, deserialize = true)
    var serialCard: String? = null,

    @SerializedName(value = "expdate")
    @Expose(serialize = true, deserialize = true)
    var expDate: String? = null,

    @SerializedName(value = "completestate")
    @Expose(serialize = true, deserialize = true)
    var completeState: Boolean? = null,

    @SerializedName(value = "active")
    @Expose(serialize = true, deserialize = true)
    var active: Boolean? = null,

    @SerializedName(value = "password")
    @Expose(serialize = true, deserialize = true)
    var password: String? = null,

    @SerializedName(value = "signedin")
    @Expose(serialize = true, deserialize = true)
    var signedIn: Boolean? = null

) : KParcelable {


    constructor(parcel: Parcel) : this(
        cId = parcel.readString(),
        regDate = parcel.readString(),
        nCode = parcel.readString(),
        name = parcel.readString(),
        family = parcel.readString(),
        gender = parcel.readEnum<Gender>(),
        birthDate = parcel.readString(),
        tell = parcel.readString(),
        phone = parcel.readString(),
        email = parcel.readString(),
        city = parcel.readInt(),
        address = parcel.readString(),
        postalCode = parcel.readString(),
        serialCard = parcel.readString(),
        expDate = parcel.readString(),
        completeState = parcel.readBool(),
        active = parcel.readBool(),
        password = parcel.readString(),
        signedIn = parcel.readBool()
    )

    /* another constructor
    constructor(it: CustomerInfo) : this(
        cId = it.computerID,
        regDate = it.registerDate,
        nCode = it.nationalCode,
        name = it.firstName,
        family = it.lastName,
        gender = Gender.get(it.sexId),
        birthDate = it.birthDate,
        tell = it.tell,
        phone = it.mobile,
        email = it.emailAddress,
        city = it.cityID,
        address = it.address,
        postalCode = it.postalCode,
        serialCard = it.serialCard,
        expDate = it.expDate,
        completeState = true,
        active = it.active,
        password = it.nationalCode,
        signedIn = true
    )*/

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(cId)
        dest.writeString(regDate)
        dest.writeString(nCode)
        dest.writeString(name)
        dest.writeString(family)
        dest.writeEnum(gender)
        dest.writeString(birthDate)
        dest.writeString(tell)
        dest.writeString(phone)
        dest.writeString(email)
        dest.writeInt(city ?: 1)
        dest.writeString(address)
        dest.writeString(postalCode)
        dest.writeString(serialCard)
        dest.writeString(expDate)
        dest.writeBool(completeState)
        dest.writeBool(active)
        dest.writeString(password)
        dest.writeBool(signedIn)
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = parcelableCreator(::User)
    }

}