package pl.szczodrzynski.navlib

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.navlibfont.NavLibFont
import com.mikepenz.iconics.utils.sizeDp
import pl.szczodrzynski.navlib.drawer.NavDrawer

class NavToolbar : MaterialToolbar {

    constructor(context: Context) : super(context) {
        create(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        create(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        create(attrs, defStyle)
    }

    var toolbarImage: ImageView? = null
        set(value) {
            field = value
            toolbarImage?.setOnClickListener {
                profileImageClickListener?.invoke()
            }
        }

    override fun setSubtitle(subtitle: CharSequence?) {
        if(subtitle.isNullOrEmpty()) {
            setPadding(0, 0, 0, 0)
            toolbarImage?.translationY = 0f
        } else {
            setPadding(0, -1, 0, 5)
            toolbarImage?.translationY = 6f
        }
        super.setSubtitle(subtitle)
    }

    private fun create(attrs: AttributeSet?, defStyle: Int) {
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_menu_badge) as LayerDrawable?
        icon?.apply {
            mutate()
            setDrawableByLayerId(R.id.ic_menu, IconicsDrawable(context).apply {
                this.icon = NavLibFont.Icon.nav_menu
                sizeDp = 24
                colorAttr(context, R.attr.colorOnSurface)
            })
            setDrawableByLayerId(R.id.ic_badge, BadgeDrawable(context))
        }
        navigationIcon = icon
        setNavigationOnClickListener {
            menuClickListener?.invoke()
        }

    }

    var subtitleFormat: Int? = null
    var subtitleFormatWithUnread: Int? = null

    var profileImageClickListener: (() -> Unit)? = null
    var menuClickListener: (() -> Unit)? = null

    var profileImage
        get() = toolbarImage?.drawable
        set(value) {
            toolbarImage?.setImageDrawable(value)
        }
}