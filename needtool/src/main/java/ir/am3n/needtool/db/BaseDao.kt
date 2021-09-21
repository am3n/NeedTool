package ir.am3n.needtool.db
import android.annotation.SuppressLint

import androidx.lifecycle.Observer
import androidx.room.*
import java.util.ArrayList

@SuppressLint("StaticFieldLeak")
@Dao
abstract class BaseDao<T> {

    companion object {
        const val priority: Int = android.os.Process.THREAD_PRIORITY_BACKGROUND
    }


    // --------------------- insert --------------------------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(entity: T?): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSync(entity: T?): Long?

    @JvmOverloads
    fun insertAsync(entity: T?, observer: Observer<Long?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val id = insertSync(entity)
            observer?.onChanged(id)
        }.start()
    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(vararg entities: T?): List<Long?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(entities: List<T?>?): List<Long?>?
    
    @JvmOverloads
    fun insertAsync(entities: List<T?>?, observer: Observer<List<Long?>?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val ids = insert(entities)
            observer?.onChanged(ids)
        }.start()
    }



    // ------------------- update ----------------------------

    @Update
    abstract fun update(device: T?): Int?

    @Update
    abstract fun updateSync(entity: T?): Int?

    @JvmOverloads
    fun updateAsync(entity: T?, observer: Observer<Boolean?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val bool = updateSync(entity) == 1
            observer?.onChanged(bool)
        }.start()
    }

    @Update
    abstract fun updateSync(entity: List<T?>?): Int?

    @JvmOverloads
    fun updateAsync(entity: List<T?>?, observer: Observer<Boolean?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val bool = (updateSync(entity) ?:0) > 0
            observer?.onChanged(bool)
        }.start()
    }



    // -------------- update or insert ------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsertSync(entity: T?): Long?

    @JvmOverloads
    fun upsertAsync(entity: T?, observer: Observer<Long?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val id = upsertSync(entity)
            observer?.onChanged(id)
        }.start()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsertSync(entity: List<T?>?): List<Long?>?

    @JvmOverloads
    fun upsertAsync(entity: List<T?>?, observer: Observer<List<Long?>?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val ids = upsertSync(entity)
            observer?.onChanged(ids)
        }.start()
    }



    // ------------------- delete ------------------------------

    @Delete
    abstract fun delete(entity: T?): Int?

    @JvmOverloads
    fun deleteAsync(entity: T?, observer: Observer<Boolean?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val bool = delete(entity) == 1
            observer?.onChanged(bool)
        }.start()
    }

    @Delete
    abstract fun delete(entities: List<T?>?): Int?

    @JvmOverloads
    fun deleteAsync(entities: List<T?>?, observer: Observer<DeletionResult?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val dCount = delete(entities)
            observer?.onChanged(
                when {
                    dCount == entities?.size -> DeletionResult.COMPLETE
                    (dCount ?:0) > 0 -> DeletionResult.SOME_NOT_REMOVED
                    else -> DeletionResult.FAILED
                }
            )
        }.start()
    }


    //-----------------------------------------------

    enum class DeletionResult {
        FAILED,
        SOME_NOT_REMOVED,
        COMPLETE
    }

}
