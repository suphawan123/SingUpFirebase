package notificationexample.android.com.singupfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.ui.main.MainActivity
import notificationexample.android.com.singupfirebase.ui.register.RegisterActivity
import notificationexample.android.com.singupfirebase.ui.resetpassword.ResetPasswordActivity

class LoginActivity : AppCompatActivity(), LoginView.View {


    private lateinit var persenter: LoginPersenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        persenter = LoginPersenter(this)

        setOnClick()

    }

    private fun setOnClick() {
        btn_login.setOnClickListener {
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

        register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }

        forgot_password.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java)) }

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
