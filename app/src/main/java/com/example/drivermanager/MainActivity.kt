package com.example.drivermanager

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.tabs.TabLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    lateinit var btnLogout : Button
    lateinit var tabs : TabLayout
    lateinit var driverList : ListView

    lateinit var driverService: DriverServiceImpl
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    lateinit var dialogView: View
    lateinit var dialog: AlertDialog.Builder

    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "배달원 현황"

        btnLogout = findViewById(R.id.btnLogout)
        tabs = findViewById(R.id.tabs)
        driverList = findViewById(R.id.driverList)

        driverService = DriverServiceImpl(this)
        preferences = getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()


        if (!isLogon()) {
            // 로그인 화면 전환
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivityForResult(intent, 0)
        }

        // tab click
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                getDatas()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // DO NOTHING
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                getDatas()
            }
        })

        // logout click
        btnLogout.setOnClickListener {
            editor.remove(getString(R.string.login_token))
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            token = data?.getStringExtra(getString(R.string.login_token))
            // app에 토큰 저장
            editor.putString(getString(R.string.login_token), token)

            // 헤더 표시
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            findViewById<TextView>(R.id.txtDate).text = current.format(formatter)

            // 배달원 목록 표시
            getDatas()
        }
    }

    fun getDriver(did: String) {
        driverService.getDriver(token!!, did) {driver -> showDriver(driver) }
    }

    fun showDriver(driver: Driver?) {
        dialogView = View.inflate(this, R.layout.info_driver, null)
        dialogView.findViewById<EditText>(R.id.txtId).setText(driver!!.did)
        dialogView.findViewById<EditText>(R.id.txtName).setText(driver!!.name)
        dialogView.findViewById<EditText>(R.id.txtTel).setText(driver!!.mobile)
        val reason = dialogView.findViewById<EditText>(R.id.txtReason)

        dialog = AlertDialog.Builder(this)
        dialog.setTitle("배달원 등록 승인")
        dialog.setView(dialogView)
        dialog.setNegativeButton("승인") { _, _ ->
            driver.reason = reason.text.toString()
            driverService.approve(token!!, driver) {getDatas()}
        }
        dialog.setPositiveButton("거부") { _, _ ->
            driver.reason = reason.text.toString()
            driverService.reject(token!!, driver) {getDatas()}
        }

        dialog.show()
    }

    fun getDatas() {
        driverService.getDriverSummary(token!!) { datas -> showDatas(datas)}
    }

    fun showDatas(datas: ArrayList<DriverSummary>?) {
        print("DEBUG:showDatas: ")
        print(datas)
        println(tabs.selectedTabPosition)

        if (datas == null)
            return

        // 1. 데이터 필터링
        val newDatas = ArrayList<DriverSummary>()
        for (data in datas) {
            if (tabs.selectedTabPosition == 0) {
                newDatas.add(data)
            } else if (tabs.selectedTabPosition == 1) {
                if(data.status == 1) {
                    newDatas.add(data)
                }
            } else if (tabs.selectedTabPosition == 2) {
                if(data.status == 3) {
                    newDatas.add(data)
                }
            } else if (tabs.selectedTabPosition == 3) {
                if(data.status == 4) {
                    newDatas.add(data)
                }
            }
        }

        // 2. adapter
        val adapter = DriverAdapter(this, newDatas)
        driverList.adapter = adapter

        // 2. 숭인버튼
//        driverList.setOnItemClickListener { adapterView, view, i, l ->
//            // DriverAdapter안에 구현
//        }

    }

    private fun isLogon() : Boolean {
        preferences.getString(getString(R.string.login_token), token)
        print("DEBUG:isLogon: ")
        println(token)
        //token으로 로그인 체크
        return (token != null)
    }

    inner class DriverAdapter(var context : Context, var datas : ArrayList<DriverSummary>) : BaseAdapter() {

        override fun getCount(): Int {
            return datas.size
        }

        override fun getItem(p0: Int): Any {
            print("DEBUG:getItem: ")
            print(p0)
            return 0
        }

        override fun getItemId(p0: Int): Long {
            print("DEBUG:getItemId: ")
            print(p0)
            return 0
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
            var convertView = view

            if(convertView == null) {
                println("convertView is null")
                convertView = LayoutInflater.from(viewGroup?.context).inflate(R.layout.item_driver, viewGroup, false);
            }

            val data = datas[i]

            // data link
            convertView!!.findViewById<TextView>(R.id.driverName).text = datas[i].name
            convertView!!.findViewById<TextView>(R.id.driverDestination).text = datas[i].address
            var performance = datas[i].reserved.toString() + "/" +datas[i].completed.toString() + "/" +datas[i].total.toString()
            convertView!!.findViewById<TextView>(R.id.driverPerformance).text = performance

            val btnStatus = convertView!!.findViewById<Button>(R.id.driverStatus)
            btnStatus.text = datas[i].statusDisp
            if (btnStatus.text.toString() == "승인대기") {
                btnStatus.setBackgroundColor(Color.parseColor("#78c2ad"))
                btnStatus.setOnClickListener {
                    print("DEBUG:OnItemClick: ")
                    println(i)
                    val mainActivity = context as MainActivity
                    mainActivity.getDriver(datas[i].did)
                }
            } else {
                btnStatus.setBackgroundColor(Color.parseColor("#ffffff"))
                btnStatus.setOnClickListener {}
            }


            return convertView;
        }
    }
}