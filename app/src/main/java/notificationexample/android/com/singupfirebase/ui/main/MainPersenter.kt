package notificationexample.android.com.singupfirebase.ui.main

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.model.User

class MainPersenter (private val view: MainView.View){

     var firebaseUser: FirebaseUser? = null
     var reference: DatabaseReference
      var user: User? = null


    init {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
    }


    fun onAdd() {

        reference.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(p0: DataSnapshot) {

                user = p0.getValue(User::class.java)!!
                view.onSuccess(user!!)
            }
            override fun onCancelled(p0: DatabaseError) {
                view.onError(p0.message)

            }


        })
    }
}