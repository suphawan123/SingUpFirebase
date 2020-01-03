package notificationexample.android.com.singupfirebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.model.Chat

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    var mChat: List<Chat> = arrayListOf()
    var imageurl: String = ""
    var fuser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        holder.show_message.text = chat.message
        if (imageurl == "default") {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(holder.itemView.context).load(imageurl).into(holder.profile_image)
        }

        if (position == mChat.size -1){
            holder.txt_seen.visibility = View.VISIBLE
            if (chat.isseen!!){
                holder.txt_seen.setText("อ่านแล้ว")
            }else{
                holder.txt_seen.setText("ส่งแล้ว")
            }
        }else{
            holder.txt_seen.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_message: TextView
        var profile_image: ImageView
        var txt_seen:TextView

        init {
            show_message = itemView.findViewById(R.id.show_message)
            profile_image = itemView.findViewById(R.id.profile_image)
            txt_seen = itemView.findViewById(R.id.txt_seen)
        }
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (mChat[position].sender == fuser!!.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

}