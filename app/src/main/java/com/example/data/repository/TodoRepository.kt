package com.example.data.repository

import com.example.data.dao.TodoDao
import com.example.data.model.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<TodoEntity>> = todoDao.getAllTodos()
    val pendingTodos: Flow<List<TodoEntity>> = todoDao.getPendingTodos()

    suspend fun getTodoById(id: Long): TodoEntity? = todoDao.getTodoById(id)

    suspend fun insert(todo: TodoEntity): Long = todoDao.insertTodo(todo)

    suspend fun update(todo: TodoEntity) = todoDao.updateTodo(todo)

    suspend fun toggleCompleted(todo: TodoEntity) {
        todoDao.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
    }

    suspend fun delete(todo: TodoEntity) = todoDao.deleteTodo(todo)

    suspend fun deleteById(id: Long) = todoDao.deleteTodoById(id)

    suspend fun incrementPomodoro(id: Long) = todoDao.incrementPomodoro(id)
}
