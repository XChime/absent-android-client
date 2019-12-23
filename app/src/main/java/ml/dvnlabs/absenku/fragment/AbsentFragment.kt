package ml.dvnlabs.absenku.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import ml.dvnlabs.absenku.databinding.AbsentFragmentBinding

import ml.dvnlabs.absenku.R
import ml.dvnlabs.absenku.util.database.UsersHelper
import ml.dvnlabs.absenku.util.network.*
import org.jetbrains.anko.*
import org.json.JSONObject


class AbsentFragment : Fragment() {
    var bi : AbsentFragmentBinding? = null
    var usersHelper : UsersHelper?= null
    var toasts = ""
    var progress : ProgressDialog? = null
    var alert : AlertDialog? = null
    private var lastRequest : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersHelper = UsersHelper(context!!)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bi = DataBindingUtil.inflate(inflater, R.layout.absent_fragment,container,false)
        progress = context!!.indeterminateProgressDialog (
            message = "Requesting...",
            title = "Request"
        )
        progress!!.hide()
        return bi!!.root
    }

    private fun initialize(){
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
            == PackageManager.PERMISSION_DENIED){
            val strMan = Array<String?>(1){Manifest.permission.CAMERA}
            activity?.let { ActivityCompat.requestPermissions(it,strMan, 0x1) }

        }else{
            runQrCamera()
        }
    }

    fun runQrCamera(){
        val formats: Collection<BarcodeFormat?> =
            listOf(BarcodeFormat.QR_CODE)
        bi!!.barcodeView.setStatusText("Scan Absent QR Code!")
        bi!!.barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        bi!!.barcodeView.resume()
        bi!!.barcodeView.decodeContinuous(qrCallback)
    }


    val qrCallback : BarcodeCallback = object : BarcodeCallback{
        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {

        }
        override fun barcodeResult(result: BarcodeResult?) {
            toasts = if (result!!.text == null) {
                "Cancelled from fragment"
            }
            // At this point we may or may not have a reference to the activity
            else {
                readUser(result.text)
                "Scanned from fragment: " + result.text
            }
        }
    }

    override fun onPause() {
        bi!!.barcodeView.pauseAndWait()
        super.onPause()
    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        if (menuVisible){
            initialize()
        }
        super.setMenuVisibility(menuVisible)
    }

   /* private fun displayToast(){
        Toast.makeText(context!!,toasts,Toast.LENGTH_LONG).show()
    }*/

    private fun requestAbsent(values : HashMap<String,String>,machine: String){
        val params = HashMap<String,String>()
        params["client"] = values["token"]!!
        params["machine"] = machine
        params["deviceid"] = "123"
        lastRequest = System.currentTimeMillis()
        NetworkRequest(context!!, listenerAbsent, ABSENTREQUEST, NetworkPOST,params)
    }

    var listenerAbsent : listener = object : listener{
        override fun onFetchComplete(data: String) {
            progress!!.hide()
            val datas = JSONObject(data)
            if(!datas.getBoolean("Error")){
                Toast.makeText(context!!,"Success Absent!",Toast.LENGTH_SHORT).show()
            }else{
                context!!.alert{
                    title = "Error"
                    message = datas.getString("Message")
                    positiveButton("OK"){
                        it.dismiss()
                    }
                }.show()
            }
        }

        override fun onFetchFailure(msg: String) {
            progress!!.hide()
            context!!.alert{
                title = "Error"
                message = msg
                positiveButton("OK"){
                    it.dismiss()
                }
            }.show()
        }

        override fun onFetchStart() {
            progress!!.show()
        }
    }

    private fun readUser(machine : String){
        doAsync {
            val values = usersHelper!!.readUser()
            uiThread {
                if (values != null) {
                    val left= System.currentTimeMillis() - lastRequest
                    println("Now Timestamp: ${System.currentTimeMillis()} LastRequest: $lastRequest")
                    println("Sisa $left")
                    if (lastRequest == 0.toLong()){
                        requestAbsent(values,machine)
                    }else if(left >= 5000.toLong() ){
                        requestAbsent(values,machine)
                    }
                }
            }
        }
    }

}
