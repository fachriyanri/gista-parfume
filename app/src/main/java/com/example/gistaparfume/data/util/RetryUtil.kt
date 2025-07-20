package com.example.gistaparfume.data.util

import com.example.gistaparfume.data.error.CategoryError
import com.example.gistaparfume.data.error.toCategoryError
import kotlinx.coroutines.delay
import kotlin.math.pow

/**
 * Utility class for implementing retry mechanisms with exponential backoff
 */
object RetryUtil {
    
    /**
     * Default retry configuration
     */
    data class RetryConfig(
        val maxAttempts: Int = 3,
        val initialDelayMs: Long = 1000L,
        val maxDelayMs: Long = 10000L,
        val backoffMultiplier: Double = 2.0,
        val retryableErrors: Set<Class<out Throwable>> = setOf(
            java.net.UnknownHostException::class.java,
            java.net.SocketTimeoutException::class.java,
            java.net.ConnectException::class.java,
            java.io.IOException::class.java
        )
    )
    
    /**
     * Executes an operation with retry logic and exponential backoff
     * @param config Retry configuration
     * @param operation The operation to execute
     * @return Result of the operation
     */
    suspend fun <T> withRetry(
        config: RetryConfig = RetryConfig(),
        operation: suspend () -> T
    ): Result<T> {
        var lastException: Throwable? = null
        
        repeat(config.maxAttempts) { attempt ->
            try {
                return Result.success(operation())
            } catch (e: Throwable) {
                lastException = e
                
                // Check if this error is retryable
                val isRetryable = config.retryableErrors.any { retryableError ->
                    retryableError.isAssignableFrom(e::class.java)
                }
                
                // If not retryable or this is the last attempt, don't retry
                if (!isRetryable || attempt == config.maxAttempts - 1) {
                    return Result.failure(lastException?.toCategoryError() ?: CategoryError.UnknownError())
                }
                
                // Calculate delay with exponential backoff
                val delayMs = calculateDelay(
                    attempt = attempt,
                    initialDelay = config.initialDelayMs,
                    maxDelay = config.maxDelayMs,
                    backoffMultiplier = config.backoffMultiplier
                )
                
                delay(delayMs)
            }
        }
        
        // All attempts failed, return the last exception
        return Result.failure(lastException?.toCategoryError() ?: CategoryError.UnknownError())
    }
    
    /**
     * Executes a database operation with retry logic
     * @param operation The database operation to execute
     * @return Result of the operation
     */
    suspend fun <T> withDatabaseRetry(
        operation: suspend () -> T
    ): Result<T> {
        val databaseRetryConfig = RetryConfig(
            maxAttempts = 2, // Fewer retries for database operations
            initialDelayMs = 500L,
            maxDelayMs = 2000L,
            retryableErrors = setOf(
                android.database.sqlite.SQLiteDatabaseLockedException::class.java
            )
        )
        
        return withRetry(databaseRetryConfig, operation)
    }
    
    /**
     * Executes a network operation with retry logic
     * @param operation The network operation to execute
     * @return Result of the operation
     */
    suspend fun <T> withNetworkRetry(
        operation: suspend () -> T
    ): Result<T> {
        val networkRetryConfig = RetryConfig(
            maxAttempts = 3,
            initialDelayMs = 1000L,
            maxDelayMs = 8000L,
            retryableErrors = setOf(
                java.net.UnknownHostException::class.java,
                java.net.SocketTimeoutException::class.java,
                java.net.ConnectException::class.java,
                java.io.IOException::class.java,
                javax.net.ssl.SSLException::class.java
            )
        )
        
        return withRetry(networkRetryConfig, operation)
    }
    
    /**
     * Calculates delay for exponential backoff
     */
    private fun calculateDelay(
        attempt: Int,
        initialDelay: Long,
        maxDelay: Long,
        backoffMultiplier: Double
    ): Long {
        val exponentialDelay = (initialDelay * backoffMultiplier.pow(attempt)).toLong()
        return minOf(exponentialDelay, maxDelay)
    }
    
    /**
     * Checks if an exception is retryable based on its type and message
     */
    fun isRetryableException(exception: Throwable): Boolean {
        return when (exception) {
            is java.net.UnknownHostException,
            is java.net.SocketTimeoutException,
            is java.net.ConnectException,
            is java.io.IOException -> true
            is android.database.sqlite.SQLiteDatabaseLockedException -> true
            else -> false
        }
    }
    
    /**
     * Gets user-friendly error message for retryable operations
     */
    fun getRetryErrorMessage(exception: Throwable, attemptNumber: Int, maxAttempts: Int): String {
        val baseMessage = when (exception) {
            is java.net.UnknownHostException -> "Tidak dapat terhubung ke server"
            is java.net.SocketTimeoutException -> "Koneksi timeout"
            is java.net.ConnectException -> "Gagal terhubung ke server"
            is android.database.sqlite.SQLiteDatabaseLockedException -> "Database sedang sibuk"
            else -> "Terjadi kesalahan"
        }
        
        return if (attemptNumber < maxAttempts) {
            "$baseMessage. Mencoba lagi... (${attemptNumber + 1}/$maxAttempts)"
        } else {
            "$baseMessage. Silakan coba lagi nanti."
        }
    }
}