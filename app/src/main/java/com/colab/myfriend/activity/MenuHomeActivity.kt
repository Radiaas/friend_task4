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
import com.colab.myfriend.adapter.NewsAdapter
import com.colab.myfriend.databinding.ActivityMenuHomeBinding
import com.colab.myfriend.viewmodel.NewsViewModel
import com.colab.newsapp.activity.DetailNewsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuHomeBinding
    private lateinit var adapter: NewsAdapter
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize adapter with empty list and onItemClick callback
        adapter = NewsAdapter(emptyList()) { news ->
            val intent = Intent(this, DetailNewsActivity::class.java).apply {
                putExtra("EXTRA_TITLE", news.title)
                putExtra("EXTRA_DESCRIPTION", news.description)
                putExtra("EXTRA_IMAGE_URL", news.imageUrl)
                putExtra("EXTRA_ID", news.id)
            }
            startActivity(intent)
        }

        // Setup RecyclerView with GridLayoutManager
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        // Observe data from ViewModel
        lifecycleScope.launch {
            viewModel.getAllNews().collect { newsList ->
                adapter.updateData(newsList)
            }
        }

        // Search bar functionality
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchNews(s.toString())  // Call search function
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Button to add new news (if applicable)
//        binding.btnAddFriend.setOnClickListener {
            // Redirect to AddNewsActivity or remove this button if unnecessary
//            val intent = Intent(this, AddNewsActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun searchNews(keyword: String) {
        lifecycleScope.launch {
            viewModel.searchNews(keyword).collect { results ->
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
