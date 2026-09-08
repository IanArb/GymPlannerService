package com.ianarbuckle.gymplannerservice.authentication

import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Anchors Spring slice tests (e.g. @WebFluxTest) in this module. The real
 * @SpringBootApplication lives in :app, which library modules don't depend on,
 * so their slice tests need a local one to bootstrap from and to component-scan
 * this module's controllers (a bare @SpringBootConfiguration doesn't scan, which
 * leaves @WebFluxTest with no controllers to register → 404s).
 */
@SpringBootApplication
class AuthenticationTestApplication
