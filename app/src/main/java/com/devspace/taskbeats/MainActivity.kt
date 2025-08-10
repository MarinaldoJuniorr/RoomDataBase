package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext, TaskBeatsDataBase::class.java, "database-task-beat"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }
    private val taskDao: TaskDao by lazy {
        db.getTaskDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        insertDefaultCategory()
        insertDefaultTask()

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()

        categoryAdapter.setOnClickListener { selected ->
            val categoryTemp = categories.map { item ->
                when {
                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                    item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }

            val taskTemp = if (selected.name != "ALL") {
                tasks.filter { it.category == selected.name }
            } else {
                tasks
            }
            taskAdapter.submitList(taskTemp)
            categoryAdapter.submitList(categoryTemp)
        }

        rvCategory.adapter = categoryAdapter
        getCategoriesFromDataBase(categoryAdapter)

        rvTask.adapter = taskAdapter
        taskAdapter.submitList(tasks)
    }

    private fun insertDefaultCategory() {
        val categoriesEntity = categories.map {
            CategoryEntity(
                name = it.name, isSelected = it.isSelected
            )
        }

        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insetAll(categoriesEntity)
        }
    }

    private fun insertDefaultTask() {
        val taskEntity = tasks.map {
            TaskEntity(
                name = it.name, category = it.category
            )
        }
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.insetAll(taskEntity)
        }
    }

    private fun getCategoriesFromDataBase(categoryListAdapter: CategoryListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categoriesfromDb = categoryDao.getAll()
            val categoriesUiData = categoriesfromDb.map {
                CategoryUiData(
                    name = it.name, isSelected = it.isSelected
                )
            }

            categoryListAdapter.submitList(categoriesUiData)
        }
    }
}

//val categories: List<CategoryUiData> = listOf()

//val tasks: List<TaskUiData> = listOf()