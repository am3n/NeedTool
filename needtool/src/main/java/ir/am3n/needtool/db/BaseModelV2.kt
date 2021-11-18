package ir.am3n.needtool.db

import androidx.room.ColumnInfo
import java.io.Serializable

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
abstract class BaseModelV2 : Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName(value = "id")
    var id: Long = 0L

    @ColumnInfo(name = "createdAt")
    @SerializedName(value = "createdAt")
    var createdAt: Long = 0L

    @ColumnInfo(name = "modifiedAt")
    @SerializedName(value = "modifiedAt")
    var modifiedAt: Long = 0L

    @ColumnInfo(name = "deletedAt")
    @SerializedName(value = "deletedAt")
    var deletedAt: Long = 0L

}