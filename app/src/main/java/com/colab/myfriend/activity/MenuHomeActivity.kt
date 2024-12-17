package com.colab.myfriend.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.colab.myfriend.adapter.FriendAdapter
import com.colab.myfriend.app.DataProduct
import com.colab.myfriend.databinding.ActivityMenuHomeBinding
import com.colab.myfriend.viewmodel.FriendViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuHomeBinding
    private lateinit var adapter: FriendAdapter
    private val viewModel: FriendViewModel by viewModels()
    private var productList = ArrayList<DataProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FriendAdapter(emptyList()) { _ ->
            // Tambahkan logika klik item di sini
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.getProduct()
        }

        lifecycleScope.launch {
            viewModel.product.collect { data ->
                adapter.updateData(data)
                binding.noDataLayout.visibility = View.GONE // Tidak tampil sebelum pencarian
            }
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchProduct(s.toString())  // Memanggil fungsi pencarian
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchProduct(keyword: String) {
        lifecycleScope.launch {
            viewModel.searchProduct(keyword).collect { results ->
                // Bersihkan data lama terlebih dahulu
                productList.clear()

                if (results.isEmpty() && keyword.isNotEmpty()) {
                    // Tampilkan layout "No Data" jika hasil pencarian kosong
                    binding.noDataLayout.visibility = View.VISIBLE
                    adapter.updateData(emptyList()) // Hapus semua data yang ada di adapter
                } else {
                    // Sembunyikan layout "No Data" jika ada hasil
                    binding.noDataLayout.visibility = View.GONE
                    productList.addAll(results)
                    adapter.updateData(productList) // Perbarui adapter dengan hasil pencarian
                }
            }
        }
    }

}