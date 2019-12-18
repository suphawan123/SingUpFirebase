package notificationexample.android.com.singupfirebase.ui.login

class LoginView {
    interface View{
        fun onSuccess(success:Boolean)
        fun onError(error:String)
    }
}