plugins {
    alias(libs.plugins.android.application) apply true

}

android {
    namespace = "com.example.yeschef"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.yeschef"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0") // Latest version for CardView
        implementation ("androidx.navigation:navigation-fragment-ktx:2.7.0")
        implementation ("androidx.navigation:navigation-ui-ktx:2.7.0")



    // Using the libraries from your version catalog
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // ViewPager2 dependency
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Testing libraries
    implementation("androidx.viewpager2:viewpager2:1.0.0") // Add ViewPager2 dependency here

    // Add GridLayout dependency
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // GSON DEPENDENCY
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
