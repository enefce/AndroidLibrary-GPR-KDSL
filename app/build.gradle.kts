import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

/**Create github.properties in root project folder file with gpr.usr=GITHUB_USER_ID  & gpr.key=PERSONAL_ACCESS_TOKEN**/
val githubPropertiesFile = rootProject.file("github.properties");
val githubProperties = Properties()
githubProperties.load(FileInputStream(githubPropertiesFile))

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.enefce.samples.androidlibdemo"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    /****GitHubPackages Path and credentials***/
    repositories {
        maven {
            name = "GitHubPackages"
            /*  Configure path to the library hosted on GitHub Packages Registry
             *  Replace UserID with package owner userID and REPOSITORY with the repository name
             *  e.g. "https://maven.pkg.github.com/enefce/AndroidLibrary-GPR-KDSL"
             */
            //url = uri("https://maven.pkg.github.com/UserID/REPOSITORY")
            url = uri("https://maven.pkg.github.com/prasad79/AndroidLibrary-GPR-KDSL")

            credentials {
                /**Create github.properties in root project folder file with gpr.usr=GITHUB_USER_ID  & gpr.key =PERSONAL_ACCESS_TOKEN**/
                username = githubProperties["gpr.usr"] as String? ?: System.getenv("GPR_USER")
                password = githubProperties["gpr.key"] as String? ?: System.getenv("GPR_API_KEY")
            }
        }
    }

}


dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    //Use SampleAndroidLib library
    implementation("com.enefce.libraries:sampleAndroidLib2:1.0.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
