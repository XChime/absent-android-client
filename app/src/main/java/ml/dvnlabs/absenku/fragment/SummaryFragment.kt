package ml.dvnlabs.absenku.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ml.dvnlabs.absenku.databinding.SummaryFragmentBinding
import ml.dvnlabs.absenku.R
import ml.dvnlabs.absenku.model.SessionUserData
import ml.dvnlabs.absenku.util.database.UsersHelper
import ml.dvnlabs.absenku.util.network.GETPROFILE
import ml.dvnlabs.absenku.util.network.NetworkGET
import ml.dvnlabs.absenku.util.network.NetworkRequest
import ml.dvnlabs.absenku.util.network.listener
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONException
import org.json.JSONObject

class SummaryFragment : Fragment() {

    var values : HashMap<String,String>? = null
    var usersHelper : UsersHelper?= null

    var bi : SummaryFragmentBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersHelper = UsersHelper(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bi = DataBindingUtil.inflate(inflater,R.layout.summary_fragment,container,false)
        readUser()
        return bi!!.root
    }

    private fun requestUserData(values : HashMap<String,String>){
        NetworkRequest(context!!,listenerUserData, "$GETPROFILE/${values["token"]}", NetworkGET,null)
    }

    private fun parseUserData(obj : JSONObject){
        try {
            val nik = obj.getString("NIK")
            val name = obj.getString("Nama")
            val divisi = obj.getInt("Divisi")
            val namadivisi = obj.getString("NamaDivisi")
            val jadwal  = obj.getString("Jadwal")
            val userdata = SessionUserData(nik,name,divisi,namadivisi,jadwal)
            bi!!.nikText.text = nik
            bi!!.nameText.text = name
        }catch (e : JSONException){
            e.printStackTrace()
        }
    }

    val listenerUserData : listener = object : listener {
        override fun onFetchComplete(data: String) {
            try {
                val dats = JSONObject(data)
                if(!dats.getBoolean("Error")){
                    parseUserData(dats.getJSONObject("Profile"))
                }else{
                    activity!!.alert {
                        title = "Error"
                        message = dats.getString("Message")
                    }
                }

            }catch (e : JSONException){
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String) {
            activity!!.alert {
                title = "Error!"
                message = msg
            }
        }

        override fun onFetchStart() {

        }
    }

    private fun readUser(){
        doAsync {
            val values = usersHelper!!.readUser()
            uiThread {
                if (values != null) {
                    requestUserData(values)
                }
            }
        }
    }
}
