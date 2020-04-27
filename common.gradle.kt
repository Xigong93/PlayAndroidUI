apply plugin: 'kotlin-android'
android{
    android {

        compileSdkVersion(Version.COMPILE_SDK_VERSION)
        defaultConfig {
            minSdkVersion(Version.MIN_SDK_VERSION)
            targetSdkVersion(Version.TARGET_SDK_VERSION)
        }
        // To inline the bytecode built with JVM target 1.8 into
        // bytecode that is being built with JVM target 1.6. (e.g. navArgs)

        compileOptions {
            targetCompatibility(JavaVersion.VERSION_1_8)
            sourceCompatibility(JavaVersion.VERSION_1_8)
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

dependencies {
    implementation fileTree (dir: 'libs', include: ['*.jar'])
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")
    implementation('androidx.appcompat:appcompat:1.1.0')
    implementation('androidx.core:core-ktx:1.2.0')
    implementation('com.google.android.material:material:1.1.0')
    implementation('androidx.constraintlayout:constraintlayout:1.1.3')
    implementation('androidx.navigation:navigation-fragment:2.2.2')
    implementation('androidx.navigation:navigation-ui:2.2.2')
    implementation('androidx.lifecycle:lifecycle-extensions:2.2.0')
    implementation('androidx.navigation:navigation-fragment-ktx:2.2.2')
    implementation('androidx.navigation:navigation-ui-ktx:2.2.2')
    testImplementation('junit:junit:4.12')
    androidTestImplementation('androidx.test.ext:junit:1.1.1')
    androidTestImplementation('androidx.test.espresso:espresso-core:3.2.0')

}