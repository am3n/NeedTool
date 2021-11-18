package ir.am3n.needtool.db

import java.io.Serializable

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
abstract class BaseModel<Int> : Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName(value = "id")
    var id: Int? = null

}