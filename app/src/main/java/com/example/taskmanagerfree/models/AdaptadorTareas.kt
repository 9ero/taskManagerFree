package com.example.taskmanagerfree.models



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerfree.R
import com.example.taskmanagerfree.models.AdaptadorTareas.*
import kotlinx.android.synthetic.main.lyt_task_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class AdaptadorTareas( val tasklist: ArrayList<Task>) : RecyclerView.Adapter<AdaptadorTareas.ViewHoldertask>() {


    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: onItemClickListener){
        mListener=listener
    }

    class ViewHoldertask(val view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener { listener.onItemClick(bindingAdapterPosition) }
        }
        fun reder(tarea: Task) {

            view.txt_fecha.text = tarea.fecha
            view.txt_categoria.text = tarea.categoria
            view.txt_descripcion.text = tarea.descripcion
            view.txt_estado.text = tarea.estado
            view.txt_nombre.text = tarea.nombre
            view.txt_hora.text = tarea.hora



        }


    }






    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoldertask {
        val layoutInflater =LayoutInflater.from(parent.context)
        return ViewHoldertask(layoutInflater.inflate(R.layout.lyt_task_item, parent, false),mListener)
    }
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHoldertask: ViewHoldertask, position: Int) {

        viewHoldertask.reder(tasklist[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = tasklist.size

}
