package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var task = listOf<TaskUiData>()
    private val categoryAdapter = CategoryListAdapter()
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
        val fabCreateTask = findViewById<FloatingActionButton>(R.id.fab_create_task)


        fabCreateTask.setOnClickListener {
            val createTaskBottomSheet = CreateTaskBottomSheet(
                categories
            ) { taskToBeCreated ->
            }
            createTaskBottomSheet.show(
                supportFragmentManager,
                "createTaskBottomSheet"
            )
        }

        val taskAdapter = TaskListAdapter()

        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {

                val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
                    val categoryEntity = CategoryEntity(
                        name = categoryName,
                        isSelected = false
                    )

                    insertCategory(categoryEntity)
                }

                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")

            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected && item.name != "+" -> item.copy(
                            isSelected = true
                        )

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

        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        rvTask.adapter = taskAdapter
        getTaskFromDataBase(taskAdapter)
    }

    private fun getCategoriesFromDataBase() {
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
            categoryAdapter.submitList(categoriesUiData)
        }
    }

    private fun getTaskFromDataBase(adapter: TaskListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val tasksFromDb: List<TaskEntity> = taskDao.getAll()
            val taskUiData = tasksFromDb.map {
                TaskUiData(
                    name = it.name,
                    category = it.category
                )
            }
            GlobalScope.launch(Dispatchers.Main) {
                task = taskUiData
                adapter.submitList(taskUiData)
            }
        }
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {

        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.inset(categoryEntity)
            getCategoriesFromDataBase()
        }
    }
}