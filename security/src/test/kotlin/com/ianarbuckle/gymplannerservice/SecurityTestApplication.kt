package com.ianarbuckle.gymplannerservice

import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Anchors this module's Spring slice tests (@WebFluxTest). The real
 * @SpringBootApplication lives in :app, which :security does not depend on; placing
 * this at the base package lets @WebFluxTest find a @SpringBootConfiguration.
 */
@SpringBootApplication
class SecurityTestApplication
