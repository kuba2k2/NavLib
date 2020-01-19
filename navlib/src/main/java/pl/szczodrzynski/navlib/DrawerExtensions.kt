package pl.szczodrzynski.navlib

import android.view.View
import com.mikepenz.materialdrawer.*
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile

inline fun DrawerBuilder.withOnDrawerItemClickListener(crossinline listener: (view: View?, position: Int, drawerItem: IDrawerItem<*>) -> Boolean): DrawerBuilder {
    return this.withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
            return listener(view, position, drawerItem)
        }
    })
}

inline fun DrawerBuilder.withOnDrawerItemLongClickListener(crossinline listener: (view: View, position: Int, drawerItem: IDrawerItem<*>) -> Boolean): DrawerBuilder {
    return this.withOnDrawerItemLongClickListener(object : Drawer.OnDrawerItemLongClickListener {
        override fun onItemLongClick(view: View, position: Int, drawerItem: IDrawerItem<*>): Boolean {
            return listener(view, position, drawerItem)
        }
    })
}

inline fun AccountHeaderBuilder.withOnAccountHeaderListener(crossinline listener: (view: View?, profile: IProfile<*>, current: Boolean) -> Boolean): AccountHeaderBuilder {
    return this.withOnAccountHeaderListener(object : AccountHeader.OnAccountHeaderListener {
        override fun onProfileChanged(view: View?, profile: IProfile<*>, current: Boolean): Boolean {
            return listener(view, profile, current)
        }
    })
}

inline fun AccountHeaderBuilder.withOnAccountHeaderItemLongClickListener(crossinline listener: (view: View, profile: IProfile<*>, current: Boolean) -> Boolean): AccountHeaderBuilder {
    return this.withOnAccountHeaderItemLongClickListener(object : AccountHeader.OnAccountHeaderItemLongClickListener {
        override fun onProfileLongClick(view: View, profile: IProfile<*>, current: Boolean): Boolean {
            return listener(view, profile, current)
        }
    })
}

inline fun AccountHeaderBuilder.withOnAccountHeaderProfileImageListener(
    crossinline onClick: (
        view: View,
        profile: IProfile<*>,
        current: Boolean
    ) -> Boolean,
    crossinline onLongClick: (
        view: View,
        profile: IProfile<*>,
        current: Boolean
    ) -> Boolean
): AccountHeaderBuilder {
    return this.withOnAccountHeaderProfileImageListener(object : AccountHeader.OnAccountHeaderProfileImageListener {
        override fun onProfileImageClick(view: View, profile: IProfile<*>, current: Boolean): Boolean {
            return onClick(view, profile, current)
        }
        override fun onProfileImageLongClick(view: View, profile: IProfile<*>, current: Boolean): Boolean {
            return onLongClick(view, profile, current)
        }
    })
}

inline fun MiniDrawer.withOnMiniDrawerItemClickListener(crossinline listener: (view: View?, position: Int, drawerItem: IDrawerItem<*>, type: Int) -> Boolean): MiniDrawer {
    return this.withOnMiniDrawerItemClickListener(object : MiniDrawer.OnMiniDrawerItemClickListener {
        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>, type: Int): Boolean {
            return listener(view, position, drawerItem, type)
        }
    })
}