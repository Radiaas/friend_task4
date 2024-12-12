package com.colab.myfriend.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend")
data class Friend(
    var name: String, // Required field
    var school: String, // Required field
    var photoPath: String? = null, // Nullable field with default value
    @ColumnInfo(defaultValue = "") var phone: String = "" // Default value to avoid null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // Auto-generated primary key
}
