package com.kienht.imagedrawing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * @author kienht
 * @company OICSoft
 * @since 25/10/2019
 */
class MainActivity : AppCompatActivity() {

    private val PICK_IMG_REQUEST_CODE =  1000

    lateinit var drawImageView: ImageDrawingView
    lateinit var imageTest: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawImageView = findViewById<ImageDrawingView>(R.id.image_drawing)
        drawImageView.loadImage(R.drawable.iphonex)

        imageTest = findViewById<ImageView>(R.id.image_test)

        val btnSelectImg = findViewById<ImageButton>(R.id.button_select_image)
        btnSelectImg.setOnClickListener {
            openGalleryForImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_photo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_done -> {
                getBitmap()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMG_REQUEST_CODE){
//            drawImageView.setImageURI(data?.data) // handle chosen image
            data?.data?.let { uri ->
//                URIPathHelper().getPath(this, uri)?.let {
//                    Log.d("chi.trinh", "onActivityResult: $it")
//                    drawImageView.loadImage(it)
//                }
                drawImageView.loadImage(uri)
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMG_REQUEST_CODE)
    }


    private fun getBitmap() {
        lifecycleScope.launch {
            drawImageView.showLoading()
            val path = withContext(Dispatchers.IO) {
                val bitmap = drawImageView.createBitmap()
                getScreenshotPath()
                    .takeUnless { it.isEmpty() }
                    ?.let { bitmap.saveToFile(it) }
                    ?.absolutePath
            }
            drawImageView.hideLoading()
            Glide.with(imageTest).clear(imageTest)
            Glide.with(imageTest)
                .load(path)
                .dontAnimate()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageTest)
        }
    }
}

fun Context.getScreenshotPath(): String {
    val folderPath = "${cacheDir}/screenshot"
    val folder = File(folderPath)
    if (!folder.exists()) {
        folder.mkdirs()
    }
    return "${folderPath}/${System.currentTimeMillis()}.png"
}

fun Bitmap.saveToFile(path: String): File? {
    return try {
        val file = File(path)
        file.createNewFile()
        FileOutputStream(file).use { out -> this.compress(Bitmap.CompressFormat.PNG, 100, out) }
        file
    } catch (e: Exception) {
        null
    }
}