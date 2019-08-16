package pl.szczodrzynski.navlib.bottomsheet

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.szczodrzynski.navlib.R
import pl.szczodrzynski.navlib.bottomsheet.items.EditTextFilledItem
import pl.szczodrzynski.navlib.bottomsheet.items.IBottomSheetItem
import pl.szczodrzynski.navlib.bottomsheet.items.PrimaryItem
import pl.szczodrzynski.navlib.bottomsheet.items.SeparatorItem

class BottomSheetAdapter(val items: List<IBottomSheetItem<*>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewHolderProvider = ViewHolderProvider()

    init {
        viewHolderProvider.registerViewHolderFactory(1, R.layout.nav_bs_item_primary) { itemView ->
            PrimaryItem.ViewHolder(itemView)
        }
        viewHolderProvider.registerViewHolderFactory(2, R.layout.nav_bs_item_separator) { itemView ->
            SeparatorItem.ViewHolder(itemView)
        }
        viewHolderProvider.registerViewHolderFactory(3, R.layout.nav_bs_item_edittext_filled) { itemView ->
            EditTextFilledItem.ViewHolder(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewHolderProvider.provideViewHolder(viewGroup = parent, viewType = viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].bindViewHolder(viewHolder = holder)
    }
}