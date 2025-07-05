package com.example.emmaintegrationtest.ui.notification

import io.emma.android.EMMA
import io.emma.android.enums.CommunicationTypes
import io.emma.android.interfaces.EMMABatchNativeAdInterface
import io.emma.android.interfaces.EMMAInAppMessageInterface
import io.emma.android.interfaces.EMMANativeAdInterface
import io.emma.android.model.EMMACampaign
import io.emma.android.model.EMMANativeAd
import io.emma.android.model.EMMANativeAdRequest

class NativeAdHandler(
    private val onAdReceived: (EMMANativeAd) -> Unit
) : EMMAInAppMessageInterface, EMMABatchNativeAdInterface, EMMANativeAdInterface {

    override fun onReceived(nativeAd: EMMANativeAd) {
        // Send impression to the platform
        EMMA.getInstance().sendInAppImpression(CommunicationTypes.NATIVE_AD, nativeAd)
        EMMA.getInstance().sendInAppClick(CommunicationTypes.NATIVE_AD, nativeAd)
        onAdReceived(nativeAd)
    }

    fun getNativeAd(templateId: String) {
        println("Se activa getNativeAd()")
        val nativeAdRequest = EMMANativeAdRequest()
        nativeAdRequest.templateId = templateId
        EMMA.getInstance().getInAppMessage(nativeAdRequest, this)

    }

    fun getNativeAdBatch(templateId: String) {
        println("Se activa getNativeAdBatch()")
        val nativeAdRequest = EMMANativeAdRequest()
        nativeAdRequest.templateId = templateId
        nativeAdRequest.isBatch = true
        EMMA.getInstance().getInAppMessage(nativeAdRequest, this)
    }

    /**
     * Gestiona los anuncios nativos en bloque que se descargan desde la plataforma.
     *
     * @param nativeAds Bloque de anuncios nativos.
     */
    override fun onBatchReceived(nativeAds: MutableList<EMMANativeAd>) {
        println("Se activa onBatchRecieved()")
        nativeAds.forEach { nativeAd ->
            // Send impression to the platform
            EMMA.getInstance().sendInAppImpression(CommunicationTypes.NATIVE_AD, nativeAd)
            EMMA.getInstance().sendInAppClick(CommunicationTypes.NATIVE_AD, nativeAd)
            val content = nativeAd.nativeAdContent
            val container = content["container"]
            container?.fieldContainer?.forEach { containerSide ->
                // extract field information
            }
        }
    }

    fun sendNativeAdClick(nativeAd: EMMANativeAd) {
        EMMA.getInstance().sendInAppClick(CommunicationTypes.NATIVE_AD, nativeAd)
    }

    fun openNativeAd(nativeAd: EMMANativeAd) { EMMA.getInstance().openNativeAd(nativeAd) }

    override fun onShown(p0: EMMACampaign?) {
        // not implemented
    }

    override fun onHide(p0: EMMACampaign?) {
        // not implemented
    }

    override fun onClose(p0: EMMACampaign?) {
        // not implemented
    }
}