package ml.dvnlabs.absenku.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ml.dvnlabs.absenku.R
import ml.dvnlabs.absenku.databinding.HistoryFragmentBinding
import ml.dvnlabs.absenku.model.AbsentModel
import ml.dvnlabs.absenku.recycler.ListAbsentAdapter
import ml.dvnlabs.absenku.util.database.UsersHelper
import ml.dvnlabs.absenku.util.network.ABSENTLIST
import ml.dvnlabs.absenku.util.network.NetworkGET
import ml.dvnlabs.absenku.util.network.NetworkRequest
import ml.dvnlabs.absenku.util.network.listener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HistoryFragment:Fragment() {
    var bi : HistoryFragmentBinding? = null
    var adapter : ListAbsentAdapter? = null
    private var usersHelper : UsersHelper? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bi = DataBindingUtil.inflate(inflater, R.layout.history_fragment,container,false)
        usersHelper = UsersHelper(context!!)
        readUser()
        return bi!!.root
    }

    private fun initialization(values : HashMap<String,String>){
        NetworkRequest(context!!,listHistoryNet, ABSENTLIST+values["nik"], NetworkGET,null)
    }
    private fun parseHistoryData(model : JSONObject){
        try {
            val arrays : JSONArray = model.getJSONArray("Absent")
            val models : ArrayList<AbsentModel> = ArrayList()
            for (i in 0 until arrays.length()){
                val item = arrays.getJSONObject(i)
                val status = item.getString("Status")
                val date = item.getString("Date")
                val intime = item.getString("IN")
                val outtime = item.getString("OUT")
                val info = item.getString("Info")
                models.add(AbsentModel(status,date,intime,outtime,info))
            }
            adapter = ListAbsentAdapter(context!!,R.layout.rv_list_absent,models)
            val linearLayoutManager = LinearLayoutManager(context!!)
            bi!!.historyList.layoutManager = linearLayoutManager
            bi!!.historyList.adapter = adapter

        }catch (e : JSONException){
            e.printStackTrace()
        }
    }

    private val listHistoryNet : listener = object : listener{
        override fun onFetchComplete(data: String) {
            try {
                val objects = JSONObject(data)
                if(!objects.getBoolean("Error")){
                    parseHistoryData(objects.getJSONObject("Data"))
                }
            }catch (e : JSONException) {
                e.printStackTrace()
            }

        }

        override fun onFetchFailure(msg: String) {

        }

        override fun onFetchStart() {

        }
    }

    private fun readUser(){
        doAsync {
            val values = usersHelper!!.readUser()
            uiThread {
                if (values != null) {
                    initialization(values)
                }
            }
        }
    }


}
