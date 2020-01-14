package notificationexample.android.com.singupfirebase

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import kotlin.system.exitProcess


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.restartApp() {
    activity!!.finish()
//    activity!!.finishAffinity()
    activity!!.startActivity(
        Intent(
            context,
            SplashScreen::class.java
        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    )


//    try{
//        val mStartActivity  = Intent(context,SplashScreen::class.java)
//        val mPendingIntentId = 123456
//        val mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT) as PendingIntent
//        val mgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
//        exitProcess(0)
//    }catch (e : Exception){
//        e.printStackTrace();
//    }
}