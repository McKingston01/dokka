package com.example.emmaintegrationtest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.emma.android.controllers.EMMAConfig;
import io.emma.android.controllers.EMMAController;
import io.emma.android.controllers.EMMALinkController;
import io.emma.android.controllers.EMMASecurityController;
import io.emma.android.enums.CommunicationTypes;
import io.emma.android.enums.EMMAPushType;
import io.emma.android.exceptions.EMMASessionKeyException;
import io.emma.android.interfaces.EMMABatchNativeAdInterface;
import io.emma.android.interfaces.EMMACouponsInterface;
import io.emma.android.interfaces.EMMADeviceIdListener;
import io.emma.android.interfaces.EMMAInAppMessageInterface;
import io.emma.android.interfaces.EMMAInstallAttributionInterface;
import io.emma.android.interfaces.EMMANativeAdInterface;
import io.emma.android.interfaces.EMMAPermissionInterface;
import io.emma.android.interfaces.EMMASessionStartListener;
import io.emma.android.model.EMMABannerParams;
import io.emma.android.model.EMMACampaign;
import io.emma.android.model.EMMAEventRequest;
import io.emma.android.model.EMMAInAppRequest;
import io.emma.android.model.EMMANativeAd;
import io.emma.android.model.EMMAPushOptions;
import io.emma.android.plugins.EMMAInAppPlugin;
import io.emma.android.utils.EMMALog;
import io.emma.android.utils.EMMAUtils;
import io.emma.android.utils.ManifestInfo;

/**
 * Core class for interacting with EMMA
 * <p>
 * <p>Call {@link #startEMMASession(Application)} within
 * your main application class to start tracking events
 * with EMMA
 */

@SuppressWarnings("all")
public class EMMA {

    private static volatile EMMA INSTANCE;

    private static final String EMMA_SESSION_KEY = "io.emma.SESSION_KEY";

    private EMMAController emmaController;
    private Activity lastLoad;

    /**
     * Class constructor.
     */
    private EMMA() {
    }

