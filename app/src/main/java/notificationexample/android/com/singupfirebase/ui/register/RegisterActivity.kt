package notificationexample.android.com.singupfirebase.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_register.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.ui.main.MainActivity

class RegisterActivity : AppCompatActivity(), RegisterView.View {


    private lateinit var persenter: RegisterPersenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        persenter = RegisterPersenter(this)

//        onToobar()
        onClick()
    }

    private fun onToobar() {
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Register")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun onClick() {
        btn_register.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var txt_username: String = username.text.toString()
                var txt_email: String = email.text.toString()
                var txt_password: String = password.text.toString()

                if (TextUtils.isEmpty(txt_username) or TextUtils.isEmpty(txt_email) or TextUtils.isEmpty(
                        txt_password
                    )
                ) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "All fileds are required",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (txt_password.length < 6) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    persenter.register(txt_username, txt_email, txt_password)
                }
            }
        }
        )
    }

    override fun onSuccess(success: Boolean) {
        var intent: Intent =
            Intent(this@RegisterActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onError(error: String) {
        Toast.makeText(this@RegisterActivity, error, Toast.LENGTH_SHORT).show()
    }


}
