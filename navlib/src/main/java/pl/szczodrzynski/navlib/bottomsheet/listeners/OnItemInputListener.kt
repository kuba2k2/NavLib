package pl.szczodrzynski.navlib.bottomsheet.listeners

import pl.szczodrzynski.navlib.bottomsheet.items.EditTextFilledItem

interface OnItemInputListener {
    fun onItemInput(itemId: Int, item: EditTextFilledItem, text: CharSequence)
}