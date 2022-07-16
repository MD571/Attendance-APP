package com.example.attendanceapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import com.example.attendanceapp.databinding.ActivityStudentLoginBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import java.io.IOException
import java.util.*

class student_login : AppCompatActivity() {
    private lateinit var binding: ActivityStudentLoginBinding
    private lateinit var database: DatabaseReference
    private var sch=""
    private var password=""
    private var checkpassword=""
    private var lat:Double?=null
    private var lon:Double?=null
    private var go:Int?=null
    private lateinit var actionBar: ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStudentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //actionBar=supportActionBar!!
        //supportActionBar?.hide()//to hide action bar
        binding.spchoice.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(adapterView?.getItemAtPosition(position).toString()){
                    "Staff"->{
                        go=2
                        database= FirebaseDatabase.getInstance().getReference().child("Staff")
                    }
                    "Student"->{
                        go=1
                        database= FirebaseDatabase.getInstance().getReference().child("student")
                    }
                    else -> Toast.makeText( this@student_login,"Select who you are", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        getCurrentLocation()
        binding.registernowbtn.setOnClickListener {
            startActivity(Intent(this,Student_register::class.java))
        }
        binding.loginbtn.setOnClickListener{
        validateData()
            //Toast.makeText(this,binding.spchoice.selectedItem.toString(),Toast.LENGTH_LONG).show()
        }

    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                175
            )
        }

        var locationRequest=com.google.android.gms.location.LocationRequest()
        locationRequest.interval=10000
        locationRequest.fastestInterval=5000
        locationRequest.priority=com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        LocationServices.getFusedLocationProviderClient(this@student_login).requestLocationUpdates(locationRequest,object :LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                LocationServices.getFusedLocationProviderClient(this@student_login)
                    .removeLocationUpdates(this)
                if(locationRequest!=null&& p0.locations.size>0){
                    var locIndex=p0.locations.size-1
                    lat=p0.locations.get(locIndex).latitude
                    lon=p0.locations.get(locIndex).longitude
                    //Toast.makeText(this@student_login,lat.toString()+lon.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }, Looper.getMainLooper())
    }
    private fun validateData() {
        //get data
        sch=binding.Snum.text.toString().trim()
        password=binding.password.text.toString().trim()
        if(TextUtils.isEmpty(sch)){
            //Invalid email format
            binding.Snum.error="Please enter your scholar number"
        }
        else if(TextUtils.isEmpty(password)){
            //no password entered
            binding.password.error="Please enter password"
        }
        else{
            database.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(sch)){
                        checkpassword=(snapshot.child(sch).child("password").value).toString()
                        if(checkpassword.equals(password)){
                            Toast.makeText(this@student_login,"Successfully Logged In",Toast.LENGTH_SHORT).show()
                             if(go==1){
                                val i=Intent(this@student_login,Student::class.java)
                                i.putExtra("ID",sch)
                                 i.putExtra("lat",lat.toString())
                                 i.putExtra("lon",lon.toString())
                                startActivity(i)
                            }
                            else if(go==2){
                                val i=Intent(this@student_login,Professor::class.java)
                                i.putExtra("ID",sch)
                                startActivity(i)
                            }
                            else{
                                Toast.makeText(this@student_login,"check student or professor",Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(this@student_login,"Wrong Password",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this@student_login,"No User exist",Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@student_login,p0.toString(),Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

}