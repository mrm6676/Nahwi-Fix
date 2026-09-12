package com.example.nahwifix.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HistoryRepository(private val dao: HistoryDao) {
    val allHistory: Flow<List<HistoryEntity>> = dao.getAllHistory()

    suspend fun saveCheck(original: String, corrected: String, issueCount: Int): Long = withContext(Dispatchers.IO) {
        dao.insert(
            HistoryEntity(
                originalText = original,
                correctedText = corrected,
                issueCount = issueCount
            )
        )
    }

    suspend fun deleteCheck(item: HistoryEntity): Int = withContext(Dispatchers.IO) {
        dao.delete(item)
    }

    suspend fun clearHistory(): Int = withContext(Dispatchers.IO) {
        dao.clearAll()
    }
}
