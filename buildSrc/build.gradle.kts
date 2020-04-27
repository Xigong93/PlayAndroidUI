plugins {
    `kotlin-dsl`
}
repositories {
    maven {  setUrl ("https://maven.aliyun.com/repository/gradle-plugin") }
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation("com.squareup.okhttp3:okhttp:4.2.2")
    implementation("com.google.code.gson:gson:2.8.6")
}
