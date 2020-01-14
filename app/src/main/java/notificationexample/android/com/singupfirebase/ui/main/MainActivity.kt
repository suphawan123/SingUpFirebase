package notificationexample.android.com.singupfirebase.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.component_tabbar_badge.view.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.fragments.search.SearchFragment
import notificationexample.android.com.singupfirebase.fragments.chats.ChatsFragment
import notificationexample.android.com.singupfirebase.fragments.profile.ProfileFragment
import notificationexample.android.com.singupfirebase.fragments.users.UsersFragment
import notificationexample.android.com.singupfirebase.model.Chat
import notificationexample.android.com.singupfirebase.model.User
import notificationexample.android.com.singupfirebase.ui.login.LoginActivity
import notificationexample.android.com.singupfirebase.ui.start.StartActivity
import java.lang.Exception


class MainActivity : AppCompatActivity(), MainView.View {

    private var persenter: MainPersenter? = null
    var firebaseUser: FirebaseUser? = null
    lateinit var databaseReference: DatabaseReference
    var notificationBadge: View? = null
    var unread: Int = 0
    var itemView: BottomNavigationItemView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)

        setupBadge()

        persenter = MainPersenter(this)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        onToobar()

        persenter!!.onAdd()

        onEvent()

        navigation.selectedItemId = R.id.chat

        setPage()


    }

    private fun setupBadge() {
        try {
            var menuView = navigation.getChildAt(0) as BottomNavigationMenuView
            itemView = menuView.getChildAt(0) as BottomNavigationItemView
            notificationBadge =
                LayoutInflater.from(this).inflate(R.layout.component_tabbar_badge, menuView, false)
            itemView!!.addView(notificationBadge!!)
            notificationBadge!!.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun onEvent() {
        navigation.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {

            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var fragment: Fragment? = null
                when (item.itemId) {
                    R.id.chat -> {
                        fragment = ChatsFragment()
                        toolbar.visibility = View.VISIBLE

                    }
                    R.id.user -> {
                        fragment = UsersFragment()
                        toolbar.visibility = View.VISIBLE
                    }
                    R.id.profile -> {
                        fragment = ProfileFragment()
                        toolbar.visibility = View.GONE
                    }
                    R.id.search -> {
                        fragment =
                            SearchFragment()
                        toolbar.visibility = View.GONE
                    }
                }
                return loadFragment(fragment)
            }

            private fun loadFragment(fragment: Fragment?): Boolean {
                if (fragment != null) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, fragment)
                        .commit()
                    return true
                }
                return false
            }
        })
    }

    private fun setPage() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                unread = 0
                for (snapshot: DataSnapshot in p0.children) {
                    var chat: Chat = snapshot.getValue(Chat::class.java)!!
                    if (chat.receiver == firebaseUser!!.uid && !chat.isseen!!) {
                        unread++
                    }
                }
                if (unread == 0)
                    notificationBadge!!.visibility = View.GONE
                else
                    notificationBadge!!.visibility = View.VISIBLE
                notificationBadge!!.notificationsBadgeTextView.text = "$unread"
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    private fun refreshBadgeView() {
        try {
            val badgeIsVisible = notificationBadge!!.visibility != View.GONE
            notificationBadge!!.visibility = if (badgeIsVisible) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onToobar() {
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

    }

    override fun onSuccess(user: User) {
        username_main.text = user.username

        if (user.imageURL == "default") {
            profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide
                .with(applicationContext)
                .load(user.imageURL)
                .into(profile_image)
        }
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun status(status: String) {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["status"] = status

        databaseReference.updateChildren(hashMap)

    }

    override fun onResume() {
        super.onResume()
        unread = 0
        img_on_main.visibility = View.VISIBLE
        status("online")

    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
