package com.example.gistaparfume.data.repository

import com.example.gistaparfume.data.config.EmailConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Concrete implementation of EmailService using Gmail SMTP
 */
class EmailServiceImpl : EmailService {
    
    /**
     * Sends a password reset email using Gmail SMTP
     */
    override suspend fun sendPasswordResetEmail(email: String, resetToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Check if email configuration is set up
                if (!EmailConfig.isConfigured()) {
                    return@withContext Result.failure(Exception(EmailConfig.getConfigurationMessage()))
                }
                
                // Configure Gmail SMTP properties
                val properties = Properties().apply {
                    put("mail.smtp.host", EmailConfig.SMTP_HOST)
                    put("mail.smtp.port", EmailConfig.SMTP_PORT)
                    put("mail.smtp.auth", EmailConfig.SMTP_AUTH)
                    put("mail.smtp.starttls.enable", EmailConfig.SMTP_STARTTLS)
                    put("mail.smtp.ssl.protocols", EmailConfig.SMTP_SSL_PROTOCOLS)
                }
                
                // Create authenticator for Gmail
                val authenticator = object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EmailConfig.GMAIL_USERNAME, EmailConfig.GMAIL_PASSWORD)
                    }
                }
                
                // Create mail session
                val session = Session.getInstance(properties, authenticator)
                
                // Create email message
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(EmailConfig.FROM_EMAIL, EmailConfig.FROM_NAME))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                    subject = EmailConfig.PASSWORD_RESET_SUBJECT
                    
                    // Create HTML email content
                    val htmlContent = createPasswordResetEmailContent(resetToken)
                    setContent(htmlContent, "text/html; charset=utf-8")
                }
                
                // Send email
                Transport.send(message)
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Result.failure(Exception("Gagal mengirim email: ${e.message}"))
            }
        }
    }
    
    /**
     * Creates HTML content for password reset email
     */
    private fun createPasswordResetEmailContent(resetToken: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Reset Password - Gista Parfume</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #2c3e50;">Gista Parfume</h1>
                    </div>
                    
                    <h2 style="color: #34495e;">Reset Password Anda</h2>
                    
                    <p>Halo,</p>
                    
                    <p>Kami menerima permintaan untuk mereset password akun Anda di Gista Parfume.</p>
                    
                    <p>Kode reset password Anda adalah:</p>
                    
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;">
                        <strong style="font-size: 18px; color: #2c3e50;">$resetToken</strong>
                    </div>
                    
                    <p>Silakan gunakan kode ini untuk mereset password Anda di aplikasi Gista Parfume.</p>
                    
                    <p><strong>Catatan:</strong> Kode ini berlaku selama 24 jam. Jika Anda tidak meminta reset password, abaikan email ini.</p>
                    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    
                    <p style="font-size: 12px; color: #666;">
                        Email ini dikirim secara otomatis. Mohon jangan membalas email ini.<br>
                        © 2024 Gista Parfume. All rights reserved.
                    </p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: EmailServiceImpl? = null
        
        fun getInstance(): EmailServiceImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = EmailServiceImpl()
                INSTANCE = instance
                instance
            }
        }
    }
}