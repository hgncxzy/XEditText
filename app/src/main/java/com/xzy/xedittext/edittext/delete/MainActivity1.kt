package com.xzy.xedittext.edittext.delete

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.xzy.keyboard.collapseOnBlankArea
import com.xzy.xedittext.R
import kotlinx.android.synthetic.main.activity_main1.*

class MainActivity1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        collapseOnBlankArea()
       // et_input1.requestFocus()
        et_input1.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    iv_delete.visibility = View.VISIBLE
                } else {
                    iv_delete.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        iv_delete.setOnClickListener {
            et_input1.setText("")
            iv_delete.visibility = View.GONE
        }
    }
}
