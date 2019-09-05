package pl.szczodrzynski.navlib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import pl.droidsonroids.gif.GifDrawable
import java.io.FileNotFoundException

/**
 * Created by mikepenz on 13.07.15.
 */

open class ImageHolder : com.mikepenz.materialdrawer.holder.ImageHolder {

    constructor(url: String) : super(url) {}

    constructor(uri: Uri) : super(uri) {}

    constructor(icon: Drawable?) : super(icon) {}

    constructor(bitmap: Bitmap?) : super(bitmap) {}

    constructor(@DrawableRes iconRes: Int) : super(iconRes) {}

    constructor(@DrawableRes iconRes: Int, colorFilter: Int?) : super(iconRes) {
        this.colorFilter = colorFilter
    }

    constructor(iicon: IIcon) : super(null as Bitmap?) {
        this.iIcon = iicon
    }

    @ColorInt
    var colorFilter: Int? = null
    var colorFilterMode: PorterDuff.Mode = PorterDuff.Mode.DST_OVER

    override fun applyTo(imageView: ImageView): Boolean {
        return applyTo(imageView, null)
    }

    /**
     * sets an existing image to the imageView
     *
     * @param imageView
     * @param tag       used to identify imageViews and define different placeholders
     * @return true if an image was set
     */
    override fun applyTo(imageView: ImageView, tag: String?): Boolean {
        val ii = iIcon

        if (uri != null) {
            if (uri.toString().endsWith(".gif", true)) {
                imageView.setImageDrawable(GifDrawable(uri.toString()))
            }
            else {
                val consumed = DrawerImageLoader.instance.setImage(imageView, uri, tag)
                if (!consumed) {
                    imageView.setImageURI(uri)
                }
            }
        } else if (icon != null) {
            imageView.setImageDrawable(icon)
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else if (iconRes != -1) {
            imageView.setImageResource(iconRes)
        } else if (ii != null) {
            imageView.setImageDrawable(IconicsDrawable(imageView.context, ii).actionBar())
        } else {
            imageView.setImageBitmap(null)
            return false
        }

        if (colorFilter != null) {
            imageView.colorFilter = PorterDuffColorFilter(colorFilter!!, colorFilterMode)
        }

        return true
    }
}
