package com.kianosh.cotmonitor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private lateinit var prefs: android.content.SharedPreferences; private lateinit var chart:CotChartView; private lateinit var status:TextView; private lateinit var high:TextView; private lateinit var low:TextView; private lateinit var price:TextView; private lateinit var volume:TextView; private lateinit var feed:TextView; private lateinit var toggle:Button; private var serviceOn=false; private val handler=Handler(Looper.getMainLooper())
    private val refresh=object:Runnable{override fun run(){render(CotEngine(prefs).snapshot());handler.postDelayed(this,1000)}}
    private val bg=Color.rgb(247,248,244); private val ink=Color.rgb(54,43,61); private val muted=Color.rgb(105,99,107); private val plum=Color.rgb(94,58,120); private val green=Color.rgb(47,139,88); private val coral=Color.rgb(210,85,51)
    override fun onCreate(b:Bundle?){super.onCreate(b);prefs=getSharedPreferences("cot_state",MODE_PRIVATE);if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),22);buildUi();handler.post(refresh)}
    override fun onDestroy(){handler.removeCallbacks(refresh);super.onDestroy()}
    private fun buildUi(){
        val scroll=ScrollView(this); val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,18,18,110);setBackgroundColor(bg);layoutDirection=View.LAYOUT_DIRECTION_RTL;textDirection=View.TEXT_DIRECTION_RTL};scroll.addView(root);setContentView(scroll)
        val head=LinearLayout(this).apply{gravity=Gravity.CENTER_VERTICAL;layoutDirection=View.LAYOUT_DIRECTION_RTL};val title=TextView(this).apply{text="COT Monitor
محاسبه‌ی مستقل High / Low";setTextColor(ink);textSize=20f;setTypeface(null,1)};head.addView(title,LinearLayout.LayoutParams(0,70,1f));val reset=button("پاک کردن",false);reset.setOnClickListener{CotEngine(prefs).reset();render(CotEngine(prefs).snapshot());toast("داده‌ها پاک شد")};head.addView(reset);root.addView(head)
        val intro=panel(root,plum);intro.addView(text("نسخه‌ی اندرویدی آماده‌ی تست",22f,Color.rgb(247,248,244),true));intro.addView(text("هسته‌ی محاسبه با کد Quantower یکسان است. در این نسخه شبیه‌ساز در سرویس پس‌زمینه اجرا می‌شود و وضعیت روی گوشی ذخیره می‌ماند.",14f,Color.rgb(225,218,230),false));toggle=button("شروع شبیه‌ساز",true);toggle.setOnClickListener{if(serviceOn)stopEngine()else startEngine()};intro.addView(toggle,LinearLayout.LayoutParams(-1,52));
        root.addView(text("وضعیت فعلی",18f,ink,true));val grid=GridLayout(this).apply{columnCount=2;setPadding(0,4,0,4)};high=metric(grid,"COT High");low=metric(grid,"COT Low");price=metric(grid,"آخرین قیمت");volume=metric(grid,"حجم تجمعی");root.addView(grid)
        root.addView(text("ردیابی فشار سفارش",18f,ink,true));chart=CotChartView(this).apply{setBackgroundColor(Color.rgb(253,253,250));layoutParams=LinearLayout.LayoutParams(-1,270).apply{setMargins(0,8,0,0)}};root.addView(chart);val legend=text("سبز: High                 قرمز: Low",12f,muted,false);root.addView(legend)
        root.addView(text("آخرین معاملات",18f,ink,true));feed=text("",13f,muted,false);feed.setPadding(14,10,14,10);feed.setBackgroundColor(Color.rgb(253,253,250));root.addView(feed)
        root.addView(text("ثبت تیک دستی برای تست",18f,ink,true));val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;layoutDirection=View.LAYOUT_DIRECTION_LTR;gravity=Gravity.CENTER_VERTICAL};val priceInput=EditText(this).apply{hint="قیمت";setText("18000");inputType=2};val sizeInput=EditText(this).apply{hint="حجم";setText("1");inputType=2};val side=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,arrayOf("خرید تهاجمی","فروش تهاجمی"))};val add=button("ثبت",true);row.addView(priceInput,LinearLayout.LayoutParams(0,52,1f));row.addView(sizeInput,LinearLayout.LayoutParams(0,52,.55f));row.addView(side,LinearLayout.LayoutParams(0,52,1f));row.addView(add,LinearLayout.LayoutParams(0,52,.7f));root.addView(row);add.setOnClickListener{val p=priceInput.text.toString().toDoubleOrNull();val s=sizeInput.text.toString().toIntOrNull();if(p!=null&&s!=null&&s>0){CotEngine(prefs).ingest(p,s,if(side.selectedItemPosition==0)Side.BUY else Side.SELL);toast("تیک ثبت شد")}else toast("قیمت و حجم را درست وارد کن")}
    }
    private fun startEngine(){val i=Intent(this,CotForegroundService::class.java);if(Build.VERSION.SDK_INT>=26)startForegroundService(i)else startService(i);serviceOn=true;toggle.text="توقف محاسبه";toast("سرویس پس‌زمینه فعال شد")}
    private fun stopEngine(){stopService(Intent(this,CotForegroundService::class.java));serviceOn=false;toggle.text="شروع شبیه‌ساز";toast("محاسبه متوقف شد")}
    private fun render(s:Snapshot){high.text="COT High
${nf(s.high)}
مرجع ${nf(s.highPrice)}";low.text="COT Low
${nf(s.low)}
مرجع ${nf(s.lowPrice)}";price.text="آخرین قیمت
${if(s.price>0)nf(s.price) else "--"}
${s.tickCount} تیک";volume.text="حجم تجمعی
${nf(s.volume)}
خرید منهای فروش";high.setTextColor(if(s.high>=0)green else coral);low.setTextColor(if(s.low>=0)green else coral);chart.setData(s.history);feed.text=if(s.trades.isEmpty())"برای دیدن تیک‌ها، شبیه‌ساز را شروع کن." else s.trades.joinToString("
"){"${nf(it.price)}   ${if(it.side==Side.BUY)"خرید" else "فروش"}   حجم ${it.size}"})}
    private fun panel(parent:LinearLayout,color:Int):LinearLayout{val p=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,18,18,18);setBackgroundColor(color);layoutParams=LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT).apply{setMargins(0,12,0,18)}};parent.addView(p);return p}
    private fun metric(g:GridLayout,name:String):TextView{val t=text(name+"
--
منتظر داده",16f,ink,true);t.setPadding(14,16,14,16);t.setBackgroundColor(Color.rgb(253,253,250));val lp=GridLayout.LayoutParams().apply{width=0;columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);rowSpec=GridLayout.spec(GridLayout.UNDEFINED);setMargins(0,1,1,1)};g.addView(t,lp);return t}
    private fun text(s:String,size:Float,color:Int,bold:Boolean)=TextView(this).apply{text=s;textSize=size.toFloat();setTextColor(color);setPadding(0,8,0,8);if(bold)setTypeface(null,1)}
    private fun button(s:String,primary:Boolean)=Button(this).apply{text=s;textSize=13f;setTextColor(if(primary)Color.WHITE else ink);setBackgroundColor(if(primary)plum else Color.rgb(232,231,225));isAllCaps=false}
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
}
