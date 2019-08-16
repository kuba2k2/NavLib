package pl.szczodrzynski.navlib.bottomsheet.items

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.utils.sizeDp
import com.mikepenz.materialize.holder.ImageHolder
import pl.szczodrzynski.navlib.R
import pl.szczodrzynski.navlib.bottomsheet.listeners.OnItemInputListener

class EditTextFilledItem(override val isContextual: Boolean = true) : IBottomSheetItem<EditTextFilledItem.ViewHolder> {

    /*_                             _
     | |                           | |
     | |     __ _ _   _  ___  _   _| |_
     | |    / _` | | | |/ _ \| | | | __|
     | |___| (_| | |_| | (_) | |_| | |_
     |______\__,_|\__, |\___/ \__,_|\__|
                   __/ |
                  |__*/
    override var id: Int = -1
    override val viewType: Int
        get() = 3
    override val layoutId: Int
        get() = R.layout.nav_bs_item_edittext_filled

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textInputLayout = itemView.findViewById<TextInputLayout>(R.id.item_text_input_layout)
        val textInputEditText = itemView.findViewById<TextInputEditText>(R.id.item_text_input_edit_text)
    }

    override fun bindViewHolder(viewHolder: ViewHolder) {
        viewHolder.textInputLayout.apply {
            hint = this@EditTextFilledItem.hint
            helperText = this@EditTextFilledItem.helperText
            error = this@EditTextFilledItem.error
        }
        viewHolder.textInputEditText.apply {
            setText(this@EditTextFilledItem.text)

            removeTextChangedListener(textChangedListener)
            addTextChangedListener(textChangedListener)
        }
    }

    /*_____        _
     |  __ \      | |
     | |  | | __ _| |_ __ _
     | |  | |/ _` | __/ _` |
     | |__| | (_| | || (_| |
     |_____/ \__,_|\__\__,*/
    var hint: CharSequence? = null
    var helperText: CharSequence? = null
    var error: CharSequence? = null
    var text: CharSequence? = null
    private var textChangedListener: TextWatcher? = null
    var onItemInputListener: OnItemInputListener? = null
        set(value) {
            field = value
            textChangedListener = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onItemInputListener?.onItemInput(id, this@EditTextFilledItem, s?:"")
                }
            }
        }
}