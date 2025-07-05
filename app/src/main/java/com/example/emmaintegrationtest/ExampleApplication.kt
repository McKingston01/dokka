package com.example.emmaintegrationtest

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import io.emma.android.EMMA
import io.emma.android.model.EMMAPushOptions
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import io.emma.plugin_prism.EMMAInAppPrismPlugin

/**
 * Clase principal.
 */
class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        emmaConfiguration()

        emmaPushConfiguration()

        firebasePush()

        EMMA.getInstance().setDebuggerOutput(true)
    }

    /**
     * Configuración de la aplicación
     */
    private fun emmaConfiguration() {
        // Configuración de la aplicación
        val configuration = EMMA.Configuration.Builder(this)
            .setSessionKey("example")
            .trackScreenEvents(false) // Envío de pantallas activo por defecto
            .setDebugActive(BuildConfig.DEBUG)
            .build()

        EMMA.getInstance().startSession(configuration)
        EMMA.getInstance().addInAppPlugins(EMMAInAppPrismPlugin())
    }

    /**
     * Configuración de los mensajes push de Emma
     */
    private fun emmaPushConfiguration() {
        val pushOpt = EMMAPushOptions.Builder(MainActivity::class.java, R.drawable.notification_icon)
            .setNotificationColor(ContextCompat.getColor(this, R.color.yellow))
            .setNotificationChannelName("Mi custom channel")
            .build()

        EMMA.getInstance().startPushSystem(pushOpt)
    }

    /**
     * Configuración para recibir mensajes push desde Firebase
     */
    private fun firebasePush() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
        })
    }
}