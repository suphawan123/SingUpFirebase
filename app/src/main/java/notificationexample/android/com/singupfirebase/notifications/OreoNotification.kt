package notificationexample.android.com.singupfirebase.notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build


class OreoNotification(base: Context?) : ContextWrapper(base) {

    private var notificationManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {

        val channel = NotificationChannel(
           "notificationexample.android.com.singupfirebase",
            "SingUpFirebase",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(channel)
    }

    val manager: NotificationManager?
        get() {
            if (notificationManager == null) {
                notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return notificationManager
        }

    @TargetApi(Build.VERSION_CODES.O)
    fun getOreoNotification(
        title: String?, body: String?,
        pendingIntent: PendingIntent?, soundUri: Uri?, icon: String
    ): Notification.Builder {
        return Notification.Builder(applicationContext, "notificationexample.android.com.singupfirebase")
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setSmallIcon(icon.toInt())

    }


}