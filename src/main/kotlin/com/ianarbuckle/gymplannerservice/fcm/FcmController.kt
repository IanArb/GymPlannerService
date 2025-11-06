package com.ianarbuckle.gymplannerservice.fcm

import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenRequest
import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenResponse
import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/fcm")
class FcmController(private val fcmTokenService: FcmTokenService) {

    @PostMapping("/register")
    suspend fun registerPushNotificationToken(
        @RequestBody tokenRequest: FcmTokenRequest
    ): FcmTokenResponse {
        return fcmTokenService.registerToken(
            userId = tokenRequest.userId,
            token = tokenRequest.token,
        )
    }

    @DeleteMapping("/delete/{userId}")
    suspend fun deletePushNotificationToken(@PathVariable userId: String) {
        fcmTokenService.deleteToken(userId)
    }
}
