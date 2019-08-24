package pl.szczodrzynski.navlib

import android.content.Context
import android.system.Os.close
import android.util.AttributeSet
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import com.mikepenz.materialdrawer.Drawer
import pl.szczodrzynski.navlib.bottomsheet.NavBottomSheet

class NavBottomBar : BottomAppBar {
    constructor(context: Context) : super(context) {
        create(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        create(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        create(attrs, defStyle)
    }

    var drawer: Drawer? = null
    var bottomSheet: NavBottomSheet? = null
    var fabView: FloatingActionButton? = null
    var fabExtendedView: ExtendedFloatingActionButton? = null

    /**
     * Shows the BottomAppBar and sets the contentView's margin to be
     * above the BottomAppBar.
     */
    var enable = true
        set(value) {
            field = value
            visibility = if (value) View.VISIBLE else View.GONE
            setFabParams()
        }
    /**
     * Whether the FAB should be visible.
     */
    var fabEnable = true
        set(value) {
            field = value
            setFabVisibility()
        }
    /**
     * Whether an ExtendableFloatingActionButton should be used
     * instead of a normal FloatingActionButton.
     * Note that the extendable button does not support end alignment/gravity
     * when used together with the bottom app bar.
     */
    var fabExtendable = true
        set(value) {
            field = value
            setFabParams()
        }
    /**
     * If BottomAppBar is enabled, sets its fabAlignmentMode.
     * Else, sets the actual FAB's gravity.
     */
    var fabGravity = Gravity.CENTER
        set(value) {
            field = value
            setFabParams()
        }
    /**
     * Whether the FAB should be extended and its text visible.
     */
    var fabExtended = false
        set(value) {
            field = value
            if (fabExtended)
                fabExtendedView?.extend()
            else
                fabExtendedView?.shrink()
        }
    /**
     * Set the FAB's icon.
     */
    var fabIcon: IIcon = CommunityMaterial.Icon.cmd_android
        set(value) {
            field = value
            fabView?.setImageDrawable(IconicsDrawable(context, value).colorInt(R.attr.colorFabIcon).sizeDp(24))
            fabExtendedView?.icon = IconicsDrawable(context, value).colorInt(R.attr.colorFabIcon).sizeDp(24)
        }
    /**
     * Set the ExtendedFAB's text.
     */
    var fabExtendedText
        get() = fabExtendedView?.text
        set(value) {
            fabExtendedView?.text = value
        }

    private fun create(attrs: AttributeSet?, defStyle: Int) {
        setOnTouchListener { _, event ->
            if (bottomSheet?.enable != true || bottomSheet?.enableDragToOpen != true)
                return@setOnTouchListener false
            bottomSheet?.dispatchBottomBarEvent(event)
            true
        }

        elevation = 0f

        navigationIcon = IconicsDrawable(context)
            .icon(CommunityMaterial.Icon2.cmd_menu)
            .sizeDp(20)
            .colorInt(getColorFromAttr(context, R.attr.colorOnPrimary))

        menu.add(0, -1, 0, "Menu")
            .setIcon(
                IconicsDrawable(context)
                    .icon(CommunityMaterial.Icon.cmd_dots_vertical)
                    .sizeDp(20)
                    .colorInt(getColorFromAttr(context, R.attr.colorOnPrimary)))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        setNavigationOnClickListener {
            if (drawer?.isDrawerOpen == true)
                drawer?.closeDrawer()
            else
                drawer?.openDrawer()
        }

        super.setOnMenuItemClickListener {
            if (it.itemId == -1 && bottomSheet?.enable == true) {
                bottomSheet?.toggle()
            }
            else {
                onMenuItemClickListener?.onMenuItemClick(it)
            }
            true
        }
    }

    private fun setFabParams() {
        val layoutParams =
            ((if (fabExtendable) fabExtendedView?.layoutParams else fabView?.layoutParams) ?: return) as CoordinatorLayout.LayoutParams

        if (enable) {
            layoutParams.anchorId = this.id
            if (fabExtendable)
                layoutParams.anchorGravity = if (fabExtendable) fabGravity or Gravity.TOP else Gravity.NO_GRAVITY
            layoutParams.gravity = Gravity.NO_GRAVITY
        }
        else {
            layoutParams.anchorId = View.NO_ID
            if (fabExtendable)
                layoutParams.anchorGravity = Gravity.NO_GRAVITY
            layoutParams.gravity = fabGravity or Gravity.BOTTOM
        }
        fabAlignmentMode = if (fabGravity == Gravity.END) FAB_ALIGNMENT_MODE_END else FAB_ALIGNMENT_MODE_CENTER
        if (fabExtendable)
            fabExtendedView?.layoutParams = layoutParams
        else
            fabView?.layoutParams = layoutParams
        setFabVisibility()
    }
    private fun setFabVisibility() {
        if (fabEnable && fabExtendable) {
            fabView?.hide()
            fabExtendedView?.show()
        }
        else if (fabEnable) {
            fabView?.show()
            fabExtendedView?.hide()
        }
        else {
            fabView?.hide()
            fabExtendedView?.hide()
        }
    }

    private var onMenuItemClickListener: OnMenuItemClickListener? = null
    override fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        onMenuItemClickListener = listener
    }
}