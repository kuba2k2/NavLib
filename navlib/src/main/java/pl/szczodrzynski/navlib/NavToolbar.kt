package pl.szczodrzynski.navlib

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.children
import com.google.android.material.appbar.MaterialToolbar

class NavToolbar : MaterialToolbar {
    constructor(context: Context) : super(context) {
        create(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        create(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        create(attrs, defStyle)
    }

    var toolbarImage: ImageView? = null
        set(value) {
            field = value
            toolbarImage?.setOnClickListener {
                profileImageClickListener?.invoke()
            }
        }

    private fun create(attrs: AttributeSet?, defStyle: Int) {

    }

    var profileImageClickListener: (() -> Unit)? = null

    var profileImage
        get() = toolbarImage?.drawable
        set(value) {
            toolbarImage?.setImageDrawable(value)
        }
}