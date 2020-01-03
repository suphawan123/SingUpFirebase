package notificationexample.android.com.singupfirebase.ui.resetpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.fragments.users.UsersView
import notificationexample.android.com.singupfirebase.ui.login.LoginActivity

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)


        onToobar()

        firebaseAuth = FirebaseAuth.getInstance()

        onClickReset()

    }

    private fun onToobar() {
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Reset Password")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun onClickReset() {
        btn_reset.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var email:String = send_email.getText().toString()

                if (email == ""){
                    Toast.makeText(this@ResetPasswordActivity, "All fileds are required!", Toast.LENGTH_SHORT).show()
                }else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(object :OnCompleteListener<Void>{
                        override fun onComplete(p0: Task<Void>) {
                            if (p0.isSuccessful){
                                Toast.makeText(this@ResetPasswordActivity,"Please check your Email", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                            }else{
                                var error:String = p0.exception!!.message!!
                                Toast.makeText(this@ResetPasswordActivity,error, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }
            }

        })
    }


}
