package pl.szczodrzynski.navlib

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.nav_view.view.*

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

    fun getTopInset(view: View): Float {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (view.rootWindowInsets?.systemWindowInsetTop ?: 24)
        } else {
            24
        }*displayMetrics.density
    }
    fun getLeftInset(view: View): Float {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (view.rootWindowInsets?.systemWindowInsetLeft ?: 0)
        } else {
            0
        } * displayMetrics.density
    }
    fun getRightInset(view: View): Float {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (view.rootWindowInsets?.systemWindowInsetRight ?: 0)
        } else {
            0
        } * displayMetrics.density
    }
    fun getBottomInset(view: View): Float {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (view.rootWindowInsets?.systemWindowInsetBottom ?: 48)
        } else {
            48
        } * displayMetrics.density
    }

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

        // TODO add vars for status/navigation bar background

        bottomAppBar = findViewById(R.id.nv_bottomAppBar)
        floatingActionButton = findViewById(R.id.nv_floatingActionButton)
        scrimView = findViewById(R.id.nv_scrim)
        bottomSheet = findViewById(R.id.nv_bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state = STATE_HIDDEN
        bottomSheetBehavior.peekHeight = displayHeight

        nv_main.setPadding(
            getLeftInset(nv_main).toInt(),
            getTopInset(nv_main).toInt(),
            getRightInset(nv_main).toInt(),
            getBottomInset(nv_main).toInt()
        )
        nv_statusBarBackground.layoutParams.height = getTopInset(nv_main).toInt()
        nv_navigationBarBackground.layoutParams.height = getBottomInset(nv_main).toInt()


        bottomAppBar.setOnTouchListener { v, event ->
            val location = IntArray(2)
            bottomSheet.getLocationOnScreen(location)
            event.setLocation(event.rawX - location[0], event.rawY - location[1])
            bottomSheet.dispatchTouchEvent(event)
            true
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
                    bottomSheet.scrollTo(0, 0)
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

        nv_elevation.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = "Set toolbar elevation ${progress}dp"
                nv_toolbar.elevation = progress * displayMetrics.density
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
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
