package ml.dvnlabs.absenku.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ml.dvnlabs.absenku.model.AbsentModel

class ListAbsentAdapter(context : Context,resource : Int,model : ArrayList<AbsentModel>) : RecyclerView.Adapter<ListAbsentHolder>() {
    var data : ArrayList<AbsentModel>? = model
    var context : Context? = context
    var itemRes : Int = resource
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAbsentHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(itemRes,parent,false)
        return ListAbsentHolder(view,context!!)
    }

    override fun onBindViewHolder(holder: ListAbsentHolder, position: Int) {
        val models : AbsentModel = this.data!![holder.adapterPosition]
        holder.bindView(models)
    }

    override fun getItemCount(): Int {
        if(data != null){
            return data!!.size
        }
        return 0
    }
}
