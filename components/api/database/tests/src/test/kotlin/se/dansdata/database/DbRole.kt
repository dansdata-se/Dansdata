package se.dansdata.database

enum class DbRole(val roleName: String) {
    DbmsOwner("postgres"),
    DbOwner("dansdata"),
    AppBackstage("backstage"),
    TranslationsOwner("translations_owner"),
}
