package notificationexample.android.com.singupfirebase.ui.login

import android.content.Intent
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import notificationexample.android.com.singupfirebase.ui.main.MainActivity

class LoginPersenter(private val view: LoginView.View){

    var auth: FirebaseAuth? = null

    init {
        auth = FirebaseAuth.getInstance()
    }

    fun postLogin(txt_email:String, txt_password:String){

        auth!!.signInWithEmailAndPassword(txt_email, txt_password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {

                    if (task.isSuccessful()){
                        view.onSuccess(task.isSuccessful())
//
                    }else{
                        view.onError("Authention failed!")
//
                    }

                }

            })

    }
}