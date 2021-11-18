package ir.am3n.needtool.db

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

import androidx.lifecycle.Observer
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import ir.am3n.needtool.onIO
import java.util.concurrent.Callable

@SuppressLint("StaticFieldLeak", "RestrictedApi")
@Dao
abstract class BaseDao<T>(
    private val tableName: String = "",
    private val database: RoomDatabase? = null
) {

    companion object {
        const val priority: Int = android.os.Process.THREAD_PRIORITY_BACKGROUND
    }

    @RawQuery
    protected abstract fun runRawSelectQuery(query: SupportSQLiteQuery): List<T>?

    @RawQuery
    protected abstract fun runRawQuery(query: SupportSQLiteQuery): Int?


    // --------------------------------------------------------
    // --------------------- select ---------------------------


    // all

    fun getAllSync(): List<T>? {
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        return runRawSelectQuery(SimpleSQLiteQuery("SELECT * FROM $tableName ;"))
    }
    fun getAll(observer: Observer<List<T>?>) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(getAllSync())
        }
    }
    fun getAll(): LiveData<List<T>?> {
        if (tableName.isEmpty() || database == null)
            throw Error("Table name is empty or database is null")
        return database.invalidationTracker.createLiveData(arrayOf(tableName), false) {
            return@createLiveData getAllSync()
        }
        /*return object : ComputableLiveData<List<T>>() {
            private var observer: InvalidationTracker.Observer? = null
            override fun compute(): List<T>? {
                Log.d("Meeeeeeeeee", "compute()")
                if (observer == null) {
                    observer = object : InvalidationTracker.Observer(tableName) {
                        override fun onInvalidated(tables: Set<String>) {
                            Log.d("Meeeeeeeeee", "onInvalidated()")
                            invalidate()
                        }
                    }
                    database.invalidationTracker.addWeakObserver(observer)
                }
                return getAllSync().apply {
                    Log.d("Meeeeeeeeee", "$this")
                }
            }
        }.liveData*/
    }


    // by id

    fun getByIdSync(id: Int): T? {
        return getByIdSync(listOf(id))?.firstOrNull()
    }
    fun getByIdSync(ids: List<Int>): List<T>? {
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        val result = StringBuilder()
        for (index in ids.indices) {
            if (index != 0) {
                result.append(",")
            }
            result.append("'").append(ids[index]).append("'")
        }
        val query = SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id IN ($result);")
        return runRawSelectQuery(query)
    }

    fun getById(id: Int, observer: Observer<T?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(getByIdSync(id))
        }
    }
    fun getById(ids: List<Int>, observer: Observer<List<T>?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(getByIdSync(ids))
        }
    }

    fun getById(id: Int): LiveData<T> {
        val resultLiveData = MediatorLiveData<T>()
        resultLiveData.addSource(getById(listOf(id))) { obj ->
            resultLiveData.postValue(obj?.firstOrNull())
        }
        return resultLiveData
    }

    fun getById(ids: List<Int>): LiveData<List<T>?> {
        if (tableName.isEmpty() || database == null)
            throw Error("Table name is empty or database is null")
        return database.invalidationTracker.createLiveData(arrayOf(tableName), false) {
            return@createLiveData getByIdSync(ids)
        }
        /*return object : ComputableLiveData<List<T>>() {
            private var observer: InvalidationTracker.Observer? = null
            override fun compute(): List<T>? {
                if (observer == null) {
                    observer = object : InvalidationTracker.Observer(tableName) {
                        override fun onInvalidated(tables: Set<String>) = invalidate()
                    }
                    database.invalidationTracker.addWeakObserver(observer)
                }
                return getByIdSync(ids)
            }
        }.liveData*/
    }



    // -------------------------------------------------------
    // --------------------- insert --------------------------


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSync(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insertOrAbortSync(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSync(entities: List<T>): List<Long>?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insertOrAbortSync(entities: List<T>): List<Long>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSync(vararg entities: T): List<Long>?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insertOrAbortSync(vararg entities: T): List<Long>?


    fun insert(entity: T, observer: Observer<Long> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(insertSync(entity))
        }
    }

    fun insert(entities: List<T>, observer: Observer<List<Long>?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(insertSync(entities))
        }
    }

    fun insert(vararg entities: T, observer: Observer<List<Long>?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(insertSync(*entities))
        }
    }


    // -------------------------------------------------------
    // ------------------- update ----------------------------

    @Update
    abstract fun updateSync(entity: T): Int?

    @Update
    abstract fun updateSync(entities: List<T?>?): Int?

    @Update
    abstract fun updateSync(vararg entities: T): Int?

    fun update(entity: T, observer: Observer<Int?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(updateSync(entity))
        }
    }

    fun update(entities: List<T>, observer: Observer<Int?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(updateSync(entities))
        }
    }

    fun update(vararg entities: T, observer: Observer<Int?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(updateSync(*entities))
        }
    }



    // -------------------------------------------------------
    // -------------- update or insert ------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsertSync(entity: T): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsertSync(entities: List<T>): List<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsertSync(vararg entities: T): List<Long?>

    fun upsert(entity: T, observer: Observer<Long?> = Observer {}) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(upsertSync(entity))
        }.start()
    }

    fun upsert(entities: List<T>, observer: Observer<List<Long?>> = Observer {}) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(upsertSync(entities))
        }.start()
    }

    fun upsert(vararg entities: T, observer: Observer<List<Long?>> = Observer {}) {
        Thread {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(upsertSync(*entities))
        }.start()
    }




    // ---------------------------------------------------------
    // ------------------- delete ------------------------------

    fun deleteAllSync(): Int? {
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        val result = runRawQuery(SimpleSQLiteQuery("DELETE FROM $tableName ;"))
        database?.invalidationTracker?.notifyObserversByTableNames(tableName)
        return result
    }

    @Delete
    abstract fun deleteSync(entity: T): Int?

    @Delete
    abstract fun deleteSync(entities: List<T>): Int?

    @Delete
    abstract fun deleteSync(vararg entities: T): Int?

    fun deleteAll(observer: Observer<Int?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(deleteAllSync())
        }
    }

    fun delete(entity: T, observer: Observer<Int?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(deleteSync(entity))
        }
    }

    fun delete(entities: List<T>, observer: Observer<Int?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(deleteSync(entities))
        }
    }

    fun delete(vararg entities: T, observer: Observer<Int?> = Observer {}) {
        onIO {
            android.os.Process.setThreadPriority(priority)
            observer.onChanged(deleteSync(*entities))
        }
    }

}
