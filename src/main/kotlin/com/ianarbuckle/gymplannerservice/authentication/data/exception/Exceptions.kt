package com.ianarbuckle.gymplannerservice.authentication.data.exception

class UserAlreadyExistsException : RuntimeException() {
    override val message: String
        get() = "Username already exists!"
}

class EmailAlreadyExistsException : RuntimeException() {
    override val message: String
        get() = "Email already exists!"
}

class RoleNotFoundException : RuntimeException() {
    override val message: String
        get() = "Role not found!"
}