package se.dansdata.database

enum class DbUser(role: DbRole, val password: String) {
    DbmsOwner(DbRole.DbmsOwner, "postgres-pwd"),
    DbOwner(DbRole.DbOwner, "dansdata-pwd"),
    AppBackstage(DbRole.AppBackstage, "backstage-pwd");

    val userName: String = role.roleName
}
