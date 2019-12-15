package ml.dvnlabs.absenku.activity

import android.R.attr.data
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import ml.dvnlabs.absenku.databinding.DashboardActivityBinding
import ml.dvnlabs.absenku.fragment.PagerAdapter
import ml.dvnlabs.absenku.fragment.SummaryFragment
import ml.dvnlabs.absenku.util.database.UsersHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import ml.dvnlabs.absenku.R


class DashboardActivity : AppCompatActivity() {
    private var bi : DashboardActivityBinding? = null
    val usersHelper = UsersHelper(this)
    var adapter : PagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this,R.layout.dashboard_activity)
        if(!usersHelper.isUsersAvail()){
            onBackPressed()
            this.finish()
        }else{
            initialize()
        }
    }
    private fun initialize(){
        val pager = bi!!.dashPager
        val tabs = bi!!.dashTabs

        adapter = PagerAdapter(supportFragmentManager,tabs.tabCount,this)
        pager.adapter = adapter
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.setupWithViewPager(pager)
        tabs.getTabAt(0)?.icon = this.getDrawable(R.drawable.ic_qr_code)
        pager.currentItem = 1
        MaterialShowcaseView.Builder(this)
            .setTarget(tabs)
            .setDismissText("I Understand!")
            .setContentText("AbsenKu is Application for Employee on respectively company with tracking down your absent\nSwipe to right to absent!")
            .setDelay(1000)
            .singleUse("001")
            .show()
    }
}