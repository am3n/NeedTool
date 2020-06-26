package ir.am3n.needtool.db

import java.io.Serializable

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
abstract class BaseModel<Int> : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}