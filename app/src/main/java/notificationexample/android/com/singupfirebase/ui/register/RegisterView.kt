package notificationexample.android.com.singupfirebase.ui.register

class RegisterView {
    interface View{
        fun onSuccess(success:Boolean)
        fun onError(error:String)
    }
}