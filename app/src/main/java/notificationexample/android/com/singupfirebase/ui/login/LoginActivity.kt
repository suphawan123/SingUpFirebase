package notificationexample.android.com.singupfirebase.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.activity_login.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.ui.main.MainActivity
import notificationexample.android.com.singupfirebase.ui.resetpassword.ResetPasswordActivity

class LoginActivity : AppCompatActivity(), LoginView.View {


    private lateinit var persenter: LoginPersenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        persenter = LoginPersenter(this)

//        setToolbar()
        setOnClickForgotPassword()

        setOnClick()

    }

    private fun setOnClickForgotPassword() {
        forgot_password.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
            }

        })

    }


//    private fun setToolbar() {
//        var toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setTitle("Login")
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//
//    }

    private fun setOnClick() {
        btn_login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var txt_email: String = email.text.toString()
                var txt_password: String = password.text.toString()

                if (TextUtils.isEmpty(txt_email) or TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(
                        this@LoginActivity,
                        "All fileds are required",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    persenter.postLogin(txt_email,txt_password)
                }
            }
        })

    }

    override fun onSuccess(success: Boolean) {
        var intent: Intent =
            Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onError(error: String) {
        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT).show()
    }
}
