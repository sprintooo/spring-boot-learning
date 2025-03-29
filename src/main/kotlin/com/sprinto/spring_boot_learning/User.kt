package com.sprinto.spring_boot_learning

import jakarta.persistence.*

// User Table

@Entity
@Table(name= "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long,
    val username: String,
    val password: String
)
