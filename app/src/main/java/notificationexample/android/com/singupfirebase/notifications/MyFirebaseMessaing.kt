package notificationexample.android.com.singupfirebase.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import notificationexample.android.com.singupfirebase.ui.message.MessageActivity


class MyFirebaseMessaing : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val sented = remoteMessage.data["sented"]
        val user = remoteMessage.data["user"]

        val preferences =
            getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentuser", "none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sented == firebaseUser.uid) {
            if (currentUser != user) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage)
                } else {
                    sendNotification(remoteMessage)
                }
            }
        }
    }

    private fun sendOreoNotification(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val notification = remoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = OreoNotification(this)
        val builder = oreoNotification.getOreoNotification(
            title, body, pendingIntent,
            defaultSound, icon!!
        )
        var i = 0
        if (j > 0) {
            i = j
        }
        oreoNotification.manager!!.notify(i, builder.build())
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        var user: String = remoteMessage.data.get("user")!!
        var icon: String = remoteMessage.data.get("icon")!!
        var title: String = remoteMessage.data.get("title")!!
        var body: String = remoteMessage.data.get("body")!!

        var notification: RemoteMessage.Notification = remoteMessage.notification!!
        var j = Integer.parseInt(user.replace("[\\D]", ""))
        var intent: Intent = Intent(this, MessageActivity::class.java)
        var bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        var defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i: Int = 0
        if (j > 0) {
            i = j
        }

        noti.notify(i, builder.build())
    }
}