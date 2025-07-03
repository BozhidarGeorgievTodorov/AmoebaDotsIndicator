# AmoebaDotsIndicator

A customizable animated dots indicator for ViewPager2, featuring smooth "amoeba"-style transitions between dots. Supports light and dark themes with automatic color adaptation, plus full color and size customization.

![AmoebaDotsIndicator demo](../../Desktop/AmoebaDotsIndicator/assets/amoebadots_demo.gif)

---

## Features

- Animated dots with connected "arms" during swipes.
- Smoothly moving body dot.
- Default colors adapt automatically to light and dark themes.
- Easily customizable via XML attributes or Kotlin/Java setters.
- Simple integration with `ViewPager2`.

---

## Setup

Add the library to your project:
```gradle
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io")}
}

dependencies {
    implementation("com.github.BozhidarGeorgievTodorov:AmoebaDotsIndicator:v1.0.0")
}

```

Make sure you have the required resources for colors defined in your project:
```xml
<!-- res/values/colors.xml -->
<resources>
    <color name="amoeba_dot_color">#008000</color> <!-- green for light theme -->
    <color name="amoeba_body_color">#40E0D0</color> <!-- turquoise for light theme -->
</resources>

<!-- res/values-night/colors.xml -->
<resources>
    <color name="amoeba_dot_color">#9C27B0</color> <!-- purple for dark theme -->
    <color name="amoeba_body_color">#673AB7</color> <!-- purple for dark theme -->
</resources>
```
## Usage

### Layout XML

Include the indicator in your layout. No need to specify colors unless you want to override defaults:
```xml
<androidx.viewpager2.widget.ViewPager2
        android:id="@+id/viewPager"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

<com.trbrnapps.amoebadotsindicator.AmoebaDotsIndicator
    android:id="@+id/indicator"
    android:layout_width="match_parent"
    android:layout_height="60dp"
    app:layout_constraintTop_toTopOf="parent"
    android:background="@android:color/transparent"
    android:elevation="10dp"/>
```
### Connecting to ViewPager2
In your Activity or Fragment:
```kotlin
val viewPager = findViewById<ViewPager2>(R.id.viewPager)
val indicator = findViewById<AmoebaDotsIndicator>(R.id.indicator)

viewPager.adapter = YourPagerAdapter(this)
indicator.attachToViewPager(viewPager)
```
> **NOTE:**  
> You may want to cap the swipe offset to ±1 swipe to ensure clean animation and prevent glitches in the amoeba-style transition.

Make sure your ViewPager2 adapter properly implements `getItemCount()` so the indicator reflects the correct number of pages. If you manually set the dot count via `setDotCount()`, ensure it matches or intentionally differs from your adapter item count as needed.


### Customization

You can customize colors, size, and animation duration either in XML:
```xml
<com.trbrnapps.amoebadotsindicator.AmoebaDotsIndicator
    android:layout_width="match_parent"
    android:layout_height="60dp"
    app:dotRadius="10dp"                     <!-- Default: 4dp -->
    app:dotSpacingMultiplier="4.0"           <!-- Default: 4.0 -->
    app:dotColor="@color/custom_dot_color"   <!-- Default: amoeba_dot_color from resources -->
    app:bodyColor="@color/custom_body_color" <!-- Default: amoeba_body_color from resources -->
    app:animationDuration="300" />           <!-- Default: 200 (milliseconds) -->

```
Or programmatically:
```kotlin
indicator.setDotCount(5)                     // Default: 3 or adapter item count
indicator.setDotRadius(30f)                  // Default: 10f
indicator.setDotSpacingMultiplier(4.0f)      // Default: 4.0f
indicator.setDotColor(Color.GREEN)           // Default: amoeba_dot_color resource
indicator.setBodyColor(Color.CYAN)           // Default: amoeba_body_color resource
indicator.setAnimationDuration(250L)         // Default: 200L (milliseconds)
```

## How colors work

- By default, the indicator loads colors from your app resources (R.color.amoeba_dot_color and R.color.amoeba_body_color) which adapt automatically to light/dark themes.
- Explicit colors set via XML attributes or setters override these defaults.
- This enables flexible theming combined with manual control.

## Requirements
- Minimum SDK: 21
- Supports ViewPager2

## License
This project is released without any specific license.  
Feel free to use, modify, and distribute it as you wish.
## Contributions and Issues
Feel free to open issues or pull requests for improvements!

## Contact

Author: Bozhidar Georgiev Todorov
