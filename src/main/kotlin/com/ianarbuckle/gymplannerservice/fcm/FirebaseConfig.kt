package com.ianarbuckle.gymplannerservice.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import javax.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun initialize() {
        val serviceAccount =
            this::class.java.getResourceAsStream("src/main/resources/firebase-service-account.json")

        val options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }
    }
}
