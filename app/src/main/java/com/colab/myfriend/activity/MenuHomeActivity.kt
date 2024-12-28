package com.colab.myfriend.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.colab.myfriend.R
import com.colab.myfriend.adapter.FriendAdapter
import com.colab.myfriend.app.DataProduct
import com.colab.myfriend.btm_sht.BottomSheetFilterProducts
import com.colab.myfriend.btm_sht.BottomSheetSortingProducts
import com.colab.myfriend.databinding.ActivityItemFriendBinding
import com.colab.myfriend.databinding.ActivityMenuHomeBinding
import com.colab.myfriend.viewmodel.FriendViewModel
import com.crocodic.core.base.activity.CoreActivity
import com.crocodic.core.base.adapter.ReactiveListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MenuHomeActivity : CoreActivity<ActivityMenuHomeBinding, FriendViewModel>(R.layout.activity_menu_home) {

    private lateinit var adapter: FriendAdapter
    private var productList = ArrayList<DataProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerView.adapter = adapterCore

        adapter = FriendAdapter(emptyList()) { _ ->
            // Tambahkan logika klik item di sini
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        lifecycleScope.launch {
            viewModel.getProduct()
        }

        lifecycleScope.launch {
            viewModel.product.collect { data ->
                adapter.updateData(data)
                binding.noDataLayout.visibility = View.GONE // Tidak tampil sebelum pencarian
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.product.collect { data ->
                        Timber.tag("API").d("Data Response: $data")

                        adapterCore.submitList(data)
                    }
                }
            }
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchProduct(s.toString())  // Memanggil fungsi pencarian
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ftbnFilter.setOnClickListener {
            val btmSht = BottomSheetFilterProducts { filter ->
                viewModel.filterProducts(filter)
            }

            btmSht.show(supportFragmentManager, "BtmShtFilteringProducts")
        }

        binding.ftbnSort.setOnClickListener {
            val btmSht = BottomSheetSortingProducts { sortBy, order ->
                viewModel.sortProducts(sortBy, order)
            }

            btmSht.show(supportFragmentManager, "BtmShtSortingProducts")
        }

    }

    private val adapterCore by lazy {
        ReactiveListAdapter<ActivityItemFriendBinding, DataProduct>(R.layout.activity_item_friend)
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
