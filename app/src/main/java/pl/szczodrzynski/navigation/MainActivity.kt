package pl.szczodrzynski.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.iconics.Iconics
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.paddingDp
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.model.utils.withIsHiddenInMiniDrawer
import kotlinx.android.synthetic.main.sample_nav_view.*
import pl.szczodrzynski.navlib.SystemBarsUtil
import pl.szczodrzynski.navlib.SystemBarsUtil.Companion.COLOR_DO_NOT_CHANGE
import pl.szczodrzynski.navlib.SystemBarsUtil.Companion.COLOR_HALF_TRANSPARENT
import pl.szczodrzynski.navlib.SystemBarsUtil.Companion.COLOR_PRIMARY_DARK
import pl.szczodrzynski.navlib.bottomsheet.NavBottomSheet
import pl.szczodrzynski.navlib.bottomsheet.NavBottomSheet.Companion.SORT_MODE_ASCENDING
import pl.szczodrzynski.navlib.bottomsheet.NavBottomSheet.Companion.SORT_MODE_DESCENDING
import pl.szczodrzynski.navlib.bottomsheet.NavBottomSheet.Companion.TOGGLE_GROUP_SORTING_ORDER
import pl.szczodrzynski.navlib.bottomsheet.items.BottomSheetPrimaryItem
import pl.szczodrzynski.navlib.bottomsheet.items.BottomSheetSeparatorItem
import pl.szczodrzynski.navlib.colorAttr
import pl.szczodrzynski.navlib.drawer.items.DrawerPrimaryItem
import pl.szczodrzynski.navlib.getColorFromAttr
import pl.szczodrzynski.navlib.withIcon


class MainActivity : AppCompatActivity() {
    companion object {
        var darkTheme: Boolean? = null
    }

    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Iconics.respectFontBoundsDefault = true

        if (darkTheme == null)
            darkTheme = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("darkTheme", false)

        Log.d("MainActivity", "Dark theme $darkTheme")
        setTheme(if (darkTheme == true) R.style.AppTheme else R.style.AppTheme_Light)

        Log.d("NavLib", "ACTIVITY created")

        setContentView(R.layout.sample_nav_view)

        appFullscreen.isChecked = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("appFullscreen", true)
        statusBarDarker.isChecked = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("statusBarDarker", false)
        statusBarTranslucent.isChecked = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("statusBarTranslucent", false)
        navigationBarTransparent.isChecked = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("navigationBarTransparent", false)

        statusBarColor.check(when (getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("statusBarColor", "colorBackground")) {
            "colorPrimaryDark" -> R.id.colorPrimaryDark
            "colorPrimary" -> R.id.colorPrimary
            "colorAccent" -> R.id.colorAccent
            "colorBackground" -> R.id.colorBackground
            else -> R.id.colorBackground
        })
        statusBarFallbackLight.check(when (getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("statusBarFallbackLight", "lightHalfTransparent")) {
            "lightHalfTransparent" -> R.id.lightHalfTransparent
            "lightPrimaryDark" -> R.id.lightPrimaryDark
            "lightDoNotChange" -> R.id.lightDoNotChange
            else -> R.id.lightHalfTransparent
        })
        statusBarFallbackGradient.check(when (getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("statusBarFallbackGradient", "gradientDoNotChange")) {
            "gradientHalfTransparent" -> R.id.gradientHalfTransparent
            "gradientPrimaryDark" -> R.id.gradientPrimaryDark
            "gradientDoNotChange" -> R.id.gradientDoNotChange
            else -> R.id.gradientDoNotChange
        })

        themeButton.setOnClickListener {
            // use commit instead of apply because of recreating the activity
            darkTheme = (darkTheme == false)
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("darkTheme", darkTheme == true).commit()
            recreate()
        }

        //navView.init(this)

        // init the drawer before SystemBarsUtil
        navView.drawer.init(this)

