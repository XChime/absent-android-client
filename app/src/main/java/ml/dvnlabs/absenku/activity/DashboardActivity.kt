package ml.dvnlabs.absenku.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ml.dvnlabs.absenku.databinding.DashboardActivityBinding

import ml.dvnlabs.absenku.R
import ml.dvnlabs.absenku.util.database.UsersHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DashboardActivity : AppCompatActivity() {
    private var bi : DashboardActivityBinding? = null
    val usersHelper = UsersHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this,R.layout.dashboard_activity)
        if(!usersHelper.isUsersAvail()){
            onBackPressed()
            this.finish()
        }else{
            readUser()
        }
    }

    private fun readUser(){
        doAsync {
            val values = usersHelper.readUser()
            uiThread {
                //TODO
            }
        }
    }
}