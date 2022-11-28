package com.example.sleephelper

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.sleephelper.databinding.ActivityCalendarBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.util.*


class CalendarActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCalendarBinding
    private lateinit var chart : LineChart

    var firebaseAuth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

    // firebase에서 받아온 정보를 저장할 변수 선언
    var wine:String?=null // 와인 섭취량
    var beer:String?=null // 맥주 섭취량
    var makguli:String?=null // 탁주 섭취량
    var soju:String?=null // 소주 섭취량
    var coffee:String?=null // 커피 섭취량
    var nap_time:String?=null // 낮잠 시간
    var sleep_time:String?=null // 취침 시각
    var wakeup_time:String?=null // 기상 시각
    var gotobed_time:String?=null // 침대에 누운 시각
    var outtobed_time:String?=null // 침대에서 벗어난 시각
    var emoji:String?=null // 수면 이모지

    var time_to_sleep:String?=null // 잠들 때까지 걸린 시간
    var time_in_bed:String?=null // 침대에서 머문 시간
    var sleeptime:String?=null // 총 수면 시간

    lateinit var time1:String
    lateinit var time2: String
    lateinit var time3:String
    lateinit var time4:String
    lateinit var time5:String
    lateinit var time6:String
    lateinit var sleepLevel:String
    lateinit var filePath:String

    var emojiPath:Int = 0 // 사용자가 선택한 이모지를 캘린더에 나타내기 한 변수


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // firebase 연동
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth!!.currentUser?.email

        filePath = "20221128"

        firestore!!.collection("Data")
            .document(firebaseAuth?.currentUser?.email!!)
            .collection("sleepdata").document(
<<<<<<< HEAD
                "20221125").get()      // 문서 가져오기
=======
                filePath).get()      // 문서 가져오기
