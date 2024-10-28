package se.dansdata.backstage.profiles.ports

import se.dansdata.backstage.profiles.entities.ProfileExtRef

fun interface AllocateTranslation {
    suspend fun execute(): ProfileExtRef.Translation
}
