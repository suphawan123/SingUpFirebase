package notificationexample.android.com.singupfirebase.ui.message

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.profile_image
import kotlinx.android.synthetic.main.activity_message.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.adapter.MessageAdapter
import notificationexample.android.com.singupfirebase.notifications.APIService
import notificationexample.android.com.singupfirebase.model.Chat
import notificationexample.android.com.singupfirebase.model.User
import notificationexample.android.com.singupfirebase.notifications.*
import notificationexample.android.com.singupfirebase.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageActivity : AppCompatActivity() {

    private var fuser: FirebaseUser? = null
    lateinit var reference: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private var mchat = arrayListOf<Chat>()
    lateinit var seenListener: ValueEventListener

    var userid: String = ""
    var apiService: APIService? = null
    var notify:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        if (intent == null)return

        userid = intent.getStringExtra("userid")

        fuser = FirebaseAuth.getInstance().currentUser

        setToobar()


//        apiService = Client.getRetrofit("http://fcm.googleapis.com/")!!.create(APIService::class.java)

        setUpRecyclerView()
        onEvent()
        getDataUser(userid)
        setMassage(userid)

        seenMessage(userid)



    }

    private fun setUpRecyclerView() {
        messageAdapter = MessageAdapter()
        recycler_view_message.setHasFixedSize(true)
        var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_message.setLayoutManager(linearLayoutManager)
        recycler_view_message.adapter = messageAdapter
    }

    private fun setMassage(userid: String) {

        btn_send.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                notify = true
                var msg: String = text_send.text.toString()
                if (!msg.equals("")) {
                    sendMessage(fuser!!.uid, userid, msg)
                } else {
                    Toast.makeText(
                        this@MessageActivity,
                        "You can't send empty message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                text_send.setText("")
            }
        })
    }

    private fun setToobar() {
        setSupportActionBar(toolbarMassage)
        supportActionBar!!.setTitle("")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun onEvent() {
        toolbarMassage.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
//                startActivity(
//                    (Intent(this@MessageActivity, MainActivity::class.java).setFlags(
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    ))
//                )
            }

        })
    }

    private fun getDataUser(userid: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid)
        reference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                title_toolbar_message.text = user!!.username
                if (user.imageURL == "defaul") {
                    profile_image.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(applicationContext).load(user.imageURL).into(profile_image)
                }
                readMessager(fuser!!.uid, userid, user.imageURL)
            }
        })
    }

    private fun seenMessage(userid: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshort: DataSnapshot in p0.children) {
                    var chat: Chat = snapshort.getValue(Chat::class.java)!!
                    if (chat.receiver == (fuser!!.uid) && chat.sender == (userid)) {
                        var hashMap: HashMap<String, Any> = HashMap()
                        hashMap.put("isseen", true)
                        snapshort.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun sendMessage(sender: String, receiver: String, message: String) {
        reference = FirebaseDatabase.getInstance().reference
        reference.child("Chats").push().setValue(
            Chat(
                sender,
                receiver,
                message,
                false
            )
        )
        //add user to chat fragment
        val chatRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(fuser!!.uid)
            .child(userid)

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.exists()){
                    chatRef.child("id").setValue(userid)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        var msg: String = message

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (notify){
                    sendNotifiaction(receiver, user!!.username, msg)
                }
                notify = false
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun sendNotifiaction(receiver: String, username: String, message: String){
        val apiService = Client.getRetrofit ("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        val tokens: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val query: Query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children){
                    var mToken = snapshot.getValue(Token::class.java)!!
                    var data =
                        Data(
                            fuser!!.uid,
                            R.mipmap.ic_launcher,
                            "$username: $message",
                            "New Message",
                            userid
                        )

                    val sender = Sender(data, mToken.token)///////////////.getToken()
                    apiService.sendNotification(sender)!!
                        .enqueue(object : Callback<MyResponse?> {
                            override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                                if (response.code() == 200){
                                    if (response.body()!!.success != 1){
                                        Toast.makeText(this@MessageActivity,"Failed!",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse?>, t: Throwable) {

                            }
                        })

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    private fun readMessager(myid: String, userid: String, imageurl: String) {
        mchat = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                mchat!!.clear()
                for (snapshort: DataSnapshot in p0.children) {
                    var chat = snapshort.getValue(Chat::class.java)
                    if (chat!!.receiver == myid && chat.sender == userid ||
                        chat.receiver == userid && chat.sender == myid
                    ) {
                        mchat!!.add(chat)
                    }
                    messageAdapter.mChat = mchat
                    messageAdapter.imageurl = imageurl
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }


    private fun currentUser(userid: String){
        var editor:SharedPreferences.Editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        editor.putString("currentuser", userid)
        editor.apply()
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status

        reference.updateChildren(hashMap)

    }

    override fun onResume() {
        super.onResume()
        status("online")
        currentUser(userid)
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
        status("offline")
        currentUser("none")
    }
}
