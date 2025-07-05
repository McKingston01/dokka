package com.example.emmaintegrationtest

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.emmaintegrationtest.ui.notification.CouponsActivity
import com.example.emmaintegrationtest.ui.notification.NativeAdHandler
import com.google.android.material.snackbar.Snackbar
import io.emma.android.EMMA
import io.emma.android.interfaces.EMMAUserInfoInterface
import io.emma.android.model.EMMACampaign
import io.emma.android.model.EMMAInAppRequest
import io.emma.android.model.EMMANativeAd
import io.emma.android.utils.EMMALog
import org.json.JSONObject

/**
 * Actividad principal de la aplicación.
 *
 * El uso de la clase es para probar las diferentes funcionalidades que presenta EMMA.
 *
 * Se ha seguido la siguiente [documentación](https://developer.emma.io/es/android/integracion-sdk)
 * de integración.
 *
 * La clase está estructurada de la siguiente manera
 * ```mermaid
 * ---
 * title: Estructura de la clase
 * ---
 * classDiagram
 *     class EMMAUserInfoInterface {
 *         <<interface>>
 *         OnGetUserInfo(var1: JSONObject)
 *         OnGetUserID(var1: int)
 *     }
 *
 *     class MainActivity {
 *         #onCreate(savedInstanceState: Bundle?)
 *         #onPause()
 *         -registrar()
 *         -comprar()
 *         -cancelarCompra(orden: String)
 *         -iniciarSesion()
 *         -nativeAdEvent(plantilla: String)
 *         -nativeAdBatchEvent(plantilla: String)
 *         -recibirStartView(identificador: String)
 *         -recibirAdBall()
 *         -recibirBanner()
 *         -extractAndInflate() EMMANativeAd
 *         -notificacion(msg: String)
 *         +OnGetUserInfo(userInfo: JSONObject?)
 *         +OnGetUserID(id: Int)
 *     }
 *
 *     MainActivity ..|> EMMAUserInfoInterface
 *     MainActivity --|> AppCompatActivity
 *
 * ```
 *
 * @see AppCompatActivity
 * @see EMMAUserInfoInterface
 * @see EMMALog
 */
class MainActivity : AppCompatActivity(), EMMAUserInfoInterface {

    /**
     * Se ejecuta al iniciar la aplicación.
     *
     * {@inheritDoc}
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Solicitar al usuario permisos para recibir notificaciones
        EMMA.getInstance().requestNotificationPermission()

        // Comprobar richpush al abrir la aplicación
        EMMA.getInstance().checkForRichPushUrl()

        // Funcionalidad de registro
        registrar()

        // Funcionalidad de inicio de sesión
        iniciarSesion()

        // Funcionalidad de compra
        comprar()

        // Funcionalidad de compra
        cancelarCompra("1001")

        // Mensajes In-App
        nativeAdEvent("plantilla-nativead-android") // Funcionalidad de NativeAd único
        nativeAdBatchEvent("nativead-batch")        // Funcionalidad de NativeAd batch (múltiple)
        recibirStartView("36713")                   // Funcionalidad de StartView
        recibirAdBall()                             // Recibir AdBall
        recibirBanner()                             // Recibir Banner
    }

    /**
     * Realiza una instrucción de registro con los valores dados.
     *
     * Los datos se insertan de forma manual, no se obtienen del usuario.
     *
     * Tras el registro, se muestra una notificación Snackbar con un mensaje confirmando el registro.
     * Aunque no se diferencia entre registro satisfactorio o fallido.
     *
     * @see notificacion
     */
    private fun registrar() {
        findViewById<Button>(R.id.button_registro).setOnClickListener {
            EMMA.getInstance().registerUser("pascual", "pascual@ext.arkana.io")
            notificacion("Acción de registro invocada para ${EMMA.getInstance().getUserID()}")
        }
    }

