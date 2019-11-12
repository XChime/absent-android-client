package ml.dvnlabs.absenku.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.login_activity.*
import ml.dvnlabs.absenku.databinding.LoginActivityBinding

import ml.dvnlabs.absenku.R
import ml.dvnlabs.absenku.util.database.UsersHelper
import ml.dvnlabs.absenku.util.network.LOGINURL
import ml.dvnlabs.absenku.util.network.NetworkPOST
import ml.dvnlabs.absenku.util.network.NetworkRequest
import ml.dvnlabs.absenku.util.network.listener
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    var bi: LoginActivityBinding? = null
    val usersHelper = UsersHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isUserAvail()){
            toDashBoard()
        }else{
            bi = DataBindingUtil.setContentView(this,R.layout.login_activity)
            bi!!.ButtonLogin.setOnClickListener {
                requestLogin()
            }
        }
    }

    private fun requestLogin(){
        val params = HashMap<String,String>()
        val nik = InputNIK.text.toString()
        val password = InputPassword.text.toString()
        if (nik!= "" && password!= ""){
            params["nik"] = nik
            params["password"] = password
            params["device"] = "123"
            NetworkRequest(this,listenerLogin, LOGINURL, NetworkPOST,params)
        }else{
            Toast.makeText(this@LoginActivity,"NIK / Password Empty",Toast.LENGTH_LONG).show()
        }
    }

    private val listenerLogin : listener = object : listener{
        override fun onFetchComplete(data: String) {
            val datas = JSONObject(data)
            try {
                if (!datas.getBoolean("Error")){
                    Toast.makeText(this@LoginActivity,"Success Login!",Toast.LENGTH_SHORT).show()
                    writeUsers(InputNIK.text.toString(),"123",datas.getString("Data"))
                }else{
                    Toast.makeText(this@LoginActivity,datas.getString("Message"),Toast.LENGTH_LONG).show()
                }
            }catch (e : JSONException) {
                e.printStackTrace()
            }
        }

        override fun onFetchFailure(msg: String) {
            alert {
                title = "Error"
                message = msg
            }.show()
        }

        override fun onFetchStart() {
            println("STARTING REQ")
        }
    }
    private fun toDashBoard(){
        val intent = Intent(this,DashboardActivity::class.java)
        startActivity(intent)
    }

    /*Here Some Function for writing data to internal DB with coroutines*/
    fun writeUsers(nik : String, deviceID : String, token : String){
        doAsync {
            usersHelper.createUsers(nik, deviceID, token)
            uiThread {
                toDashBoard()
            }
        }
    }
    private fun isUserAvail():Boolean{
        return usersHelper.isUsersAvail()
    }

}