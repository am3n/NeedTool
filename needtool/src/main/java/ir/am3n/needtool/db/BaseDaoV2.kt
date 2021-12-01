package ir.am3n.needtool.db

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import ir.am3n.needtool.onIO

@SuppressLint("StaticFieldLeak", "RestrictedApi")
@Dao
abstract class BaseDaoV2<T: BaseModelV2>(
    private val tableName: String = "",
    private val paranoid: Boolean = true
) {

    protected var db: RoomDatabase? = null

    fun setDatabase(database: RoomDatabase) {
        this.db = database
    }

    private fun idsToStr(ids: List<Long>): String {
        val idsStr = StringBuilder()
        for (index in ids.indices) {
            if (index != 0) {
                idsStr.append(",")
            }
            idsStr.append("'").append(ids[index]).append("'")
        }
        return idsStr.toString()
    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertOrIgnoreSync(entities: List<T>): List<Long>?
    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract fun insertOrAbortSync(entities: List<T>): List<Long>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insertOrUpdateSync(entities: List<T>): List<Long>?

    @Update(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun updateOrIgnoreSync(entities: List<T>): Int?
    @Update(onConflict = OnConflictStrategy.ABORT)
    protected abstract fun updateOrAbortSync(entities: List<T>): Int?

    @Delete
    protected abstract fun deleteSync(entities: List<T>): Int?

    @RawQuery
    protected abstract fun runRawSelectQuery(query: SupportSQLiteQuery): List<T>?


    // #############################################################################


    // GET ALL

    fun getAllSync(): List<T>? {
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        return runRawSelectQuery(SimpleSQLiteQuery(
            if (paranoid) "SELECT * FROM $tableName WHERE deletedAt = 0 ;"
            else "SELECT * FROM $tableName ;"
        ))
    }

    fun getAll(observer: Observer<List<T>?>) {
        onIO {
            observer.onChanged(getAllSync())
        }
    }

    fun getAll(): LiveData<List<T>?> {
        if (db == null)
            throw Error("Database is null")
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        return db!!.invalidationTracker.createLiveData(arrayOf(tableName), false) {
            return@createLiveData getAllSync()
        }
    }

    // GET BY ID

    fun getByIdSync(id: Long): T? {
        return getByIdSync(listOf(id))?.firstOrNull()
    }

    fun getByIdSync(ids: List<Long>): List<T>? {
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        val result = StringBuilder()
        for (index in ids.indices) {
            if (index != 0) {
                result.append(",")
            }
            result.append("'").append(ids[index]).append("'")
        }
        val query = SimpleSQLiteQuery(
            if (paranoid) "SELECT * FROM $tableName WHERE id IN (${idsToStr(ids)}) AND deletedAt = 0;"
            else "SELECT * FROM $tableName WHERE id IN (${idsToStr(ids)}) ;"
        )
        return runRawSelectQuery(query)
    }

    fun getById(id: Long, observer: Observer<T?> = Observer {}) {
        getById(listOf(id)) {
            observer.onChanged(it?.firstOrNull())
        }
    }
    fun getById(ids: List<Long>, observer: Observer<List<T>?> = Observer {}) {
        onIO {
            observer.onChanged(getByIdSync(ids))
        }
    }

    fun getById(id: Long): LiveData<T> {
        val resultLiveData = MediatorLiveData<T>()
        resultLiveData.addSource(getById(listOf(id))) { obj ->
            resultLiveData.postValue(obj?.firstOrNull())
        }
        return resultLiveData
    }

    fun getById(ids: List<Long>): LiveData<List<T>?> {
        if (db == null)
            throw Error("Database is null")
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        return db!!.invalidationTracker.createLiveData(arrayOf(tableName), false) {
            return@createLiveData getByIdSync(ids)
        }
    }


    // -----------------------------------------------------------------------------


    fun insertSync(entity: T, abort: Boolean = false): Long? {
        return insertSync(listOf(entity), abort)?.firstOrNull()
    }

    fun insertSync(entities: List<T>, abort: Boolean = false): List<Long>? {
        entities.map {
            it.apply {
                createdAt = System.currentTimeMillis()
                modifiedAt = createdAt
            }
        }.let {
            return if (abort) insertOrAbortSync(it)
            else insertOrIgnoreSync(it)
        }
    }

    fun insert(entity: T, observer: Observer<Long?> = Observer {}) {
        insert(listOf(entity)) {
            observer.onChanged(it?.firstOrNull())
        }
    }

    fun insert(entities: List<T>, observer: Observer<List<Long>?> = Observer {}) {
        onIO {
            observer.onChanged(insertSync(entities))
        }
    }


    // -----------------------------------------------------------------------------


    fun updateSync(entity: T, abort: Boolean = false): Int? {
        return updateSync(listOf(entity), abort)
    }

    fun updateSync(entities: List<T>, abort: Boolean = false): Int? {
        entities.map {
            it.apply {
                if (createdAt == 0L)
                    createdAt = System.currentTimeMillis()
                modifiedAt = System.currentTimeMillis()
            }
        }.let {
            return if (abort) updateOrAbortSync(it)
            else updateOrIgnoreSync(it)
        }
    }

    fun update(entity: T, observer: Observer<Int?> = Observer {}) {
        update(listOf(entity)) {
            observer.onChanged(it)
        }
    }

    fun update(entities: List<T>,  observer: Observer<Int?> = Observer {}) {
        onIO {
            observer.onChanged(updateSync(entities))
        }
    }


    // -----------------------------------------------------------------------------


    fun upsertSync(entity: T): Long? {
        return upsertSync(listOf(entity))?.firstOrNull()
    }

    fun upsertSync(entities: List<T>): List<Long>? {
        entities.map {
            it.apply {
                if (createdAt == 0L)
                    createdAt = System.currentTimeMillis()
                modifiedAt = System.currentTimeMillis()
            }
        }.let {
            return insertOrUpdateSync(it)
        }
    }

    fun upsert(entity: T, observer: Observer<Long?> = Observer {}) {
        upsert(listOf(entity)) {
            observer.onChanged(it?.firstOrNull())
        }
    }

    fun upsert(entities: List<T>, observer: Observer<List<Long>?> = Observer {}) {
        onIO {
            observer.onChanged(upsertSync(entities))
        }
    }


    // -----------------------------------------------------------------------------


    // all

    fun deleteAllSync(force: Boolean = false): Int {
        if (db == null)
            throw Error("Database is null")
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        var result = 0
        db!!.query(
            if (!force && paranoid) "UPDATE $tableName SET deletedAt=${System.currentTimeMillis()} ;"
            else "DELETE FROM $tableName ;"
            , arrayOf()
        ).apply {
            result = try {
                moveToFirst()
                1
            } catch (t: Throwable) {
                -1
            } finally {
                close()
            }
        }
        db!!.invalidationTracker.notifyObserversByTableNames(tableName)
        return result
    }

    fun deleteAll(force: Boolean = false, observer: Observer<Int?> = Observer {}) {
        onIO { observer.onChanged(deleteAllSync(force)) }
    }


    // by entity

    fun deleteSync(entity: T, force: Boolean = false): Int? {
        return deleteSync(listOf(entity), force)
    }

    fun deleteSync(entities: List<T>, force: Boolean = false): Int? {
        return if (!force && paranoid) {
            updateSync(entities.map { it.apply { deletedAt = System.currentTimeMillis() } })
        } else {
            deleteSync(entities)
        }
    }

    fun delete(entity: T, force: Boolean = false, observer: Observer<Int?> = Observer {}) {
        delete(listOf(entity), force, observer)
    }

    fun delete(entities: List<T>, force: Boolean = false, observer: Observer<Int?> = Observer {}) {
        onIO { observer.onChanged(deleteSync(entities, force)) }
    }


    // by id

    fun deleteByIdSync(id: Long, force: Boolean = false): Int? {
        return deleteByIdSync(listOf(id), force)
    }

    fun deleteByIdSync(ids: List<Long>, force: Boolean = false): Int {
        if (db == null)
            throw Error("Database is null")
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        var result = 0
        db!!.query(
            if (!force && paranoid) "UPDATE $tableName SET deletedAt=${System.currentTimeMillis()} WHERE id IN (${idsToStr(ids)}) ;"
            else "DELETE FROM $tableName WHERE id IN (${idsToStr(ids)}) ;",
            arrayOf()
        ).apply {
            result = try {
                moveToFirst()
                1
            } catch (t: Throwable) {
                -1
            } finally {
                close()
            }
        }
        db!!.invalidationTracker.notifyObserversByTableNames(tableName)
        return result
    }

    fun deleteById(id: Long, force: Boolean = false, observer: Observer<Int?> = Observer {}) {
        deleteById(listOf(id), force, observer)
    }

    fun deleteById(ids: List<Long>, force: Boolean = false, observer: Observer<Int?> = Observer {}) {
        onIO {
            observer.onChanged(deleteByIdSync(ids, force))
        }
    }



    // -----------------------------------------------------------------------------


    // all

    fun restoreAllSync(): Int {
        if (!paranoid)
            throw Error("Table is not paranoid")
        if (db == null)
            throw Error("Database is null")
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        var result = 0
        db!!.query("UPDATE $tableName SET deletedAt=0 ;", arrayOf()).apply {
            result = try {
                moveToFirst()
                1
            } catch (t: Throwable) {
                -1
            } finally {
                close()
            }
        }
        db!!.invalidationTracker.notifyObserversByTableNames(tableName)
        return result
    }

    fun restoreAll(observer: Observer<Int> = Observer {}) {
        onIO { observer.onChanged(restoreAllSync()) }
    }

    // by entity

    fun restoreSync(entity: T): Int {
        return restoreSync(listOf(entity))
    }

    fun restoreSync(entities: List<T>): Int {
        return restoreByIdSync(entities.map { it.id })
    }

    fun restore(entity: T, observer: Observer<Int> = Observer {}) {
        restore(listOf(entity), observer)
    }

    fun restore(entities: List<T>, observer: Observer<Int> = Observer {}) {
        onIO { observer.onChanged(restoreSync(entities)) }
    }

    // by id

    fun restoreByIdSync(id: Long): Int {
        return restoreByIdSync(listOf(id))
    }

    fun restoreByIdSync(ids: List<Long>): Int {
        if (!paranoid)
            throw Error("Table is not paranoid")
        if (db == null)
            throw Error("Database is null")
        if (tableName.isEmpty())
            throw Error("Table name is empty")
        var result = 0
        db!!.query("UPDATE $tableName SET deletedAt=0 WHERE id IN (${idsToStr(ids)}) ;", arrayOf()).apply {
            result = try {
                moveToFirst()
                1
            } catch (t: Throwable) {
                -1
            } finally {
                close()
            }
        }
        db!!.invalidationTracker.notifyObserversByTableNames(tableName)
        return result
    }

    fun restoreByIdSync(id: Long, observer: Observer<Int> = Observer {}) {
        restoreByIdSync(listOf(id), observer)
    }

    fun restoreByIdSync(ids: List<Long>, observer: Observer<Int> = Observer {}) {
        onIO { observer.onChanged(restoreByIdSync(ids)) }
    }

}
