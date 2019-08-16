package pl.szczodrzynski.navlib

import android.content.Context
import android.util.AttributeSet
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

    private fun create(attrs: AttributeSet?, defStyle: Int) {

    }
}