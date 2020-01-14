package notificationexample.android.com.singupfirebase.fragments.search

import notificationexample.android.com.singupfirebase.model.User

class SearchView {

    interface View {
        fun dataListUser(list: List<User>)
        fun onError(error:String)
    }
}