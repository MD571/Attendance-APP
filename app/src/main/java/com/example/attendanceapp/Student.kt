package com.example.attendanceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.attendanceapp.databinding.ActivityStudentBinding
import com.google.firebase.database.*

class Student : AppCompatActivity() {
    private lateinit var binding: ActivityStudentBinding
    private lateinit var actionBar: ActionBar
    private lateinit var database:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStudentBinding.inflate(layoutInflater)
        database= FirebaseDatabase.getInstance().getReference("student")
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val intent=getIntent()
        val id=intent.getStringExtra("ID").toString()
        val lat=intent.getStringExtra("lat").toString()
        val lon=intent.getStringExtra("lon").toString()
        val fragment1:Fragment=FirstFragment()
        val fragment2:Fragment=SecondFragment()
        val fragment3:Fragment=ThirdFragment()
        val bundle=Bundle()
        bundle.putString("ID",id)
        bundle.putString("lat",lat)
        bundle.putString("lon",lon)
        fragment1.arguments=bundle
        fragment2.arguments=bundle
        fragment3.arguments=bundle
        setCurrentFragment(fragment1)
        binding.bottomNavigationView
            .setOnItemSelectedListener{
            when(it.itemId){
                R.id.firstFragment -> setCurrentFragment(fragment1)
                R.id.secondFragment -> setCurrentFragment(fragment2)
                R.id.thirdFragment -> setCurrentFragment(fragment3)
            }
            true
        }
    }
    private fun setCurrentFragment(fragment: Fragment)=supportFragmentManager.beginTransaction().apply {
        replace(R.id.frame_layout,fragment).addToBackStack(null)
        commit()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()//go back to previous activity
        return super.onSupportNavigateUp()
    }
}