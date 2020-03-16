package com.xzy.xedittext

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xzy.keyboard.collapseOnBlankArea
import com.xzy.xedittext.edittext.delete.MainActivity1
import com.xzy.xedittext.edittext.zhifubao.WidgetEditText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        collapseOnBlankArea()
        et_input.setInputCompleteListener(object : WidgetEditText.InputCompleteListener {
            override fun inputComplete() {
                val inputContent = et_input.inputContent
            }

            override fun deleteContent() {
                val inputContent = et_input.inputContent
            }
        })

        btn_click.setOnClickListener {
            val intent = Intent(this, MainActivity1::class.java)
            startActivity(intent)
        }
    }
}
