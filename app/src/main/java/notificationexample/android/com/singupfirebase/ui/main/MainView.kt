package notificationexample.android.com.singupfirebase.ui.main

import notificationexample.android.com.singupfirebase.model.User

class MainView{
    interface View{
        fun onSuccess(user: User)
        fun onError(error:String)
    }
}