        SystemBarsUtil(this).apply {
            paddingByKeyboard = navView
            appFullscreen = this@MainActivity.appFullscreen.isChecked
            statusBarColor = when (this@MainActivity.statusBarColor.checkedRadioButtonId) {
                R.id.colorPrimaryDark -> COLOR_PRIMARY_DARK
                R.id.colorPrimary -> getColorFromAttr(this@MainActivity, R.attr.colorPrimary)
                R.id.colorAccent -> getColorFromAttr(this@MainActivity, R.attr.colorAccent)
                R.id.colorBackground -> getColorFromAttr(this@MainActivity, android.R.attr.colorBackground)
                else -> 0xffff00ff.toInt()
            }
            statusBarDarker = this@MainActivity.statusBarDarker.isChecked
            statusBarFallbackLight = when (this@MainActivity.statusBarFallbackLight.checkedRadioButtonId) {
                R.id.lightHalfTransparent -> COLOR_HALF_TRANSPARENT
                R.id.lightPrimaryDark -> COLOR_PRIMARY_DARK
                R.id.lightDoNotChange -> COLOR_DO_NOT_CHANGE
                else -> 0xff00ffff.toInt()
            }
            statusBarFallbackGradient = when (this@MainActivity.statusBarFallbackGradient.checkedRadioButtonId) {
                R.id.gradientHalfTransparent -> COLOR_HALF_TRANSPARENT
                R.id.gradientPrimaryDark -> COLOR_PRIMARY_DARK
                R.id.gradientDoNotChange -> COLOR_DO_NOT_CHANGE
                else -> 0xffffff00.toInt()
            }
            statusBarTranslucent = this@MainActivity.statusBarTranslucent.isChecked
            navigationBarTransparent = this@MainActivity.navigationBarTransparent.isChecked

            navView.configSystemBarsUtil(this)

            commit()
        }

