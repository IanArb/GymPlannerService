package com.ianarbuckle.gymplannerservice.security

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Minimal controller used only by [SecurityConfigTests] to exercise the security
 * filter chain in isolation. It exposes one public auth route and one route under
 * `/api/v1/facilities`, whose path matches the ADMIN/MODERATOR rule in
 * [SecurityConfig] — so the module can be tested without depending on any feature
 * module's real controllers.
 */
@RestController
@Suppress("FunctionOnlyReturningConstant")
class TestSecuredController {
    @PostMapping("/api/v1/auth/register")
    suspend fun register(): String = "registered"

    @GetMapping("/api/v1/facilities")
    suspend fun facilities(): String = "facilities"
}
