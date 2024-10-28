package se.dansdata.backstage.framework.entities

interface Edge {
    val id: OpaqueIdentifier
    val from: Node
    val to: Node
}
