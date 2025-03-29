package com.sprinto.spring_boot_learning

import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long>