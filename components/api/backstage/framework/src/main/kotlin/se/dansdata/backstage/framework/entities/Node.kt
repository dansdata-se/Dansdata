package se.dansdata.backstage.framework.entities

interface Node {
    val id: OpaqueIdentifier

    suspend fun edges(): List<Edge>
}