    /**
     * Returns ths runtime object associated with current
     *
     * @return the <code>EMMA</code> object associated.
     */
    public static EMMA getInstance() {
        if (INSTANCE == null) {
            synchronized (EMMA.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EMMA();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Class Configuration. This class allows to create the EMMA configuration for init SDK.
     */
    public static final class Configuration {

        public Context getContext() {
            return context;
        }

        public String getUrlBase() {
            return this.urlBase;
        }

        public String getSessionKey() {
            return this.sessionKey;
        }

        public String getApiUser() {
            return this.apiUser;
        }

        public String getApiKey() {
            return this.apiKey;
        }

        public Boolean isDebugActive() {
            return debugActive;
        }

        public Integer getQueueTime() {
            return queueTime;
        }

        public String[] getPowlinkDomains() {
            return powlinkDomains;
        }

        public Boolean trackScreenEvents() {
            return screenEvents;
        }

        public String[] getShortPowlinkDomains() {
            return shortPowlinkDomains;
        }

        public EMMAInstallAttributionInterface getInstallAttributionListener() {
            return installAttributionlistener;
        }

        public Boolean isFamiliesPolicyTreatmentActive() {
            return familiesPolicyTreatment;
        }

        public Integer getWaitForAttributionInfo() {
            return waitForAttributionInfo;
        }

        public static final class Builder {
            private Context context;
            private String sessionKey;
            private Integer queueTime;
            private String[] powlinkDomains;
            private Boolean debugActive;
            private String webserviceURL;
            private Boolean screenEvents;
            private String[] shortPowlinkDomains;
            private String testInstallReferrer;
            private Boolean familiesPolicyTreatment;
            private EMMAInstallAttributionInterface installAttributionlistener;
            private Integer waitForAttributionInfo;

            public Builder(Context context) {
                this.context = context;
                if (context != null) {
                    Context applicationContext = context.getApplicationContext();
                    Bundle metaData = ManifestInfo.getApplicationMetadata(applicationContext);
                    if (metaData != null) {
                        sessionKey = metaData.getString(EMMA_SESSION_KEY);
                    }
                }
            }

            public Builder setSessionKey(String sessionKey) {
                this.sessionKey = sessionKey;
                return this;
            }

            public Builder setWebServiceUrl(String url) {
                this.webserviceURL = url;
                return this;
            }

            public Builder setDebugActive(boolean active) {
                this.debugActive = active;
                return this;
            }

            public Builder setQueueTime(int seconds) {
                this.queueTime = seconds;
                return this;
            }

            public Builder setPowlinkDomains(String... powlinkDomains) {
                this.powlinkDomains = powlinkDomains;
                return this;
            }

            public Builder trackScreenEvents(boolean screenEvents) {
                this.screenEvents = screenEvents;
                return this;
            }

            public Builder setShortPowlinkDomains(String... shortPowlinkDomains) {
                this.shortPowlinkDomains = shortPowlinkDomains;
                return this;
            }

            public Builder setTestInstallReferrer(String installReferrer) {
                this.testInstallReferrer = installReferrer;
                return this;
            }

            public Builder setFamilyPolicyTreatment(boolean enable) {
                this.familiesPolicyTreatment = enable;
                return this;
            }

            public void setInstallAttributionListener(EMMAInstallAttributionInterface installAttributionlistener) {
                this.installAttributionlistener = installAttributionlistener;
            }

            public void setWaitForAttributionInfo(Integer waitForAttributionInfo) {
                this.waitForAttributionInfo = waitForAttributionInfo;
            }

            public Configuration build() {
                try {
                    return new Configuration(this);
                } catch (EMMASessionKeyException e) {
                    return null;
                }
            }
        }

        final Context context;
        final String sessionKey;
        final String urlBase;
        final String apiUser;
        final String apiKey;
        final Boolean debugActive;
        final Integer queueTime;
        final String[] powlinkDomains;
        final Boolean screenEvents;
        final String[] shortPowlinkDomains;
        final String testInstallReferrer;
        final Boolean familiesPolicyTreatment;
        final EMMAInstallAttributionInterface installAttributionlistener;
        final Integer waitForAttributionInfo;

        private Configuration(Builder builder) throws EMMASessionKeyException {
            this.context = builder.context;
            this.sessionKey = builder.sessionKey;
            this.urlBase = builder.webserviceURL;
            this.debugActive = builder.debugActive;

            if (debugActive != null && debugActive) {
                EMMALog.setLevel(EMMALog.VERBOSE);
            } else {
                EMMALog.setLevel(EMMALog.NONE);
            }

            this.apiUser = EMMAUtils.getApiUserFromSessionKey(this.sessionKey);
            this.apiKey = EMMAUtils.getApiKeyFromSessionKey(this.sessionKey);
            this.queueTime = builder.queueTime;
            this.powlinkDomains = builder.powlinkDomains;
            this.screenEvents = builder.screenEvents;
            this.shortPowlinkDomains = builder.shortPowlinkDomains;
            this.testInstallReferrer = builder.testInstallReferrer;
            this.familiesPolicyTreatment = builder.familiesPolicyTreatment;
            this.installAttributionlistener = builder.installAttributionlistener;
            this.waitForAttributionInfo = builder.waitForAttributionInfo;
        }
    }

    /**
     * Checks if EMMA configuration has the correct params.
     *
     * @param configuration The EMMA configuration.
     */
    private void checkConfig(Configuration configuration) {
        if (configuration != null && configuration.sessionKey != null)
            return;

        throw new RuntimeException("EMMA Session Key not defined" +
                "You must provide SESSION KEY in AndroidManifest.xml.\n" +
                "<meta-data\n" +
                "    android:name=\"" + EMMA_SESSION_KEY + "\"\n" +
                "    android:value=\"<Your Session Key>\" />");
    }

    /**
     * Starts the communication with EMMA Servers. Sessionkey is managed on Android Manifest with meta tag.
     *
     * @param application The application context you are tracking
     */
    public void startSession(Application application) {
        startSession(application, null);
    }

    /**
     * Starts the communication with EMMA Servers with a new EMMA configuration.
     *
     * @param configuration The EMMA configuration.
     */
    public void startSession(Configuration configuration) {
        startSession(configuration, null);
    }

    /**
     * Starts the communication with EMMA Servers. Sessionkey is managed on Android Manifest with meta tag.
     *
     * @param application The application context you are tracking
     * @param sessionStartListener Listener to notify when the session is started
     */
    public void startSession(Application application,
                             EMMASessionStartListener sessionStartListener) {
        Configuration.Builder builder = new Configuration.Builder(application);
        builder.setDebugActive(EMMALog.getLevel() == EMMALog.VERBOSE);
        Configuration configuration = builder.build();
        startSession(configuration, sessionStartListener);
    }

    /**
     * Starts the communication with EMMA Servers with a new EMMA configuration.
     *
     * @param configuration The EMMA configuration.
     * @param sessionStartListener Listener to notify when the session is started
     */
    public void startSession(Configuration configuration,
                             EMMASessionStartListener sessionStartListener) {
        checkConfig(configuration);
        startEMMAController(configuration, sessionStartListener,  false);
    }

    /**
     * Starts basic session without notifying installs and session events.
     *
     * @param configuration The EMMA configuration.
     */
    public void startSessionBackground(Configuration configuration) {
        checkConfig(configuration);
        startEMMAController(configuration, null, true);
    }

    private void startEMMAController(Configuration configuration,
                                     EMMASessionStartListener sessionStartListener,
                                     boolean backgroundSession) {
        if (emmaController == null) {
            final Context appContext = configuration.getContext();
            EMMAConfig.saveConfig(appContext, configuration);
            emmaController = new EMMAController(appContext);

            if (backgroundSession) {
                emmaController.startSessionBackground();
            } else {
                if (lastLoad != null) {
                    emmaController.setCurrentActivity(lastLoad);
                }

                emmaController.startSession(sessionStartListener);
                if (configuration.testInstallReferrer != null) {
                    emmaController.getReferrerController().testReferrer(configuration.testInstallReferrer);
                }
                emmaController.getReferrerController().startInstallReferrerConnection();
                emmaController.getInstallAttributionController()
                        .getInstallAttributionInfoGlobal(configuration.getInstallAttributionListener(),
                                configuration.getWaitForAttributionInfo());
            }
        } else {
            EMMALog.i("Session already started");
        }
    }

    /**
     * Gets the current web services url. You can use this for proxies.
     */
    public String getWebServiceURL() {
        return EMMAConfig.BASE_URL;
    }

    /**
     * Sets the current web services url. You can use this for proxies.
     */
    public void setWebServiceUrl(String webServiceURL) {
        EMMAConfig.BASE_URL = webServiceURL;
        if (isSdkStarted()) {
            EMMAConfig.getInstance(emmaController.getApplicationContext()).saveBaseUrl(webServiceURL);
        }
    }

    public void trackScreenEvents(boolean screenEvents) {
        if (isSdkStarted()) {
            EMMAConfig.getInstance(emmaController.getApplicationContext()).saveTrackScreenEvents(screenEvents);
        }
    }

    /**
     * Starts the tracking location in SDK. By default the tracking is enabled, when permission was accepted.
     */
    public void startTrackingLocation() {
        if (isSdkStarted()) {
            emmaController.getDeviceController().startTrackingLocationWithPermissions();
        }
    }

    /**
     * Disable the tracking location.
     */
    public void disableTrackingLocation() {
        if (isSdkStarted()) {
            emmaController.getDeviceController().stopTrackLocation();
        }
    }

    /**
     * Gets the user identifier added previously in login and register call.
     */
    public void getUserID() {
        if (isSdkStarted()) {
            emmaController.getDataController().getUserID();
        }
    }

    /**
     * Gets the device UUID.
     */
    public String getUUID() {
        if (isSdkStarted()) {
            return emmaController.getUserController().getUserUDID();
        }
        return null;
    }

    /**
     * Gets the current SDK version.
     *
     * @return The EMMA SDK version.
     */
    public String getSDKVersion() {
        return EMMAConfig.SDK_VERSION;
    }

    /**
     * Gets the current SDK build.
     *
     * @return The EMMA SDK build.
     */
    public int getSDKBuild() {
        return EMMAConfig.SDK_BUILD;
    }

    /**
     * Use setWhitelist to restrict urls that can be opened for SDK in-app communications
     * By default all urls are permited.
     * <p>
     * Only URLs that starts by an url in the whitelist are opened
     */
    public void setWhitelist(List<String> urls) {
        EMMASecurityController.getInstance().setWhitelist(urls);
    }

    /**
     * Get the urls previously added in setWhitelist method.
     * By default all urls are allowed.
     */
    public List<String> getWhitelist() {
        return EMMASecurityController.getInstance().getWhitelist();
    }

    public Context getApplicationContext() {
        if (isSdkStarted()) {
            return emmaController.getApplicationContext();
        }
        return null;
    }

    /**
     * Request a event message providing a custom EMMAEventRequest
     *
     * <pre>
     * {@code
     *
     * EMMAEventRequest eventRequest = new EMMAEventRequest("token");
     * EMMA.trackEvent(eventRequest);
     * }
     * </pre>
     *
     * @param params The request
     */
    public void trackEvent(@NonNull EMMAEventRequest eventRequest) {
        if (isSdkStarted()) {
            emmaController.getEventController().trackEvent(eventRequest);
        }
    }

    public void loginUser(String userId, String mail) {
        if (isSdkStarted()) {
            emmaController.getUserController().loginUser(userId, mail, null);
        }
    }

    public void loginUser(String userId, String mail, Map<String, String> extras) {
        if (isSdkStarted()) {
            emmaController.getUserController().loginUser(userId, mail, extras);
        }
    }

    public void registerUser(@NonNull String userId, String mail) {
        if (isSdkStarted()) {
            emmaController.getUserController().registerUser(userId, mail, null);
        }
    }


    public void registerUser(@NonNull String userId, String mail, Map<String, String> extras) {
        if (isSdkStarted()) {
            emmaController.getUserController().registerUser(userId, mail, extras);
        }
    }

    public void trackExtraUserInfo(Map<String, String> info) {
        if (isSdkStarted()) {
            emmaController.getUserController().trackExtraUserInfo(info);
        }
    }


    /**
     *  Start the ordering process.
     *
     * <pre>
     * {@code
     *  Map<String,String> extras = new HashMap<>();
     *  extras.put("CATEGORY", "cart");
     *  EMMA.getInstance().startOrder("A4UE312", "18743", 10.0, "EXTRA-10", extras);
     *  EMMA.getInstance().addProduct("CATEGORY", "Blue jeans", 10.0, extras);
     *  EMMA.getInstance().trackOrder();
     *  }
     *  </pre>
     * @param orderId The ID is used to identify the order in EMMA.
     * @param customerId App session user ID. Also used in login and register.
     * @param totalPrice Total price of the order. The sum of all products.
     * @param coupon Coupon applied to the order If nedded.
     * @param extras Tags associated with order.
     */
    public void startOrder(String orderId, String customerId, float totalPrice, String coupon, Map<String, String> extras) {
        if (isSdkStarted()) {
            emmaController.getOrderController().startOrder(orderId, customerId, totalPrice, coupon, extras);
        }
    }

    public void startOrder(String orderId, String customerId, float totalPrice, Map<String, String> extras) {
        if (isSdkStarted()) {
            emmaController.getOrderController().startOrder(orderId, customerId, totalPrice, null, extras);
        }
    }

    public void startOrder(String orderId, String customerId, float totalPrice) {
        if (isSdkStarted()) {
            emmaController.getOrderController().startOrder(orderId, customerId, totalPrice, null, null);
        }
    }

    public void startOrder(String orderId, float totalPrice) {
        if (isSdkStarted()) {
            emmaController.getOrderController().startOrder(orderId, null, totalPrice, null, null);
        }
    }

    public void startOrder(String orderId, float totalPrice, Map<String, String> extras) {
        if (isSdkStarted()) {
            emmaController.getOrderController().startOrder(orderId, null, totalPrice, null, extras);
        }
    }

    public void addProduct(String productId, String name, float qty, float price, Map<
            String, String> extras) {
        if (isSdkStarted()) {
            emmaController.getOrderController().addProduct(productId, name, qty, price, extras);
        }
    }

    public void addProduct(String productId, String name, float qty, float price) {
        if (isSdkStarted()) {
            emmaController.getOrderController().addProduct(productId, name, qty, price, null);
        }
    }

    /**
     * Finish the ordering process sending the order to EMMA.
     */
    public void trackOrder() {
        if (isSdkStarted()) {
            emmaController.getOrderController().trackOrder();
        }
    }

    /**
     * Cancel the previus order sent to EMMA.
     *
     * @param orderId ID of the previous sent order.
     */
    public void cancelOrder(String orderId) {
        if (isSdkStarted()) {
            emmaController.getOrderController().cancelOrder(orderId);
        }
    }

    public void startPushSystem(EMMAPushOptions pushOptions) {
        if (isSdkStarted()) {
            emmaController.getPushController().startPushSystem(pushOptions);
        }
    }

    public void onNewNotification(Intent intent, boolean checkForRichPush) {
        if (isSdkStarted()) {
            emmaController.getPushController().onNewNotification(intent, checkForRichPush);
        }
    }

    public void checkForRichPushUrl() {
        if (isSdkStarted()) {
            emmaController.getPushController().checkForRichPushUrl();
        }
    }

    public void setPowlinkDomains(String... domains) {
        if (isSdkStarted()) {
            EMMAConfig.getInstance(emmaController.getApplicationContext())
                    .savePowlinkDomains(domains);
        }
    }

    public void setShortPowlinkDomains(String... domains) {
        if (isSdkStarted()) {
            EMMAConfig.getInstance(emmaController.getApplicationContext())
                    .saveShortPowlinkDomains(domains);
        }
    }

    public void syncWithSdkWeb(String url) {
        if (isSdkStarted()) {
            emmaController.getPushController().syncWithSDKWeb(url);
        }
    }

    public void addCouponsCallback(EMMACouponsInterface callback) {
        if (isSdkStarted()) {
            emmaController.getCouponsController().addCouponsCallback(callback);
        }
    }

    public void setDebuggerOutput(boolean activate) {
        if (activate) {
            EMMALog.setLevel(EMMALog.VERBOSE);
        } else {
            EMMALog.setLevel(EMMALog.NONE);
        }
    }

    public void checkDelegateMethods() {
        if (isSdkStarted()) {
            emmaController.checkDelegateMethods();
        }
    }

    public void getUserInfo() {
        if (isSdkStarted()) {
            emmaController.getDeviceController().getUserInfo();
        }
    }

    public void getNotificationInfo() {
        if (isSdkStarted()) {
            emmaController.getPushController().getNotificationInfo();
        }
    }

    public void getPushToken() {
        if (isSdkStarted()) {
            emmaController.getPushController().getPushToken();
        }
    }

    public void sendInAppClick(CommunicationTypes type, EMMACampaign campaign) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().sendClick(type.getCommunicationId(), campaign);
        }
    }

    public void sendInAppDismissedClick(CommunicationTypes type, EMMACampaign campaign) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().sendDismissedClick(type.getCommunicationId(), campaign);
        }
    }

    public void sendInAppImpression(CommunicationTypes type, EMMACampaign campaign) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().sendImpression(type.getCommunicationId(), campaign);
        }
    }

    public void unregisterPushService() {
        if (isSdkStarted()) {
            emmaController.getPushController().unregisterPush(emmaController.getApplicationContext());
        }
    }

    /**
     * Request a new in-app message providing a custom EMMAInAppRequest
     *
     * <p>
     * Startview Example:
     * <pre>
     * {@code
     *
     * EMMAInAppRequest inappRequest = new EMMAInAppRequest(EMMACampaign.Type.STARTVIEW);
     * EMMA.getInAppMessage(inAppRequest);
     * }
     * </pre>
     *
     * @param params The request
     */
    public void getInAppMessage(@NonNull EMMAInAppRequest params) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().getInAppMessage(params, null);
        }
    }

    /**
     * Request a new in-app message providing a custom EMMAInAppRequest
     *
     * <p>
     * Startview Example:
     * <pre>
     * {@code
     *
     * EMMAInAppRequest inappRequest = new EMMAInAppRequest(EMMACampaign.Type.STARTVIEW);
     * EMMA.getInAppMessage(inAppRequest, this);
     * }
     * </pre>
     *
     * @param params The request
     */
    public void getInAppMessage(@NonNull EMMAInAppRequest requestParams, EMMAInAppMessageInterface listener) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().getInAppMessage(requestParams, listener);
        }
    }

    /**
     * Cancel specific InAppMessage previously invoked
     *
     * @param messageId previously passed in EMMAInAppRequest.inAppMessageId;
     */
    public void cancelInAppMessage(String messageId) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().cancelInAppMessage(messageId);
        }
    }

    public void addNativeAdListener(EMMANativeAdInterface listener, String templateId) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().addNativeAdInterface(listener, templateId);
        }
    }

    public void addBatchNativeAdListener(EMMABatchNativeAdInterface listener, String templateId) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().addNativeAdBatchInterface(listener, templateId);
        }
    }

    public void addInAppMessageListener(EMMAInAppMessageInterface listener) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().addInAppMessageInterface(listener);
        }
    }

    public void removeInAppMessageListener(EMMAInAppMessageInterface listener) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().removeInAppMessageInterface(listener);
        }
    }

    public void removeNativeAdListener(EMMANativeAdInterface listener) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().removeNativeAdInterface(listener);
        }
    }

    public void removeBatchNativeAdListenenr(EMMABatchNativeAdInterface listener) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().removeBatchNativeAdInterface(listener);
        }
    }

    public void openNativeAd(EMMANativeAd nativeAd) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().openNativeAd(nativeAd);
        }
    }

    public Set<EMMAInAppMessageInterface> getInAppListeners() {
        if (isSdkStarted()) {
            return emmaController.getCampaignController().getInAppListeners();
        }
        return null;
    }

    public void setBannerParams(EMMABannerParams bannerParams) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().getBannerController().setBannerParams(bannerParams);
        }
    }

    public void reset() {
        if (isSdkStarted()) {
            emmaController.getOperationsQueue().reset();
            emmaController.getDataController().reset();
            lastLoad = emmaController.getCurrentActivity();
            emmaController = null;
        }
    }

    public boolean isSdkStarted() {
        return emmaController != null;
    }

    /**
     * This method disables all the communication between SDK and EMMA
     *
     * @param deleteUser If this flag is set to true deletes all user data on server. *WARNING* Can alter dashboard stats
     */
    public void disableUserTracking(boolean deleteUser) {
        if (isSdkStarted()) {
            if (deleteUser) {
                emmaController.getUserController().resetUser();
            }
            EMMAConfig.getInstance(emmaController.getApplicationContext()).saveUserTracking(true);
        }
    }

    /**
     * This method enables communication between SDK and EMMA on previously disabled user.
     * If already enabled, does nothing
     */
    public void enableUserTracking() {
        if (isSdkStarted()) {
            EMMAConfig.getInstance(emmaController.getApplicationContext()).saveUserTracking(false);
        }
    }

    public Boolean isUserTrackingEnabled() {
        if (isSdkStarted()) {
            return !EMMAConfig.getInstance(emmaController.getApplicationContext()).isUserTrackingDisabled();
        }
        return false;
    }

    /**
     * This method gets the install attribution info. The response can have three status
     * for attribution: pending, organic or campaign
     *
     * @param attributionInfoInterface async interface for response
     */

    public void getInstallAttributionInfo(@NonNull EMMAInstallAttributionInterface attributionInfoInterface) {
        if (isSdkStarted()) {
            emmaController.getInstallAttributionController().getInstallAttributionInfo(attributionInfoInterface);
        }
    }

    public void setCurrentActivity(Activity activity) {
        if (isSdkStarted()) {
            emmaController.setCurrentActivity(activity);
        }
    }

    /**
     * Adds to EMMA the custom push token.
     *
     * @param token The push service resgistration token
     * @param pushType Push system used
     */
    public void addPushToken(String token, EMMAPushType pushType) {
        if (isSdkStarted()) {
            emmaController.getPushController().sendTokenToServer(token, pushType);
        }
    }

    /**
     * This method gets the id associate with the device.
     *
     * @param deviceIdListener listener called when device id is obtained.
     */
    public void getDeviceId(EMMADeviceIdListener deviceIdListener) {
        if (isSdkStarted()) {
            emmaController.getDeviceController().getDeviceId(deviceIdListener);
        }
    }

    public String getDeviceId() {
        if (isSdkStarted()) {
            return emmaController.getDeviceController().getDeviceId();
        }
        return Constants.TEXT_EMPTY;
    }

    /**
     * This method associates a customerId with the device.
     *
     * @param customerId The user identifier in customer's database.
     */
    public void setCustomerId(String customerId) {
        if (isSdkStarted()) {
            emmaController.getUserController().setCustomerId(customerId);
        }
    }

    /**
     * Add custom plugins to manage inapp formats through nativeAd power.
     *
     * @param plugins An plugin or multiples plugins.
     */
    public void addInAppPlugins(EMMAInAppPlugin ...plugins) {
        if (isSdkStarted()) {
            emmaController.getInAppPluginController().addPlugins(plugins);
        }
    }

    /**
     * This method decouples the handleLink from the deeplink activity and
     * allows it to be added to any part of the app that processes the deeplink opening.
     *
     * @param context Application context
     * @param uri Uri obtained to send click action and deeplinking if needed
     */
    public static void handleLink(Context context, Uri uri) {
        EMMALinkController.handleLink(context, uri);
    }

    /**
     * On Android 13 shows the system notification permission prompt to enable displaying notifications.
     * This is required for apps that target Android API level 33 to subscribe the device for push notifications.
     */
    public void requestNotificationPermission() {
        requestNotificationPermission(null);
    }

    /**
     * On Android 13 shows the system notification permission prompt to enable displaying notifications.
     * This is required for apps that target Android API level 33 to subscribe the device for push notifications.
     * Execute
     *
     * @param permissionInterface interface fires when the user accepts or declines permission.
     */
    public void requestNotificationPermission(EMMAPermissionInterface permissionInterface) {
        if (isSdkStarted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                EMMAUtils.getTargetSdkVersion(emmaController.getApplicationContext()) >= Build.VERSION_CODES.TIRAMISU) {
            emmaController.getPushController().checkPushNotificationPermission(permissionInterface);
        } else {
            EMMALog.w("Check if your app support Android 13 to verify permission access for notifications");
        }
    }

    /**
     * This method returns if notifications are enabled or disabled. User can disabled notification from settings section,
     * this method detects even if notifications are manually disabled.
     *
     * @return if notifications are enabled or disabled
     */
    public boolean areNotificationsEnabled() {
        if (isSdkStarted()) {
            return EMMAUtils.areNotificationsEnabled(emmaController.getApplicationContext());
        }
        return true;
    }

    /**
     * This method closes inapp communication that is being displayed on the screen.
     * Allowed ADBALL, BANNER, STARTVIEW and STRIP types.
     * @param type Communication type.
     */
    public void closeInAppMessage(EMMACampaign.Type type) {
        if (isSdkStarted()) {
            emmaController.getCampaignController().closeInAppMessage(type);
        }
    }

    /**
     * This method gets the last click key associated with the rt attribution.
     */
    public String getLastRetargetingClickKey() {
        if (isSdkStarted()) {
            return emmaController.getAttributionController().getLastRetargetingClickKey();
        }
        return Constants.TEXT_EMPTY;
    }

    /**
     * Sets the user preferred language manually.
     *
     * This method allows to override the device's default language
     * and set a custom language for all requests.
     *
     * @param language The language code (e.g., "en", "es", "fr").
     */
    public void setUserLanguage(String language) {
        if (isSdkStarted()) {
            emmaController.getDeviceController().setUserLanguage(language);
        }
    }
}

