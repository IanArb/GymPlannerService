package com.ianarbuckle.gymplannerservice.authentication.data.exception

class UserAlreadyExistsException : RuntimeException("Username already exists!")

class EmailAlreadyExistsException : RuntimeException("Email already exists!")
