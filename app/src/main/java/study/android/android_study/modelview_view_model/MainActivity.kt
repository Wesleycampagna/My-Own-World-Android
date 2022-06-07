package study.android.android_study.modelview_view_model

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import study.android.android_study.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToCameraBn.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        binding.openFragments.setOnClickListener {
            startActivity(Intent(this, YourActivity::class.java))
        }
    }
}