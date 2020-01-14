package notificationexample.android.com.singupfirebase.ui.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_start.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.ui.login.LoginActivity
import notificationexample.android.com.singupfirebase.ui.main.MainActivity
import notificationexample.android.com.singupfirebase.ui.register.RegisterActivity


class StartActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        checkUser()

        onEvent()

    }

    private fun checkUser() {
        if (firebaseUser != null) {
            var intent: Intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            return
        }
    }

    private fun onEvent() {
        login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(
                    Intent(
                        this@StartActivity,
                        LoginActivity::class.java
                    )
                )
            }

        })

        register.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(
                    Intent(
                        this@StartActivity,
                        RegisterActivity::class.java
                    )
                )
            }

        })
    }
}
