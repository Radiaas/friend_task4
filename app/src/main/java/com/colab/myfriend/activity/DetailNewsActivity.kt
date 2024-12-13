package com.colab.newsapp.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.exifinterface.media.ExifInterface
import com.colab.myfriend.database.NewsArticle
import com.colab.myfriend.databinding.ActivityDetailFriendBinding
import com.colab.myfriend.viewmodel.NewsViewModel
import java.io.File

@AndroidEntryPoint
class DetailNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFriendBinding
    private val viewModel: NewsViewModel by viewModels()
    private var currentNews: NewsArticle? = null
    private var newsId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntentData()
        setupListeners()

        if (newsId != 0) fetchNewsData()
    }

    private fun handleIntentData() {
        intent?.let {
            newsId = it.getIntExtra("EXTRA_ID", 0)
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.deleteButton.setOnClickListener { showDeleteDialog() }
    }

    private fun fetchNewsData() {
        lifecycleScope.launch {
            viewModel.getNewsById(newsId).collect { news ->
                currentNews = news
                binding.tvTitle.text = news?.title ?: ""
                binding.tvContent.text = news?.description ?: ""

                news?.imageUrl?.let { path ->
                    val imageFile = File(path)
                    if (imageFile.exists()) {
                        val orientedBitmap = getOrientedBitmap(imageFile.absolutePath)
                        binding.newsImage.setImageBitmap(orientedBitmap)
                    }
                }
            }
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete News")
            .setMessage("Are you sure you want to delete this news?")
            .setPositiveButton("Delete") { _, _ -> deleteNews() }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun deleteNews() {
        currentNews?.let {
            lifecycleScope.launch {
                viewModel.deleteNews(it)
                finish()
            }
        }
    }

    private fun getOrientedBitmap(filePath: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(filePath)
        val exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
