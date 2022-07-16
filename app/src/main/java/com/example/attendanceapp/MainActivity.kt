package com.example.attendanceapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.example.attendanceapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView.animation=AnimationUtils.loadAnimation(this,R.anim.top_animation)
        binding.textView.animation=AnimationUtils.loadAnimation(this,R.anim.right_animation)
        binding.textView1.animation=AnimationUtils.loadAnimation(this,R.anim.left_animation)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.circle.isVisible=true
            binding.circle.startAnimation(AnimationUtils.loadAnimation(this,R.anim.circle_animation))
            binding.circle.isVisible=false
        },3000)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,student_login::class.java))
            finish()
        },4000)
    }
}