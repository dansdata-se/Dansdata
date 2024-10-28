package se.dansdata.database

enum class DbSchema(val schemaName: String, val owner: DbRole, val isPublic: Boolean) {
    EventsPublic("events_public", DbRole.DbOwner, true),
    TranslationsPrivate("translations_private", DbRole.TranslationsOwner, false),
    TranslationsPublic("translations_public", DbRole.TranslationsOwner, true),
}
