package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object GymLocationsProvider {
    fun createGymLocation(
        id: String = "1",
        title: String = "Gym A",
        subTitle: String = "Gym A Subtitle",
        description: String = "Gym A Description",
        imageUrl: String = "https://www.gym-a.com",
    ): GymLocation =
        GymLocation(
            id = id,
            title = title,
            subTitle = subTitle,
            description = description,
            imageUrl = imageUrl,
        )

    fun gymLocations(): Flow<GymLocation> =
        flowOf(
            createGymLocation(),
            createGymLocation(
                id = "2",
                title = "Gym B",
                subTitle = "Gym B Subtitle",
                description = "Gym B Description",
                imageUrl = "https://www.gym-b.com",
            ),
        )
}
