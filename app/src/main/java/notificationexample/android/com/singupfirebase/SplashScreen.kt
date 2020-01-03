package notificationexample.android.com.singupfirebase

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import notificationexample.android.com.singupfirebase.ui.main.MainActivity


class SplashScreen : AppCompatActivity() {

    var handler: Handler? = null
    var runnable: Runnable? = null
    var delay_time: Long = 0
    var time = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()

        runnable = Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        delay_time = time
        handler!!.postDelayed(runnable, delay_time)
        time = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        handler!!.removeCallbacks(runnable)
        time = delay_time - (System.currentTimeMillis() - time)
    }
}
