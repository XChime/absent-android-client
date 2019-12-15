package ml.dvnlabs.absenku.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import ml.dvnlabs.absenku.databinding.AbsentFragmentBinding
import org.jetbrains.anko.alert

import ml.dvnlabs.absenku.R


class AbsentFragment : Fragment() {
    var bi : AbsentFragmentBinding? = null
    var toasts = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bi = DataBindingUtil.inflate(inflater, R.layout.absent_fragment,container,false)
        return bi!!.root
    }

    private fun initialize(){
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
            == PackageManager.PERMISSION_DENIED){
            val strMan = Array<String?>(1){Manifest.permission.CAMERA}
            activity?.let { ActivityCompat.requestPermissions(it,strMan, 0x1) };

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
                activity!!.alert {
                    title = "Contents"
                    message = result.text
                }
                "Scanned from fragment: " + result.text
            }

            displayToast()
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

    private fun displayToast(){
        Toast.makeText(context!!,toasts,Toast.LENGTH_LONG).show()
    }

}