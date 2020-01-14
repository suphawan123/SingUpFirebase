package notificationexample.android.com.singupfirebase.fragments.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ligl.android.widget.iosdialog.IOSDialog
import kotlinx.android.synthetic.main.fragment_profile.view.*

import notificationexample.android.com.singupfirebase.R
import notificationexample.android.com.singupfirebase.SplashScreen
import notificationexample.android.com.singupfirebase.model.User
import notificationexample.android.com.singupfirebase.restartApp
import notificationexample.android.com.singupfirebase.ui.login.LoginActivity
import notificationexample.android.com.singupfirebase.ui.main.MainActivity
import java.io.IOException
import java.util.*


class ProfileFragment : Fragment() {

    private var fuser: FirebaseUser? = null
    lateinit var reference: DatabaseReference

    private var fragment_profile: View? = null


    lateinit var storageReference: StorageReference
    var IMAGE_REQUEST: Int = 1
    lateinit var imageUri: Uri
    lateinit var uploadTask: UploadTask
    private var part: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragment_profile = inflater.inflate(R.layout.fragment_profile, container, false)

        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        fuser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)

        gatdataReceiver()

        setOnClick(fragment_profile!!)

        return fragment_profile
    }

    private fun setOnClick(view: View) {
        view.profile_image_upload.setOnClickListener {
            openImage()
        }

        view.profilelogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            IOSDialog.Builder(context)
                .setTitle("แจ้งเตือน")
                .setMessage("คุณต้องการออกจากระบบ ?")
                .setPositiveButton("ยืนยัน", DialogInterface.OnClickListener { _, _ ->
                    restartApp()
                })
                .show()
        }
    }

    private fun openImage() {
        var intent: Intent = Intent()
        intent.setType("image/*")
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST)
    }

    private fun gatdataReceiver() {

        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                var user: User = p0.getValue(User::class.java)!!
                fragment_profile!!.username.text = user.username
                if (user.imageURL == "default") {
                    fragment_profile!!.profile_image_upload.setImageResource(R.mipmap.ic_launcher)
                } else {
                    if (context != null) {
                        Glide
                            .with(context!!)
                            .load(user.imageURL)
                            .into(fragment_profile!!.profile_image_upload)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun upDateProfile() {
        reference.setValue(
            User(
                fuser!!.uid,
                fragment_profile!!.username.text.toString(),
                if (part != "") part else "default"
            )
        )
            .addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
//                            view.onSuccess(p0.isSuccessful)
//
                }

            })
    }

    private fun uploadImage() {
//        val pd:ProgressDialog = ProgressDialog(context!!)
//        pd.setMessage("Uploading")
//        pd.show()
//
//        if (imageUri != null){
//            val fileReference:StorageReference = storageReference.child(System.currentTimeMillis()$"."+getFileExtension(imageUrl))
//        }
        if (imageUri != null) {
            var progressDialog: ProgressDialog = ProgressDialog(context)
            progressDialog.setTitle("Uploading....")
            progressDialog.show()

            val ref = storageReference?.child("images/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(imageUri!!)
            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    progressDialog.dismiss()
                    part = downloadUri.toString()
                    upDateProfile()
//                    Toast.makeText(context, downloadUri.toString(), Toast.LENGTH_SHORT).show()

                } else {
//                    view.onError("อัพโหลดรูปภาพไม่สำเร็จ !")
                }
            }?.addOnFailureListener {
                //                view.saveImageFail(it)
            }
        } else {
//            view.onError("กรุณาอัพโหลดภาพ")
        }
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.getData() != null
        ) {
            imageUri = data.getData()!!
            Toast.makeText(context, imageUri.toString(), Toast.LENGTH_SHORT).show()
            try {
                var bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(
                        activity!!.contentResolver,
                        imageUri
                    )
                fragment_profile!!.profile_image_upload.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
