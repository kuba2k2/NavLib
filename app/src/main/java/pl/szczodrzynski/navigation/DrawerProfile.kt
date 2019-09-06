package pl.szczodrzynski.navigation

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import pl.droidsonroids.gif.GifDrawable
import pl.szczodrzynski.navlib.ImageHolder
import pl.szczodrzynski.navlib.crc16
import pl.szczodrzynski.navlib.drawer.IDrawerProfile
import pl.szczodrzynski.navlib.getColorFromRes
import pl.szczodrzynski.navlib.getDrawableFromRes

/*
Example IDrawerProfile implementation
 */
class DrawerProfile(
    override var id: Int,
    override var name: String?,
    override var subname: String?,
    override var image: String?
) : IDrawerProfile {

    private fun colorFromName(context: Context, name: String?): Int {
        var crc = crc16(name ?: "")
        crc = (crc and 0xff) or (crc shr 8)
        crc %= 16
        val color = when (crc) {
            13 -> R.color.md_red_500
            4  -> R.color.md_pink_A400
            2  -> R.color.md_purple_A400
            9  -> R.color.md_deep_purple_A700
            5  -> R.color.md_indigo_500
            1  -> R.color.md_indigo_A700
            6  -> R.color.md_cyan_A200
            14 -> R.color.md_teal_400
            15 -> R.color.md_green_500
            7  -> R.color.md_yellow_A700
            3  -> R.color.md_deep_orange_A400
            8  -> R.color.md_deep_orange_A700
            10 -> R.color.md_brown_500
            12 -> R.color.md_grey_400
            11 -> R.color.md_blue_grey_400
            else -> R.color.md_light_green_A700
        }
        return context.getColorFromRes(color)
    }

    /* This method is not used in the drawer */
    /* return null if you do not want an image in the Toolbar */
    override fun getImageDrawable(context: Context): Drawable? {
        if (!image.isNullOrEmpty()) {
            try {
                if (image?.endsWith(".gif", true) == true) {
                    /* if you want to use GIFs as profile drawables, add
                    implementation "pl.droidsonroids.gif:android-gif-drawable:${versions.gifdrawable}"
                     */
                    return GifDrawable(image ?: "")
                }
                else {
                    return RoundedBitmapDrawableFactory.create(context.resources, image ?: "")
                    //return Drawable.createFromPath(image ?: "") ?: throw Exception()
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return context.getDrawableFromRes(R.drawable.profile).also {
            it.colorFilter = PorterDuffColorFilter(colorFromName(context, name), PorterDuff.Mode.DST_OVER)
        }
    }

    override fun getImageHolder(context: Context): ImageHolder {
        return if (!image.isNullOrEmpty()) {
            try {
                ImageHolder(image ?: "")
            } catch (_: Exception) {
                ImageHolder(R.drawable.profile, colorFromName(context, name))
            }
        }
        else {
            ImageHolder(R.drawable.profile, colorFromName(context, name))
        }
    }
    override fun applyImageTo(imageView: ImageView) {
        getImageHolder(imageView.context).applyTo(imageView)
    }
}