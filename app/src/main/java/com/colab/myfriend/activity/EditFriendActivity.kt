package com.colab.myfriend.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.colab.myfriend.databinding.ActivityEditFriendBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import androidx.exifinterface.media.ExifInterface
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.colab.myfriend.database.Friend
import com.colab.myfriend.viewmodel.FriendViewModel
import com.colab.myfriend.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditFriendBinding
    private lateinit var photoFile: File
    private var oldFriend: Friend? = null
    private var idFriend: Int = 0
    private var currentPhotoPath: String? = null
    private val viewModel: FriendViewModel by viewModels()


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: return@registerForActivityResult
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val outputStream = FileOutputStream(photoFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                parcelFileDescriptor.close()

                val orientedBitmap = getOrientedBitmap(photoFile.absolutePath)
                binding.profileImage.setImageBitmap(orientedBitmap)
                currentPhotoPath = photoFile.absolutePath
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val orientedBitmap = getOrientedBitmap(photoFile.absolutePath)
                binding.profileImage.setImageBitmap(orientedBitmap)
                currentPhotoPath = photoFile.absolutePath
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        photoFile = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Cannot create Image File", Toast.LENGTH_SHORT).show()
            return
        }

        idFriend = intent.getIntExtra("EXTRA_ID", 0)
        val name = intent.getStringExtra("EXTRA_NAME")
        val school = intent.getStringExtra("EXTRA_SCHOOL")
        currentPhotoPath = intent.getStringExtra("EXTRA_PHOTO_PATH")
        val phoneNumber = intent.getStringExtra("EXTRA_PHONE_NUMBER")
        binding.etPhoneNumber.setText(phoneNumber)


        binding.etName.setText(name)
        binding.etSchool.setText(school)

        currentPhotoPath?.let {
            val photoFile = File(it)
            if (photoFile.exists()) {
                val orientedBitmap = getOrientedBitmap(photoFile.absolutePath)
                binding.profileImage.setImageBitmap(orientedBitmap)
            } else {
                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
            }
        }

        if (idFriend != 0) {
            getFriendData()
        }

        binding.saveButton.setOnClickListener {
            showSaveDialog()
        }

        binding.backButton.setOnClickListener {
            navigateToDetailFriend()
        }

        binding.cameraButton.setOnClickListener {
            showInsertPhotoDialog()
        }

        setupKeyboardHandling()
    }

    private fun setupKeyboardHandling() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                val focusedView = currentFocus
                focusedView?.post {
                    focusedView.scrollIntoView()
                }
            }
        }
    }

    private fun showInsertPhotoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_insert_photo, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val alertDialog = dialogBuilder.create()

        val fromCamera = dialogView.findViewById<TextView>(R.id.from_camera)
        val pickGallery = dialogView.findViewById<TextView>(R.id.pick_gallery)

        fromCamera.setOnClickListener {
            takePhoto()
            alertDialog.dismiss()
        }

        pickGallery.setOnClickListener {
            openGallery()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun takePhoto() {
        val photoUri = FileProvider.getUriForFile(this, "com.colab.myfriend.fileprovider", photoFile)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        try {
            cameraLauncher.launch(cameraIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Cannot use Camera", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun getFriendData() {
        lifecycleScope.launch {
            viewModel.getFriendById(idFriend).collect { friend ->
                oldFriend = friend
                binding.etName.setText(friend?.name)
                binding.etSchool.setText(friend?.school)
                binding.etPhoneNumber.setText(friend?.phone) // Tambahkan ini
                friend?.photoPath?.let { path ->
                    val photoFile = File(path)
                    if (photoFile.exists()) {
                        val orientedBitmap = getOrientedBitmap(photoFile.absolutePath)
                        binding.profileImage.setImageBitmap(orientedBitmap)
                        currentPhotoPath = path
                    }
                }
            }
        }
    }

    private fun showSaveDialog() {
        AlertDialog.Builder(this)
            .setTitle("Edit Friend")
            .setMessage("Are you sure you want to save this friend's details?")
            .setPositiveButton("Save") { _, _ ->
                saveFriendData()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun saveFriendData() {
        val name = binding.etName.text.toString().trim()
        val school = binding.etSchool.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()

        val photoPathToSave = currentPhotoPath ?: photoFile.absolutePath

        val friendData = if (oldFriend == null) {
            Friend(name, school, photoPathToSave, phoneNumber)
        } else {
            oldFriend!!.copy(
                name = name,
                school = school,
                photoPath = photoPathToSave,
                phone = phoneNumber
            ).apply {
                id = idFriend
            }
        }

        lifecycleScope.launch {
            if (oldFriend == null) {
                viewModel.insertFriend(friendData)
            } else {
                viewModel.editFriend(friendData)
            }
            navigateToDetailFriend()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_", ".jpg", storageDir)
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

    private fun navigateToDetailFriend() {
        val destination = Intent(this, DetailFriendActivity::class.java).apply {
            putExtra("EXTRA_ID", idFriend)
        }
        startActivity(destination)
        finish()
    }

    private fun View.scrollIntoView() {
        post {
            val rect = Rect()
            getWindowVisibleDisplayFrame(rect)
            val scrollY = bottom - rect.bottom
            if (scrollY > 0) {
                scrollBy(0, scrollY)
            }
        }
    }
}