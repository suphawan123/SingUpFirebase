package notificationexample.android.com.singupfirebase.fragments.users

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_users.*
import kotlinx.android.synthetic.main.fragment_users.view.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.adapter.UserAdapter
import notificationexample.android.com.singupfirebase.listener.RecyclerViewCallBack
import notificationexample.android.com.singupfirebase.model.User
import notificationexample.android.com.singupfirebase.ui.message.MessageActivity


class UsersFragment : Fragment(), UsersView.View {

    private lateinit var userAdapter: UserAdapter
    private lateinit var persenter: UsersPersenter
    private var mUsers = arrayListOf<User>()

    private var listUser: List<User> = arrayListOf()

    lateinit var search_user: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_users, container, false)
        userAdapter = UserAdapter()
        persenter = UsersPersenter(this)
        persenter.readUsers()

        search_user = view.findViewById(R.id.search_user)
        setSearch()

        setUpRecyclerView(view)
        onEvent()


        return view
    }

    private fun setSearch() {
        search_user.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                searchUsers(s.toString().toLowerCase())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }


    private fun searchUsers(s: String) {
        val fuser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val query: Query =
            FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                mUsers.clear()
                for (snapshot: DataSnapshot in p0.children) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    assert(fuser != null)
                    if (user.id != (fuser.uid)) {
                        mUsers.add(user)
                    }
                }
//                    userAdapter = context.let { UserAdapter(it, mUsers,false) }
                userAdapter.mUsers = mUsers
                userAdapter.ischat = true
                userAdapter.notifyDataSetChanged()


            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }

    private fun setUpRecyclerView(view: View) {

        view.recycler_view.setHasFixedSize(true)
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.adapter = userAdapter
    }

    private fun onEvent() {
        userAdapter.setOnClickUser(object : RecyclerViewCallBack {
            override fun onClickItem(position: Int) {
                val intent = Intent(context!!, MessageActivity::class.java)
                intent.putExtra("userid", mUsers[position].id)
                startActivity(intent)
            }

        })
    }

    override fun dataListUser(list: List<User>) {
        listUser = list
        mUsers = list as ArrayList<User>
        userAdapter.mUsers = list
        userAdapter.ischat = false
        userAdapter.notifyDataSetChanged()
    }

    override fun onError(error: String) {

    }
}
