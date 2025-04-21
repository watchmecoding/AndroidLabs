package com.example.myapplicationlab5

import android.hardware.*
import android.os.Bundle
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometerReading = FloatArray(3)
    private var magnetometerReading = FloatArray(3)

    private lateinit var arrowImage: ImageView
    private lateinit var directionText: TextView
    private var currentAzimuth: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arrowImage = findViewById(R.id.compass_arrow)
        directionText = findViewById(R.id.direction_text)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    val hasGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerReading = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerReading = event.values.clone()
        }

        if (accelerometerReading.isNotEmpty() && magnetometerReading.isNotEmpty()) {
            val rotationMatrix = FloatArray(9)
            val success = SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )
            if (success) {
                val orientationAngles = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuthInRadians = orientationAngles[0]
                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                val newAzimuth = (azimuthInDegrees + 360) % 360

                // Плавне обертання стрілки та тексту навколо центру
                smoothRotate(arrowImage, currentAzimuth, -newAzimuth)
                smoothRotate(directionText, currentAzimuth, -newAzimuth)

                // Оновлення напрямку з градусами
                val direction = getDirectionLabel(newAzimuth)
                val degreeText = "$direction (${newAzimuth.toInt()}°)"
                directionText.text = degreeText

                currentAzimuth = -newAzimuth
            }
        }
    }

    private fun smoothRotate(view: android.view.View, from: Float, to: Float) {
        val rotateAnimation = RotateAnimation(
            from,
            to,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 200  // Плавний час для анімації
        rotateAnimation.fillAfter = true
        view.startAnimation(rotateAnimation)
    }

    private fun getDirectionLabel(degrees: Float): String {
        return when (degrees) {
            in 337.5..360.0, in 0.0..22.5 -> "Північ"
            in 22.5..67.5 -> "Північний схід"
            in 67.5..112.5 -> "Схід"
            in 112.5..157.5 -> "Південний схід"
            in 157.5..202.5 -> "Південь"
            in 202.5..247.5 -> "Південний захід"
            in 247.5..292.5 -> "Захід"
            in 292.5..337.5 -> "Північний захід"
            else -> ""
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("azimuth", currentAzimuth)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentAzimuth = savedInstanceState.getFloat("azimuth")
        arrowImage.rotation = currentAzimuth
        directionText.rotation = currentAzimuth
    }
}
