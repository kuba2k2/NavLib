package pl.szczodrzynski.navigation

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import pl.szczodrzynski.navlib.ImageHolder
import pl.szczodrzynski.navlib.drawer.IDrawerProfile

class DrawerProfile(
    override var id: Int,
    override var name: String?,
    override var subname: String?,
    override var image: String?
) : IDrawerProfile {

    override fun getImageDrawable(context: Context): Drawable? {
        return null
    }

    override fun getImageHolder(context: Context): ImageHolder? {
        return ImageHolder(image ?: return null)
    }

    override fun applyImageTo(imageView: ImageView) {
        ImageHolder(image ?: return).applyTo(imageView)
    }
}