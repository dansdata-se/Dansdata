plugins { id("buildlogic.kotlin-application-conventions") }

dependencies {
    implementation(project(":framework"))
    implementation(project(":domain:events"))
    implementation(project(":domain:images"))
    implementation(project(":domain:locations"))
    implementation(project(":domain:profiles"))
    implementation(project(":domain:tickets"))
    implementation(project(":domain:translations"))
}

application {
    // Define the main class for the application.
    mainClass = "se.dansdata.backstage.app.AppKt"
}
