package se.dansdata.backstage.framework.entities

import java.time.LocalDateTime

interface MutableGraph

abstract class GraphNode {
    private var fieldListener: ((Scalar<*>) -> Unit)? = null

    fun stringScalar(): Scalar<String> =
        object : Scalar<String> {
            override fun unaryPlus() {
                fieldListener?.invoke(this)
            }
        }

    fun <T : GraphNode> reference(): NodeReference<T> =
        object : NodeReference<T> {
            override fun invoke(requestBuilder: T.() -> Unit) {

                fieldListener?.invoke(this)
            }
        }
}

interface NodeReference<T : GraphNode> {
    operator fun invoke(requestBuilder: T.() -> Unit)
}

interface Scalar<T> {
    operator fun unaryPlus()
}

data object Apple : GraphNode() {
    val name = stringScalar()
    val orange = reference<Orange>()
}

data object Orange : GraphNode() {
    val name = stringScalar()
    val red = reference<Apple>()
    val green = reference<Apple>()
}

fun test() {
    Apple.orange {
        +name
        red {
            +name
            orange {
                +name
                green {}
            }
        }
    }
}

sealed interface Field {
    data object Int : Field

    data object String : Field

    interface Reference : Field {
        operator fun invoke(): Unit
    }
}

@DslMarker annotation class QueryMarker

@QueryMarker
interface AbstractNode<T : AbstractNode<T>> {
    operator fun unaryPlus() {}
}

interface AbstractEdge<TFrom : AbstractNode<TFrom>, TTo : AbstractNode<TTo>> {
    val label: String
    val from: TFrom
    val to: TTo
}

class Query {
    class Builder<T : AbstractNode<T>>(private val innerQuery: Query? = null) {
        private val queriedFields = mutableSetOf<String>()

        internal fun build(init: T.() -> Unit): Query = Query()
    }
}

data object EventNode : AbstractNode<EventNode> {
    val title = Field.String

    fun bands(spider: BandNode.() -> Unit) = BandNode.run { spider }

    data class Attributes(val start: LocalDateTime?, val end: LocalDateTime?)
}

data object BandNode : AbstractNode<BandNode> {
    val name = Field.String

    fun events(spider: EventNode.() -> Unit) = PlayedAtEdge.to.run { spider() }
}

data object PlayedAtEdge : AbstractEdge<BandNode, EventNode> {
    override val label: String = "Played At"
    override val from: BandNode = BandNode
    override val to: EventNode = EventNode
}

object Queries {
    fun bands(action: BandNode.() -> Unit) = Query.Builder<BandNode>().build(action)
}

suspend fun BandNode.spider(action: suspend BandNode.() -> Unit) = BandNode.run { action() }

suspend fun EventNode.spider(action: suspend EventNode.() -> Map<String, Any>) =
    EventNode.run { action() }

@Suppress("MagicNumber")
suspend fun main() {
    val event =
        EventNode.Attributes(
            LocalDateTime.of(2024, 10, 13, 18, 0, 0),
            LocalDateTime.of(2024, 10, 13, 22, 0, 0),
        )

    val band = BandNode.Attributes("Xplays")

    DataSpider.from(BandNode) /*.bring(BandNode.name)*/.followPlayedAt()

    Queries.bands {
        +name
        events {}
    }
}
