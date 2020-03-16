
package com.xzy.xedittext.edittext.zhifubao

import android.content.Context

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.xzy.keyboard.KeyboardEditText
import com.xzy.keyboard.OnKeyboardKeyListener
import com.xzy.xedittext.R
import kotlinx.android.synthetic.main.edittext.view.*

/**
 * 自定义view 验证码 输入框.
 *
 * @author xzy
 */
@Suppress("unused,deprecated")
class WidgetEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var containerLinLayout: LinearLayout? = null
    var editText: KeyboardEditText? = null
        private set
    /**
     * 输入框数量
     */
    private var etNumber: Int = 0
    /**
     * 输入框获取焦点时背景
     */
    private var etBackgroundDrawableFocus: Drawable? = null
    /**
     * 输入框没有焦点时背景
     */
    private var etBackgroundDrawableNormal: Drawable? = null
    /**
     * 输入完成
     */
    private var etBackgroundDrawableComplete: Drawable? = null
    /**
     * 存储TextView的数据 数量由自定义控件的属性传入
     */
    private lateinit var textViews: Array<TextView?>
    private val myTextWatcher = MyTextWatcher()
    /**
     * 输入完成 和 删除成功 的监听
     */
    private var inputCompleteListener: InputCompleteListener? = null
    /**
     * 获取输入文本
     *
     * @return string
     */
    //        StringBuilder stringBuilder = new StringBuilder();
    //        for (TextView tv : mTextViews) {
    //            stringBuilder.append(tv.getText().toString().trim());
    //        }
    //        return stringBuilder.toString();
    val inputContent: String
        get() = sb.toString()
    private var sb = StringBuffer()

    init {
        init(context, attrs, defStyleAttr)
    }

    fun onPressed(keyCode: Int) {
        if (keyCode == Keyboard.KEYCODE_DELETE) {
            onKeyDelete()
        }
    }

    fun setInputCompleteListener(inputCompleteListener: InputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener
    }

    private fun dp2px(dpValue: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue, context.resources.displayMetrics
        )
    }

    private fun sp2px(spValue: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spValue, context.resources.displayMetrics
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 设置当 高为 warpContent 模式时的默认值 为 50dp
        var mHeightMeasureSpec = heightMeasureSpec

        val heightMode = MeasureSpec.getMode(mHeightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST) {
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                dp2px(50f, context).toInt(),
                MeasureSpec.EXACTLY
            )
        }

        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }

    /**
     * 初始化布局和属性
     *
     * @param context 上下文
     * @param attrs 属性
     * @param defStyleAttr defStyleAttr
     */
    @Suppress("deprecated")
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        LayoutInflater.from(context).inflate(R.layout.edittext, this)
        containerLinLayout = this.findViewById(R.id.container_ll)
        editText = this.findViewById(R.id.keyboard_et)
        // 处理删除事件
        keyboard_et.keyListener = object: OnKeyboardKeyListener {
            override fun onKey(primaryCode: Int) {
                onPressed(primaryCode)
            }
        }
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.WidgetEditText, defStyleAttr, 0)
        etNumber = typedArray.getInteger(R.styleable.WidgetEditText_icv_et_number, 1)
        // 输入框的宽度
        val mEtWidth = typedArray.getDimensionPixelSize(R.styleable.WidgetEditText_icv_et_width, 42)
        // 输入框分割线
        var mEtDividerDrawable =
            typedArray.getDrawable(R.styleable.WidgetEditText_icv_et_divider_drawable)
        // 输入框文字大小
        val mEtTextSize = typedArray.getDimensionPixelSize(
            R.styleable.WidgetEditText_icv_et_text_size,
            sp2px(16f, context).toInt()
        ).toFloat()
        // 输入框文字颜色
        val mEtTextColor =
            typedArray.getColor(R.styleable.WidgetEditText_icv_et_text_color, Color.BLACK)
        etBackgroundDrawableFocus =
            typedArray.getDrawable(R.styleable.WidgetEditText_icv_et_bg_focus)
        etBackgroundDrawableNormal =
            typedArray.getDrawable(R.styleable.WidgetEditText_icv_et_bg_normal)
        etBackgroundDrawableComplete =
            typedArray.getDrawable(R.styleable.WidgetEditText_icv_et_bg_complete)
        // 释放资源
        typedArray.recycle()

        // 当xml中未配置时 这里进行初始配置默认图片
        if (mEtDividerDrawable == null) {
            mEtDividerDrawable = context.resources.getDrawable(R.drawable.shape_icv_et_bg_press)
        }

        if (etBackgroundDrawableFocus == null) {
            etBackgroundDrawableFocus =
                context.resources.getDrawable(R.drawable.shape_icv_et_bg_press)
        }

        if (etBackgroundDrawableNormal == null) {
            etBackgroundDrawableNormal =
                context.resources.getDrawable(R.drawable.shape_icv_et_bg_normal)
        }
        if (etBackgroundDrawableComplete == null) {
            etBackgroundDrawableComplete =
                context.resources.getDrawable(R.drawable.shape_icv_et_bg_complete)
        }

        initTextViews(
            getContext(),
            etNumber,
            mEtWidth,
            mEtDividerDrawable,
            mEtTextSize,
            mEtTextColor
        )
        initEtContainer(textViews)

        editText!!.addTextChangedListener(myTextWatcher)
    }

    /**
     * 初始化TextView
     *
     * @param context 上下文
     * @param etNumber 显示的个数
     * @param etWidth 宽度
     * @param etDividerDrawable 分割线
     * @param etTextSize 文本大小
     * @param etTextColor 文本颜色
     */
    private fun initTextViews(
        context: Context,
        etNumber: Int,
        etWidth: Int,
        etDividerDrawable: Drawable?,
        etTextSize: Float,
        etTextColor: Int
    ) {
        // 设置分割线的宽度
        if (etDividerDrawable != null) {
            etDividerDrawable.setBounds(
                0,
                0,
                etDividerDrawable.minimumWidth,
                etDividerDrawable.minimumHeight
            )
            containerLinLayout!!.dividerDrawable = etDividerDrawable
        }

        textViews = arrayOfNulls(etNumber)
        for (i in textViews.indices) {
            val textView = TextView(context)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, etTextSize)
            textView.setTextColor(etTextColor)
            textView.width = etWidth
            textView.height = etWidth
            if (i == 0) {
                textView.background = etBackgroundDrawableFocus
            } else {
                textView.background = etBackgroundDrawableNormal
            }
            textView.gravity = Gravity.CENTER
            textView.isFocusable = true
            textViews[i] = textView
        }
    }

    /**
     * 初始化存储TextView 的容器
     *
     * @param _textViews textView合集
     */

    private fun initEtContainer(_textViews: Array<TextView?>) {
        for (i in _textViews.indices) {
            // 从第二个开始，添加marginLeft
            if (i != 0) {
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.leftMargin = resources.getDimension(R.dimen.input_code_margin).toInt()
                containerLinLayout!!.addView(_textViews[i], params)
            } else { // 第一个则不添加marginLeft
                containerLinLayout!!.addView(_textViews[i])
            }
        }
    }

    /**
     * 给TextView 设置文字
     *
     * @param inputContent 需要设置的文本
     */
    private fun setText(inputContent: String) {
        for (i in textViews.indices) {
            val tv = textViews[i]
            if ("" == tv?.text.toString().trim { it <= ' ' }) {
                tv?.text = inputContent
                sb.append(inputContent.trim { it <= ' ' })
                // 添加输入完成的监听
                if (inputCompleteListener != null) {
                    inputCompleteListener!!.inputComplete()
                }
                tv?.background = etBackgroundDrawableComplete
                if (i < etNumber - 1) {
                    textViews[i + 1]?.background = etBackgroundDrawableFocus
                }
                break
            }
        }
    }

    /**
     * 监听删除
     */
    private fun onKeyDelete() {
        for (i in textViews.indices.reversed()) {
            val tv = textViews[i]
            if ("" != tv?.text.toString().trim { it <= ' ' }) {
                tv?.text = ""
                sb.deleteCharAt(sb.length - 1)
                // 添加删除完成监听
                if (inputCompleteListener != null) {
                    inputCompleteListener!!.deleteContent()
                }
                tv?.background = etBackgroundDrawableFocus
                if (i < etNumber - 1) {
                    textViews[i + 1]?.background = etBackgroundDrawableNormal
                }
                break
            }
        }
    }

    interface InputCompleteListener {
        /**
         * 输入完成.
         */
        fun inputComplete()

        /**
         * 删除内容.
         */
        fun deleteContent()
    }

    /**
     * 文本监听.
     */
    private inner class MyTextWatcher : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            val inputStr = editable.toString()
            if ("" != inputStr) {
                setText(inputStr)
                editText!!.setText("")
            }
        }
    }
}
