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
    abstract fun insertAll(vararg entities: T?): List<Long?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(entities: List<T?>?): List<Long?>?
    
    @JvmOverloads
    fun insertAllAsync(entities: List<T?>?, observer: Observer<List<Long?>?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val ids = insertAll(entities)
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
    abstract fun updateAllSync(entity: List<T?>?): Int?

    @JvmOverloads
    fun updateAllAsync(entity: List<T?>?, observer: Observer<Boolean?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val bool = (updateAllSync(entity) ?:0) > 0
            observer?.onChanged(bool)
        }.start()
    }



    // -------------- update or insert ------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateOrInsertSync(entity: T?): Long?

    @JvmOverloads
    fun updateOrInsertAsync(entity: T?, observer: Observer<Long?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val id = updateOrInsertSync(entity)
            observer?.onChanged(id)
        }.start()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateOrInsertAllSync(entity: List<T?>?): List<Long?>?

    @JvmOverloads
    fun updateOrInsertAllAsync(entity: List<T?>?, observer: Observer<List<Long?>?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val ids = updateOrInsertAllSync(entity)
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
    abstract fun deleteAll(entities: List<T?>?): Int?

    @JvmOverloads
    fun deleteAllAsync(entities: List<T?>?, observer: Observer<DeletionResult?>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val dCount = deleteAll(entities)
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
