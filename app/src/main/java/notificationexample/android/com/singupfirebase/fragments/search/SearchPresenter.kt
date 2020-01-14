package notificationexample.android.com.singupfirebase.fragments.search

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import notificationexample.android.com.singupfirebase.model.User

class SearchPresenter(private val view: SearchView.View) {
    private var mUsers = arrayListOf<User>()

    fun readUsers() {
        var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                view.onError(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                mUsers!!.clear()
                for (snapshort in p0.children) {
                    val user = snapshort.getValue(User::class.java)
//                     firebaseUser?:return
                    assert(firebaseUser != null)
                    if (user!!.id != firebaseUser!!.uid) {
                        mUsers!!.add(user)
                    }
                }
                view.dataListUser(mUsers)
            }

        })
    }
}