package com.example.attendanceapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.attendanceapp.databinding.ActivityStudentRegisterBinding
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.*
import java.lang.Exception
import java.util.*


class Student_register : AppCompatActivity() {
    private lateinit var binding: ActivityStudentRegisterBinding
    private lateinit var database:DatabaseReference
    private var fname=""
    private var sch=""
    private var email=""
    private var branch=""
    private var sec=""
    private var password=""
    private var cpassword=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStudentRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        database= FirebaseDatabase.getInstance().getReference().child("request")
        binding.RegisterBtn.setOnClickListener {
            validateData()
            //QRCodegenrator()
            Toast.makeText(this,"Wait for Staff Approval",Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.loginnowbtn.setOnClickListener {
            startActivity(Intent(this,student_login::class.java))
            finish()
        }
    }

    private fun validateData() {
        fname=binding.fullname.text.toString().trim()
        sch=binding.Snum.text.toString().trim()
        email=binding.email.text.toString().trim()
        branch=binding.branch.selectedItem.toString()
        sec=binding.sec.selectedItem.toString()
        password=binding.password.text.toString().trim()
        cpassword=binding.conpassword.text.toString().trim()

        if(TextUtils.isEmpty(fname)||TextUtils.isEmpty(sch)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.email.error="Invalid email"
        }
        else if(password.length<1){
            binding.password.error="Password must atleast 6 charachters long"
        }
        else if(!password.equals(cpassword)){
            Toast.makeText(this,"Passwords are not matching",Toast.LENGTH_SHORT).show()
        }
        else{
            database.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(sch)){
                        Toast.makeText(this@Student_register,"$sch already exist",Toast.LENGTH_SHORT).show()
                    }
                    else{

                        database.child(sch).child("fname").setValue(fname)
                        database.child(sch).child("email").setValue(email)
                        database.child(sch).child("snum").setValue(sch)
                        database.child(sch).child("branch").setValue(branch)
                        database.child(sch).child("sec").setValue(sec)
                        database.child(sch).child("password").setValue(password)

                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }
    private fun QRCodegenrator() {
        val ID = binding.Snum.text.toString().trim()

        //val addresse=email.split(",")
        val writer = QRCodeWriter()
        try {
            val bitMatrix=writer.encode(ID,BarcodeFormat.QR_CODE,512,512)
            val bitmap = Bitmap.createBitmap(bitMatrix.width,bitMatrix.height,Bitmap.Config.RGB_565)
            for (x in 0 until bitMatrix.width){
                for (y in 0 until bitMatrix.height){
                    bitmap.setPixel(x,y,if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            val bao: ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG,100,bao)
            val byteArray=bao.toByteArray()
            val imagwBase64= Base64.encodeToString(byteArray, Base64.DEFAULT)
            //val path: String = Images.Media.insertImage(contentResolver, bitmap, "title", null)
            //val screenshotUri = Uri.parse(path)
            @Suppress("Deprecated")
            val fos: OutputStream
            try {
                val resolver = contentResolver
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                    val contentValue = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME,ID+"_QRCode"+".jpg")
                        put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+"QRCODE")
                    }
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValue)
                    fos = resolver.openOutputStream(Objects.requireNonNull(imageUri)!!)!!
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
                    Objects.requireNonNull<OutputStream?>(fos)
                }
            }catch (e: Exception){
                Toast.makeText(this,"image not saved",Toast.LENGTH_SHORT).show()
            }
            database.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(ID)){
                        database.child(ID).child("Image").setValue(imagwBase64)
                            .addOnSuccessListener {  }
                            .addOnFailureListener {  }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }catch (e: WriterException){
            e.printStackTrace()
        }
    }

}


