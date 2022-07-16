package com.example.attendanceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapp.databinding.ActivityAdminBinding
import com.example.attendanceapp.databinding.ActivityStudentBinding
import com.google.firebase.database.*

class Admin : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var actionBar: ActionBar
    private lateinit var database: DatabaseReference
    private lateinit var studentdb: DatabaseReference
    private lateinit var userArrayList: ArrayList<user>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        database = FirebaseDatabase.getInstance().getReference("admin")
        val intent = getIntent()
        val ID = intent.getStringExtra("ID").toString()
        //actionBar.title="Admin: "+ID
        binding.logoutbtn.setOnClickListener {
            finish()
        }
        binding.userlist.layoutManager=LinearLayoutManager(this)
        binding.userlist.setHasFixedSize(true)
        userArrayList= arrayListOf<user>()
        getUserData()
    }

    private fun getUserData() {
        studentdb=FirebaseDatabase.getInstance().getReference("request")
        studentdb.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(user::class.java)
                        userArrayList.add(user!!)
                    }
                    binding.userlist.adapter=MyAdapter(userArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()//go back to previous activity
        return super.onSupportNavigateUp()
    }
}