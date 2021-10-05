package com.example.taskmanagerfree.models

object Recursos{
    val TABLA_TAREA="tarea"
    val ID="id"
    val NOMBRE="nombre"
    val DESCRIPCION="descripcion"
    val ESTADO="estado"
    val FECHA="fecha"
    val HORA="hora"
    val CATEGORIA="Categoria"
    val CREART_TB_TAREA="CREATE TABLE $TABLA_TAREA ($ID INTEGER PRIMARY KEY, $NOMBRE TEXT," +
            " $DESCRIPCION TEXT, $ESTADO TEXT, $FECHA TEXT,$HORA TEXT, $CATEGORIA TEXT)"

}