package pl.szczodrzynski.navlib.drawer

data class IUnreadCounter(val profileId: Int,
                          val type: Int,
                          var drawerItemId: Int? = null,
                          var count: Int)