package com.colab.myfriend.database

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.room.RenameColumn

@RenameColumn.Entries(
    RenameColumn(
        tableName = "friend",
        fromColumnName = "phoneNumber",
        toColumnName = "phone"
    )
)

@DeleteColumn(tableName = "friend", columnName = "bio")

class FriendAutoMigrationSpec : AutoMigrationSpec