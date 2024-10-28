package se.dansdata.backstage.profiles.actions

import se.dansdata.backstage.framework.actions.Action
import se.dansdata.backstage.profiles.entities.ProfileExtRef

class CreateProfileAction(
    val nameRef: ProfileExtRef.Translation,
) : Action {
    override suspend fun execute() {

    }
}
