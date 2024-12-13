//package com.colab.myfriend.activity
//
//import android.Manifest
//import android.content.ActivityNotFoundException
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Matrix
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.exifinterface.media.ExifInterface
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import com.colab.myfriend.R
//import com.colab.myfriend.database.NewsArticle
//import com.colab.myfriend.databinding.ActivityAddFriendBinding
//import com.colab.myfriend.viewmodel.NewsViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//
//@AndroidEntryPoint
//class AddFriendActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityAddFriendBinding
//    private lateinit var viewModel: NewsViewModel
//    private lateinit var photoFile: File
//    private var oldFriend: NewsArticle? = null
//    private var idFriend: Int = 0
//    private var isImageChanged = false
//
//    private val requestCameraPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) takePhoto() else showToast("Camera permission denied")
//        }
//
//    private val requestStoragePermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) openGallery() else showToast("Storage permission denied")
//        }
//
//    private val cameraLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val rotatedImage = rotateImageIfRequired(photoFile.absolutePath)
//                binding.profileImage.setImageBitmap(rotatedImage)
//                isImageChanged = true
//            }
//        }
//
//    private val galleryLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                saveGalleryImage(result.data?.data ?: return@registerForActivityResult)
//            }
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setupViewBinding()
//        setupEdgeToEdge()
//        initializePhotoFile()
//        initializeViewModel()
//        setupUIListeners()
//
//        if (idFriend != 0) fetchFriendData()
//    }
//
//    private fun setupViewBinding() {
//        binding = ActivityAddFriendBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//    }
//
//    private fun setupEdgeToEdge() {
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//
//    private fun initializePhotoFile() {
//        photoFile = try {
//            createImageFile()
//        } catch (e: IOException) {
//            showToast("Cannot create Image File")
//            return
//        }
//    }
//
//    private fun initializeViewModel() {
//        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
//        idFriend = intent.getIntExtra("id", 0)
//    }
//
//    private fun setupUIListeners() {
//        binding.saveButton.setOnClickListener { showSaveDialog() }
//        binding.backButton.setOnClickListener { navigateToHome() }
//        binding.cameraButton.setOnClickListener { showInsertPhotoDialog() }
//    }
//
//    private fun navigateToHome() {
//        val intent = Intent(this, MenuHomeActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun showInsertPhotoDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_insert_photo, null)
//        val alertDialog = AlertDialog.Builder(this).setView(dialogView).create()
//
//        dialogView.findViewById<TextView>(R.id.from_camera).setOnClickListener {
//            if (hasPermission(Manifest.permission.CAMERA)) takePhoto()
//            else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//            alertDialog.dismiss()
//        }
//
//        dialogView.findViewById<TextView>(R.id.pick_gallery).setOnClickListener {
//            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) openGallery()
//            else requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//            alertDialog.dismiss()
//        }
//
//        alertDialog.show()
//    }
//
//    private fun takePhoto() {
//        val photoUri = FileProvider.getUriForFile(this, "com.colab.myfriend.fileprovider", photoFile)
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
//            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//        }
//
//        try {
//            cameraLauncher.launch(cameraIntent)
//        } catch (e: ActivityNotFoundException) {
//            showToast("Cannot use Camera")
//        }
//    }
//
//    private fun openGallery() {
//        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        galleryLauncher.launch(galleryIntent)
//    }
//
//    private fun fetchFriendData() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.getFriendById(idFriend).collect { friend ->
//                    oldFriend = friend
//                    binding.etName.setText(friend?.name)
//                    binding.etSchool.setText(friend?.school)
//                    binding.etPhoneNumber.setText(friend?.phone) // Tambahkan ini
//                    loadProfileImage(friend?.photoPath)
//                }
//            }
//        }
//    }
//
//    private fun loadProfileImage(photoPath: String?) {
//        if (!photoPath.isNullOrEmpty()) {
//            binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(photoPath))
//            isImageChanged = false
//        }
//    }
//
//    private fun showSaveDialog() {
//        if (isFormValid()) {
//            AlertDialog.Builder(this)
//                .setTitle("Add Friend")
//                .setMessage("Are you sure you want to save this friend?")
//                .setPositiveButton("Save") { _, _ -> saveFriendData() }
//                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//                .create()
//                .show()
//        }
//    }
//
//    private fun isFormValid(): Boolean {
//        val name = binding.etName.text.toString().trim()
//        val school = binding.etSchool.text.toString().trim()
//        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
//
//        return when {
//            name.isEmpty() -> showToast("Please fill in the name").let { false }
//            school.isEmpty() -> showToast("Please fill in the school").let { false }
//            phoneNumber.isEmpty() -> showToast("Please fill in the phone number").let { false }
//            !isImageChanged -> showToast("Please change the image").let { false }
//            else -> true
//        }
//    }
//
//    private fun saveFriendData() {
//        val name = binding.etName.text.toString().trim()
//        val school = binding.etSchool.text.toString().trim()
//        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
//
//        lifecycleScope.launch {
//            if (oldFriend == null) {
//                viewModel.insertFriend(
//                    NewsArticle(name, school, photoFile.absolutePath, phoneNumber)
//                )
//            } else {
//                viewModel.editFriend(
//                    oldFriend!!.copy(
//                        name = name,
//                        school = school,
//                        phone = phoneNumber,
//                        photoPath = photoFile.absolutePath
//                    ).apply { id = idFriend }
//                )
//            }
//            showToast("Friend saved")
//            finish()
//        }
//    }
//
//    private fun createImageFile(): File {
//        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile("PHOTO_", ".jpg", storageDir)
//    }
//
//    private fun rotateImageIfRequired(imagePath: String): Bitmap {
//        val bitmap = BitmapFactory.decodeFile(imagePath)
//        val ei = ExifInterface(imagePath)
//        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
//
//        return when (orientation) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
//            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
//            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
//            else -> bitmap
//        }
//    }
//
//    private fun rotateImage(bitmap: Bitmap, degree: Float): Bitmap {
//        val matrix = Matrix().apply { postRotate(degree) }
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//    }
//
//    private fun saveGalleryImage(uri: android.net.Uri) {
//        val inputStream = contentResolver.openInputStream(uri)
//        val outputStream = FileOutputStream(photoFile)
//
//        inputStream?.use { input ->
//            outputStream.use { output ->
//                input.copyTo(output)
//            }
//        }
//
//        val rotatedImage = rotateImageIfRequired(photoFile.absolutePath)
//        binding.profileImage.setImageBitmap(rotatedImage)
//        isImageChanged = true
//    }
//
//    private fun hasPermission(permission: String) =
//        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
//
//    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//}
