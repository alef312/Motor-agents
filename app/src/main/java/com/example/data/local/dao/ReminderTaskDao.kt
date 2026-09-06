package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReminderTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderTaskDao {
    @Query("SELECT * FROM reminder_tasks WHERE vehicleId = :vehicleId ORDER BY isDone ASC, dueDate ASC")
    fun getTasksByVehicle(vehicleId: Long): Flow<List<ReminderTaskEntity>>

    @Query("SELECT * FROM reminder_tasks ORDER BY isDone ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<ReminderTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ReminderTaskEntity): Long

    @Update
    suspend fun updateTask(task: ReminderTaskEntity)

    @Query("UPDATE reminder_tasks SET isDone = :isDone WHERE id = :id")
    suspend fun setTaskDone(id: Long, isDone: Boolean)

    @Delete
    suspend fun deleteTask(task: ReminderTaskEntity)
}
