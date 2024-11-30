package com.colab.myfriend.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend")
data class Friend(
    var name: String,
    var school: String,
    var photoPath: String?,
    @ColumnInfo(defaultValue = "")
    var phone: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
