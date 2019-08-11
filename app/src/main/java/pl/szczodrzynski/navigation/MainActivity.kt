package pl.szczodrzynski.navigation

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Switch
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomappbar.BottomAppBar
import android.view.WindowManager
import android.os.Build
import kotlinx.android.synthetic.main.sample_nav_view.*


class MainActivity : AppCompatActivity() {
    companion object {
        var darkTheme: Boolean? = null
    }

    var showing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (darkTheme == null)
            darkTheme = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("darkTheme", false)

        Log.d("MainActivity", "Dark theme $darkTheme")
        setTheme(if (darkTheme == true) R.style.AppTheme else R.style.AppTheme_Light)

        setContentView(R.layout.sample_nav_view)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or (when {
            darkTheme == true -> 0
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else -> 0
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        button.setOnClickListener {
            // use commit instead of apply because of recreating the activity
            darkTheme = (darkTheme == false)
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("darkTheme", darkTheme == true).commit()
            recreate()
        }

        /*val dim = findViewById<View>(R.id.view)

        val nestedScrollView = findViewById<View>(R.id.nestedScrollView)
        val bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        dim.setOnClickListener {

        }
        dim.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            true
        }

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.setOnTouchListener { v, event ->
            Log.d("Main", "Y: ${event.y}, Raw Y: ${event.rawY}")
            event.setLocation(event.rawX, event.rawY)
            nestedScrollView.dispatchTouchEvent(event)
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        if (showing) {
                            showing = false
                            Anim.fadeOut(dim, 300, null)
                        }
                    }
                    else -> {
                        if (!showing) {
                            showing = true
                            Anim.fadeIn(dim, 300, null)
                        }
                    }
                }
                //Toast.makeText(this@MainActivity, "Bottom Sheet State Changed to: $state", Toast.LENGTH_SHORT).show()
            }
        })*/
    }
}
