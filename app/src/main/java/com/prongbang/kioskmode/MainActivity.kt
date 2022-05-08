package com.prongbang.kioskmode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.prongbang.kiosk.AndroidDeviceAdminReceiver
import com.prongbang.kiosk.AndroidDevicePolicyManager
import com.prongbang.kioskmode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val androidDevicePolicyManager by lazy {
        AndroidDevicePolicyManager(
            this,
            AndroidDeviceAdminReceiver.getComponentName(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {

            if (androidDevicePolicyManager.isDeviceOwnerApp()) {
                Toast.makeText(applicationContext, "You are admin", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "You are not admin", Toast.LENGTH_SHORT).show()
            }

            startLockTaskButton.setOnClickListener {
                androidDevicePolicyManager.startLockTask()
            }

            stopLockTaskButton.setOnClickListener {
                androidDevicePolicyManager.stopLockTaskAndStartActivity(MainActivity::class.java)
            }
        }
    }
}