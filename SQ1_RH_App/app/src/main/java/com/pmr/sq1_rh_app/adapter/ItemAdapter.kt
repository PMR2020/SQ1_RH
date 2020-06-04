package com.pmr.sq1_rh_app.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.pmr.sq1_rh_app.R
import com.pmr.sq1_rh_app.Class.ItemToDo


class ItemAdapter(
    private val actionListener: ActionListenerItem
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    private val dataSet: MutableList<ItemToDo> = mutableListOf()

    fun showData(newDataSet : List<ItemToDo>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item,parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,position:Int) {
        Log.d("ItemAdapter", "onBindViewHolder")
        val item=dataSet[position]
        (holder as ItemViewHolder).bind(item.description,item.fait)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //)
        private val checkBox: CheckBox = itemView.findViewById(
            R.id.checkBoxitem
        )


           init {


            itemView.setOnClickListener {
                val postPosition = adapterPosition
                if (postPosition != RecyclerView.NO_POSITION) {
                    val clickedItem = dataSet[postPosition]
                    Log.i("clickedItem",clickedItem.toString())
                    actionListener.onItemClicked(clickedItem)

                }

            }
        }

        fun bind(checkBox_text:String,checked:Boolean
        ) {
            checkBox.text = checkBox_text
            checkBox.isChecked = checked
        }
    }

    interface ActionListenerItem {
        fun onItemClicked(itemToDo: ItemToDo)
    }


}