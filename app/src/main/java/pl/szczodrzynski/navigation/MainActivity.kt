package pl.szczodrzynski.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.view.Gravity
import kotlinx.android.synthetic.main.sample_nav_view.*
import pl.szczodrzynski.navlib.SystemBarsUtil
import pl.szczodrzynski.navlib.SystemBarsUtil.Companion.COLOR_DO_NOT_CHANGE
import pl.szczodrzynski.navlib.SystemBarsUtil.Companion.COLOR_HALF_TRANSPARENT
import pl.szczodrzynski.navlib.SystemBarsUtil.Companion.COLOR_PRIMARY_DARK
import pl.szczodrzynski.navlib.getColorFromAttr


class MainActivity : AppCompatActivity() {
    companion object {
        var darkTheme: Boolean? = null
    }

    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (darkTheme == null)
            darkTheme = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("darkTheme", false)

        Log.d("MainActivity", "Dark theme $darkTheme")
        setTheme(if (darkTheme == true) R.style.AppTheme else R.style.AppTheme_Light)

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

        button.setOnClickListener {
            // use commit instead of apply because of recreating the activity
            darkTheme = (darkTheme == false)
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("darkTheme", darkTheme == true).commit()
            recreate()
        }


        //navView.init(this)

        // init the drawer before SystemBarsUtil
        navView.addDrawer(activity = this)

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

        navView.bottomBar.fabExtendedText = "Compose"
        navView.bottomBar.fabExtended = false
    }
}
