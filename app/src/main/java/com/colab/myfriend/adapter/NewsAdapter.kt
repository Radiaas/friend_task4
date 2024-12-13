package com.colab.myfriend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.colab.myfriend.R
import com.colab.myfriend.database.NewsArticle

class NewsAdapter(
    private var newsList: List<NewsArticle>,
    private val onItemClick: (NewsArticle) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_friend, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news, onItemClick)
    }

    override fun getItemCount(): Int = newsList.size

    fun updateData(newNews: List<NewsArticle>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = newsList.size
            override fun getNewListSize(): Int = newNews.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return newsList[oldItemPosition].id == newNews[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return newsList[oldItemPosition] == newNews[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        newsList = newNews
        diffResult.dispatchUpdatesTo(this)
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_news_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_news_description)
        private val newsImageView: ImageView = itemView.findViewById(R.id.img_news)

        fun bind(news: NewsArticle, onItemClick: (NewsArticle) -> Unit) {
            titleTextView.text = news.title
            descriptionTextView.text = news.description ?: "No description available"

            // Load image using Glide
            Glide.with(itemView.context)
                .load(news.imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(newsImageView)

            itemView.setOnClickListener {
                onItemClick(news)
            }
        }
    }
}