    /**
     * Realiza toda la transacción de compra con unos importes establecidos.
     *
     * - Información de la orden: Id=1000, Nombre=orden_prueba
     * - Producto 1: Id=1001, Nombre=prod1
     * - Prodcuto 2: Id=1002, Nombre=prod2
     *
     * Se muestra notificación tras realizar la compra.
     *
     * @see notificacion
     */
    private fun comprar() {
        findViewById<Button>(R.id.button_compra).setOnClickListener {
            EMMA.getInstance().startOrder("1000012","orden_pruebaa", 1000000.0F)
            EMMA.getInstance().addProduct("1001", "prod1", 1.0F, 1.0F)
            EMMA.getInstance().addProduct("1002", "prod2", 1.0F, 1.0F)
            EMMA.getInstance().trackOrder()
            notificacion("Se ha realizado la compra correctamente.")
        }
    }

    /**
     * Cancela la orden de compra dada.
     *
     * Se muestra notificación tras cancelar la compra.
     *
     * @param orden Orden de compra que se quiere cancelar.
     * @see notificacion
     */
    private fun cancelarCompra(orden: String) {
        findViewById<Button>(R.id.button_cancelar_compra).setOnClickListener {
            EMMA.getInstance().cancelOrder(orden)
            notificacion("Se ha cancelado la compra correctamente.")
        }
    }

    /**
     * Realiza una instrucción de inicio de sesión con los valores dados.
     *
     * Los datos se insertan de forma manual, no se obtienen del usuario.
     *
     * Tras el inicio de sesión, se muestra una notificación Snackbar con un mensaje confirmando el
     * inicio. Aunque no se diferencia entre registro satisfactorio o fallido.
     *
     * @see notificacion
     */
    private fun iniciarSesion() {
        findViewById<Button>(R.id.button_login).setOnClickListener {
            EMMA.getInstance().loginUser("matias", "matias@emma.io")
            notificacion("Acción de login invocada para el usuario 1234 - test@emma.io")
        }
    }

    /**
     * Se activa tras pausar la aplicación y volver a usarla.
     * // todo: revisar que los cupones se activen correctamente.
     *
     * Lo uso para probar los cupones.
     */
    override fun onPause() {
        super.onPause()

        // Saltamos a la pantalla de cupones
        val cupones = Intent(this, CouponsActivity::class.java)
        startActivity(cupones)
    }

    /**
     * Click event for the native ad button.
     *
     * The listener performs several instructions to insert the received content from EMMA into
     * the main layout.
     *
     * First, we generate a 'template' for the advert by defining a callback using [extractAndInflate].
     * The generated fields inside [extractAndInflate] are important as they define how the native ad
     * will be displayed, including inflating the ad layout, populating it with content, and rendering it
     * on the screen.
     *
     * Then, we instantiate [NativeAdHandler], passing the callback to handle the native ad once it is
     * retrieved. The handler triggers [NativeAdHandler.getNativeAd] with a specific template identifier
     * to request the ad from the EMMA platform.
     *
     * Once the ad is successfully fetched, the previously defined callback is executed, allowing the
     * ad content to be dynamically inserted into the layout.
     *
     * @see EMMANativeAd
     * @see NativeAdHandler
     */
    private fun nativeAdEvent(plantilla: String) {
        findViewById<Button>(R.id.button_mostrar_nativead).setOnClickListener {
            val recievedAd: (EMMANativeAd) -> Unit = extractAndInflate()
            val nativeAdHandler = NativeAdHandler(recievedAd)
            nativeAdHandler.getNativeAd(plantilla)
        }
    }

    /**
     * Funcionalidad similar a [nativeAdEvent] pero con múltiples anuncios nativos.
     *
     * No se ha implementado a nivel visual pero se ha comprobado que se recibe un JSON desde EMMA
     * correctamente.
     *
     * @see EMMANativeAd
     * @see NativeAdHandler
     */
    private fun nativeAdBatchEvent(plantilla: String) {
        findViewById<Button>(R.id.button_mostrar_nativead_batch).setOnClickListener {
            val recievedAd: (EMMANativeAd) -> Unit = extractAndInflate()
            val nativeAdHandler = NativeAdHandler(recievedAd)
            nativeAdHandler.getNativeAdBatch(plantilla)
        }
    }

