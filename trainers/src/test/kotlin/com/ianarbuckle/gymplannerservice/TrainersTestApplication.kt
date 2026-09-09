package com.ianarbuckle.gymplannerservice

import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Anchors this module's Spring slice tests (@WebFluxTest). The real
 * @SpringBootApplication lives in :app, which feature modules don't depend on;
 * placing this at the base package lets @WebFluxTest find a @SpringBootConfiguration
 * and component-scan the feature's controller.
 */
@SpringBootApplication
class TrainersTestApplication
