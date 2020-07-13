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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawCircleRightAngleLine(i : Int, scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val r : Float = Math.min(w, h) / rFactor
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    val x : Float = w * sf3
    val y : Float = (h / 2) * sf2
    save()
    translate(r, h / 2)
    scale(1f, 1f - 2 * i)
    drawCircle(x, y, r * sf1, paint)
    drawLine(0f, 0f, 0f, y, paint)
    drawLine(0f, y, x, y, paint)
    restore()
}

fun Canvas.drawCircleRightAngleLines(scale : Float, w : Float, h : Float, paint : Paint) {
    for (j in 0..1) {
        drawCircleRightAngleLine(j, scale, w, h, paint)
    }
}

fun Canvas.drawCRALNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawCircleRightAngleLines(scale, w, h, paint)
}
