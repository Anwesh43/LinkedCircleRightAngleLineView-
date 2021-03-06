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
    val x : Float = (w - 2 * r) * sf3
    val y : Float = (h / 2 - r) * sf2
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

class CircleRightAngleLineView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CRALNode(var i : Int, val state : State = State()) {

        private var next : CRALNode? = null
        private var prev : CRALNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CRALNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCRALNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CRALNode {
            var curr : CRALNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr !=  null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class CircleRightAngleLine(var i : Int) {

        private var curr : CRALNode = CRALNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : CircleRightAngleLineView) {

        private val animator : Animator = Animator(view)
        private val cral : CircleRightAngleLine = CircleRightAngleLine(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            cral.draw(canvas, paint)
            animator.animate {
                cral.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            cral.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : CircleRightAngleLineView {
            val view : CircleRightAngleLineView = CircleRightAngleLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}