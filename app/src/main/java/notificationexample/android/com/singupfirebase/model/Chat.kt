package notificationexample.android.com.singupfirebase.model

data class Chat(

    val sender: String = "",
    val receiver: String = "",
    val message: String = "",
    val isseen:Boolean? = null

)
