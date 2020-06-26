package ir.am3n.needtool.db
import android.annotation.SuppressLint

import androidx.lifecycle.Observer
import androidx.room.*

@SuppressLint("StaticFieldLeak")
@Dao
abstract class BaseDao<T> {

    companion object {
        const val priority: Int = android.os.Process.THREAD_PRIORITY_BACKGROUND
    }

    // --------------------- insert --------------------------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSync(entity: T): Long?

    @JvmOverloads
    fun insertAsync(entity: T, observer: Observer<Long>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val id = insertSync(entity)
            observer?.onChanged(id)
        }.start()
    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAllSync(entities: MutableList<T>): MutableList<Long>

    @JvmOverloads
    fun insertAllAsync(entities: MutableList<T>, observer: Observer<MutableList<Long>>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val ids = insertAllSync(entities)
            observer?.onChanged(ids)
        }.start()
    }



    // ------------------- update ----------------------------

    @Update
    abstract fun updateSync(entity: T): Int

    @JvmOverloads
    fun updateAsync(entity: T, observer: Observer<Boolean>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val bool = updateSync(entity) == 1
            observer?.onChanged(bool)
        }.start()
    }

    @Update
    abstract fun updateAllSync(entity: MutableList<T>): Int

    @JvmOverloads
    fun updateAllAsync(entity: MutableList<T>, observer: Observer<Boolean>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val bool = updateAllSync(entity) > 0
            observer?.onChanged(bool)
        }.start()
    }



    // -------------- update or insert ------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateOrInsertSync(entity: T): Long?

    @JvmOverloads
    fun updateOrInsertAsync(entity: T, observer: Observer<Long>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val id = updateOrInsertSync(entity)
            observer?.onChanged(id)
        }.start()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateOrInsertAllSync(entity: MutableList<T>): MutableList<Long>?

    @JvmOverloads
    fun updateOrInsertAllAsync(entity: MutableList<T>, observer: Observer<MutableList<Long>>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val ids = updateOrInsertAllSync(entity)
            observer?.onChanged(ids)
        }.start()
    }



    // ------------------- delete ------------------------------

    @Delete
    abstract fun deleteSync(entity: T): Int

    @JvmOverloads
    fun deleteAsync(entity: T, observer: Observer<Boolean>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val bool = deleteSync(entity) == 1
            observer?.onChanged(bool)
        }.start()
    }

    @Delete
    abstract fun deleteAllSync(entities: MutableList<T>): Int

    @JvmOverloads
    fun deleteAllAsync(entities: MutableList<T>, observer: Observer<DeletionResult>? = null) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            val dCount = deleteAllSync(entities)
            observer?.onChanged(
                when {
                    dCount==entities.size -> DeletionResult.COMPLETE
                    dCount>0 -> DeletionResult.SOME_NOT_REMOVED
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
