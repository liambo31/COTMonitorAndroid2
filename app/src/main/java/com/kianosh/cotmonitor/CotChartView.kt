package com.kianosh.cotmonitor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import kotlin.math.max

class CotChartView(context: Context) : View(context) {
    private val grid = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(225, 226, 219); strokeWidth = 1f }
    private val highPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(48, 143, 91); style = Paint.Style.STROKE; strokeWidth = 5f; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
    private val lowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(218, 92, 55); style = Paint.Style.STROKE; strokeWidth = 5f; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
    private var points: List<Pair<Double,Double>> = emptyList()
    fun setData(value: List<Pair<Double,Double>>) { points=value; invalidate() }
    override fun onDraw(canvas: Canvas) { super.onDraw(canvas); val w=width.toFloat(); val h=height.toFloat(); for (y in 26f until h step 42f) canvas.drawLine(0f,y,w,y,grid); if(points.size<2)return; val all=points.flatMap{listOf(it.first,it.second)}; val min=(all.minOrNull()?:0.0).coerceAtMost(0.0); val maxV=(all.maxOrNull()?:0.0).coerceAtLeast(0.0); val range=max(1.0,maxV-min); fun draw(which:Int, paint:Paint){ val path=Path(); points.forEachIndexed{ i,p -> val x=i*(w/(points.size-1)); val v=if(which==0)p.first else p.second; val y=h-20f-(((v-min)/range)*(h-40f)).toFloat(); if(i==0)path.moveTo(x,y)else path.lineTo(x,y)}; canvas.drawPath(path,paint)}; draw(0,highPaint);draw(1,lowPaint) }
}
