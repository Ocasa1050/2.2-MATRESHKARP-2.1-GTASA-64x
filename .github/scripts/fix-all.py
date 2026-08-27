#!/usr/bin/env python3
import os
import shutil

print("=" * 60)
print("SPACE RP - Auto Fix Script")
print("=" * 60)

# ─── 1. Remove exposed keystore ─────────────────────
if os.path.exists("app/edgar.jks"):
    os.remove("app/edgar.jks")
    print("✅ Removed exposed keystore: app/edgar.jks")

# ─── 2. Create new build.gradle (Project level) ─────
build_gradle_project = """buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.0'
        classpath 'com.google.gms:google-services:4.4.0'
        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.9.9'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
"""

with open("build.gradle", "w") as f:
    f.write(build_gradle_project)
print("✅ Created build.gradle (Project level)")

# ─── 3. Create new settings.gradle ──────────────────
settings_gradle = """pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "MatreshkaRP-2.1"
include ':app'
"""

with open("settings.gradle", "w") as f:
    f.write(settings_gradle)
print("✅ Created settings.gradle")

# ─── 4. Create new gradle.properties ────────────────
gradle_props = """org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
android.useAndroidX=true
android.enableJetifier=true
android.nonTransitiveRClass=true
android.enableR8.fullMode=true
"""

with open("gradle.properties", "w") as f:
    f.write(gradle_props)
print("✅ Created gradle.properties")

# ─── 5. Create new app/build.gradle ─────────────────
app_build_gradle = """plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
    id 'com.google.firebase.crashlytics'
}

android {
    compileSdk 34
    namespace 'ru.edgar.space'

    signingConfigs {
        debug {
            storeFile file(System.getProperty("user.home") + "/.android/debug.keystore")
            storePassword "android"
            keyAlias "androiddebugkey"
            keyPassword "android"
        }
        release {
            storeFile file(System.getProperty("user.home") + "/.android/debug.keystore")
            storePassword "android"
            keyAlias "androiddebugkey"
            keyPassword "android"
        }
    }

    defaultConfig {
        applicationId 'ru.edgar.space'
        minSdk 26
        targetSdk 34
        versionCode 105
        versionName "0.8.2.1"
        multiDexEnabled true
        ndk {
            abiFilters 'armeabi-v7a', 'arm64-v8a'
        }
        buildConfigField "int", "VK_APP_ID", "51874098"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    packagingOptions {
        jniLibs {
            excludes += ['META-INF/*']
        }
        resources {
            excludes += ['META-INF/*']
        }
    }

    ndkVersion "25.2.9519653"

    externalNativeBuild {
        cmake {
            path "src/main/cpp/CMakeLists.txt"
        }
    }

    buildTypes {
        debug {
            debuggable true
            jniDebuggable true
            minifyEnabled false
            shrinkResources false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.debug
        }
        release {
            firebaseCrashlytics {
                nativeSymbolUploadEnabled true
                strippedNativeLibsDir 'build/intermediates/stripped_native_libs/release/out/lib'
                unstrippedNativeLibsDir 'build/intermediates/merged_native_libs/release/out/lib'
            }
            debuggable false
            jniDebuggable false
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
        all {
            multiDexEnabled true
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    lintOptions {
        checkReleaseBuilds true
        abortOnError false
        disable 'MissingTranslation', 'ExtraTranslation'
    }

    buildFeatures {
        prefab true
        buildConfig true
    }
}

dependencies {
    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation "androidx.multidex:multidex:2.0.1"
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'ru.egslava:MaskedEditText:1.0.5'
    implementation 'com.dinuscxj:circleprogressbar:1.3.6'
    implementation 'com.makeramen:roundedimageview:2.3.0'
    implementation 'com.mikhaellopez:circularprogressbar:3.1.0'
    implementation 'com.github.Triggertrap:SeekArc:v1.1'
    implementation 'com.github.smarteist:autoimageslider:1.4.0'
    implementation 'com.github.skydoves:colorpickerview:2.3.0'
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.mindorks.android:prdownloader:0.6.0'
    implementation 'com.liulishuo.filedownloader:library:1.7.7'
    implementation group: 'org.ini4j', name: 'ini4j', version: '0.5.4'
    implementation 'net.lingala.zip4j:zip4j:2.11.5'
    implementation 'com.hzy:un7zip:1.7.1'
    implementation 'com.github.hzy3774:AndroidP7zip:v1.7.2'
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-messaging'
    implementation 'com.google.firebase:firebase-database'
    implementation 'com.google.firebase:firebase-config'
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-analytics'
    implementation 'com.google.firebase:firebase-crashlytics'
    implementation 'com.google.firebase:firebase-crashlytics-ndk'
    implementation 'com.vk:android-sdk-core:4.1.0'
    implementation 'com.google.android.gms:play-services-auth:20.7.0'
    implementation "com.joom.paranoid:paranoid-gradle-plugin:0.3.14"
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
"""

with open("app/build.gradle", "w") as f:
    f.write(app_build_gradle)
print("✅ Created app/build.gradle")

# ─── 6. Fix MainActivity.java ───────────────────────
main_activity_path = "app/src/main/java/ru/edgar/launcher/activity/MainActivity.java"
if os.path.exists(main_activity_path):
    with open(main_activity_path, "r") as f:
        content = f.read()
    content = content.replace("51874098", "BuildConfig.VK_APP_ID")
    with open(main_activity_path, "w") as f:
        f.write(content)
    print("✅ Fixed MainActivity.java")

# ─── 7. Fix AndroidManifest.xml ─────────────────────
manifest_path = "app/src/main/AndroidManifest.xml"
if os.path.exists(manifest_path):
    with open(manifest_path, "r") as f:
        content = f.read()
    content = content.replace('usesCleartextTraffic="true"', 'usesCleartextTraffic="false"')
    with open(manifest_path, "w") as f:
        f.write(content)
    print("✅ Fixed AndroidManifest.xml")

# ─── 8. Create network_security_config.xml ──────────
os.makedirs("app/src/main/res/xml", exist_ok=True)
network_config = """<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
"""
with open("app/src/main/res/xml/network_security_config.xml", "w") as f:
    f.write(network_config)
print("✅ Created network_security_config.xml")

# ─── 9. Add networkSecurityConfig to manifest ───────
if os.path.exists(manifest_path):
    with open(manifest_path, "r") as f:
        content = f.read()
    if "networkSecurityConfig" not in content:
        content = content.replace(
            'usesCleartextTraffic="false"',
            'usesCleartextTraffic="false"\n        android:networkSecurityConfig="@xml/network_security_config"'
        )
        with open(manifest_path, "w") as f:
            f.write(content)
        print("✅ Added networkSecurityConfig to manifest")

print("=" * 60)
print("All fixes applied successfully!")
print("=" * 60)