>>>>>>> 8a1f2060cb1597c720990894c54e890db5c15a98
            .addOnSuccessListener { result ->
                // 성공할 경우
                Log.e("로그", result.get("beer").toString())

                // 수면일기에 입력된 정보 변수에 넣기
                wine = result.get("wine").toString() + "잔"
                beer = result.get("beer").toString() + "잔"
                makguli = result.get("makgeolli").toString() + "잔"
                soju = result.get("soju").toString() + "잔"
                coffee = result.get("coffee").toString() + "잔"
                nap_time = result.get("napTime").toString()
                sleep_time = result.get("startSleepTime").toString()
                wakeup_time = result.get("wakeUpTime").toString()
                gotobed_time = result.get("bedStartTime").toString()
                outtobed_time = result.get("bedEndTime").toString()
                emoji = result.get("emoji").toString()

                time1 = sleep_time.toString()
                time2 = gotobed_time.toString()
                calTimeToSleep(time2, time1)

                time3 = outtobed_time.toString()
                time4 = calTimeinBed(time2,time3).toString()
                time_in_bed = minToTime(time4)

                time5 = wakeup_time.toString()
                time6 = calTimeinBed(time1,time5).toString()
                sleeptime = minToTime(time6)

                sleepLevel = emoji.toString()

                // 사용자가 선택한 수면 이모지에 따라 다른 경로 설정
                if(sleepLevel.toInt()==1){
                    emojiPath = R.drawable.emoji_bad
                }
                else if(sleepLevel.toInt()==2){
                    emojiPath = R.drawable.emoji_normal
                }
                else if(sleepLevel.toInt()==3){
                    emojiPath = R.drawable.emoji_good
                }

                setCalendar()

            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }

        binding.calendarView.setOnDayClickListener(OnDayClickListener { eventDay ->
            //val clickedDayCalendar = eventDay.calendar

            binding.slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED

            // 캘린더 페이지에 넣어주기
            binding.tvWine.text = wine
            binding.tvBeer.text = beer
            binding.tvMakgurli.text = makguli
            binding.tvSoju.text = soju
            binding.tvCoffee.text = coffee
            binding.tvNapTime.text = nap_time
            binding.tvBedTime.text = sleep_time
            binding.tvWakeUpTime.text = wakeup_time
            binding.tvBI.text = time_to_sleep
            binding.tvTimeInBed.text = time_in_bed
            binding.tvSleepTime.text = sleeptime
            binding.tvScore.text = calScore().toString()
        })

        setChart()

        setBottomNavigation()
        setFabAdd()

    }


    // 잠들 때까지 걸린 시간 계산 메소드
    private fun calTimeToSleep(test: String, test2:String){
        var cal: Int = timeToMin(test2)-timeToMin(test)

        if(cal<0){
            cal = cal + 1440
        }

        time_to_sleep = cal.toString()
    }

    // 시간 -> 분
    private fun timeToMin(test: String) : Int {
        //var input = test
        var token = test.split(":")
        var h: Int = token[0].toInt()
        var m: Int = token[1].toInt()
        var result: Int = 60 * h + m

        return result
    }

    private fun isTrue(test: String) : Int{
        var token = test.split(":")
        var hour : Int = token[0].toInt()

        return hour
    }

    // 침대에서 머문 시간 계산 메소드
    private fun calTimeinBed(gotobedtime: String, outtobedtime: String): Int {
        var timeinbed = 0

        if (isTrue(outtobedtime) - isTrue(gotobedtime) < 0){
            timeinbed = 1440 - timeToMin(gotobedtime) + timeToMin(outtobedtime)
        }
        else {
            //timeinbed = 1440 - timeToMin(gotobedtime) + timeToMin(outtobedtime)
            timeinbed = timeToMin(outtobedtime) - timeToMin(gotobedtime)
        }

        if(timeinbed < 0){
            timeinbed = 1440 - (-1*timeinbed)
        }

        return timeinbed
    }

    private fun minToTime(test: String) : String {
        var input = test.toInt()
        var hour = input/60
        var min = input - hour * 60

        var result : String = hour.toString() + "시간" + min.toString() + "분"

        return result
    }

    private fun setChart(){
        chart = binding.sleepLineChart


        val values = ArrayList<Entry>()

        for(i in 1..100){
            var value : Float = Math.random().toFloat()
            values.add(Entry(i.toFloat(), value))
        }

        val set1 = LineDataSet(values, "Sleep Chart")

        val dataSets:ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)

        val data : LineData = LineData(dataSets)

        chart?.legend?.textColor = Color.parseColor("#FCA311")
        chart?.xAxis?.textColor = Color.parseColor("#E5E5E5")
        chart?.axisLeft?.textColor = Color.parseColor("#E5E5E5")
        chart?.axisRight?.textColor = Color.parseColor("#14213D")

        set1.setColor(Color.parseColor("#FCA311"))
        set1.setCircleColor(Color.parseColor("#FCA311"))
        set1.setDrawCircles(false)
        set1.setDrawValues(false)

        chart?.setData(data)
    }

    //수면효율 계산
    private fun calScore(
        //bedStartTime: String,
        //bedEndTime: String,
        //startSleepTime: String,
        //wakeUpTime: String
    ): Int {
        val score =
            (calTimeinBed(sleep_time.toString(), wakeup_time.toString()).toDouble() / calTimeinBed(
                gotobed_time.toString(), outtobed_time.toString()
            ).toDouble()) * 100.0
        return score.toInt()
    }

    private fun setFabAdd(){
        binding!!.fabAdd.setOnClickListener(){
            intent = Intent(this, WritingDiaryActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setBottomNavigation(){
        binding!!.bottomNavigation.setOnItemSelectedListener(){
            when(it.itemId){
                R.id.nav_report -> {
                    intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_alarm -> {
                    intent = Intent(this, RecommendTimeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_myPage -> {
                    intent = Intent(this, MypageActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }


    private fun setCalendar() {
        val events: MutableList<EventDay> = ArrayList()

        val calendar: Calendar = Calendar.getInstance()

        val calendarView: CalendarView = binding.calendarView as CalendarView
        events.add(EventDay(calendar, emojiPath))



        calendarView.setEvents(events)
    }

}