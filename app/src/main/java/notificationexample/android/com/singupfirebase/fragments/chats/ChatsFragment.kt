package notificationexample.android.com.singupfirebase.fragments.chats


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_chats.view.*
import notificationexample.android.com.singupfirebase.adapter.UserAdapter
import notificationexample.android.com.singupfirebase.listener.RecyclerViewCallBack
import notificationexample.android.com.singupfirebase.model.Chatlist
import notificationexample.android.com.singupfirebase.model.User
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.model.Chat
import notificationexample.android.com.singupfirebase.notifications.Token
import notificationexample.android.com.singupfirebase.ui.message.MessageActivity


class ChatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

//    var mUsers: MutableList<User?>? = null

    //แบบนี้ add ได้
    private var mUsers = arrayListOf<User>()

//    var mUsers: List<User> = arrayListOf()  เอาไว้get

    private lateinit var userAdapter: UserAdapter


    private var fuser: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    // var usersList: MutableList<String?>? = null
    private var usersList = arrayListOf<Chatlist>()

    lateinit var theLastMessage: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_chats, container, false)

        setUpRecyclerView(view)
//     /   gatdataReceiver()
        onEvent()

//        fuser = FirebaseAuth.getInstance().currentUser
//
//        setUserList()


        return view
    }

    private fun updateToken(token: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token1: Token = Token(token)
        reference.child(fuser!!.uid).setValue(token1)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    println("token $token1")
                else
                    println("token ชัั่งมีน")
            }

    }

    private fun setUpRecyclerView(view: View) {
        userAdapter = UserAdapter()
        view.recycler_view_chat.setHasFixedSize(true)
        view.recycler_view_chat.setLayoutManager(LinearLayoutManager(context))
        view.recycler_view_chat.adapter = userAdapter
    }

    ///กดจาก userchat แล้วแสดงข้อความ
    private fun onEvent() {
//        userAdapter.setOnClickUser(object : RecyclerViewCallBack {
//            override fun onClickItem(position: Int) {
//                val intent = Intent(context!!, MessageActivity::class.java)
//                intent.putExtra("userid", mUsers[position].id)
//                startActivity(intent)
//            }
//
//        })

        userAdapter.setOnClickUser {
            val intent = Intent(context!!, MessageActivity::class.java)
            intent.putExtra("userid", mUsers[it].id)
            startActivity(intent)
        }



    }


    private fun setUserList() {
        usersList.clear()
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot: DataSnapshot in p0.children) {
                    var chatlist: Chatlist = snapshot.getValue(Chatlist::class.java)!!
                    usersList.add(chatlist)
                }
                chatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { instanceIdResult ->
                val deviceToken = instanceIdResult.token
                updateToken(deviceToken)
            }
    }

    private fun chatList() {
        mUsers.clear()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                mUsers.clear()
                for (snapshot: DataSnapshot in p0.children) {
                    var user: User = snapshot.getValue(User::class.java)!!
                    for (chatlist: Chatlist in usersList) {
                        if (user.id == chatlist.id) {
                            mUsers.add(user)
                        }
                    }
                }
//                userAdapter = UserAdapter(context,mUsers,true)
                userAdapter.mUsers = mUsers
                userAdapter.ischat = true
//                recyclerView.setAdapter(userAdapter)
                userAdapter.notifyDataSetChanged()

                userAdapter.setlastMessage { userid, last_msg ->
                    lastMessage(userid,last_msg)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }

    private fun lastMessage(userid: String, last_msg: TextView) {
        theLastMessage = "default"
        var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot:DataSnapshot in p0.children){
                    var chat: Chat = snapshot.getValue(Chat::class.java)!!
                    if (chat.receiver == (firebaseUser.uid) && chat.sender == (userid) ||
                        chat.receiver == (userid) && chat.sender == (firebaseUser.uid)){
                        theLastMessage = chat.message
                    }
                }

                when (theLastMessage) {
                    "default" -> last_msg.setText("No Message")
                    else -> last_msg.setText(theLastMessage)
                }
                theLastMessage = "default"


            }

        })
    }

    override fun onResume() {
        super.onResume()
        fuser = FirebaseAuth.getInstance().currentUser

        setUserList()

        updateToken(FirebaseInstanceId.getInstance().token!!)
    }
}

