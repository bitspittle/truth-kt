## Truthish

![version](https://img.shields.io/badge/version-1.0.2-blue.svg)
![truthish tests](https://github.com/varabyte/truthish/actions/workflows/gradle-test-all.yml/badge.svg)
![coverage badge](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/bitspittle/01b6bfe88483946d9f5438f5616d9b9f/raw/truthish-coverage-badge.json)
<br>
![kotlin version](https://img.shields.io/badge/kotlin_compatibility-1.6+-lightgray?logo=kotlin)
![targets](https://img.shields.io/badge/targets-JVM,_JS,_Win,_Linux,_Mac,_Android,_iOS-white.svg)
<br>
<a href="https://discord.gg/bCdxPr7aTV">
  <img alt="Varabyte Discord" src="https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2" />
</a>
[![Bluesky](https://img.shields.io/badge/Bluesky-0285FF?logo=bluesky&logoColor=fff)](https://bsky.app/profile/bitspittle.bsky.social)

A testing API inspired by [Google Truth](https://github.com/google/truth) but
rewritten in Kotlin from the ground up, so it can be used in Kotlin
multiplatform projects.

For example, you can write `assertThat` checks in tests like this:

```kotlin
import com.varabyte.truthish.*

fun isEven(num: Int) = (num % 2) == 0
fun square(num: Int) = (num * num)

@Test
fun testEvenOdd() {
    assertThat(isEven(1234)).isTrue()
    assertThat(isEven(1235)).isFalse()
}

@Test
fun testSum() {
    val nums = listOf(1, 2, 3, 4, 5)
    assertThat(nums.sum()).isEqualTo(15)
}

@Test
fun testMap() {
    assertThat(listOf(1, 2, 3, 4, 5).map { square(it) })
        .containsExactly(1, 4, 9, 16, 25)
        .inOrder()
}

@Test
fun customMessage() {
    assertWithMessage("Unexpected list size")
        .that(listOf(1, 2, 3, 4, 5)).hasSize(5)
}

@Test
fun testDivideByZeroException() {
    val ex = assertThrows<ArithmeticException> {
        10 / 0
    }
    assertThat(ex.message).isEqualTo("/ by zero")
}
```

If you would like to check multiple assertions at the same time (meaning a failure won't be reported until all checks
have run), you can use the `assertAll` function:

```kotlin
val person = Person("Alice", 30)
assertAll {
    that(person.name).isEqualTo("Bob")
    that(person.age).isEqualTo(45)
}
// The above will assert once, reporting both equality check failures
```

You can read the [Google Truth documentation](https://truth.dev/) for why they
believe their fluent approach to assertions is both more readable and produces
cleaner error messages, but let's break one of the tests above to see a
specific example error message:

```kotlin
@Test
fun testMapButIntentionallyBroken() {
    assertThat(listOf(1, 2, 3, 4, 5).map { square(it) })
        .containsExactly(1, 4, 9, 15, 26) // <-- Ooops, messed up 16 and 25 here
        .inOrder()
}
```

Output:

```text
A collection did not contain element(s)

Expected exactly all elements from: [ 1, 4, 9, 15, 26 ]
But was                           : [ 1, 4, 9, 16, 25 ]
Missing                           : [ 15, 26 ]
Extraneous                        : [ 16, 25 ]
```

# Using Truthish in Your Project

## Multiplatform

To use *Truthish* in your multiplatform application, declare any of the following dependencies relevant to your project:

```kotlin
// build.gradle.kts
// Multiplatform

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        browser()
        nodeJs()
    }

    linuxX64()
    macosArm64() // Mac M1+
    macosX64() // Mac Intel
    mingwX64() // Windows
    iosX64() // iOS Intel
    iosArm64() // iOS M1+
    iosSimulatorArm64()
    androidTarget()

    sourceSets {
        commonTest.dependencies {
            implementation("com.varabyte.truthish:truthish:1.0.2")
            implementation(kotlin("test"))
        }
    }
}
```

## Single platform

You can also use *Truthish* in non-multiplatform projects as well:

### JVM

```kotlin
// build.gradle.kts

repositories {
    mavenCentral()
}

dependencies {
    // ...

    testImplementation(kotlin("test"))
    testImplementation("com.varabyte.truthish:truthish:1.0.2")
}
```

### Android

```kotlin
// build.gradle.kts

repositories {
    mavenCentral()
}

android { /* ... */ }

dependencies {
    // ...

    // If used in tests that are run on the host (i.e. your dev machine)
    testImplementation("com.varabyte.truthish:truthish:1.0.2")

    // If used in tests that are run on the device
    androidTestImplementation("com.varabyte.truthish:truthish:1.0.2")
}
```

### Testing snapshots

Most users won't ever need to run a Truthish snapshot, so feel free to skip this section! However, occasionally, bug
fixes and new features will be available for testing for a short period before they are released.

If you ever file a bug with Truthish and are asked to test a fix using a snapshot, you must add an entry for the sonatype
snapshots repository to your `repositories` block in order to allow Gradle to find it:

```diff
// build.gradle.kts

repositories {
  mavenCentral()
+ maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
+   mavenContent { includeGroup("com.varabyte.truthish") }
+ }
}
```