    /**
     * Recibe el contenido de un StartView lanzado desde EMMA.
     *
     * No se ha realizar la implementación visual. Únicamente se comprueba que se recibe la información
     * por Logcat en formato JSON.
     *
     * @param identificador Valor del campo 'inAppMessageId' necesario.
     * @see EMMAInAppRequest
     * @see EMMACampaign
     */
    private fun recibirStartView(identificador: String) {
        findViewById<Button>(R.id.button_mostrar_starview).setOnClickListener {
            val startViewRequest = EMMAInAppRequest(EMMACampaign.Type.STARTVIEW)
            startViewRequest.inAppMessageId = identificador
            EMMA.getInstance().getInAppMessage(startViewRequest)
        }
    }

    /**
     * Recibe el contenido de un AdBall lanzado desde EMMA.
     *
     * No se ha realizar la implementación visual. Únicamente se comprueba que se recibe la información
     * por Logcat en formato JSON.
     *
     * No es necesario pasarle ningún parámetro o valor para mostrarlo.
     *
     * @see EMMAInAppRequest
     * @see EMMACampaign
     */
    private fun recibirAdBall() {
        findViewById<Button>(R.id.button_mostrar_adball).setOnClickListener {
            val adBallRequest = EMMAInAppRequest(EMMACampaign.Type.ADBALL)
            EMMA.getInstance().getInAppMessage(adBallRequest)
        }
    }

    /**
     * Recibe el contenido de un Banner lanzado desde EMMA.
     *
     * No se ha realizar la implementación visual. Únicamente se comprueba que se recibe la información
     * por Logcat en formato JSON.
     *
     * No es necesario pasarle ningún parámetro o valor para mostrarlo.
     *
     * @see EMMAInAppRequest
     * @see EMMACampaign
     */
    private fun recibirBanner() {
        findViewById<Button>(R.id.button_mostrar_banner).setOnClickListener {
            val bannerRequest = EMMAInAppRequest(EMMACampaign.Type.BANNER)
            EMMA.getInstance().getInAppMessage(bannerRequest)
        }
    }

    /**
     * Crea una plantilla para el contenido que se recibe de EMMA e infla la vista para poder mostrar
     * el NativeAd en la actividad principal.
     *
     * @see EMMANativeAd
     * @see LayoutInflater
     * @see RelativeLayout
     * @return Una función que recibe un [EMMANativeAd] y lo procesa para mostrarlo en la UI.
     */
    private fun extractAndInflate(): (EMMANativeAd) -> Unit {
        val recievedAd: (EMMANativeAd) -> Unit = { nativeAd ->

            // Inflate the view from this activity to the nativead's activity
            val inflater = LayoutInflater.from(this)
            val adView = inflater.inflate(R.layout.activity_native_ads, null, false)

            // Save the plain data from recieved json file
            val title = nativeAd.nativeAdContent["Title"]?.fieldValue
            val body = nativeAd.nativeAdContent["Body"]?.fieldValue

            // Fullfill recieved data into advert's fields
            adView.findViewById<TextView>(R.id.textview_titulo).text = title
            adView.findViewById<TextView>(R.id.textview_cuerpo).text = body

            // By a referenced container, cleaning the view and adding the native ad
            val container = findViewById<RelativeLayout>(R.id.nativead_container)
            container.removeAllViews()  // Limpiar el contenedor
            container.addView(adView)  // Añadir el nuevo anuncio
        }

        return recievedAd
    }

    /**
     * Muestra una notificación Snackbar en la parte de abajo con el mensaje recibido.
     *
     * @see Snackbar
     */
    private fun notificacion(msg: String) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE)
            .setAction("De acuerdo") {}
            .setTextMaxLines(3)
            .show()
    }

    override fun OnGetUserInfo(userInfo: JSONObject?) {
        userInfo?.let { info: JSONObject ->
            info.toString()
        }
    }

    override fun OnGetUserID(id: Int) {
        // Not implemented
    }
}