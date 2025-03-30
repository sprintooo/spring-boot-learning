package com.sprinto.spring_boot_learning

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

// Task related stuff
@Entity
@Table(name = "tasks")
data class Task(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val createdBy: String
)

interface TaskRepository : JpaRepository<Task, Long>

@Service
class TaskService(private val repository: TaskRepository) {

    @Transactional
    fun addTask(task: Task): Task {
        return repository.save(task)
    }
}

data class TaskRequest(val title:String)

@RestController
@RequestMapping("/tasks")
class TaskController(private val service: TaskService) {

    @PostMapping
    fun addTask(@RequestBody task: TaskRequest, authentication: Authentication): Task {
        val user = authentication.name
        val newTask = Task(
            title = task.title,
            createdBy = user
        )
        return service.addTask(newTask)
    }
}