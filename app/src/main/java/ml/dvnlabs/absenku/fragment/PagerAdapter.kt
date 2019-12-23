package ml.dvnlabs.absenku.fragment

import android.content.Context
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class PagerAdapter(fm : FragmentManager,numTabs : Int,context: Context) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var counter : Int = numTabs
    var mContext : Context = context
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> AbsentFragment()
            1 -> SummaryFragment()
            2 -> HistoryFragment()
            else-> null!!
        }
    }

    override fun getCount(): Int {
        return counter
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> ""
            1 -> "Summary"
            2 -> "History"
            else -> null
        }
    }
}
