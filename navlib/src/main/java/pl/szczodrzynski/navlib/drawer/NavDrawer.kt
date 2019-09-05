package pl.szczodrzynski.navlib.drawer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator
import com.mikepenz.materialdrawer.*
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.BaseDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import pl.szczodrzynski.navlib.*
import pl.szczodrzynski.navlib.R
import pl.szczodrzynski.navlib.drawer.items.DrawerPrimaryItem

class NavDrawer(
    val context: Context,
    val drawerContainer: LinearLayout,
    val fixedDrawerContainer: FrameLayout,
    val miniDrawerContainerLandscape: FrameLayout,
    val miniDrawerContainerPortrait: FrameLayout,
    val miniDrawerElevation: View
) {
    companion object {
        private const val DRAWER_MODE_NORMAL = 0
        private const val DRAWER_MODE_MINI = 1
        private const val DRAWER_MODE_FIXED = 2
    }

    private lateinit var activity: Activity
    private val resources: Resources
        get() = context.resources

    internal lateinit var toolbar: NavToolbar
    internal lateinit var bottomBar: NavBottomBar

    private var drawer: Drawer? = null
    private var drawerView: View? = null
    private var accountHeader: AccountHeader? = null
    private var miniDrawer: MiniDrawer? = null
    private var miniDrawerView: View? = null

    private var drawerMode: Int = DRAWER_MODE_NORMAL
    private var selection: Int = -1

    lateinit var badgeStyle: BadgeStyle

    @SuppressLint("ClickableViewAccessibility")
    fun init(activity: Activity) {
        this.activity = activity

        /*badgeStyle = BadgeStyle(
            R.drawable.material_drawer_badge,
            getColorFromAttr(context, R.attr.colorError),
            getColorFromAttr(context, R.attr.colorError),
            getColorFromAttr(context, R.attr.colorOnError)
        )*/

        badgeStyle = BadgeStyle()
            .withTextColor(Color.WHITE)
            .withColorRes(R.color.md_red_700)

        val drawerBuilder = DrawerBuilder()
                .withActivity(activity)
                .withDrawerLayout(R.layout.material_drawer_fits_not)
                .withHasStableIds(true)
                .withItemAnimator(AlphaCrossFadeAnimator())
                .withRootView(drawerContainer)
                .withFullscreen(true)
                .withTranslucentStatusBar(false)
                .withTranslucentNavigationBar(true)
                .withTranslucentNavigationBarProgrammatically(false)
                .withToolbar(bottomBar)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withShowDrawerOnFirstLaunch(true)
                .withShowDrawerUntilDraggedOpened(true)
                .withGenerateMiniDrawer(true /* if it is not showing on screen, clicking items throws an exception */)
                .withOnDrawerListener(object : Drawer.OnDrawerListener {
                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
                    override fun onDrawerOpened(drawerView: View) {
                        drawerOpenedListener?.invoke()
                    }
                    override fun onDrawerClosed(drawerView: View) {
                        drawerClosedListener?.invoke()
                        profileSelectionClose()
                    }
                })
                .withOnDrawerItemClickListener { _, position, drawerItem ->
                    if (drawerItem.identifier.toInt() == selection) {
                        return@withOnDrawerItemClickListener false
                    }
                    when (drawerItemSelectedListener?.invoke(drawerItem.identifier.toInt(), position, drawerItem)) {
                        true -> {
                            when {
                                !drawerItem.isSelectable ->  {
                                    setSelection(selection, false)
                                    return@withOnDrawerItemClickListener false
                                }
                                drawerItem is DrawerPrimaryItem -> toolbar.title = drawerItem.appTitle ?: drawerItem.name?.getText(context) ?: ""
                                drawerItem is BaseDrawerItem<*, *> -> toolbar.title = drawerItem.name?.getText(context) ?: ""
                            }
                            //setSelection(drawerItem.identifier.toInt(), false)
                            return@withOnDrawerItemClickListener false
                        }
                        false -> {
                            setSelection(selection, false)
                            return@withOnDrawerItemClickListener true
                        }
                        else -> {
                            return@withOnDrawerItemClickListener false
                        }
                    }

                }
                .withOnDrawerItemLongClickListener { _, position, drawerItem ->
                    drawerItemLongClickListener?.invoke(drawerItem.identifier.toInt(), position, drawerItem) ?: true
                }


        val accountHeaderBuilder = AccountHeaderBuilder()
            .withActivity(activity)
            .withTranslucentStatusBar(true)
            .withOnAccountHeaderListener { view, profile, current ->
                if (profile is ProfileSettingDrawerItem) {
                    return@withOnAccountHeaderListener drawerProfileSettingClickListener?.invoke(profile.identifier.toInt(), view) ?: false
                }
                updateBadges()
                if (current) {
                    close()
                    profileSelectionClose()
                    return@withOnAccountHeaderListener true
                }
                (drawerProfileSelectedListener?.invoke(profile.identifier.toInt(), profile, current, view) ?: false).also {
                    setToolbarProfileImage(profileList.singleOrNull { it.id == profile.identifier.toInt() })
                }
            }
            .withOnAccountHeaderItemLongClickListener { view, profile, current ->
                if (profile is ProfileSettingDrawerItem) {
                    return@withOnAccountHeaderItemLongClickListener drawerProfileSettingLongClickListener?.invoke(profile.identifier.toInt(), view) ?: true
                }
                drawerProfileLongClickListener?.invoke(profile.identifier.toInt(), profile, current, view) ?: false
            }
            .withOnAccountHeaderProfileImageListener(
                onClick = { view, profile, current ->
                    drawerProfileImageClickListener?.invoke(profile.identifier.toInt(), profile, current, view) ?: false
                },
                onLongClick = { view, profile, current ->
                    drawerProfileImageLongClickListener?.invoke(profile.identifier.toInt(), profile, current, view) ?: false
                }
            )
            .withHeaderBackground(R.drawable.header)
            .withTextColor(ContextCompat.getColor(context, R.color.material_drawer_dark_primary_text))

        accountHeader = accountHeaderBuilder.build()
        drawerBuilder.withAccountHeader(accountHeader!!)
        drawer = drawerBuilder.build()

        drawerView = drawer?.slider

        miniDrawer = drawer?.miniDrawer
        miniDrawer?.withOnMiniDrawerItemClickListener { _, _, _, type ->
            if (type == MiniDrawer.PROFILE) {
                profileSelectionOpen()
                open()
                return@withOnMiniDrawerItemClickListener true
            }
            return@withOnMiniDrawerItemClickListener false
        }
        miniDrawer?.withIncludeSecondaryDrawerItems(false)

        // TODO 2019-08-27 build miniDrawerView only if needed
        // building in decideDrawerMode causes an exception when clicking drawer items
        // also update method updateMiniDrawer...
        miniDrawerView = miniDrawer?.build(context)
        updateMiniDrawer()

        toolbar.profileImageClickListener = {
            profileSelectionOpen()
            open()
        }

        val configuration = context.resources.configuration
        decideDrawerMode(
            configuration.orientation,
            configuration.screenWidthDp,
            configuration.screenHeightDp
        )
    }

    /*    _____ _
         |_   _| |
           | | | |_ ___ _ __ ___  ___
           | | | __/ _ \ '_ ` _ \/ __|
          _| |_| ||  __/ | | | | \__ \
         |_____|\__\___|_| |_| |_|__*/
    operator fun plusAssign(item: IDrawerItem<*>) {
        appendItem(item)
    }
    fun appendItem(item: IDrawerItem<*>) {
        drawer?.addItem(item)
        updateMiniDrawer()
    }
    fun appendItems(vararg items: IDrawerItem<*>) {
        drawer?.addItems(*items)
        updateMiniDrawer()
    }
    fun prependItem(item: IDrawerItem<*>) {
        drawer?.addItemAtPosition(item, 0)
        updateMiniDrawer()
    }
    fun prependItems(vararg items: IDrawerItem<*>) {
        drawer?.addItemsAtPosition(0, *items)
        updateMiniDrawer()
    }
    fun addItemAt(index: Int, item: IDrawerItem<*>) {
        drawer?.addItemAtPosition(item, index)
        updateMiniDrawer()
    }
    fun addItemsAt(index: Int, vararg items: IDrawerItem<*>) {
        drawer?.addItemsAtPosition(index, *items)
        updateMiniDrawer()
    }
    fun removeItemById(id: Int) {
        drawer?.removeItem(id.toLong())
        updateMiniDrawer()
    }
    fun removeItemAt(index: Int) {
        drawer?.removeItemByPosition(index)
        updateMiniDrawer()
    }
    fun removeAllItems() {
        drawer?.removeAllItems()
        updateMiniDrawer()
    }

    fun getItemById(id: Int, run: (it: IDrawerItem<*>?) -> Unit) {
        drawer?.getDrawerItem(id.toLong()).also {
            run(it)
            if (it != null)
                drawer?.updateItem(it)
            updateMiniDrawer()
        }
    }
    fun getItemByIndex(index: Int, run: (it: IDrawerItem<*>?) -> Unit) {
        drawer?.drawerItems?.getOrNull(index).also {
            run(it)
            if (it != null)
                drawer?.updateItem(it)
            updateMiniDrawer()
        }
    }

    fun setItems(vararg items: IDrawerItem<*>) {
        drawer?.removeAllItems()
        drawer?.addItems(*items)
        updateMiniDrawer()
    }

    /*    _____      _            _                        _   _               _
         |  __ \    (_)          | |                      | | | |             | |
         | |__) | __ ___   ____ _| |_ ___   _ __ ___   ___| |_| |__   ___   __| |___
         |  ___/ '__| \ \ / / _` | __/ _ \ | '_ ` _ \ / _ \ __| '_ \ / _ \ / _` / __|
         | |   | |  | |\ V / (_| | ||  __/ | | | | | |  __/ |_| | | | (_) | (_| \__ \
         |_|   |_|  |_| \_/ \__,_|\__\___| |_| |_| |_|\___|\__|_| |_|\___/ \__,_|__*/
    private fun drawerSetDragMargin(size: Float) {
        val mDrawerLayout = drawer?.drawerLayout
        val mDragger = mDrawerLayout?.javaClass?.getDeclaredField(
            "mLeftDragger"
        )//mRightDragger for right obviously
        mDragger?.isAccessible = true
        val draggerObj = mDragger?.get(mDrawerLayout) as ViewDragHelper?

        val mEdgeSize = draggerObj?.javaClass?.getDeclaredField(
            "mEdgeSize"
        )
        mEdgeSize?.isAccessible = true

        mEdgeSize?.setInt(
            draggerObj,
            size.toInt()
        ) //optimal value as for me, you may set any constant in dp
    }

    internal fun decideDrawerMode(orientation: Int, widthDp: Int, heightDp: Int) {
        Log.d("NavLib", "Deciding drawer mode:")
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (fixedDrawerContainer.childCount > 0) {
                fixedDrawerContainer.removeAllViews()
            }
            Log.d("NavLib", "- fixed container disabled")
            drawer?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            if (drawer?.drawerLayout?.indexOfChild(drawerView) == -1) {
                drawer?.drawerLayout?.addView(drawerView)
            }
            Log.d("NavLib", "- slider enabled")

            if (miniDrawerContainerLandscape.childCount > 0) {
                miniDrawerContainerLandscape.removeAllViews()
            }
            Log.d("NavLib", "- mini drawer land disabled")

            if (widthDp >= 480) {
                if (miniDrawerView == null)
                    miniDrawerView = miniDrawer?.build(context)
                if (miniDrawerContainerPortrait.indexOfChild(miniDrawerView) == -1)
                    miniDrawerContainerPortrait.addView(miniDrawerView)
                Log.d("NavLib", "- mini drawer port enabled")
                drawerSetDragMargin(72 * resources.displayMetrics.density)
                drawerMode = DRAWER_MODE_MINI
                updateMiniDrawer()
            }
            else {
                if (miniDrawerContainerPortrait.childCount > 0) {
                    miniDrawerContainerPortrait.removeAllViews()
                }
                Log.d("NavLib", "- mini drawer port disabled")
                drawerSetDragMargin(20 * resources.displayMetrics.density)
                drawerMode = DRAWER_MODE_NORMAL
            }
        }
        else {
            if (miniDrawerContainerPortrait.childCount > 0) {
                miniDrawerContainerPortrait.removeAllViews()
            }
            Log.d("NavLib", "- mini drawer port disabled")

            if (widthDp in 480 until 9000) {
                if (miniDrawerView == null)
                    miniDrawerView = miniDrawer?.build(context)
                if (miniDrawerContainerLandscape.indexOfChild(miniDrawerView) == -1)
                    miniDrawerContainerLandscape.addView(miniDrawerView)
                Log.d("NavLib", "- mini drawer land enabled")
                drawerSetDragMargin(72 * resources.displayMetrics.density)
                drawerMode = DRAWER_MODE_MINI
                updateMiniDrawer()
            }
            else {
                if (miniDrawerContainerLandscape.childCount > 0) {
                    miniDrawerContainerLandscape.removeAllViews()
                }
                Log.d("NavLib", "- mini drawer land disabled")
                drawerSetDragMargin(20 * resources.displayMetrics.density)
                drawerMode = DRAWER_MODE_NORMAL
            }
            if (widthDp >= 9000) {
                // screen is big enough to show fixed drawer
                if (drawer?.drawerLayout?.indexOfChild(drawerView) != -1) {
                    // remove from slider
                    drawer?.drawerLayout?.removeView(drawerView)
                }
                // lock the slider
                drawer?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                Log.d("NavLib", "- slider disabled")
                // add to fixed container
                if (fixedDrawerContainer.indexOfChild(drawerView) == -1)
                    fixedDrawerContainer.addView(drawerView)
                Log.d("NavLib", "- fixed container enabled")
                drawerMode = DRAWER_MODE_FIXED
            }
            else {
                // screen is too small for the fixed drawer
                if (fixedDrawerContainer.childCount > 0) {
                    // remove from fixed container
                    fixedDrawerContainer.removeAllViews()
                }
                Log.d("NavLib", "- fixed container disabled")
                // unlock the slider
                drawer?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                if (drawer?.drawerLayout?.indexOfChild(drawerView) == -1) {
                    // add to slider
                    drawer?.drawerLayout?.addView(drawerView)
                }
                Log.d("NavLib", "- slider enabled")
            }
        }

        miniDrawerElevation.visibility = if (drawerMode == DRAWER_MODE_MINI) View.VISIBLE else View.GONE
    }

    private fun updateMiniDrawer() {
        selection = drawer?.currentSelection?.toInt() ?: -1
        //if (drawerMode == DRAWER_MODE_MINI)
            miniDrawer?.createItems()
    }

    /*    _____       _     _ _                       _   _               _
         |  __ \     | |   | (_)                     | | | |             | |
         | |__) |   _| |__ | |_  ___   _ __ ___   ___| |_| |__   ___   __| |___
         |  ___/ | | | '_ \| | |/ __| | '_ ` _ \ / _ \ __| '_ \ / _ \ / _` / __|
         | |   | |_| | |_) | | | (__  | | | | | |  __/ |_| | | | (_) | (_| \__ \
         |_|    \__,_|_.__/|_|_|\___| |_| |_| |_|\___|\__|_| |_|\___/ \__,_|__*/
    var isOpen
        get() = drawer?.isDrawerOpen ?: false || drawerMode == DRAWER_MODE_FIXED
        set(value) {
            if (drawerMode == DRAWER_MODE_FIXED)
                return
            if (value && !isOpen) drawer?.openDrawer() else if (!value && isOpen) drawer?.closeDrawer()
        }
    fun open() { isOpen = true }
    fun close() { isOpen = false }
    fun toggle() {
        if (drawer == null)
            return
        isOpen = !isOpen
    }

    var profileSelectionIsOpen
        get() = accountHeader?.isSelectionListShown == true
        set(value) {
            if (value != profileSelectionIsOpen)
                profileSelectionToggle()
        }
    fun profileSelectionOpen() { profileSelectionIsOpen = true }
    fun profileSelectionClose() { profileSelectionIsOpen = false }
    fun profileSelectionToggle() {
        accountHeader?.let {
            it.toggleSelectionList(it.view.context)
        }
    }

    var drawerOpenedListener: (() -> Unit)? = null
    var drawerClosedListener: (() -> Unit)? = null
    var drawerItemSelectedListener: ((id: Int, position: Int, drawerItem: IDrawerItem<*>) -> Boolean)? = null
    var drawerItemLongClickListener: ((id: Int, position: Int, drawerItem: IDrawerItem<*>) -> Boolean)? = null
    var drawerProfileSelectedListener: ((id: Int, profile: IProfile<*>, current: Boolean, view: View?) -> Boolean)? = null
    var drawerProfileLongClickListener: ((id: Int, profile: IProfile<*>, current: Boolean, view: View) -> Boolean)? = null
    var drawerProfileImageClickListener: ((id: Int, profile: IProfile<*>, current: Boolean, view: View) -> Boolean)? = null
    var drawerProfileImageLongClickListener: ((id: Int, profile: IProfile<*>, current: Boolean, view: View) -> Boolean)? = null
    var drawerProfileListEmptyListener: (() -> Unit)? = null
    var drawerProfileSettingClickListener: ((id: Int, view: View?) -> Boolean)? = null
    var drawerProfileSettingLongClickListener: ((id: Int, view: View) -> Boolean)? = null

    fun miniDrawerEnabled(): Boolean = drawerMode == DRAWER_MODE_MINI
    fun fixedDrawerEnabled(): Boolean = drawerMode == DRAWER_MODE_FIXED

    fun setSelection(id: Int, fireOnClick: Boolean = true) {
        Log.d("NavDebug", "setSelection(id = $id, fireOnClick = $fireOnClick)")
        // seems that this cannot be open, because the itemAdapter has Profile items
        // instead of normal Drawer items...
        profileSelectionClose()
        selection = id

        if (drawer?.currentSelection != id.toLong()) {

        }

        if (drawer?.currentSelection != id.toLong() || !fireOnClick)
            drawer?.setSelection(id.toLong(), fireOnClick)

        if (drawerMode == DRAWER_MODE_MINI)
            miniDrawer?.setSelection(id.toLong())
    }
    fun getSelection(): Int = selection

    // TODO 2019-08-27 add methods for Drawable, @DrawableRes
    fun setAccountHeaderBackground(path: String?) {
        if (path == null) {
            accountHeader?.setBackgroundRes(R.drawable.header)
            return
        }
        accountHeader?.setHeaderBackground(ImageHolder(path))
    }

    /*    _____            __ _ _
         |  __ \          / _(_) |
         | |__) | __ ___ | |_ _| | ___  ___
         |  ___/ '__/ _ \|  _| | |/ _ \/ __|
         | |   | | | (_) | | | | |  __/\__ \
         |_|   |_|  \___/|_| |_|_|\___||__*/
    private var profileList: MutableList<IDrawerProfile> = mutableListOf()

    fun addProfileSettings(vararg items: ProfileSettingDrawerItem) {
        accountHeader?.profiles?.addAll(items)
    }

    private fun updateProfileList() {
        // remove all profile items
        val profiles = accountHeader?.profiles?.filterNot { it is ProfileDrawerItem } as MutableList<IProfile<*>>?

        if (profileList.isEmpty())
            drawerProfileListEmptyListener?.invoke()

        profileList.forEachIndexed { index, profile ->
            val image = profile.getImageHolder(context)
            ProfileDrawerItem()
                .withIdentifier(profile.id.toLong())
                .withName(profile.name)
                .withEmail(profile.subname)
                .also { it.icon = image }
                .withNameShown(true)
                .also { profiles?.add(index, it) }
        }

        accountHeader?.profiles = profiles

        updateMiniDrawer()
    }

    fun setProfileList(profiles: MutableList<out IDrawerProfile>) {
        profileList = profiles as MutableList<IDrawerProfile>
        updateProfileList()
    }
    val profileListEmpty: Boolean
        get() = profileList.isEmpty()
    var currentProfile: Int
        get() = accountHeader?.activeProfile?.identifier?.toInt() ?: -1
        set(value) {
            Log.d("NavDebug", "currentProfile = $value")
            accountHeader?.setActiveProfile(value.toLong(), false)
            setToolbarProfileImage(profileList.singleOrNull { it.id == value })
            updateBadges()
        }
    fun appendProfile(profile: IDrawerProfile) {
        profileList.add(profile)
        updateProfileList()
    }
    fun appendProfiles(vararg profiles: IDrawerProfile) {
        profileList.addAll(profiles)
        updateProfileList()
    }
    fun prependProfile(profile: IDrawerProfile) {
        profileList.add(0, profile)
        updateProfileList()
    }
    fun prependProfiles(vararg profiles: IDrawerProfile) {
        profileList.addAll(0, profiles.asList())
        updateProfileList()
    }
    fun addProfileAt(index: Int, profile: IDrawerProfile) {
        profileList.add(index, profile)
        updateProfileList()
    }
    fun addProfilesAt(index: Int, vararg profiles: IDrawerProfile) {
        profileList.addAll(index, profiles.asList())
        updateProfileList()
    }
    fun removeProfileById(id: Int) {
        profileList = profileList.filterNot { it.id == id }.toMutableList()
        updateProfileList()
    }
    fun removeProfileAt(index: Int) {
        profileList.removeAt(index)
        updateProfileList()
    }
    fun removeAllProfile() {
        profileList.clear()
        updateProfileList()
    }
    fun removeAllProfileSettings() {
        accountHeader?.profiles = accountHeader?.profiles?.filterNot { it is ProfileSettingDrawerItem }?.toMutableList()
    }

    fun getProfileById(id: Int, run: (it: IDrawerProfile?) -> Unit) {
        profileList.singleOrNull { it.id == id }.also {
            run(it)
            updateProfileList()
        }
    }
    fun getProfileByIndex(index: Int, run: (it: IDrawerProfile?) -> Unit) {
        profileList.getOrNull(index).also {
            run(it)
            updateProfileList()
        }
    }

    private fun setToolbarProfileImage(profile: IDrawerProfile?) {
        toolbar.profileImage = profile?.getImageDrawable(context)
    }


    /*    ____            _
         |  _ \          | |
         | |_) | __ _  __| | __ _  ___  ___
         |  _ < / _` |/ _` |/ _` |/ _ \/ __|
         | |_) | (_| | (_| | (_| |  __/\__ \
         |____/ \__,_|\__,_|\__, |\___||___/
                             __/ |
                            |__*/
    private var unreadCounterList: MutableList<IUnreadCounter> = mutableListOf()
    private val unreadCounterTypeMap = mutableMapOf<Int, Int>()

    fun updateBadges() {
        Log.d("NavDebug", "updateBadges()")
        unreadCounterList.map {
            it.drawerItemId = unreadCounterTypeMap[it.type]
        }
        unreadCounterList.forEach {
            if (it.drawerItemId == null)
                return@forEach
            if (it.profileId != currentProfile) {
                Log.d("NavDebug", "- Remove badge for ${it.drawerItemId}")
                drawer?.updateBadge(it.drawerItemId?.toLong() ?: 0, null)
                return@forEach
            }
            Log.d("NavDebug", "- Set badge ${it.count} for ${it.drawerItemId}")
            drawer?.updateBadge(
                it.drawerItemId?.toLong() ?: 0,
                when {
                    it.count == 0 -> null
                    it.count >= 99 -> StringHolder("99+")
                    else -> StringHolder(it.count.toString())
                }
            )
        }
        updateMiniDrawer()
    }

    fun setUnreadCounterList(unreadCounterList: MutableList<out IUnreadCounter>) {
        this.unreadCounterList = unreadCounterList as MutableList<IUnreadCounter>
        updateBadges()
    }

    fun addUnreadCounterType(type: Int, drawerItem: Int) {
        unreadCounterTypeMap[type] = drawerItem
    }

    data class UnreadCounter(
            override var profileId: Int,
            override var type: Int,
            override var drawerItemId: Int?,
            override var count: Int
    ) : IUnreadCounter

    fun setUnreadCount(profileId: Int, type: Int, count: Int) {
        val item = unreadCounterList.singleOrNull {
            it.type == type && it.profileId == profileId
        }
        if (item != null) {
            item.count = count
            updateBadges()
        }
        else {
            unreadCounterList.add(UnreadCounter(profileId, type, null, count))
        }
    }
}