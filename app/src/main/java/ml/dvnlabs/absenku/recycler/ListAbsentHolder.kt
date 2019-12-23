package ml.dvnlabs.absenku.recycler

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_list_absent.view.*
import ml.dvnlabs.absenku.R
import ml.dvnlabs.absenku.model.AbsentModel
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class ListAbsentHolder(view : View, var context: Context) : RecyclerView.ViewHolder(view) {
    var inClock : TextView = view.findViewById(R.id.inClock)
    var outClock : TextView = view.findViewById(R.id.outClock)
    var date : TextView = view.findViewById(R.id.dateText)
    var status : TextView = view.findViewById(R.id.statusText)
    fun bindView(data : AbsentModel){
        val format =  SimpleDateFormat("dd MMMM yyyy")
        inClock.text = data.inTime
        outClock.text = data.outTime
        val dates = SimpleDateFormat("yyyy-MM-dd").parse(data.date)
        date.text = "Date : ${format.format(dates)}"
        status.text = data.status
    }
}
