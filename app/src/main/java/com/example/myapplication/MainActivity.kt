package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException
import android.widget.Button

class MainActivity : AppCompatActivity() {
    var eyeState: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var  text1: TextView = findViewById(R.id.text1)

        val image: ImageView = findViewById(R.id.imageView)
        val button: Button = findViewById(R.id.eyestate)
        val image_name = "Both_eyes_are_close.jpg"
        val bitmap: Bitmap? = assetsToBitmap(image_name)
        bitmap?.apply { image.setImageBitmap(this) }
        button.setOnClickListener {
            val highAccuracyOpts = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val classifier = FaceDetection.getClient(highAccuracyOpts)
            val input_image = InputImage.fromBitmap(bitmap!!,270)

            val result = classifier.process(input_image).addOnSuccessListener {
                    faces -> findEyeState(faces,text1)

            }.addOnFailureListener{e -> }

        }}

    fun findEyeState(faces: List<Face>,text1:TextView) {
        for (face in faces){
            val leftEyeIsOpen =  face.leftEyeOpenProbability
            val rightEyeIsOpen = face.rightEyeOpenProbability
            if (leftEyeIsOpen != null && rightEyeIsOpen != null) {
                if (leftEyeIsOpen > 0.5) {
                    if (rightEyeIsOpen > 0.5) {
                        eyeState = "Both Eyes are Open \uD83D\uDE03"
                    }else{ eyeState = "Left Eyes is Open \uD83D\uDE09" }
                }
                else if (rightEyeIsOpen > 0.5){
                    eyeState = "Right Eyes is Open \uD83D\uDE1C"
                }else{
                    eyeState = "Both Eyes are Close \uD83D\uDE0C"
                }
            }
            text1.text = eyeState
        }

    }

    fun Context.assetsToBitmap(imagename: String): Bitmap?{
        return try{with(assets.open(imagename)){BitmapFactory.decodeStream(this)}} catch(e: IOException) {null}
    }
}