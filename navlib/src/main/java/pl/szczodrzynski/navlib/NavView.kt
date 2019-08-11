package pl.szczodrzynski.navlib

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.core.widget.NestedScrollView
import androidx.customview.widget.ViewDragHelper
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NavView : FrameLayout {

    private var contentView: LinearLayout? = null
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var scrimView: View
    private lateinit var bottomSheet: NestedScrollView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var bottomSheetVisible = false

    private val displayMetrics by lazy {
        context.resources.displayMetrics
    }
    private val configuration by lazy { context.resources.configuration }
    private val displayWidth: Int by lazy { configuration.screenWidthDp }
    private val displayHeight: Int by lazy { configuration.screenHeightDp }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.NavView, defStyle, 0)
        /*_exampleString = a.getString(
            R.styleable.NavView_exampleString
        )*/
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.nav_view, this)

        contentView = findViewById<LinearLayout>(R.id.nv_content)

        //findViewById<TextView>(R.id.textView).text = "${displayWidth}dp x ${displayHeight}dp"

        bottomAppBar = findViewById(R.id.nv_bottomAppBar)
        floatingActionButton = findViewById(R.id.nv_floatingActionButton)
        scrimView = findViewById(R.id.nv_scrim)
        bottomSheet = findViewById(R.id.nv_bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state = STATE_HIDDEN

        bottomAppBar.setOnTouchListener { v, event ->
            bottomSheet.dispatchTouchEvent(event)
            false
        }

        scrimView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP && bottomSheetBehavior.state != STATE_HIDDEN) {
                bottomSheetBehavior.state = STATE_HIDDEN
            }
            true
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(v: View, p1: Float) {}
            override fun onStateChanged(v: View, newState: Int) {
                if (newState == STATE_HIDDEN && bottomSheetVisible) {
                    bottomSheetVisible = false
                    Anim.fadeOut(scrimView, 300, null)
                }
                else if (!bottomSheetVisible) {
                    bottomSheetVisible = true
                    Anim.fadeIn(scrimView, 300, null)
                }
            }
        })

        floatingActionButton.setOnClickListener {
            if (bottomSheetBehavior.state == STATE_HIDDEN) {
                bottomSheetBehavior.state = STATE_COLLAPSED
            }
            else {
                bottomSheetBehavior.state = STATE_HIDDEN
            }
        }



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
