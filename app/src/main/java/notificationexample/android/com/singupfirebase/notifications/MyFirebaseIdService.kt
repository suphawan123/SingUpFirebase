package notificationexample.android.com.singupfirebase.notifications



import android.util.Log
import androidx.constraintlayout.widget.Constraints.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService



class MyFirebaseIdService : FirebaseMessagingService() {


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
            Log.d(TAG, "Refreshed token: $p0")

        var user:FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var refreshToken:String = FirebaseInstanceId.getInstance().token!!
        if (user != null) {
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: String?) {
        var user:FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var ref:DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token:Token = Token(refreshToken)
        ref.child(user.uid).setValue(token)
    }

}