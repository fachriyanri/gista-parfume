package com.example.gistaparfume.data.error

/**
 * Sealed class representing different types of errors that can occur in category operations
 */
sealed class CategoryError : Exception() {
    
    /**
     * Network-related errors
     */
    data class NetworkError(
        override val message: String = "Koneksi bermasalah. Periksa koneksi internet Anda.",
        override val cause: Throwable? = null
    ) : CategoryError()
    
    /**
     * Database-related errors
     */
    data class DatabaseError(
        override val message: String = "Terjadi kesalahan pada database. Silakan coba lagi.",
        override val cause: Throwable? = null
    ) : CategoryError()
    
    /**
     * Validation errors for form fields
     */
    data class ValidationError(
        val field: String,
        override val message: String
    ) : CategoryError()
    
    /**
     * Duplicate data errors
     */
    data class DuplicateError(
        val field: String,
        override val message: String = "Data sudah ada. Silakan gunakan data yang berbeda."
    ) : CategoryError()
    
    /**
     * Resource not found errors
     */
    data class NotFoundError(
        override val message: String = "Data tidak ditemukan."
    ) : CategoryError()
    
    /**
     * Permission or authorization errors
     */
    data class PermissionError(
        override val message: String = "Anda tidak memiliki izin untuk melakukan operasi ini."
    ) : CategoryError()
    
    /**
     * Generic server errors
     */
    data class ServerError(
        override val message: String = "Terjadi kesalahan pada server. Silakan coba lagi nanti.",
        val code: Int? = null
    ) : CategoryError()
    
    /**
     * Timeout errors
     */
    data class TimeoutError(
        override val message: String = "Operasi timeout. Silakan coba lagi."
    ) : CategoryError()
    
    /**
     * Unknown or unexpected errors
     */
    data class UnknownError(
        override val message: String = "Terjadi kesalahan yang tidak terduga. Silakan coba lagi.",
        override val cause: Throwable? = null
    ) : CategoryError()
}

/**
 * Extension function to convert generic exceptions to CategoryError
 */
fun Throwable.toCategoryError(): CategoryError {
    return when (this) {
        is CategoryError -> this
        is java.net.UnknownHostException -> CategoryError.NetworkError("Tidak dapat terhubung ke server. Periksa koneksi internet Anda.", this)
        is java.net.SocketTimeoutException -> CategoryError.TimeoutError("Koneksi timeout. Silakan coba lagi.")
        is java.net.ConnectException -> CategoryError.NetworkError("Gagal terhubung ke server. Periksa koneksi internet Anda.", this)
        is android.database.sqlite.SQLiteConstraintException -> {
            when {
                message?.contains("UNIQUE constraint failed") == true -> {
                    if (message?.contains("slug") == true) {
                        CategoryError.DuplicateError("slug", "Slug sudah digunakan. Silakan gunakan slug yang berbeda.")
                    } else if (message?.contains("title") == true) {
                        CategoryError.DuplicateError("title", "Nama kategori sudah ada. Silakan gunakan nama yang berbeda.")
                    } else {
                        CategoryError.DuplicateError("unknown", "Data sudah ada. Silakan gunakan data yang berbeda.")
                    }
                }
                else -> CategoryError.DatabaseError("Terjadi kesalahan pada database. Silakan coba lagi.", this)
            }
        }
        is android.database.sqlite.SQLiteException -> CategoryError.DatabaseError("Terjadi kesalahan pada database. Silakan coba lagi.", this)
        is IllegalArgumentException -> CategoryError.ValidationError("general", message ?: "Data tidak valid.")
        else -> CategoryError.UnknownError("Terjadi kesalahan yang tidak terduga. Silakan coba lagi.", this)
    }
}