package se.dansdata.backstage.framework.actions

interface Action {
    suspend fun execute()
}
