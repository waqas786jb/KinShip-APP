// Top-level build file where you can add configuration options common to all sub-projects/modules.


plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

    id("com.google.firebase.crashlytics") version "3.0.2" apply false
   // id("com.google.devtools.ksp") version "1.9.21-1.0.16" apply false
}
true