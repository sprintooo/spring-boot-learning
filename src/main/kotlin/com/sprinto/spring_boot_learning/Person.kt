package com.sprinto.spring_boot_learning

import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@Entity
@Table(name= "users")
data class Person(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,
    val username: String = "",  // Default values added
    val password: String = "",   // Default values added
    val role: String = "USER"
)

// request response
data class UserRequest(val username: String, val password: String)
data class UserResponse(val id: Long, val username: String, val role: String)


// Exceptions
class UserEmailNotAvailable(message: String): RuntimeException(message)

@RestController
@RequestMapping("/api/auth")
class UserController(private val service: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody user: UserRequest): UserResponse{
        return service.createAccount(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody user: UserRequest): UserResponse {
        return service.login(user)
    }

    @ExceptionHandler(UserEmailNotAvailable::class)
    fun handleUserNotFound(ex: UserEmailNotAvailable): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUserNotFound(ex: UsernameNotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.message)
    }
}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createAccount(user: UserRequest): UserResponse {
        // check if user already exist
        if (userRepository.findByUsername(user.username) != null){
            throw UserEmailNotAvailable("User Already exist with this email!")
        }

        // else register user
        val hashedPassword = passwordEncoder.encode(user.password)
        val savedUser = userRepository.save(
            Person(
                username = user.username,
                password = hashedPassword
            )
        )
        return UserResponse(
            id = savedUser.id,
            username = savedUser.username,
            role = savedUser.role
        )
    }

    fun login(user: UserRequest): UserResponse {
        val person = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException("Invalid username")
        return if(passwordEncoder.matches(user.password, person.password)){
            UserResponse(
                id = person.id,
                username = person.username,
                role = person.role
            )
        } else {
            throw UsernameNotFoundException("Invalid Password")
        }
    }
}

@Service
class customUserDetailService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = username?.let { userRepository.findByUsername(it) }
            ?: throw UsernameNotFoundException("User not found")

        return User.withUsername(user.username)
            .password(user.password)
            .build()
    }
}

interface UserRepository: CrudRepository<Person, Long> {
    fun findByUsername(username: String): Person?
}
