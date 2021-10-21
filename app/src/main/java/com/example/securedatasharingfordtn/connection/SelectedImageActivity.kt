package com.example.securedatasharingfordtn.connection
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.securedatasharingfordtn.R
import java.io.File


class SelectedImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_image)

        val title = intent.getStringExtra("title")
        val folder = intent.getStringExtra("folder")
        //val bitmap = intent.getParcelableExtra<Bitmap>("image")
        val image = getPhotoFileUri(title, folder)
        val bitmap = BitmapFactory.decodeFile(image.path)
        val rotatedImage = getRotatedImage(bitmap, image.path)

        val titleTextView = findViewById<View>(R.id.selected_title) as TextView
        titleTextView.text = title

        val imageView: ImageView = findViewById<View>(R.id.selected_image) as ImageView
        imageView.setImageBitmap(rotatedImage)

        //when user click delete
        val deleteImageButton = findViewById<Button>(R.id.delete_image_button)
        deleteImageButton.setOnClickListener {
            if (image.exists()) {
                image.delete()
                val intent = Intent()
                intent.putExtra("isDeleted", true)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String?, folder: String?): File {
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), folder)

        if (!mediaStorageDir.exists()) {
            Log.d("Selected_Image", "directory doesn't exist")
        }

        return File(mediaStorageDir.getPath() + File.separator.toString() + fileName)
    }

    private fun getRotatedImage(image: Bitmap, path: String): Bitmap? {
//        val ei = ExifInterface(path)
//        val orientation: Int = ei.getAttributeInt(
//            ExifInterface.TAG_ORIENTATION,
//            ExifInterface.ORIENTATION_UNDEFINED
//        )
//
//        return when (orientation) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90)
//            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180)
//            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270)
//            ExifInterface.ORIENTATION_NORMAL -> image
//            else -> image
//        }
        return rotateImage(image, 90)
    }

    private fun rotateImage(image: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            image, 0, 0, image.width, image.height,
            matrix, true
        )
    }
}