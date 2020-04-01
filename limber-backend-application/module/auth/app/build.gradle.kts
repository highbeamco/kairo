dependencies {
    implementation(project(":limber-backend-application:module:orgs:api"))
    implementation(project(":limber-backend-application:module:users:api"))
    implementation(project(":piper:sql"))
    implementation(Dependencies.Bcrypt.jbcrypt)
    testImplementation(project(":piper:sql:testing"))
}
