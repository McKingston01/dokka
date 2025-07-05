package com.example.emmaintegrationtest.ui.notification

import io.emma.android.EMMA
import io.emma.android.interfaces.EMMAInAppMessageInterface
import io.emma.android.model.EMMACampaign
import io.emma.android.model.EMMAInAppRequest
import io.emma.android.model.EMMAStartViewCampaign

class StarviewHandler(
    private val onAdReceived: (EMMAStartViewCampaign) -> Unit
) : EMMAInAppMessageInterface{

    fun getStartView() {
        val startViewRequest = EMMAInAppRequest(EMMACampaign.Type.STARTVIEW)
        EMMA.getInstance().getInAppMessage(startViewRequest)
    }

    override fun onShown(p0: EMMACampaign?) {
        TODO("Not yet implemented")
    }

    override fun onHide(p0: EMMACampaign?) {
        TODO("Not yet implemented")
    }

    override fun onClose(p0: EMMACampaign?) {
        TODO("Not yet implemented")
    }
}