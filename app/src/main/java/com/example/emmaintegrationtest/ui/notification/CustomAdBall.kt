package com.example.emmaintegrationtest.ui.notification

import android.app.Activity
import io.emma.android.model.EMMANativeAd
import io.emma.android.plugins.EMMAInAppPlugin
import io.emma.android.utils.EMMALog

class CustomAdBall : EMMAInAppPlugin() {

    private val PLUGIN_ID = "emma-custom-plugin"

    /**
     *
     * @return Identificador del plugin que corresponde con el templateId generado en la plantilla.
     */
    override fun getId(): String? {
        EMMALog.v("Obtenemos el identificador de la plantilla.")
        return PLUGIN_ID
    }

    /**
     * Principal
     *
     * @param p0 Actividad visible en la app.
     * @param p1 Contenido de la plantilla.
     */
    override fun show(p0: Activity?, p1: EMMANativeAd?) {
        EMMALog.v("Entramos en el método principal del plugin personalizado.")
        TODO("Not yet implemented")
    }

    override fun dismiss() {
        TODO("Not yet implemented")
    }
}