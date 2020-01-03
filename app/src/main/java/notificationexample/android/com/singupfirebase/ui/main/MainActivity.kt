package notificationexample.android.com.singupfirebase.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.fragments.chats.ChatsFragment
import notificationexample.android.com.singupfirebase.fragments.profile.ProfileFragment
import notificationexample.android.com.singupfirebase.fragments.users.UsersFragment
import notificationexample.android.com.singupfirebase.model.Chat
import notificationexample.android.com.singupfirebase.model.User
import notificationexample.android.com.singupfirebase.ui.start.StartActivity


class MainActivity : AppCompatActivity(), MainView.View {

    private lateinit var persenter: MainPersenter
    var firebaseUser: FirebaseUser? = null
    lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)

        persenter = MainPersenter(this)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        onToobar()
        persenter.onAdd()
        setPage()
    }

    private fun setPage() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        databaseReference.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                var viewPagerAdapter:ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var unread:Int = 0
                for (snapshot:DataSnapshot in p0.children){
                    var chat:Chat = snapshot.getValue(Chat::class.java)!!
                    if (chat.receiver == firebaseUser!!.uid && !chat.isseen!!) {
                        unread++
                    }

                }
                if (unread == 0){
                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
                }else{
                    viewPagerAdapter.addFragment(ChatsFragment(),"("+unread+") Chats")
                }

                viewPagerAdapter.addFragment(UsersFragment(),"Users")
                viewPagerAdapter.addFragment(ProfileFragment(),"Profile")

                view_pager.adapter = viewPagerAdapter

                tap_layout.setupWithViewPager(view_pager)


            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }


    private fun onToobar() {
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("")

    }

    override fun onSuccess(user: User) {
        username_main.text = user.username

        if (user.imageURL == "default") {
            profile_image.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide
                .with(this)
                .load(user.imageURL)
                .into(profile_image)
        }
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                //////////
                startActivity(Intent(applicationContext, StartActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                return true
            }
        }
        return false
    }


    internal inner class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        init {
            fragments = ArrayList()
            titles = ArrayList()
        }
    }

    private fun status( status:String){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["status"] = status

        databaseReference.updateChildren(hashMap)

    }

    override fun onResume() {
        super.onResume()
        img_on_main.visibility = View.VISIBLE
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

}
