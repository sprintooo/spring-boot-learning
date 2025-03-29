package com.sprinto.spring_boot_learning

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun createAccount(user: User): User{
        return userRepository.save(user)
    }
}