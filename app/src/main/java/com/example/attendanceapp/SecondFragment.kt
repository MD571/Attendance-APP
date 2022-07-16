package com.example.attendanceapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.attendanceapp.databinding.FragmentSecondBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var database: DatabaseReference
private lateinit var cdatabase: DatabaseReference
private var _binding:FragmentSecondBinding? =null
private val binding get() = _binding!!
var bitmap:Bitmap?=null
private lateinit var storageref:StorageReference
private var date:String=SimpleDateFormat("yyyy/LLL/dd", Locale.getDefault()).format(Date()).toString()
private var time:String=SimpleDateFormat("hh:mm a",Locale.getDefault()).format(Date()).toString()
private lateinit var faceuri: Uri
private lateinit var executor:Executor
private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
lateinit var fusedLocationProviderClient: FusedLocationProviderClient
var addresses:List<Address>?=null
private var c1=0
private  var c2=0
/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentSecondBinding.inflate(inflater,container,false)

        return binding.root
    }
        private val activityLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {  result->
            if(result.resultCode==1){
                val intent=result.data
                if (intent != null) {
                    val s=intent.getStringExtra("ID").toString()
                    Toast.makeText(requireContext(),"QR Code: $s",Toast.LENGTH_LONG).show()
                    //binding.QRscan.text="QR Code: "+intent.getStringExtra("ID").toString()
                    binding.cardright1.visibility=View.VISIBLE
                    c1=1
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),arrayOf(android.Manifest.permission.CAMERA),571)
        }
        val ID:String?
        val bundle=arguments
        if (bundle != null) {
            ID = bundle.getString("ID").toString()
            val lat=bundle.getString("lat")
            val lon=bundle.getString("lon")
            database = FirebaseDatabase.getInstance().getReference("student")
            database.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n", "ResourceType")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(ID)) {
                        binding.branchshow.text=getString(R.string.branc)+" "+snapshot.child(ID).child("branch").value as String?
                        binding.secshow.text=getString(R.string.sect)+" "+snapshot.child(ID).child("sec").value as String?
                    }
                    //Toast.makeText(requireContext(),snapshot.child(ID).child("branch").value.toString(),Toast.LENGTH_SHORT).show()
                    //val classshow = find
                    when(snapshot.child(ID).child("branch").value.toString()){
                        "CSE"->binding.classtv.adapter= ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,resources.getStringArray(R.array.CSE))
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })
            binding.QRscanbtn.setOnClickListener {
                val intent=Intent(requireContext(),qrcodescanner::class.java)
                intent.putExtra("ID",ID)
                activityLauncher.launch(intent)
            }
            executor= ContextCompat.getMainExecutor(requireContext())
            biometricPrompt=androidx.biometric.BiometricPrompt(requireActivity(), executor,object:androidx.biometric.BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(),errString,Toast.LENGTH_LONG).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(),"Authentication Failed",Toast.LENGTH_LONG).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    //Toast.makeText(requireContext(),"",Toast.LENGTH_LONG).show()
                    binding.cardright2.visibility=View.VISIBLE
                    c2=1
                }
            } )
            promptInfo=androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Check Fingerprint")
                .setNegativeButtonText("Cancel")
                .build()
            binding.capture.setOnClickListener {
                /*val intent=Intent(requireContext(),face_detector::class.java)
                //intent.putExtra("ID",ID)
                //activityLauncher.launch(intent)
                //biometricPrompt.authenticate(promptInfo)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                @Suppress("Deprecated")
                startActivityForResult(intent, 571)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.cardright2.visibility=View.VISIBLE
                },2000)*/
                biometricPrompt.authenticate(promptInfo)

            }
            //val fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(requireActivity())
            //getCurrentLocation()
            //Toast.makeText(requireContext(),lat.toString()+lon.toString(),Toast.LENGTH_LONG).show()

            binding.save.setOnClickListener {
                time=SimpleDateFormat("hh:mm a",Locale.getDefault()).format(Date()).toString()
                date=SimpleDateFormat("yyyy/LLL/dd", Locale.getDefault()).format(Date()).toString()
                if (c1 == 1 && c2 == 1) {
                    database.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild(ID)) {
                                storageref = FirebaseStorage.getInstance().getReference()
                                //val c:ContentResolver=ContentResolver()
                                //bitmap=MediaStore.Images.Media.getBitmap(contex,faceuri)
                                // val bao: ByteArrayOutputStream = ByteArrayOutputStream()
                                //bitmap!!.compress(Bitmap.CompressFormat.PNG,100,bao)
                                //val byteArray=bao.toByteArray()
                                //val imagwBase64= Base64.encodeToString(byteArray, Base64.DEFAULT)
                                val fname = snapshot.child(ID).child("fname").value.toString()
                                cdatabase = FirebaseDatabase.getInstance().getReference(
                                    "attend/${
                                        snapshot.child(ID).child("branch").value.toString()
                                    }/${binding.classtv.selectedItem.toString()}"
                                )
                                cdatabase.child(date).child(ID).child("ID").setValue(ID)
                                cdatabase.child(date).child(ID).child("fname").setValue(fname)
                                cdatabase.child(date).child(ID).child("sec")
                                    .setValue(snapshot.child(ID).child("sec").value.toString())
                                cdatabase.child(date).child(ID).child("time").setValue(time)
                                cdatabase.child(date).child(ID).child("locat").setValue(lat+","+lon)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show()
                }
                else if(c1==0||c2==0){
                    Toast.makeText(requireContext(), "Give Correct QR Code and Fingerprint", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==571&&resultCode==Activity.RESULT_OK){
            bitmap=data?.getParcelableExtra<Bitmap>("data")
            //binding.imageView.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private fun IntentIntegrator.captureActivity(java: Class<Capture>) {

}

class Capture: CaptureActivity() {

}
