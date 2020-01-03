package notificationexample.android.com.singupfirebase.fragments.users

import notificationexample.android.com.singupfirebase.model.User

class UsersView {
    interface View {
        fun dataListUser(list: List<User>)
        fun onError(error:String)
    }
}