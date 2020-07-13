package com.anwesh.uiprojects.circlerightanglelineview

/**
 * Created by anweshmishra on 14/07/20.
 */
import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas

val colors : Array<String> = arrayOf("#F44336", "#3F51B5", "#009688", "#2196F3", "#4CAF50")
val parts : Int = 3
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val rFactor : Float = 9f

