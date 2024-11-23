package se.dansdata.database

import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.slf4j.LoggerFactory
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.utility.DockerImageName

/** Testcontainers implementation for Dansdata's backing database. */
class DansdataDbContainer private constructor() :
    JdbcDatabaseContainer<DansdataDbContainer>(DockerImageName.parse("dansdata/database:test")) {
    private val logger = LoggerFactory.getLogger(DansdataDbContainer::class.java)

    init {
        waitStrategy =
            LogMessageWaitStrategy()
                .withRegEx(".*database system is ready to accept connections.*\\s")
                .withTimes(2)
                .withStartupTimeout(30.seconds.toJavaDuration())

        addExposedPort(5432)

        withLogConsumer(Slf4jLogConsumer(logger).withSeparateOutputStreams())
    }

    fun getConfigForUser(dbUser: DbUser) =
        DbConnectionConfig(jdbcUrl, dbUser.userName, dbUser.password)

    override fun configure() {
        addEnv("DBMS_OWNER_USER", DbUser.DbmsOwner.userName)
        addEnv("DBMS_OWNER_PASSWORD", DbUser.DbmsOwner.password)
        addEnv("DB_OWNER_USER", DbUser.DbOwner.userName)
        addEnv("DB_OWNER_PASSWORD", DbUser.DbOwner.password)
        addEnv("DB_APP_BACKSTAGE_USER", DbUser.AppBackstage.userName)
        addEnv("DB_APP_BACKSTAGE_PASSWORD", DbUser.AppBackstage.password)
    }

    override fun getDriverClassName(): String = "org.postgresql.Driver"

    override fun getJdbcUrl(): String =
        "jdbc:postgresql://" +
            host +
            ":" +
            getMappedPort(5432) +
            "/" +
            databaseName +
            constructUrlParameters("?", "&")

    override fun getUsername(): String = DbUser.DbOwner.userName

    override fun getPassword(): String = DbUser.DbOwner.password

    override fun getDatabaseName(): String = "dansdata"

    override fun getTestQueryString(): String = "SELECT 1"

    override fun waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this)
    }

    class TestWrapper(private val container: DansdataDbContainer) {
        fun <R> queryAs(user: DbUser, block: DbConnectionConfig.() -> R): R =
            container.getConfigForUser(user).run(block)
    }

    object Shared {
        @JvmStatic private val container = DansdataDbContainer().apply { start() }

        fun use(block: TestWrapper.() -> Unit) {
            block(TestWrapper(container))
        }
    }

    companion object {
        /**
         * Creates a new [DansdataDbContainer] for use only under the given block.
         *
         * Note: container starts are quite expensive! Prefer [Shared.use] when possible.
         *
         * @see Shared.use
         */
        fun use(block: TestWrapper.() -> Unit) {
            DansdataDbContainer().use { container ->
                container.start()
                block(TestWrapper(container))
            }
        }
    }
}
