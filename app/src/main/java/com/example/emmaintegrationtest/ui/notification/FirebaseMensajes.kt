package com.example.emmaintegrationtest.ui.notification

import android.content.ContentValues
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

/**
 * Esta clase configura y trabaja en segundo plano para que los mensajes de Firebase funcionen y el
 * dispositivo pueda recibirlos.
 */
class FirebaseMensajes : FirebaseMessagingService() {

    /**
     * Called if the FCM registration token is updated. This may occur if the security of the
     * previous token had been compromised. Note that this is called when the FCM registration token
     * is initially generated so this is where you would retrieve the token.
     *
     * @param token Token que se recibe y se pasa al servidor.
     */
    override fun onNewToken(token: String) {
        Log.d(ContentValues.TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    /**
     * Send a token to our app server.
     *
     * @param token Token que se envía al servidor.
     */
    private fun sendRegistrationToServer(token: String?) {
        Log.d(ContentValues.TAG, "sendRegistrationTokenToServer($token)")
    }
}