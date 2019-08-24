package pl.szczodrzynski.navlib

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import com.google.android.material.elevation.ElevationOverlayProvider
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable


/*private val displayMetrics by lazy {
    context.resources.displayMetrics
}*/
/*private val configuration by lazy { context.resources.configuration }
private val displayWidth: Int by lazy { configuration.screenWidthDp }
private val displayHeight: Int by lazy { configuration.screenHeightDp }*/

fun getTopInset(context: Context, view: View): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        (view.rootWindowInsets?.systemWindowInsetTop ?: 24)
    } else {
        24
    } * context.resources.displayMetrics.density
}
fun getLeftInset(context: Context, view: View): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        (view.rootWindowInsets?.systemWindowInsetLeft ?: 0)
    } else {
        0
    } * context.resources.displayMetrics.density
}
fun getRightInset(context: Context, view: View): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        (view.rootWindowInsets?.systemWindowInsetRight ?: 0)
    } else {
        0
    } * context.resources.displayMetrics.density
}
fun getBottomInset(context: Context, view: View): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        (view.rootWindowInsets?.systemWindowInsetBottom ?: 48)
    } else {
        48
    } * context.resources.displayMetrics.density
}

fun getColorFromAttr(context: Context, @AttrRes color: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(color, typedValue, true)
    return typedValue.data
}

fun View.getActivity(): Activity {
    return findViewById<View>(android.R.id.content).context as Activity
}

fun blendColors(background: Int, foreground: Int): Int {
    val r1 = (background shr 16 and 0xff)
    val g1 = (background shr 8 and 0xff)
    val b1 = (background and 0xff)

    val r2 = (foreground shr 16 and 0xff)
    val g2 = (foreground shr 8 and 0xff)
    val b2 = (foreground and 0xff)
    val a2 = (foreground shr 24 and 0xff)
    //ColorUtils.compositeColors()

    val factor = a2.toFloat() / 255f
    val red = (r1 * (1 - factor) + r2 * factor)
    val green = (g1 * (1 - factor) + g2 * factor)
    val blue = (b1 * (1 - factor) + b2 * factor)

    return (0xff000000 or (red.toLong() shl 16) or (green.toLong() shl 8) or (blue.toLong())).toInt()
}

fun elevateSurface(context: Context, dp: Int): Int {
    ElevationOverlayProvider(context).apply {
        return compositeOverlay(themeSurfaceColor, dp * context.resources.displayMetrics.density)
    }
}

fun isTablet(c: Context): Boolean {
    return (c.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

fun hasNavigationBar(resources: Resources): Boolean {
    val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    var hasNavigationBar = id > 0 && resources.getBoolean(id)

    // Allow a system property to override this. Used by the emulator.
    // See also hasNavigationBar().
    val navBarOverride = System.getProperty("qemu.hw.mainkeys")
    if (navBarOverride == "1")
        hasNavigationBar = true
    else if (navBarOverride == "0") hasNavigationBar = false

    return hasNavigationBar
}

fun IconicsDrawable.colorAttr(context: Context, @AttrRes attrRes: Int) = color(IconicsColor.colorInt(getColorFromAttr(context, attrRes)))

fun Context.getDrawableFromRes(@DrawableRes id: Int): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        resources.getDrawable(id, theme)
    }
    else {
        resources.getDrawable(id)
    }
}