package pl.szczodrzynski.navlib.bottomsheet

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import kotlinx.android.synthetic.main.nav_bottom_sheet.view.*
import pl.szczodrzynski.navlib.Anim
import pl.szczodrzynski.navlib.bottomsheet.items.EditTextFilledItem
import pl.szczodrzynski.navlib.bottomsheet.items.IBottomSheetItem
import pl.szczodrzynski.navlib.bottomsheet.items.PrimaryItem
import pl.szczodrzynski.navlib.bottomsheet.items.SeparatorItem
import pl.szczodrzynski.navlib.bottomsheet.listeners.OnItemInputListener
import pl.szczodrzynski.navlib.getColorFromAttr


class NavBottomSheet : CoordinatorLayout {
    constructor(context: Context) : super(context) {
        create(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        create(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        create(attrs, defStyle)
    }

    private lateinit var scrimView: View
    private lateinit var bottomSheet: NestedScrollView
    private lateinit var bottomSheetContent: LinearLayout
    private lateinit var bottomSheetDragBar: View

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var bottomSheetVisible = false

    /**
     * Enable the bottom sheet.
     * This value is mostly relevant to the [pl.szczodrzynski.navlib.NavBottomBar].
     */
    var enable = true
        set(value) {
            field = value
            if (!value && bottomSheetVisible)
                close()
        }
    /**
     * Whether the [pl.szczodrzynski.navlib.NavBottomBar] should open this BottomSheet
     * when the user drags the bottom bar.
     */
    var enableDragToOpen = true

    /**
     * Control the scrim view visibility, shown when BottomSheet
     * is expanded.
     */
    var scrimViewEnabled = true
        set(value) {
            scrimView.visibility = if (value) View.INVISIBLE else View.GONE // INVISIBLE
            field = value
        }
    /**
     * Whether tapping the Scrim view should hide the BottomSheet.
     */
    var scrimViewTapToClose = true


    fun hideKeyboard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }


    private fun create(attrs: AttributeSet?, defStyle: Int) {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(pl.szczodrzynski.navlib.R.layout.nav_bottom_sheet, this)

        scrimView = findViewById(pl.szczodrzynski.navlib.R.id.nv_scrim)
        bottomSheet = findViewById(pl.szczodrzynski.navlib.R.id.nv_bottomSheetView)
        bottomSheetContent = findViewById(pl.szczodrzynski.navlib.R.id.nv_bottomSheetContent)
        bottomSheetDragBar = findViewById(pl.szczodrzynski.navlib.R.id.nv_bottomSheetDragBar)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        scrimView.setOnTouchListener { _, event ->
            if (!scrimViewTapToClose)
                return@setOnTouchListener true
            if (event.action == MotionEvent.ACTION_UP && bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            true
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(v: View, p1: Float) {}
            override fun onStateChanged(v: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN && bottomSheetVisible) {
                    bottomSheetVisible = false
                    bottomSheet.scrollTo(0, 0)
                    if (scrimViewEnabled)
                        Anim.fadeOut(scrimView, 300, null)
                    // steal the focus from any EditTexts
                    bottomSheetDragBar.requestFocus()
                    hideKeyboard()
                }
                else if (!bottomSheetVisible) {
                    bottomSheetVisible = true
                    if (scrimViewEnabled)
                        Anim.fadeIn(scrimView, 300, null)
                }
            }
        })

        bottomSheetContent.background.colorFilter = PorterDuffColorFilter(
            getColorFromAttr(
                context,
                pl.szczodrzynski.navlib.R.attr.colorBackgroundFloating
            ), PorterDuff.Mode.SRC_ATOP)

        // steal the focus from any EditTexts
        bottomSheetDragBar.requestFocus()

        val items = ArrayList<IBottomSheetItem<*>>()

        items += EditTextFilledItem(true).apply {
            id = 0
            hint = "Search"
            helperText = "0 results found"
            onItemInputListener = object : OnItemInputListener {
                override fun onItemInput(itemId: Int, item: EditTextFilledItem, text: CharSequence) {
                    item.helperText = "${text.length} results found"
                    bs_list.adapter?.notifyItemChanged(itemId)
                }
            }
        }

        items += PrimaryItem(true)
            .withId(1)
            .withTitle("Compose")
            .withIcon(CommunityMaterial.Icon2.cmd_pencil)
            .withOnClickListener(OnClickListener {
                Toast.makeText(context, "Compose message", Toast.LENGTH_SHORT).show()
            })
        // TODO add separator item
        items += SeparatorItem(true)
        items += PrimaryItem(true)
            .withId(3)
            .withTitle("Synchronise")
            .withIcon(CommunityMaterial.Icon2.cmd_sync)
            .withOnClickListener(OnClickListener {
                Toast.makeText(context, "Synchronising...", Toast.LENGTH_SHORT).show()
            })
        items += PrimaryItem(true)
            .withId(4)
            .withTitle("Help")
            .withIcon(CommunityMaterial.Icon2.cmd_help)
            .withOnClickListener(OnClickListener {
                Toast.makeText(context, "Want some help?", Toast.LENGTH_SHORT).show()
            })
        bs_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = BottomSheetAdapter(items)
        }
    }

    fun dispatchBottomBarEvent(event: MotionEvent) {
        val location = IntArray(2)
        bottomSheet.getLocationOnScreen(location)
        event.setLocation(event.rawX - location[0], event.rawY - location[1])
        bottomSheet.dispatchTouchEvent(event)
    }

    fun setContentPadding(left: Int, top: Int, right: Int, bottom: Int) {
        bottomSheetContent.setPadding(left, top, right, bottom)
    }
    fun getContentView() = bottomSheetContent

    var isOpen
        get() = bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
        set(value) {
            bottomSheetBehavior.state = if (value) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN
        }
    fun open() { isOpen = true }
    fun close() { isOpen = true }
    fun toggle() {
        if (!enable)
            return
        isOpen = !isOpen
    }
}