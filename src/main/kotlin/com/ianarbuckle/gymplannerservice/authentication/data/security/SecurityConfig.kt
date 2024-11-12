package com.ianarbuckle.gymplannerservice.authentication.data.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
open class SecurityConfig(private val jwtAuthenticationManager: JWTAuthenticationManager) {

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    open fun springSecurityFilter(
        converter: JwtServerAuthenticationConverter,
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain {
        val filter = AuthenticationWebFilter(jwtAuthenticationManager)
        filter.setServerAuthenticationConverter(converter)

        http
            .exceptionHandling {
                it.authenticationEntryPoint { exchange, _ ->
                    Mono.fromRunnable {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        exchange.response.headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                    }
                }
            }
            .authorizeExchange {
                it.pathMatchers(
                    HttpMethod.POST,
                    "/api/v1/auth/**"
                ).permitAll()
                    .anyExchange().authenticated()
            }
            .httpBasic {
                it.disable()
            }
            .formLogin {
                it.disable()
            }
            .csrf {
                it.disable()
            }
            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
            .authenticationManager(jwtAuthenticationManager)

        return http.build()
    }
}