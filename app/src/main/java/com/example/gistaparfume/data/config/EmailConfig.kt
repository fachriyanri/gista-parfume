package com.example.gistaparfume.data.config

/**
 * Configuration class for email settings
 * 
 * IMPORTANT: Before using email functionality, you need to:
 * 1. Replace GMAIL_USERNAME with your actual Gmail address
 * 2. Replace GMAIL_PASSWORD with your Gmail App Password (not your regular password)
 * 3. Enable 2-Factor Authentication on your Gmail account
 * 4. Generate an App Password from Google Account settings
 * 
 * For production apps, consider using:
 * - Environment variables
 * - BuildConfig fields
 * - Secure key storage
 */
object EmailConfig {
    
    // Gmail SMTP Configuration
    const val SMTP_HOST = "smtp.gmail.com"
    const val SMTP_PORT = "587"
    const val SMTP_AUTH = "true"
    const val SMTP_STARTTLS = "true"
    const val SMTP_SSL_PROTOCOLS = "TLSv1.2"
    
    // Gmail Credentials - REPLACE THESE WITH ACTUAL VALUES
    const val GMAIL_USERNAME = "gistaparfume1@gmail.com"  // Replace with your Gmail address
    const val GMAIL_PASSWORD = "rjvk sxlz jvkc afks"     // Replace with your Gmail App Password
    
    // Email Sender Information
    const val FROM_EMAIL = "noreply@gistaparfume.com"
    const val FROM_NAME = "Gista Parfume"
    
    // Email Templates
    const val PASSWORD_RESET_SUBJECT = "Reset Password - Gista Parfume"
    
    /**
     * Validates if email configuration is properly set up
     */
    fun isConfigured(): Boolean {
        return GMAIL_USERNAME != "your-email@gmail.com" && 
               GMAIL_PASSWORD != "your-app-password"
    }
    
    /**
     * Gets configuration status message
     */
    fun getConfigurationMessage(): String {
        return if (isConfigured()) {
            "Email configuration is set up correctly"
        } else {
            "Email configuration needs to be updated in EmailConfig.kt"
        }
    }
}