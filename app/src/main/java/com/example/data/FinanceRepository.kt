package com.example.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val userProfileDao: UserProfileDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertUserProfile(profile)
    }

    suspend fun clearAll() {
        transactionDao.clearTransactions()
        userProfileDao.clearUserProfile()
    }
}
