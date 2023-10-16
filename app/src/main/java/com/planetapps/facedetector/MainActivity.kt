package com.planetapps.facedetector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()






        var buttonCamera= findViewById<Button>(R.id.btnCamera)

        buttonCamera.setOnClickListener {
            val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, 123)
            } else{
                Toast.makeText(this, "Oops something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==123 && resultCode== RESULT_OK){
            val extras= data?.extras
            val bitmap= extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap){

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector= FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully, our face is successfully detected
                var resulText= " "
                var i= 1
                for(face in faces){
                    resulText= "Face Number : $i" +
                            "\nSmile : ${face.smilingProbability?.times(100)}%" +
                            "\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\nRight Eye Open : ${face.rightEyeOpenProbability?.times(100)}%"
                    i++
                }

                if(faces.isEmpty()){
                  Toast.makeText(this, "NO FACE DETECTED", Toast.LENGTH_SHORT).show()
                } else{
                   // Toast.makeText(this, resulText, Toast.LENGTH_LONG).show()

                    //Custom Toast
                    val inflater = layoutInflater
                    val customToastLayout = inflater.inflate(R.layout.custom_toast_layout, null)
                    val textView = customToastLayout.findViewById<TextView>(R.id.toast_text)
                    textView.text = resulText
                    val customToast = Toast(applicationContext)
                    customToast.duration = Toast.LENGTH_LONG// Set the duration
                    customToast.view = customToastLayout // Set the custom layout
                    customToast.show()

                    Log.d("TAG" , resulText)
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception, face detection is failed
                Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
            }

    }
}
