package com.kianosh.cotmonitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import kotlin.random.Random

class CotForegroundService : Service() {
    private val handler = Handler(Looper.getMainLooper()); private lateinit var prefs: android.content.SharedPreferences; private var running=false
    private val tickLoop = object : Runnable { override fun run() { if(!running)return; val engine=CotEngine(prefs); val s=engine.snapshot(); val base=if(s.price>0)s.price else 18000.0; val moves=listOf(-2.0,-1.0,-.5,.5,1.0,2.0,0.0); val p=((base+moves.random())*4).toInt()/4.0; val side=if(Random.nextDouble()>.49)Side.BUY else Side.SELL; engine.ingest(p,listOf(1,1,2,3,5).random(),side); updateNotification(engine.snapshot()); handler.postDelayed(this,850) } }
    override fun onCreate(){super.onCreate();prefs=getSharedPreferences("cot_state",MODE_PRIVATE);createChannel()}
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { startForeground(7, notification("در حال محاسبه‌ی COT")); running=true; handler.removeCallbacks(tickLoop); handler.post(tickLoop); return START_STICKY }
    override fun onDestroy(){running=false;handler.removeCallbacks(tickLoop);super.onDestroy()}
    override fun onBind(intent: Intent?): IBinder?=null
    private fun createChannel(){if(Build.VERSION.SDK_INT>=26)getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("cot","COT Monitor",NotificationManager.IMPORTANCE_LOW))}
    private fun notification(text:String):Notification{val pi=PendingIntent.getActivity(this,0,Intent(this,MainActivity::class.java),PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT);return if(Build.VERSION.SDK_INT>=26)Notification.Builder(this,"cot").setContentTitle("COT Monitor").setContentText(text).setSmallIcon(android.R.drawable.stat_notify_sync).setContentIntent(pi).setOngoing(true).build() else Notification.Builder(this).setContentTitle("COT Monitor").setContentText(text).setSmallIcon(android.R.drawable.stat_notify_sync).setContentIntent(pi).setOngoing(true).build()}
    private fun updateNotification(s:Snapshot){getSystemService(NotificationManager::class.java).notify(7,notification("${s.symbol}: High ${nf(s.high)} · Low ${nf(s.low)}"))}
}