        appFullscreen.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("appFullscreen", isChecked).commit()
            recreate()
        }
        statusBarDarker.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("statusBarDarker", isChecked).commit()
            recreate()
        }
        statusBarTranslucent.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("statusBarTranslucent", isChecked).commit()
            recreate()
        }
        navigationBarTransparent.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("navigationBarTransparent", isChecked).commit()
            recreate()
        }

        statusBarColor.setOnCheckedChangeListener { _, checkedId ->
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("statusBarColor",
                when (checkedId) {
                    R.id.colorPrimaryDark -> "colorPrimaryDark"
                    R.id.colorPrimary -> "colorPrimary"
                    R.id.colorAccent -> "colorAccent"
                    R.id.colorBackground -> "colorBackground"
                    else -> "colorBackground"
                }).commit()
            recreate()
        }
        statusBarFallbackLight.setOnCheckedChangeListener { _, checkedId ->
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("statusBarFallbackLight",
                when (checkedId) {
                    R.id.lightHalfTransparent -> "lightHalfTransparent"
                    R.id.lightPrimaryDark -> "lightPrimaryDark"
                    R.id.lightDoNotChange -> "lightDoNotChange"
                    else -> "lightHalfTransparent"
                }).commit()
            recreate()
        }
        statusBarFallbackGradient.setOnCheckedChangeListener { _, checkedId ->
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("statusBarFallbackGradient",
                when (checkedId) {
                    R.id.gradientHalfTransparent -> "gradientHalfTransparent"
                    R.id.gradientPrimaryDark -> "gradientPrimaryDark"
                    R.id.gradientDoNotChange -> "gradientDoNotChange"
                    else -> "gradientDoNotChange"
                }).commit()
            recreate()
        }

        navView.enableBottomSheetDrag = true
        navView.enableBottomSheet = true

        switchToolbar.setOnCheckedChangeListener { _, isChecked ->
            navView.showToolbar = isChecked
        }
        switchBottomAppBar.setOnCheckedChangeListener { _, isChecked ->
            navView.bottomBarEnable = isChecked
        }
        switchFab.setOnCheckedChangeListener { _, isChecked ->
            navView.bottomBar.fabEnable = isChecked
        }
        extendFab.setOnCheckedChangeListener { _, isChecked ->
            navView.bottomBar.fabExtended = isChecked
        }

        navView.setFabOnClickListener(View.OnClickListener {
            Toast.makeText(this, "FAB clicked", Toast.LENGTH_SHORT).show()
        })

        scrimClose.setOnCheckedChangeListener { _, isChecked ->
            navView.bottomSheet.scrimViewTapToClose = isChecked
        }

        scrimEnable.setOnCheckedChangeListener {_, isChecked ->
            navView.bottomSheet.scrimViewEnabled = isChecked
        }

        fabPosition.setOnCheckedChangeListener { _, checkedId ->
            navView.bottomBar.fabGravity = if (checkedId == R.id.fabCenter) Gravity.CENTER else Gravity.END
        }

        extendable.setOnCheckedChangeListener { _, isChecked ->
            navView.bottomBar.fabExtendable = isChecked
        }

        bsEnable.setOnCheckedChangeListener {_, isChecked ->
            navView.bottomSheet.enable = isChecked
        }
        bsDrag.setOnCheckedChangeListener {_, isChecked ->
            navView.bottomSheet.enableDragToOpen = isChecked
        }

        navView.bottomBar.fabIcon = CommunityMaterial.Icon3.cmd_pencil
        navView.bottomBar.fabExtendedText = "Compose"
        navView.bottomBar.fabExtended = false

        rippleButton.setOnClickListener {
            navView.gainAttentionOnBottomBar()
        }

        navView.toolbar.subtitleFormat = R.string.toolbar_subtitle
        navView.toolbar.subtitleFormatWithUnread = R.plurals.toolbar_subtitle_with_unread

        navView.drawer.apply {

            miniDrawerVisiblePortrait = true
            miniDrawerVisibleLandscape = null

            addUnreadCounterType(type = 10, drawerItem = 1)
            addUnreadCounterType(type = 20, drawerItem = 2)
            addUnreadCounterType(type = 30, drawerItem = 60)
            addUnreadCounterType(type = 40, drawerItem = 62)

            appendItems(
                DrawerPrimaryItem()
                    .withAppTitle("Navigation")
                    .withName("Home")
                    .withSelected(true)
                    .withIdentifier(1)
                    .withBadgeStyle(badgeStyle)
                    .withIcon(CommunityMaterial.Icon2.cmd_google_home),

                DrawerPrimaryItem()
                    .withIdentifier(2)
                    .withName("Settings")
                    .withBadgeStyle(badgeStyle)
                    .withIcon(CommunityMaterial.Icon.cmd_cog_outline),

                DrawerPrimaryItem().withName("iOS")
                    .withIdentifier(60)
                    .withBadgeStyle(badgeStyle)
                    .withIcon(CommunityMaterial.Icon.cmd_apple),

                DrawerPrimaryItem().withName("School bell")
                    .withDescription("why not?")
                    .withIdentifier(61)
                    .withBadgeStyle(badgeStyle)
                    .withIcon(CommunityMaterial.Icon.cmd_alarm_bell),

                DrawerPrimaryItem().withName("Lock screen")
                    .withDescription("aaand not visible in Mini Drawer")
                    .withIdentifier(62)
                    .withIsHiddenInMiniDrawer(true)
                    .withBadgeStyle(badgeStyle)
                    .withIcon(CommunityMaterial.Icon2.cmd_fingerprint),

                DrawerPrimaryItem().withName("HDR enable/disable")
                    .withTag(0)
                    .withIdentifier(63)
                    .withBadgeStyle(badgeStyle)
                    .withSelectable(false)
                    .withIcon(CommunityMaterial.Icon2.cmd_hdr),

                DrawerPrimaryItem().withName("AdBlockPlus")
                    .withDescription("Because we all hate ads")
                    .withIdentifier(64)
                    .withBadgeStyle(badgeStyle)
                    .withIcon(CommunityMaterial.Icon2.cmd_google_ads),

                DrawerPrimaryItem().withName("Wonderful browsing experience and this is a long string")
                    .withIdentifier(65)
                    .withBadgeStyle(badgeStyle)
                    .withIcon(CommunityMaterial.Icon3.cmd_microsoft_internet_explorer)
            )

            //setAccountHeaderBackground("/sdcard/ban.gif")

            appendProfiles(
                DrawerProfile(1, "Think Pad", "think with a pad", null),
                DrawerProfile(2, "Phil Swift", "I sawed this boat in half!!!", null),
                DrawerProfile(3, "The meme bay", "Visit my amazing website", null),
                DrawerProfile(4, "Mark Zuckerberg", null, null),
                DrawerProfile(5, "I love GDPR", null, null),
                DrawerProfile(6, "Gandalf", "http://sax.hol.es/", null)
            )

            setUnreadCount(2, 20, 30) // phil swift has 30 unreads on "Settings item"
            setUnreadCount(4, 40, 1000) // mark has 99+ unreads on "Lock screen item"

            addProfileSettings(
                ProfileSettingDrawerItem()
                    .withName("Add Account")
                    .withDescription("Add new GitHub Account")
                    .withIcon(
                        IconicsDrawable(context, CommunityMaterial.Icon3.cmd_plus).apply {
                            actionBar()
                            paddingDp = 5
                            colorAttr(context, R.attr.materialDrawerPrimaryText)
                        }
                    )
                    .withOnDrawerItemClickListener { v, item, position ->
                        Toast.makeText(context, "Add account", Toast.LENGTH_SHORT).show()
                        true
                    },
                ProfileSettingDrawerItem()
                    .withName("Manage Account")
                    .withIcon(CommunityMaterial.Icon.cmd_cog_outline)
            )

            drawerItemSelectedListener = { id, position, drawerItem ->
                navView.gainAttentionOnBottomBar()
                if (id == 1 || id == 2) {
                    getItemById(id) {
                        if (it is DrawerPrimaryItem) {
                            if (it.tag !is Int) {
                                it.tag = 0
                            }
                            it.tag = (it.tag as Int) + 1
                            // TODO 2019-08-27 allow string to be passed as name
                            it.name = StringHolder("Home ${it.tag as Int}")
                            // do not set item.badge unless you're not using Unread Counters
                            // because this *may* disappear/be overridden on profile change
                            // (if UnreadCounterList have at least one counter with matching
                            // drawer item ID)
                            // See with "Settings" when it.badge AND UnreadCounter is present.
                            //
                            // and it of course does not update the badge
                            //
                            // just do not do this.
                            it.badge = StringHolder("${it.tag as Int * 10}")
                        }
                    }
                }
                if (id == 63) {
                    getItemById(id) {
                        if (it is DrawerPrimaryItem) {
                            it.tag = if (it.tag as Int == 1) 0 else 1
                            it.withIcon(if (it.tag as Int == 1) CommunityMaterial.Icon2.cmd_hdr_off else CommunityMaterial.Icon2.cmd_hdr)
                        }
                    }
                }
                // you cannot select apple
                id != 60
            }
        }

        setSelection.setOnClickListener {
            navView.drawer.setSelection(id = 1, fireOnClick = false)
        }


        navView.bottomSheet.apply {
            this += BottomSheetPrimaryItem(true)
                .withId(1)
                .withTitle("Compose")
                .withIcon(CommunityMaterial.Icon3.cmd_pencil)
                .withOnClickListener(View.OnClickListener {
                    Toast.makeText(this@MainActivity, "Compose message", Toast.LENGTH_SHORT).show()
                })
            this += BottomSheetSeparatorItem(false)
            this += BottomSheetPrimaryItem(false)
                .withId(3)
                .withTitle("Synchronise")
                .withIcon(CommunityMaterial.Icon3.cmd_sync)
                .withOnClickListener(View.OnClickListener {
                    Toast.makeText(this@MainActivity, "Synchronising...", Toast.LENGTH_SHORT).show()
                })
            this += BottomSheetPrimaryItem(false)
                .withId(4)
                .withTitle("Help")
                .withIcon(CommunityMaterial.Icon2.cmd_help)
                .withOnClickListener(View.OnClickListener {
                    Toast.makeText(this@MainActivity, "Want some help?", Toast.LENGTH_SHORT).show()
                })

            toggleGroupEnabled = true
            toggleGroupTitle = "Sort by"
            toggleGroupRemoveItems()
            toggleGroupSelectionMode = TOGGLE_GROUP_SORTING_ORDER
            toggleGroupAddItem(0, "By date", null, SORT_MODE_DESCENDING)
            toggleGroupAddItem(1, "By subject", null, SORT_MODE_ASCENDING)
            toggleGroupAddItem(2, "By sender", null, SORT_MODE_ASCENDING)
            toggleGroupAddItem(3, "By android", null, SORT_MODE_ASCENDING)
            toggleGroupSortingOrderListener = { id, sortMode ->
                Toast.makeText(
                    this@MainActivity,
                    "Sort mode $id ${if (sortMode == SORT_MODE_ASCENDING) "ascending" else "descending"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            toggleGroupCheck(1)

            textInputEnabled = true
            textInputHint = "Search"
            textInputHelperText = "0 messages found"
            textInputIcon = CommunityMaterial.Icon3.cmd_magnify
            textInputChangedListener = object : NavBottomSheet.OnTextInputChangedListener {
                override fun onTextChanged(s: String, start: Int, before: Int, count: Int) {
                    navView.toolbar.subtitle = s
                    textInputError = if (s.length > 10) "Too many messages" else null
                    textInputHelperText = "${s.length} messages found"
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!navView.onBackPressed())
            super.onBackPressed()
    }
}
