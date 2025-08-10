package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var task = listOf<TaskUiData>()

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


        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()

        categoryAdapter.setOnClickListener { selected ->
            if(selected.name == "+") {

                val createCategoryBottomSheet = CreateCategoryBottomSheet()

                createCategoryBottomSheet.show(supportFragmentManager,"createCategoryBottomSheet")

            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected && item.name != "+"-> item.copy(isSelected = true)
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }

            val taskTemp = if (selected.name != "ALL") {
                task.filter { it.category == selected.name }
            } else {
                task
            }
            taskAdapter.submitList(taskTemp)
            categoryAdapter.submitList(categoryTemp)
            }
        }

        rvCategory.adapter = categoryAdapter
        getCategoriesFromDataBase(categoryAdapter)

        rvTask.adapter = taskAdapter
        getTaskFromDataBase(taskAdapter)
    }

    private fun getCategoriesFromDataBase(categoryListAdapter: CategoryListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categoriesfromDb = categoryDao.getAll()
            val categoriesUiData = categoriesfromDb.map {
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            }.toMutableList()

            // Add fake +  category
            categoriesUiData.add(
                CategoryUiData(
                    name = "+",
                    isSelected = false
                )
            )

            GlobalScope.launch(Dispatchers.Main) {
                categories = categoriesUiData
                categoryListAdapter.submitList(categoriesUiData)
            }
        }
    }

    private fun getTaskFromDataBase(adapter: TaskListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val tasksFromDb: List<TaskEntity> = taskDao.getAll()
            val taskUiData = tasksFromDb.map {
                TaskUiData(
                    name = it.name, category = it.category
                )
            }
            GlobalScope.launch(Dispatchers.Main) {
                task = taskUiData
                adapter.submitList(taskUiData)
            }
        }
    }
}

//val categories: List<CategoryUiData> = listOf()

//val tasks: List<TaskUiData> = listOf()