package com.example.attendanceapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.attendanceapp.databinding.ActivityQrcodescannerBinding

public open class qrcodescanner:AppCompatActivity() {
    private lateinit var codescanner: CodeScanner
    private lateinit var binding: ActivityQrcodescannerBinding
    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQrcodescannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),123)
        }else{
            startscanning()
        }
    }
    private fun startscanning() {
        val ScannerView:CodeScannerView=findViewById(R.id.scanner_view)
        codescanner= CodeScanner(this,ScannerView)
        codescanner.camera=CodeScanner.CAMERA_BACK
        codescanner.formats=CodeScanner.ALL_FORMATS
        codescanner.autoFocusMode=AutoFocusMode.SAFE
        codescanner.scanMode=ScanMode.SINGLE
        codescanner.isAutoFocusEnabled=true
        codescanner.formats=CodeScanner.ALL_FORMATS
        codescanner.isFlashEnabled=false
        val intent=getIntent()
        val id=intent.getStringExtra("ID").toString()
        codescanner.decodeCallback= DecodeCallback {
            runOnUiThread {
                if(id==it.text) {
                    val intent = Intent()
                    intent.putExtra("ID",it.text.toString())
                    setResult(1,intent)
                    finish()
                }
                else{
                    Toast.makeText(this, "scan: ${it.text} Wrong Qr Code", Toast.LENGTH_LONG).show()
                }
            }
        }
        codescanner.errorCallback= ErrorCallback {
            runOnUiThread {
                Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
            }
        }
        ScannerView.setOnClickListener {
            codescanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Camera permission granted",Toast.LENGTH_LONG).show()
                startscanning()
            }else{
                Toast.makeText(this,"Camera permission denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(::codescanner.isInitialized){
            codescanner.startPreview()
        }
    }

    override fun onPause() {
        if(::codescanner.isInitialized){
            codescanner.releaseResources()
        }
        super.onPause()
    }
}