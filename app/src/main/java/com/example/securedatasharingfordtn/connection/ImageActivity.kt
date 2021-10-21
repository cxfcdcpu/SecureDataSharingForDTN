package com.example.securedatasharingfordtn.connection
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.securedatasharingfordtn.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ImageActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 102
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1
        private const val DELETE_IMAGE_ACTIVITY_REQUEST_CODE = 2
        private const val TAG = "ImageActivity"
        private const val photoName = "photo.jpg" //initial save after photo is taken
        private const val OWN_IMAGE_FOLDER = "own_images"
        private const val COLLECTED_IMAGE_FOLDER = "collected_images"
        private const val MAIN_ACTIVITY = "MainActivity"
        private const val CONNECTION_ACTIVITY = "ConnectionActivity"
    }
    private lateinit var PARENT_ACTIVITY: String

    private lateinit var DEVICE_ID: String
    private lateinit var capturePhotoButton: Button
    private lateinit var sendPhotoTextview: TextView
    private lateinit var photoFile: File
    private var selectedGridviewPosition = 0
    private lateinit var selectedFolder: String

    private lateinit var gridView_own: GridView
    private lateinit var photolist_own: ArrayList<ImageGridItem>
    private lateinit var adapter_own: GridViewAdapter

    private lateinit var gridView_collected: GridView
    private lateinit var photolist_collected: ArrayList<ImageGridItem>
    private lateinit var adapter_collected: GridViewAdapter

    private lateinit var listview: ListView
    private lateinit var photolist: ArrayList<ImageListItem>
    private lateinit var adapter: ListViewAdapter

    /*Capture Photo and Store part*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        PARENT_ACTIVITY = intent.getStringExtra("parent")!!

        DEVICE_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)

        sendPhotoTextview = findViewById(R.id.send_photo_textview)

        //when user click capture photo button
        capturePhotoButton = findViewById(R.id.capture_photo_button)
        capturePhotoButton.setOnClickListener {
            // Function to check and request permission.
            checkPermission(Manifest.permission.CAMERA,ImageActivity.CAMERA_PERMISSION_CODE)
        }

        if (PARENT_ACTIVITY == MAIN_ACTIVITY) {

            findViewById<View>(R.id.image_activity_from_main).visibility = View.VISIBLE
            findViewById<View>(R.id.image_activity_from_con).visibility = View.GONE
            //capturePhotoButton.visibility = Button.VISIBLE
            //sendPhotoTextview.visibility = TextView.GONE

            //for showing captured photos
            gridView_own = findViewById<View>(R.id.gridview_own) as GridView
            photolist_own = getGridItemData(OWN_IMAGE_FOLDER)
            adapter_own = GridViewAdapter(this, R.layout.grid_image_layout, photolist_own)
            gridView_own.adapter = adapter_own

            //for showing collected photos
            gridView_collected = findViewById<View>(R.id.gridview_collected) as GridView
            photolist_collected = getGridItemData(COLLECTED_IMAGE_FOLDER)
            adapter_collected = GridViewAdapter(this, R.layout.grid_image_layout, photolist_collected)
            gridView_collected.adapter = adapter_collected

            //selecting an image from gridview
            gridView_own.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick( parent: AdapterView<*>, view: View, position: Int, id: Long ) {
                    val item = parent.getItemAtPosition(position)
                    switchFullImgAct(position, item as ImageGridItem, OWN_IMAGE_FOLDER)
                }
            }
            gridView_collected.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick( parent: AdapterView<*>, view: View, position: Int, id: Long ) {
                    val item = parent.getItemAtPosition(position)
                    switchFullImgAct(position, item as ImageGridItem, COLLECTED_IMAGE_FOLDER)
                }
            }
        }

        else if (PARENT_ACTIVITY == CONNECTION_ACTIVITY) {
            findViewById<View>(R.id.image_activity_from_main).visibility = View.GONE
            findViewById<View>(R.id.image_activity_from_con).visibility = View.VISIBLE

            //capturePhotoButton.visibility = Button.GONE
            //sendPhotoTextview.visibility = TextView.VISIBLE

            listview = findViewById<View>(R.id.image_list) as ListView
            photolist = getListItemData()
            adapter = ListViewAdapter(this, R.layout.list_image_layout, photolist)
            listview.adapter = adapter
            listview.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val item = parent.getItemAtPosition(position)
                    sendImageAndBack(position, item as ImageListItem)
                }
            }
        }
    }

    /** Function to check and request permission.*/
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
            onLaunchCamera()
        }
    }

    /** This function is called when the user accepts or decline the permission.*/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ImageActivity.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLaunchCamera()
            } else {
                Toast.makeText(this,"Camera Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* Handle selected image */
    // if grid view
    private fun switchFullImgAct(position: Int, item: ImageGridItem, folder: String) {
        selectedGridviewPosition = position
        selectedFolder = folder
        val full_img_intent = Intent(this, SelectedImageActivity::class.java)
        full_img_intent.putExtra("title", item.title)
        full_img_intent.putExtra("folder", folder)
        //full_img_intent.putExtra("image", item.image)
        startActivityForResult(full_img_intent, DELETE_IMAGE_ACTIVITY_REQUEST_CODE)
    }
    // if list view
    private fun sendImageAndBack(position: Int, item: ImageListItem) {
        val intent = Intent()
        intent.putExtra("fileName", item.title)
        intent.putExtra("folder", item.folder)
        setResult(RESULT_OK, intent)
        finish()
    }

    /* Take a photo and save it */
    private fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(Companion.photoName, OWN_IMAGE_FOLDER)

        // wrap File object into a content provider //required for API >= 24
        val fileProvider: Uri = FileProvider.getUriForFile(this, "com.example.basicconnection.fileprovider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        if (intent.resolveActivity(packageManager) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String, folder: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories. // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), folder)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(Companion.TAG, "failed to create directory")
        }

        return File(mediaStorageDir.getPath() + File.separator.toString() + fileName)
    }


    // After photo is taken
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // RESIZE BITMAP
                //val takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoName, OWN_IMAGE_FOLDER))
                // by this point we have the camera photo on disk
                //val rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.path)
                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                //val resizedBitmap: Bitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 640)

                // Load the taken image into a preview
                //val ivPreview: ImageView = findViewById<View>(R.id.ivPreview) as ImageView
                //ivPreview.setImageBitmap(takenImage)

                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                val resizedBitmap: Bitmap = BitmapScaler.scaleToFitWidth(takenImage, 1080)

                val bytes = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
                //val resizedFile = getPhotoFileUri(Companion.photoName.toString() + "_resized", OWN_IMAGE_FOLDER)
                val resizedFileName = DEVICE_ID + "_" + (System.currentTimeMillis()/1000).toString()
                val resizedFile = getPhotoFileUri(resizedFileName, OWN_IMAGE_FOLDER)
                resizedFile.createNewFile()

                val fos = FileOutputStream(resizedFile)
                fos.write(bytes.toByteArray())
                fos.close()

                if (PARENT_ACTIVITY == MAIN_ACTIVITY) {
                    //update the gridview
                    //photolist_own.clear()
                    //photolist_own.addAll(getData(OWN_IMAGE_FOLDER))
                    //adapter_own.notifyDataSetChanged();
                    val rotatedImage = getRotatedImage(resizedBitmap, resizedFile.path)
                    photolist_own.add(ImageGridItem(rotatedImage!!, resizedFileName))
                    adapter_own.notifyDataSetChanged();
                }
            }
            else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == DELETE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //val fileName = data!!.getStringExtra("title")
                val isDeleted = data!!.getBooleanExtra("isDeleted", false)
                if (isDeleted) {
                    if (PARENT_ACTIVITY == MAIN_ACTIVITY) {
                        if (selectedFolder == OWN_IMAGE_FOLDER) {
                            photolist_own.removeAt(selectedGridviewPosition)
                            adapter_own.notifyDataSetChanged();
                        } else if (selectedFolder == COLLECTED_IMAGE_FOLDER) {
                            photolist_collected.removeAt(selectedGridviewPosition)
                            adapter_collected.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    /*Show Photos Part*/

    // Returns the Files stored on disk given the folder
    private fun getDirectoryFileUri(folder: String): Array<File> {
        // Use `getExternalFilesDir` on Context to access package-specific directories. // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), folder)

        if (!mediaStorageDir.exists()) {
            Log.d(Companion.TAG, "directory doesn't exist")
            return emptyArray()
        }

        return mediaStorageDir.listFiles()
    }

    // Prepare data for gridview
    private fun getGridItemData(folder: String): ArrayList<ImageGridItem> {
        val imageGridItems: ArrayList<ImageGridItem> = ArrayList()
        val imgs = getDirectoryFileUri(folder)
        for (img in imgs) {
            val bitmap = BitmapFactory.decodeFile(img.path)
            if(img.name.equals("AES found verification not match, wrong keys")){
                val tmp = BitmapFactory.decodeResource(resources, R.drawable.invalid_perm)
                imageGridItems.add(ImageGridItem(tmp!!, img.name))
            }
            else if(img.name.equals("Policy not satisfied.")){
                val tmp = BitmapFactory.decodeResource(resources, R.drawable.invalid_attr)
                imageGridItems.add(ImageGridItem(tmp!!, img.name))
            }
            else if(img.name.equals("This user is in the revocation list.")) {
                val tmp = BitmapFactory.decodeResource(resources, R.drawable.revoked)
                imageGridItems.add(ImageGridItem(tmp!!, img.name))
            }
            else if(!img.name.equals(photoName)) { //don't want to show default photo when taking

                val rotatedImage = getRotatedImage(bitmap, img.path)
                imageGridItems.add(ImageGridItem(rotatedImage!!, img.name))
            }
        }
        return imageGridItems
    }

    // Prepare data for listview
    private fun getListItemData(): ArrayList<ImageListItem> {
        val ImageListItem: ArrayList<ImageListItem> = ArrayList()
        var imgs = getDirectoryFileUri(OWN_IMAGE_FOLDER)
        for (img in imgs) {
            val bitmap = BitmapFactory.decodeFile(img.path)
            if(!img.name.equals(photoName)) { //don't want to show default photo when taking
                val rotatedImage = getRotatedImage(bitmap, img.path)
                ImageListItem.add(ImageListItem(rotatedImage!!, img.name, false, 10.0, OWN_IMAGE_FOLDER))
            }
        }
        imgs = getDirectoryFileUri(COLLECTED_IMAGE_FOLDER)
        for (img in imgs) {
            val bitmap = BitmapFactory.decodeFile(img.path)
            if(!img.name.equals(photoName)) { //don't want to show default photo when taking
                val rotatedImage = getRotatedImage(bitmap, img.path)
                ImageListItem.add(ImageListItem(rotatedImage!!, img.name, false, 5.0, COLLECTED_IMAGE_FOLDER))
            }
        }
        return ImageListItem
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