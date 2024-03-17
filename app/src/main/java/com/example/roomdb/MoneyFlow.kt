package com.example.roomdb

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(tableName = "MoneyFlow",
foreignKeys = [ForeignKey
    (entity = User::class, parentColumns = ["id_user"], childColumns = ["user_id"], onDelete = CASCADE)
])
data class MoneyFlow(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_flow")
    var id: Int?,
    var category: String,
    var amount: Double,
    var user_id: Int?
)