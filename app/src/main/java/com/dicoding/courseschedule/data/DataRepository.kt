package com.dicoding.courseschedule.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.QueryUtil
import com.dicoding.courseschedule.util.SortType
import com.dicoding.courseschedule.util.executeThread
import java.util.Calendar

//TODO 4 : Implement repository with appropriate dao
class DataRepository(private val dao: CourseDao) {

    fun getNearestSchedule(queryType: QueryType): LiveData<Course?> {
        return dao.getNearestSchedule(QueryUtil.nearestQuery(queryType))
//        throw NotImplementedError("needs implementation")
    }

    fun getAllCourse(sortType: SortType): LiveData<PagingData<Course>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { dao.getAll(QueryUtil.sortedQuery(sortType)) }
        ).liveData
//        throw NotImplementedError("needs implementation")
    }

    fun getCourse(id: Int): LiveData<Course> {
        return dao.getCourse(id)
//        throw NotImplementedError("needs implementation")
    }

    fun getTodaySchedule(): List<Course> {
        val calender = Calendar.getInstance()
        val day = calender.get(Calendar.DAY_OF_WEEK)
        return dao.getTodaySchedule(day)
//        throw NotImplementedError("needs implementation")
    }

    fun insert(course: Course) = executeThread {
        dao.insert(course)
    }

    fun delete(course: Course) = executeThread {
        dao.delete(course)
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null
        private const val PAGE_SIZE = 10

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    val database = CourseDatabase.getInstance(context)
                    instance = DataRepository(database.courseDao())
                }
                return instance
            }
        }
    }
}