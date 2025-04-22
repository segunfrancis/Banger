package com.segunfrancis.wallpaperdownloader

import androidx.compose.runtime.saveable.Saver

/**
 * Generic saver for sealed classes that can be serialized to String.
 * @param toSerialized Converts your sealed class to a String.
 * @param fromSerialized Reconstructs your sealed class from the String.
 */
fun <T : Any> sealedClassSaver(
    toSerialized: (T) -> String,
    fromSerialized: (String) -> T?
): Saver<T, String> = Saver(
    save = { toSerialized(it) },
    restore = { fromSerialized(it) }
)

val appDestinationsSaver = sealedClassSaver(
    toSerialized = { destination ->
        when (destination) {
            is AppDestinations.Home -> "Home"
            is AppDestinations.Favourites -> "Favourites"
            is AppDestinations.Profile -> "Profile"
        }
    },
    fromSerialized = { serialized ->
        when (serialized) {
            "Home" -> AppDestinations.Home
            "Favourites" -> AppDestinations.Favourites
            "Profile" -> AppDestinations.Profile
            else -> null // or throw on invalid input
        }
    }
)
