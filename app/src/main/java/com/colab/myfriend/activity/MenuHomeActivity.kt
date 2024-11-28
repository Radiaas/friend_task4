package com.colab.myfriend.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.colab.myfriend.adapter.FriendAdapter
import com.colab.myfriend.database.Friend
import com.colab.myfriend.databinding.ActivityMenuHomeBinding
import com.colab.myfriend.viewmodel.FriendViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuHomeBinding
    private lateinit var adapter: FriendAdapter
    private val viewModel: FriendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menginisialisasi adapter dengan listener untuk klik item
        adapter = FriendAdapter(emptyList()) { friend ->
            val intent = Intent(this, DetailFriendActivity::class.java).apply {
                putExtra("EXTRA_NAME", friend.name)
                putExtra("EXTRA_SCHOOL", friend.school)
                putExtra("EXTRA_BIO", friend.bio)
                putExtra("EXTRA_IMAGE_PATH", friend.photoPath)
                putExtra("EXTRA_ID", friend.id)
            }
            startActivity(intent)
        }

        // Menetapkan GridLayoutManager pada RecyclerView
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        // Observasi data teman dari ViewModel
        lifecycleScope.launch {
            viewModel.getFriend().collect { friends ->
                adapter.updateData(friends)  // Memperbarui data pada adapter
            }
        }

        // Menambahkan TextWatcher untuk pencarian
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchFriends(s.toString())  // Memanggil fungsi pencarian
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Menangani tombol "Add Friend"
        binding.btnAddFriend.setOnClickListener {
            val intent = Intent(this, AddFriendActivity::class.java)
            startActivity(intent)
        }
    }

    // Fungsi pencarian menggunakan ViewModel
    private fun searchFriends(keyword: String) {
        lifecycleScope.launch {
            viewModel.searchFriend(keyword).collect { results ->
                if (results.isEmpty() && keyword.isNotEmpty()) {
                    binding.noDataLayout.visibility = View.VISIBLE
                } else {
                    binding.noDataLayout.visibility = View.GONE
                }
                adapter.updateData(results)
            }
        }
    }

}
