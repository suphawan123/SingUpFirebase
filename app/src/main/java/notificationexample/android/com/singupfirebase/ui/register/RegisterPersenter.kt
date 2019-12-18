package notificationexample.android.com.singupfirebase.ui.register

import android.content.Intent
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import notificationexample.android.com.singupfirebase.model.User
import notificationexample.android.com.singupfirebase.ui.main.MainActivity

class RegisterPersenter(private val view: RegisterView.View) {


    lateinit var referennce: DatabaseReference
    var auth: FirebaseAuth


    init {
        auth = FirebaseAuth.getInstance()
    }

    fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {

                    if (task.isSuccessful()) {
                        var firebaseUser: FirebaseUser = auth.currentUser!!
                        var userid: String = firebaseUser.uid

                        referennce =
                            FirebaseDatabase.getInstance().getReference("Users").child(userid)

                        referennce.setValue(
                            User(
                                userid,
                                username,
                                "default"
                            )
                        )
                            .addOnCompleteListener(object : OnCompleteListener<Void> {
                                override fun onComplete(p0: Task<Void>) {
                                    view.onSuccess(p0.isSuccessful)
//                                    dsdfghdtyui
//
                                }

                            })
                    } else {
                        view.onError("You can't register woth this email or password")
//

                    }

                }

            })
    }
}