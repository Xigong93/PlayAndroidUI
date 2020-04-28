package pokercc.android.ui.basic

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible

/**
 * 页面标题栏
 * 特性:
 *  - 只能添加一个子View,自动右对齐
 *  - 标题自动裁剪，并最少为100dp
 *  - 返回按钮可以替换
 * @author pokercc
 * @date 2020-4-13 11:22:10
 */
class TitleBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    @Suppress("MemberVisibilityCanBePrivate")
    val backButton = ImageView(context).apply {
        setImageResource(R.drawable.design_back_button)
        scaleType = ImageView.ScaleType.CENTER
        layoutParams = LayoutParams(55.0f.dp2Px().toInt(), LayoutParams.MATCH_PARENT)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val titleTextView = TextView(context).apply {
        gravity = Gravity.CENTER
        setSingleLine(true)
        maxLines = 1
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        ellipsize = TextUtils.TruncateAt.END
        setTextColor(0xFF333333.toInt())
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17f)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val bottomLine = View(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1.dp2Px().toInt())
    }


    @Suppress("MemberVisibilityCanBePrivate")
    val otherChild: View?
        get() = if (childCount == 3) null else getChildAt(3)

    /** 页面标题 */
    @Suppress("MemberVisibilityCanBePrivate")
    var title: String? = null
        set(value) {
            field = value
            titleTextView.text = title
        }


    /** 返回键的图标 */
    @Suppress("MemberVisibilityCanBePrivate")
    var backButtonDrawable: Drawable? = null
        set(value) {
            field = value
            backButton.setImageDrawable(value)
        }

    /** 是否显示返回键 */
    @Suppress("MemberVisibilityCanBePrivate")
    var backButtonVisible: Boolean = true
        set(value) {
            field = value
            backButton.isVisible = value
        }

    /** 下划线颜色 */
    @ColorInt
    @Suppress("MemberVisibilityCanBePrivate")
    var bottomLineColor: Int = 0xFFF3F3F3.toInt()
        set(value) {
            field = value
            bottomLine.setBackgroundColor(value)
        }

    /** 是否显示下划线 */
    @Suppress("MemberVisibilityCanBePrivate")
    var bottomLineVisible: Boolean = true
        set(value) {
            field = value
            bottomLine.isVisible = value
        }

    init {
        addView(backButton)
        addView(titleTextView)
        addView(bottomLine)
        // Load attributes
        context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0).apply {
            title = getString(R.styleable.TitleBar_title)
            backButtonDrawable = getDrawable(R.styleable.TitleBar_backButtonDrawable)
                ?: resources.getDrawable(R.drawable.design_back_button)
            backButtonVisible = getBoolean(R.styleable.TitleBar_backButtonVisible, true)
            bottomLineColor = getColor(R.styleable.TitleBar_bottomLineColor, bottomLineColor)
            bottomLineVisible = getBoolean(R.styleable.TitleBar_bottomLineVisible, true)
        }.recycle()

        // 设置默认背景色
        if (background == null) {
            background = ColorDrawable(Color.WHITE)
        }
        // 设置默认返回键
        setOnBackPressed {
            context.asActivity()?.onBackPressed()
        }
    }

    override fun onSetAlpha(alpha: Int): Boolean {
        backButton.isEnabled = alpha != 0
        return super.onSetAlpha(alpha)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return MarginLayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )

    }


    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }


    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is MarginLayoutParams && super.checkLayoutParams(p)
    }

    override fun requestLayout() {
        super.requestLayout()
        require(childCount < 5) { "child count must less than 5, means can only add one child.(只能有一个子View)" }
    }

    override fun onMeasure(widthMeasureSpec: Int, _heightMeasureSpec: Int) {
        val heightSpec = if (MeasureSpec.getMode(_heightMeasureSpec) in
            setOf(MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED)
        ) {
            MeasureSpec.makeMeasureSpec(55.dp2Px().toInt(), MeasureSpec.EXACTLY)
        } else {
            _heightMeasureSpec
        }
        super.onMeasure(
            widthMeasureSpec,
            heightSpec
        )

        measureChild(backButton, widthMeasureSpec, heightSpec)
        otherChild?.let {
            measureChild(it, widthMeasureSpec, heightSpec)
        }
        measureChild(bottomLine, widthMeasureSpec, heightSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val otherChildWidth = (otherChild?.measuredWidth) ?: 0
        val unConsumeWidth =
            width - paddingStart - paddingEnd - otherChildWidth.coerceAtLeast(backButton.measuredWidth) * 2
        val margin = 20.dp2Px().toInt()
        val titleWidth = (unConsumeWidth - margin * 2).coerceAtLeast(100.dp2Px().toInt())

        val otherChildMaxWidth = (width - titleWidth - paddingStart - paddingEnd - margin) / 2

        otherChild?.let {
            measureChild(
                it,
                MeasureSpec.makeMeasureSpec(otherChildMaxWidth, MeasureSpec.AT_MOST),
                heightSpec
            )
        }
        measureChild(
            titleTextView,
            MeasureSpec.makeMeasureSpec(titleWidth, MeasureSpec.EXACTLY),
            heightSpec
        )

    }

    private fun Number.dp2Px() =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        )

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val contentStart = paddingStart
        val contentTop = paddingTop
        val contentEnd = r - l - paddingEnd
        val contentBottom = b - t - paddingBottom
        backButton.layout(
            contentStart,
            contentTop,
            contentStart + backButton.measuredWidth,
            contentBottom
        )
        val titleStart =
            (measuredWidth - paddingStart - paddingEnd - titleTextView.measuredWidth) / 2
        titleTextView.layout(
            titleStart,
            contentTop,
            titleStart + titleTextView.measuredWidth,
            contentBottom
        )

        otherChild?.let {
            val start =
                measuredWidth - paddingStart - paddingEnd - it.measuredWidth - (it.layoutParams as MarginLayoutParams).marginEnd
            val top = (measuredHeight - paddingTop - paddingBottom - it.measuredHeight) / 2
            it.layout(
                start,
                top,
                start + it.measuredWidth,
                top + it.measuredHeight
            )
        }


        bottomLine.layout(
            contentStart,
            contentBottom - bottomLine.measuredHeight,
            contentEnd,
            contentBottom
        )
    }

    fun setOnBackPressed(listener: ((View) -> Unit)) {
        backButton.setOnClickListener(listener)
    }


}