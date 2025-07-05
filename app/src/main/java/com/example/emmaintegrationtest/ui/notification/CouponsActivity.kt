package com.example.emmaintegrationtest.ui.notification

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emmaintegrationtest.R
import io.emma.android.EMMA
import io.emma.android.enums.CommunicationTypes
import io.emma.android.interfaces.EMMACouponsInterface
import io.emma.android.model.EMMACampaign
import io.emma.android.model.EMMACoupon
import io.emma.android.model.EMMAInAppRequest

class CouponsActivity : AppCompatActivity(), EMMACouponsInterface {
    lateinit var cupon : TextView
    lateinit var canjear : Button
    lateinit var cuponId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons)

        cupon = findViewById(R.id.textview_cupon)
        canjear = findViewById(R.id.button_canjear)

        EMMA.getInstance().addCouponsCallback(this)
        getCoupons()
    }

    private fun getCoupons() {
        EMMA.getInstance().getInAppMessage(EMMAInAppRequest(EMMACampaign.Type.COUPON))
    }

    private fun getSingleCoupon() {
        val couponsRequest = EMMAInAppRequest(EMMACampaign.Type.COUPON)
        couponsRequest.inAppMessageId = cuponId
        EMMA.getInstance().getInAppMessage(couponsRequest)
    }

    private fun redeemCoupon() {
        val redeemCouponRequest = EMMAInAppRequest(EMMACampaign.Type.REDEEM_COUPON)
        redeemCouponRequest.inAppMessageId = "<COUPON_ID>"
        EMMA.getInstance().getInAppMessage(redeemCouponRequest)
    }

    private fun cancelCoupon() {
        val cancelCouponRequest = EMMAInAppRequest(EMMACampaign.Type.CANCEL_COUPON)
        cancelCouponRequest.inAppMessageId = cuponId
        EMMA.getInstance().getInAppMessage(cancelCouponRequest)
    }

    private fun couponValidRedeems() {
        val couponValidRedeems = EMMAInAppRequest(EMMACampaign.Type.COUPON_VALID_REDEEMS)
        couponValidRedeems.inAppMessageId = cuponId
        EMMA.getInstance().getInAppMessage(couponValidRedeems)
    }

    override fun onCouponsReceived(coupons: List<EMMACoupon>) {
        coupons.let {
            // Show coupons
            coupons.forEach { coupon ->
                EMMA.getInstance().sendInAppImpression(CommunicationTypes.COUPON, coupon)
            }
        }
    }

    override fun onCouponsFailure() {
        print("An error has occurred obtaining coupons")
    }

    override fun onCouponRedemption(success: Boolean) {
        print("Coupon redemption success: $success")
    }

    override fun onCouponCancelled(success: Boolean) {
        print("Coupon cancelled success: $success")
    }

    override fun onCouponValidRedeemsReceived(numRedeems: Int) {
        print("Coupon redeems: $numRedeems")
    }
}