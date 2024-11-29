package com.colab.myfriend.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.colab.myfriend.R
import com.colab.myfriend.database.Friend
import com.colab.myfriend.databinding.ActivityDetailFriendBinding
import com.colab.myfriend.viewmodel.FriendViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.exifinterface.media.ExifInterface
import java.io.File

@AndroidEntryPoint
class DetailFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFriendBinding
    private val viewModel: FriendViewModel by viewModels()
    private var currentFriend: Friend? = null
    private var idFriend: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntentData()
        setupListeners()

        if (idFriend != 0) fetchFriendData()
    }

    private fun handleIntentData() {
        intent?.let {
            idFriend = it.getIntExtra("EXTRA_ID", 0)
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }
        binding.editButton.setOnClickListener {
            val editIntent = Intent(this, EditFriendActivity::class.java).apply {
                putExtra("EXTRA_ID", currentFriend?.id)
                putExtra("EXTRA_NAME", currentFriend?.name)
                putExtra("EXTRA_SCHOOL", currentFriend?.school)
                putExtra("EXTRA_BIO", currentFriend?.bio)
                putExtra("EXTRA_PHOTO_PATH", currentFriend?.photoPath)
                putExtra("EXTRA_PHONE_NUMBER", currentFriend?.phone) // Pastikan phoneNumber disertakan
            }
            editLauncher.launch(editIntent)
      }



        binding.deleteButton.setOnClickListener { showDeleteDialog() }
    }



    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedId = result.data?.getIntExtra("UPDATED_ID", 0) ?: 0
            if (updatedId != 0) {
                fetchFriendData() // Refresh data terbaru setelah diubah
            }
        }
    }



    private fun fetchFriendData() {
        lifecycleScope.launch {
            viewModel.getFriendById(idFriend).collect { friend ->
                currentFriend = friend
                binding.tvName.text = friend?.name ?: ""
                binding.tvSchool.text = friend?.school ?: ""
                binding.tvBio.text = friend?.bio ?: ""
                binding.tvPhoneNumberValue.text = friend?.phone ?: "Not Available" // Tambahkan ini

                friend?.photoPath?.let { path ->
                    val photoFile = File(path)
                    if (photoFile.exists()) {
                        val orientedBitmap = getOrientedBitmap(photoFile.absolutePath)
                        binding.profileImage.setImageBitmap(orientedBitmap)
                    }
                }
            }
        }
    }



    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Friend")
            .setMessage("Are you sure you want to delete this friend?")
            .setPositiveButton("Delete") { _, _ -> deleteFriend() }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun deleteFriend() {
        currentFriend?.let {
            lifecycleScope.launch {
                viewModel.deleteFriend(it)
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
