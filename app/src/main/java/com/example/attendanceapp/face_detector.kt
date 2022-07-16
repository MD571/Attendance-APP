package com.example.attendanceapp

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.attendanceapp.Helper.RectOverlay
import com.example.attendanceapp.databinding.ActivityFaceDetectorBinding
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.wonderkiln.camerakit.*

private lateinit var binding: ActivityFaceDetectorBinding
class face_detector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFaceDetectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.graphicOverly.clear()
        binding.detectface.setOnClickListener {
            binding.cameraView.start()
            binding.cameraView.captureImage()
        }
        binding.cameraView.addCameraKitListener(object : CameraKitEventListener{
            override fun onEvent(p0: CameraKitEvent?) {

            }

            override fun onError(p0: CameraKitError?) {

            }

            override fun onImage(p0: CameraKitImage?) {
                var bitmap = p0!!.bitmap
                bitmap= Bitmap.createScaledBitmap(bitmap, binding.cameraView.width, binding.cameraView.height,false)
                binding.cameraView.stop()
                runFaceDetector(bitmap)
            }

            override fun onVideo(p0: CameraKitVideo?) {

            }

        })
    }

    private fun runFaceDetector(bitmap: Bitmap?) {
        val image= FirebaseVisionImage.fromBitmap(bitmap!!)
        val option= FirebaseVisionFaceDetectorOptions.Builder().build()
        val detect= FirebaseVision.getInstance().getVisionFaceDetector(option)
        detect.detectInImage(image)
            .addOnSuccessListener { result->processFaceResult(result) }
            .addOnFailureListener { e->Toast.makeText(this,e.message,Toast.LENGTH_LONG).show() }

    }

    private fun processFaceResult(result: List<FirebaseVisionFace>) {
        var count=0
        for(face in result){
            val bounds=face.boundingBox
            val rectOverlay=RectOverlay(binding.graphicOverly,bounds)
            binding.graphicOverly.add(rectOverlay)
            count++
        }
        Toast.makeText(this,count.toString(),Toast.LENGTH_LONG).show()
    }
}