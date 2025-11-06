package com.ianarbuckle.gymplannerservice.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import javax.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            val serviceAccount =
                this::class.java.getResourceAsStream("/firebase-service-account.json")
                    ?: error("Failed to load firebase-service-account.json")

            val options =
                FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()
            FirebaseApp.initializeApp(options)
        }
    }

    @Bean fun firebaseMessaging(): FirebaseMessaging? = FirebaseMessaging.getInstance()
}
