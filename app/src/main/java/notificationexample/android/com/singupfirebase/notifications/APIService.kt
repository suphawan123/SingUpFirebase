package notificationexample.android.com.singupfirebase.notifications

import notificationexample.android.com.singupfirebase.notifications.MyResponse
import notificationexample.android.com.singupfirebase.notifications.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface APIService {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA_WQMAkE:APA91bGfyNulvZ8lufQSYZqfHdRrLyBHflaccaOP9bHZyDvn5YCY2ad7Hr7tHXe_vNqMN5jxnIDPddKcj9yl-oxFIiqRqUKMGl1pnTG6c2Sb7XdbsODOM2f9dsV8dakorKiJIOOjr6SB"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse?>?

}