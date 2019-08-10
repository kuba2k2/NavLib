package pl.szczodrzynski.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomappbar.BottomAppBar


class MainActivity : AppCompatActivity() {

    var showing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_nav_view)

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
