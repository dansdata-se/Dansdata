package se.dansdata.database.tests

import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig

fun DbConnectionConfig.getConnection(): Connection =
    DriverManager.getConnection(
        url,
        Properties().also { props ->
            props.setProperty("user", this@getConnection.user)
            props.setProperty("password", this@getConnection.password)
            // Ensure EscapeSyntaxCallmode property is set to support procedures if there is no
            // return value.
            props.setProperty("escapeSyntaxCallMode", "callIfNoReturn")
        },
    )
