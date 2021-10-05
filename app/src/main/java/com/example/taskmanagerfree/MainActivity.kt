package com.example.taskmanagerfree

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerfree.models.AdaptadorTareas
import com.example.taskmanagerfree.models.MiSqlHelper
import com.example.taskmanagerfree.models.Recursos
import com.example.taskmanagerfree.models.Task
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    var dia=0 ;var mes=0 ;var anio=0 ;var hora=0 ;var minuto=0


    var diaGuardado=0; var mesGuardado=0; var anioGuardado=0 ;var horaGuardada=0 ;var minutoGuardado=0
    var dbCon=MiSqlHelper(this,"bd_tareas",null,1)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btn_guardar = findViewById<Button>(R.id.btn_add)
        btn_guardar.setOnClickListener(View.OnClickListener { view ->
            showDialog("agg",-1)
        })


        //actualizarRecycler(cargarListaBD())
        activarFltro()



    }//fin oncreate

    private fun showDialog(nombre: String,id: Int) {
         val dialog = Dialog(this)
        anioGuardado=0;//se usa para comprobar que se haya ingresado la fecha
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.lyt_task_dialog)

        var idActual:Int
        var isUpdate=false
        if(id<0) idActual=cargarListaBD().size
        else{
            idActual=id

            isUpdate=true
        }

        //definimos las variables de uso


        var nombreActual= String();
        var descripcionActual: String;var fechaActual= String()
        var horaActual= String(); var estadoActual= "";var categoriaActual= ""

        //importamos la variables de la instancia dialog
        val horaMinuto =dialog.findViewById(R.id.txt_fechaHora) as TextView
        val btn_Calendario = dialog.findViewById(R.id.btn_Calendar) as Button
        val btnActualizar= dialog.findViewById(R.id.btn_actualizar) as Button
        val btn_cancelar = dialog.findViewById(R.id.btn_cancelar) as Button
        val btn_guardar = dialog.findViewById(R.id.btn_guardar) as  Button

        val et_nombre=dialog.findViewById(R.id.et_nombreTarea) as EditText
        val et_descriptiop=dialog.findViewById(R.id.et_descripcionTarea) as EditText
        val spinnerEstado=dialog.findViewById(R.id.spinnerDialogestado) as Spinner
        val spinnerD=dialog.findViewById(R.id.spinnerDialog) as Spinner
        //fin importacion
        horaMinuto.setText(actualizarTexto())

        //llenarEspacios se se trata de una actualizacion
        if(isUpdate){
            val tareaActual=cargarListaBD().get(idActual)
            et_descriptiop.setText(tareaActual.descripcion)
            et_nombre.setText(tareaActual.nombre)
        }


        btn_Calendario.setOnClickListener(View.OnClickListener { view ->
        setCalendar()
            DatePickerDialog(this,this,anio,mes,dia).show()//lanza el dialog DatePicker

        })

        btnActualizar.setOnClickListener(View.OnClickListener { view ->
            horaMinuto.setText(actualizarTexto())

        })
        spinnerD.onItemSelectedListener= object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                when(position){
                    0->categoriaActual=""
                    1->categoriaActual="Domestica"
                    2->categoriaActual="Academica"
                    3->categoriaActual="Profesional"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }



        }
        spinnerEstado.onItemSelectedListener= object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                when(position){
                    0->estadoActual="pendiente"
                    1->estadoActual="en proceso"
                    2->estadoActual="finalizada"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }



        }


        btn_cancelar.setOnClickListener(View.OnClickListener { view ->
            dialog.dismiss()

        })


        btn_guardar.setOnClickListener(View.OnClickListener() {
            nombreActual=et_nombre.text.toString()
            descripcionActual=et_descriptiop.text.toString()
            fechaActual="Fecha $diaGuardado-$mesGuardado-$anioGuardado"
            horaActual="Hora ${addCero(horaGuardada)}$horaGuardada:${addCero(minutoGuardado)}$minutoGuardado"
            if(!isUpdate) {

                if(crearTarea(idActual,nombreActual,descripcionActual,estadoActual,fechaActual,horaActual,categoriaActual)) {
                    //Toast.makeText(applicationContext, "agregado correctamente", Toast.LENGTH_LONG).show()
                    actualizarRecycler(cargarListaBD())
                    dialog.dismiss()
                }else{
                    Toast.makeText(applicationContext, "Faltan datos", Toast.LENGTH_LONG).show()
                }
            }
            else{
                if(actualizarTarea(idActual,nombreActual,descripcionActual,estadoActual,fechaActual,horaActual,categoriaActual)) {
                    //Toast.makeText(applicationContext, "actualizado correctamente", Toast.LENGTH_LONG).show()
                    actualizarRecycler(cargarListaBD())
                    dialog.dismiss()
                }else{
                    Toast.makeText(applicationContext, "Faltan datos", Toast.LENGTH_LONG).show()
                }
            }



        })

        dialog.show()

}




    //funciones abstractas de Datepicker y timePicker
    override fun onDateSet(p0: DatePicker?, anio: Int, mes: Int, dia: Int) {
        diaGuardado=dia
        anioGuardado=anio
        mesGuardado=mes
        setCalendar()
        TimePickerDialog(this,this,hora,minuto,true).show()

    }

    override fun onTimeSet(p0: TimePicker?, hora: Int, minuto: Int) {
        horaGuardada=hora
        minutoGuardado=minuto
    }
    //fin funciones absptractas
    private fun setCalendar() {

        val cal= Calendar.getInstance()
        dia=cal.get(Calendar.DAY_OF_MONTH)
        mes=cal.get(Calendar.MONTH)
        anio=cal.get(Calendar.YEAR)
        hora=cal.get(Calendar.HOUR)
        minuto=cal.get(Calendar.MINUTE)
        //cal.set(anio,mes,dia,hora,minuto)


    }
    //actualiza el texto de la fecha y hora
    private fun actualizarTexto():String{
        return  "$anioGuardado-$mesGuardado-$diaGuardado :H: $horaGuardada - M: $minutoGuardado"
    }
    private fun addCero(tiempo:Int):String{
        if(tiempo<10) return "0"
        else return ""
    }
    private fun actualizarRecycler(arregloActual: ArrayList<Task>){
        listaReci.layoutManager=LinearLayoutManager(this)

        val adaptador=AdaptadorTareas(arregloActual)
        listaReci.adapter=adaptador
        adaptador.setOnItemClickListener(object :AdaptadorTareas.onItemClickListener{
            override fun onItemClick(position: Int) {
                showDialog("agg",position)
            }

        })


    }

    private fun activarFltro(){
        var spin=sp_filtro
        spin.onItemSelectedListener= object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {

                actualizarRecycler(filtrarLista(position))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }



        }
    }
    private fun filtrarLista(filtro:Int):ArrayList<Task>{
       var listaActual=cargarListaBD()
        var nuevaLista= ArrayList<Task>()
        when(filtro){
            0->{
                return listaActual
            }
            1->{
                for(tarea in listaActual) {
                    if (tarea.categoria.equals("Domestica")) nuevaLista.add(tarea)
                }
                return nuevaLista
            }
            2->{
                for(tarea in listaActual) {
                    if (tarea.categoria.equals("Academica")) nuevaLista.add(tarea)
                }
                return nuevaLista
            }
            3->{
                for(tarea in listaActual) {
                    if (tarea.categoria.equals("Profesional")) nuevaLista.add(tarea)
                }
                return nuevaLista
            }
            else->{ return listaActual}
        }

    }

    private fun cargarListaBD():ArrayList<Task> {

        var db: SQLiteDatabase = dbCon.readableDatabase
        var arreglo = ArrayList<Task>()
        //se Crea un arreglo para solicitar las columnas en el cursor
        val campos: Array<String> = arrayOf(
            Recursos.ID,
            Recursos.NOMBRE,//1
            Recursos.DESCRIPCION,//2
            Recursos.CATEGORIA,//3
            Recursos.ESTADO,//4
            Recursos.FECHA,//5
            Recursos.HORA)//6

        val cursor = db.query(Recursos.TABLA_TAREA, campos, null, null, null, null, "id asc")
        cursor.moveToFirst()//representa la primer fila de la tabla tareas

        while (!cursor.isAfterLast) {
            val tareaCargada = Task(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(3)
            )
            arreglo.add(tareaCargada)
            cursor.moveToNext()
        }
        cursor.close()
        /*

        val nuevaTarea= Task(1,"correr","Salir a Correr","Domestica",calActual.,"pendiente")
        val nuevaTarea1= Task(2,"respirar","Salir a respirar","Academica",calActual,"pendiente")
        val nuevaTarea2= Task(2,"respirar","Salir a respirar","Academica",calActual,"pendiente")
        val nuevaTarea3= Task(2,"respirar","Salir a respirar","Profesional",calActual,"pendiente")
        arreglo.add(nuevaTarea);arreglo.add(nuevaTarea1);arreglo.add(nuevaTarea2);arreglo.add(nuevaTarea3)

         */
        return arreglo
    }

    private fun crearTarea(id: Int, nombre:String, descripcion:String, estado:String, fecha:String, hora:String, categoria:String):Boolean{
        if(nombre.isEmpty() or descripcion.isEmpty()   or categoria.isEmpty() or estado.isEmpty() or (anioGuardado==0)) {
            return false
        }else{

            val nuevaTarea=Task(id,nombre,descripcion,estado,fecha,hora,categoria)
            addTask(nuevaTarea)
            return true
        }

    }
    private fun actualizarTarea(id: Int, nombre:String, descripcion:String, estado:String, fecha:String, hora:String, categoria:String):Boolean{
        if(nombre.isEmpty() or descripcion.isEmpty()   or categoria.isEmpty() or estado.isEmpty() or (anioGuardado==0)) {
            return false
        }else{

            val nuevaTarea=Task(id,nombre,descripcion,estado,fecha,hora,categoria)
            updateTask(nuevaTarea)
            return true
        }

    }

    private fun addTask(nuevaTarea: Task) {
        var db: SQLiteDatabase = dbCon.writableDatabase
        var valores=ContentValues()
        valores.put(Recursos.ID,nuevaTarea.id)
        valores.put(Recursos.NOMBRE,nuevaTarea.nombre)
        valores.put(Recursos.DESCRIPCION,nuevaTarea.descripcion)
        valores.put(Recursos.ESTADO,nuevaTarea.estado)
        valores.put(Recursos.FECHA,nuevaTarea.fecha)
        valores.put(Recursos.HORA,nuevaTarea.hora)
        valores.put(Recursos.CATEGORIA,nuevaTarea.categoria)
        db.insert(Recursos.TABLA_TAREA,null,valores)
        Toast.makeText(applicationContext, "agregado en el id${nuevaTarea.id}", Toast.LENGTH_LONG).show()
        //db.delete(Recursos.TABLA_TAREA,null,null)
    }
    private fun updateTask(nuevaTarea: Task) {
        var db: SQLiteDatabase = dbCon.writableDatabase
        var valores=ContentValues()

        valores.put(Recursos.NOMBRE,nuevaTarea.nombre)
        valores.put(Recursos.DESCRIPCION,nuevaTarea.descripcion)
        valores.put(Recursos.ESTADO,nuevaTarea.estado)
        valores.put(Recursos.FECHA,nuevaTarea.fecha)
        valores.put(Recursos.HORA,nuevaTarea.hora)
        valores.put(Recursos.CATEGORIA,nuevaTarea.categoria)
        Toast.makeText(applicationContext, "actualizado en el id${nuevaTarea.id}", Toast.LENGTH_LONG).show()

        db.update(Recursos.TABLA_TAREA,valores,"${Recursos.ID}=${nuevaTarea.id}",null )


    }


}



