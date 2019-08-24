package pl.szczodrzynski.navlib

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import kotlinx.android.synthetic.main.nav_view.view.*
import pl.szczodrzynski.navlib.bottomsheet.NavBottomSheet

class NavView : FrameLayout {

    private var contentView: LinearLayout? = null

    private lateinit var statusBarBackground: View
    private lateinit var navigationBarBackground: View
    private lateinit var mainView: CoordinatorLayout
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var extendedFloatingActionButton: ExtendedFloatingActionButton

    var drawer: Drawer? = null
    lateinit var topBar: NavToolbar
    lateinit var bottomBar: NavBottomBar
    lateinit var bottomSheet: NavBottomSheet



    constructor(context: Context) : super(context) {
        create(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        create(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        create(attrs, defStyle)
    }

    private fun create(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.NavView, defStyle, 0)
        /*_exampleString = a.getString(
            R.styleable.NavView_exampleString
        )*/
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.nav_view, this)

        contentView = findViewById<LinearLayout>(R.id.nv_content)

        statusBarBackground = findViewById(R.id.nv_statusBarBackground)
        navigationBarBackground = findViewById(R.id.nv_navigationBarBackground)
        mainView = findViewById(R.id.nv_main)
        floatingActionButton = findViewById(R.id.nv_floatingActionButton)
        extendedFloatingActionButton = findViewById(R.id.nv_extendedFloatingActionButton)

        topBar = findViewById(R.id.nv_toolbar)
        bottomBar = findViewById(R.id.nv_bottomBar)
        bottomSheet = findViewById(R.id.nv_bottomSheet)

        bottomBar.drawer = drawer
        bottomBar.bottomSheet = bottomSheet
        bottomBar.fabView = floatingActionButton
        bottomBar.fabExtendedView = extendedFloatingActionButton

        //bottomSheetBehavior.peekHeight = displayHeight

        nv_elevation.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = "Set toolbar elevation ${progress}dp"
                nv_toolbar.elevation = progress * context.resources.displayMetrics.density
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun configSystemBarsUtil(systemBarsUtil: SystemBarsUtil) {
        systemBarsUtil.statusBarBgView = statusBarBackground
        systemBarsUtil.navigationBarBgView = navigationBarBackground
        systemBarsUtil.statusBarDarkView = nv_statusBarDarker
        //systemBarsUtil.navigationBarDarkView = navigationBarBackground
        systemBarsUtil.paddingBySystemBars = mainView
        systemBarsUtil.paddingByNavigationBar = bottomSheet.getContentView()
    }


    var enableBottomSheet = true
    var enableBottomSheetDrag = true

    var bottomBarEnable = true
        get() = bottomBar.enable
        set(value) {
            field = value
            bottomBar.enable = value
            setContentMargins() // TODO combine bottomBarEnable and bottomBar.enable
        }

    /**
     * Shows the toolbar and sets the contentView's margin to be
     * below the toolbar.
     */
    var showToolbar = true; set(value) {
        topBar.visibility = if (value) View.VISIBLE else View.GONE
        field = value
        setContentMargins()
    }

    /**
     * Set the FAB's on click listener
     */
    fun setFabOnClickListener(onClickListener: OnClickListener) {
        floatingActionButton.setOnClickListener(onClickListener)
        extendedFloatingActionButton.setOnClickListener(onClickListener)
    }




    @SuppressLint("ClickableViewAccessibility")
    fun init(activity: Activity) {

    }

    private fun setContentMargins() {
        val layoutParams = CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        val actionBarSize = 56 * context.resources.displayMetrics.density
        layoutParams.topMargin = if (showToolbar) actionBarSize.toInt() else 0
        layoutParams.bottomMargin = if (bottomBarEnable) actionBarSize.toInt() else 0
        contentView?.layoutParams = layoutParams
    }

    fun addDrawer(activity: Activity) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        val item1 = PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(CommunityMaterial.Icon.cmd_google_home)
        val item2 = SecondaryDrawerItem().withIdentifier(2).withName("Settings").withIcon(CommunityMaterial.Icon2.cmd_settings)

        drawer = DrawerBuilder(activity)
            .withDrawerLayout(R.layout.material_drawer_fits_not)
            .withRootView(R.id.nv_drawerContainer)
            .withFullscreen(true)
            .withTranslucentStatusBar(false)
            .withTranslucentNavigationBar(false)
            .withTranslucentNavigationBarProgrammatically(false)
            .withToolbar(bottomBar)
            .withDisplayBelowStatusBar(true)
            .withActionBarDrawerToggleAnimated(true)
            .addDrawerItems(
                item1,
                DividerDrawerItem(),
                item2,
                SecondaryDrawerItem().withName("Settings")
            )
            /*.withOnDrawerItemClickListener { view, position, drawerItem ->
                true
            }*/
            .build()

        bottomBar.drawer = drawer
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (contentView == null) {
            super.addView(child, index, params)
        }
        else {
            contentView!!.addView(child, index, params)
        }
    }
}
