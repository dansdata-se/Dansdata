package se.dansdata.backstage.profiles.ports

import se.dansdata.backstage.framework.entities.ExtRef
import se.dansdata.backstage.profiles.entities.ProfileExtRef

fun interface AllocateImage {
    suspend fun execute(owner: ExtRef): ProfileExtRef.Image
}
