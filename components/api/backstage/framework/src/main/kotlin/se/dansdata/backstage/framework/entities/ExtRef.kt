package se.dansdata.backstage.framework.entities

/** Represents an *ext*ernal *ref*erence to an entity outside the current domain */
interface ExtRef {
    /** The type of entity represented by this reference. */
    val entityType: String

    /**
     * An opaque identifier uniquely identifying the entity among other entities of the same
     * [entityType].
     *
     * May be null if the external resource is known not to exist.
     */
    val id: OpaqueIdentifier?

    fun interface Resolver<T : ExtRef> {
        fun resolve(ref: T)
    }
}
