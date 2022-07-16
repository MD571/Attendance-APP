package com.example.attendanceapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.example.attendanceapp.databinding.ActivityProfessorBinding
import com.example.attendanceapp.databinding.ActivityStudentBinding
import com.google.firebase.database.*
import java.nio.file.attribute.AclEntry
import java.util.*

class Professor : AppCompatActivity() {
    private lateinit var binding: ActivityProfessorBinding
    private lateinit var actionBar: ActionBar
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfessorBinding.inflate(layoutInflater)
        database= FirebaseDatabase.getInstance().getReference("professor")
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val intent=getIntent()
        val ID=intent.getStringExtra("ID").toString()
        //actionBar.title="Professor: "+ID
        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(ID)){
                    binding.textTitle.text=getString(R.string.hello,(snapshot.child(ID).child("Full Name").value).toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        binding.cardright1.setOnClickListener {
            startActivity(Intent(this,Admin::class.java))
        }
        binding.cardright2.setOnClickListener {
            //Inflate the dialog with custom view
            val dialogView= layoutInflater.inflate(R.layout.stu_rem_popup,null)
            //Alter DialogeBuilder
            val dialogBuilder=AlertDialog.Builder(this)
                .setView(dialogView)
            //show dialog
            val mAlertDialog = dialogBuilder.show()

            //remove button click on custom layout
            dialogView.findViewById<Button>(R.id.removebtn).setOnClickListener {
                val id=dialogView.findViewById<EditText>(R.id.SID).text.toString()
                mAlertDialog.dismiss()
                FirebaseDatabase.getInstance().getReference("student").child(id).removeValue()
            }
            dialogView.findViewById<Button>(R.id.cancelbtn).setOnClickListener {
                mAlertDialog.dismiss()
            }

        }
        binding.cardright3.setOnClickListener {
            startActivity(Intent(this,download_attendance::class.java))
        }
        binding.logoutbtn.setOnClickListener{
            finish()
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()//go back to previous activity
        return super.onSupportNavigateUp()
    }
}