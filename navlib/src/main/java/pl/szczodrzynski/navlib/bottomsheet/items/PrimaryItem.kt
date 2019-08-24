package pl.szczodrzynski.navlib.bottomsheet.items

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.utils.sizeDp
import com.mikepenz.materialize.holder.ImageHolder
import pl.szczodrzynski.navlib.R
import pl.szczodrzynski.navlib.colorAttr
import pl.szczodrzynski.navlib.getColorFromAttr

data class PrimaryItem(override val isContextual: Boolean = true) : IBottomSheetItem<PrimaryItem.ViewHolder> {

    /*_                             _
     | |                           | |
     | |     __ _ _   _  ___  _   _| |_
     | |    / _` | | | |/ _ \| | | | __|
     | |___| (_| | |_| | (_) | |_| | |_
     |______\__,_|\__, |\___/ \__,_|\__|
                   __/ |
                  |__*/
    override var id: Int = -1
    override val viewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.nav_bs_item_primary

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root = itemView.findViewById<View>(R.id.item_root)
        val text = itemView.findViewById<TextView>(R.id.item_text)
    }

    override fun bindViewHolder(viewHolder: ViewHolder) {
        viewHolder.root.setOnClickListener(onClickListener)
        viewHolder.text.text = title
        viewHolder.text.setTextColor(getColorFromAttr(viewHolder.text.context, R.attr.material_drawer_primary_text))
        viewHolder.text.setCompoundDrawables(
            IconicsDrawable(viewHolder.text.context)
                .icon(iconicsIcon?:CommunityMaterial.Icon.cmd_android)
                .colorAttr(viewHolder.text.context, R.attr.material_drawer_primary_icon)
                .sizeDp(20),
            null,
            null,
            null
        )
    }

    /*_____        _
     |  __ \      | |
     | |  | | __ _| |_ __ _
     | |  | |/ _` | __/ _` |
     | |__| | (_| | || (_| |
     |_____/ \__,_|\__\__,*/
    var title: CharSequence? = null
    @StringRes
    var titleRes: Int? = null
    var description: CharSequence? = null
    @StringRes
    var descriptionRes: Int? = null
    var icon: ImageHolder? = null
    var iconicsIcon: IIcon? = null
    var onClickListener: View.OnClickListener? = null

    fun withId(id: Int): PrimaryItem {
        this.id = id
        return this
    }

    fun withTitle(title: CharSequence): PrimaryItem {
        this.title = title
        this.titleRes = null
        return this
    }
    fun withTitle(@StringRes title: Int): PrimaryItem {
        this.title = null
        this.titleRes = title
        return this
    }

    fun withDescription(description: CharSequence): PrimaryItem {
        this.description = description
        this.descriptionRes = null
        return this
    }
    fun withDescription(@StringRes description: Int): PrimaryItem {
        this.description = null
        this.descriptionRes = description
        return this
    }

    fun withIcon(icon: Drawable): PrimaryItem {
        this.icon = ImageHolder(icon)
        return this
    }
    fun withIcon(@DrawableRes icon: Int): PrimaryItem {
        this.icon = ImageHolder(icon)
        return this
    }
    fun withIcon(icon: IIcon): PrimaryItem {
        this.iconicsIcon = icon
        return this
    }

    fun withOnClickListener(onClickListener: View.OnClickListener): PrimaryItem {
        this.onClickListener = onClickListener
        return this
    }
}