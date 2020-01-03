package notificationexample.android.com.singupfirebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.listener.RecyclerViewCallBack
import notificationexample.android.com.singupfirebase.model.Chat
import notificationexample.android.com.singupfirebase.model.User

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var mUsers: List<User> = arrayListOf()

    private var mCallBack: RecyclerViewCallBack? = null
    var ischat: Boolean? = null

    lateinit var theLastMessage: String

    fun setOnClickUser(listener: RecyclerViewCallBack) {
        this.mCallBack = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user: User = mUsers.get(position)
        holder.username.text = user.username

        if (user.imageURL == "default") {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(holder.itemView.context).load(user.imageURL).into(holder.profile_image)
        }
        if (ischat!!){
            lastMessage(user.id, holder.last_msg)
        }else{
            holder.last_msg.setVisibility(View.GONE)
        }

        if (ischat!!) {
            if (user.status == "online") {
                holder.img_on.setVisibility(View.VISIBLE)
                holder.img_off.setVisibility(View.GONE)
            } else {
                holder.img_on.setVisibility(View.GONE)
                holder.img_off.setVisibility(View.VISIBLE)
            }
        } else {
            holder.img_on.setVisibility(View.GONE)
            holder.img_off.setVisibility(View.GONE)
        }
        holder.itemView.setOnClickListener {
            mCallBack!!.onClickItem(position)

        }

    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var profile_image: ImageView
        var img_on: ImageView
        var img_off: ImageView
        var last_msg: TextView

        init {
            username = itemView.findViewById(R.id.username)
            profile_image = itemView.findViewById(R.id.profile_image)
            img_on = itemView.findViewById(R.id.img_on)
            img_off = itemView.findViewById(R.id.img_off)
            last_msg = itemView.findViewById(R.id.last_msg)
        }

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
                    var chat:Chat = snapshot.getValue(Chat::class.java)!!
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

}