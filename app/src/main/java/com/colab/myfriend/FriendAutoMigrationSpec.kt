package com.colab.myfriend

import androidx.room.migration.AutoMigrationSpec
import androidx.room.RenameColumn

@RenameColumn.Entries(
    RenameColumn(
        tableName = "friend",
        fromColumnName = "phoneNumber",
        toColumnName = "phone"
    )
)
class FriendAutoMigrationSpec : AutoMigrationSpec
