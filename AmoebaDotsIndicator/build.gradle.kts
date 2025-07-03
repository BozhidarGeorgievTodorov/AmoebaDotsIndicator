plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.bgt.amoebadotsindicator"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
            groupId = "com.bgt"
            artifactId = "amoebadotsindicator"
            version = "1.0.0"

            pom {
                name.set("AmoebaDotsIndicator")
                description.set("Animated amoeba-style dots indicator for ViewPager2")
                url.set("https://github.com/BozhidarGeorgievTodorov/AmoebaDotsIndicator")
                licenses {
                    license {
                        name.set("The Unlicense")
                        url.set("http://unlicense.org/")
                    }
                }
                developers {
                    developer {
                        id.set("bosco")
                        name.set("Bozhidar Georgiev Todorov")
                        email.set("bozhidargeorgiev82@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/BozhidarGeorgievTodorov/AmoebaDotsIndicator.git")
                    developerConnection.set("scm:git:ssh://github.com/BozhidarGeorgievTodorov/AmoebaDotsIndicator.git")
                    url.set("https://github.com/BozhidarGeorgievTodorov/AmoebaDotsIndicator")
                }
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}