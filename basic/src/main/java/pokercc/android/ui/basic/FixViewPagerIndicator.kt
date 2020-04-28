package pokercc.android.ui.basic

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import com.google.android.material.animation.ArgbEvaluatorCompat


/**
 * 自定义的ViewPagerIndicator,固定宽带，均分的不能滑动
 * 功能如下:
 * - 支持自定义下划线的颜色
 * - 显示小红点
 * Created by pokercc on 19-10-26.
 */
open class FixViewPagerIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        internal const val ANIM_DURATION = 200L
        private const val ACTION_CLICK = 11
        private const val ACTION_SLIDE = 12
    }

    private val tabs = arrayListOf<TabTitle>()
    private val linearLayout = LinearLayout(context)
    private var indicator = TabIndicator(context, tabs)
    private var redDotSet = HashSet<Int>()
    private var selectedTextBold = true
    private var normalTextSize: Float = 0f
    private var selectedTextSize: Float = 0f
    private var normalTextColor: Int = 0
    private var selectedTextColor: Int = 0

    init {
        addView(linearLayout)
        addView(
            indicator,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            ).apply {
                bottomMargin = 2.dp2Px().toInt()
            }
        )
        context.obtainStyledAttributes(attrs, R.styleable.FixViewPagerIndicator, defStyleAttr, 0)
            .apply {
                indicator.paint.color =
                    getColor(R.styleable.FixViewPagerIndicator_indicatorColor, 0xFFFBDE00.toInt())
                normalTextSize =
                    getDimension(R.styleable.FixViewPagerIndicator_normalTextSize, 14.dp2Px())
                normalTextColor =
                    getColor(R.styleable.FixViewPagerIndicator_normalTextColor, 0xff999999.toInt())
                selectedTextBold =
                    getBoolean(R.styleable.FixViewPagerIndicator_selectedTextBold, true)
                selectedTextSize =
                    getDimension(R.styleable.FixViewPagerIndicator_selectedTextSize, 17.dp2Px())
                selectedTextColor =
                    getColor(R.styleable.FixViewPagerIndicator_selectedTextColor, 0xff333333.toInt())

            }.recycle()
    }

    private fun Number.dp2Px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
    )

    final override fun addView(child: View?) {
        super.addView(child)
    }

    final override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
    }


    private var animator: ValueAnimator? = null

    private var action = 0

    private val onViewPagerChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            log { "onPageScrollStateChanged($state)" }
            when (state) {
                ViewPager.SCROLL_STATE_SETTLING -> {

                }
                ViewPager.SCROLL_STATE_IDLE -> {
                    action = 0
                }
                ViewPager.SCROLL_STATE_DRAGGING -> {
                    action = ACTION_SLIDE
                }
            }

        }


        override fun onPageScrolled(
            position: Int,
            percent: Float,
            positionOffsetPixels: Int
        ) {
            log { "onPageScrolled($position,$percent,$positionOffsetPixels)" }
            if (action != ACTION_CLICK) {
                cancelAnim()
                tabs.getOrNull(position)?.percent = 1 - percent
                tabs.getOrNull(position + 1)?.percent = percent
                indicator.move(position, percent)
                lastSelected = position
            }
        }

        override fun onPageSelected(position: Int) {
            log { "onPageSelected($position)，action = $action" }
        }


    }
    private var lastSelected = -1

    private fun performSelected(position: Int) {
        if (lastSelected == position) return
        cancelAnim()
        indicator.animToTarget(position)
        tabs.getOrNull(position)?.animTo(1f)
        tabs.getOrNull(lastSelected)?.animTo(0f)
        lastSelected = position
    }

    private fun cancelAnim() {
        animator?.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelAnim()
    }

    private val onAdapterChangeListener = ViewPager.OnAdapterChangeListener { viewPager, _, _ ->
        setup(viewPager)
    }

    private fun createTab(): TabTitle = TabTitle(
        context,
        TabTitle.TitleStyle(normalTextColor, false, normalTextSize),
        TabTitle.TitleStyle(selectedTextColor, selectedTextBold, selectedTextSize)
    )


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        val newHeightMeaSpec =
            if (heightMeasureMode == MeasureSpec.AT_MOST || heightMeasureMode == MeasureSpec.UNSPECIFIED) {
                MeasureSpec.makeMeasureSpec(
                    resources.getDimensionPixelSize(R.dimen.design_viewpager_indicator_height),
                    MeasureSpec.EXACTLY
                )
            } else {
                heightMeasureSpec
            }
        super.onMeasure(widthMeasureSpec, newHeightMeaSpec)
    }

    /**
     * 关联ViewPager
     */
    fun setupWithViewPager(viewPager: ViewPager) {
        viewPager.removeOnAdapterChangeListener(onAdapterChangeListener)
        viewPager.addOnAdapterChangeListener(onAdapterChangeListener)
        setup(viewPager)

    }

    private fun setup(viewPager: ViewPager) {
        val pagerAdapter = viewPager.adapter ?: return
        viewPager.removeOnPageChangeListener(onViewPagerChangeListener)
        viewPager.addOnPageChangeListener(onViewPagerChangeListener)
        val tags = (0 until pagerAdapter.count).map {
            pagerAdapter.getPageTitle(it)?.toString() ?: ""
        }
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.removeAllViews()
        tabs.clear()
        linearLayout.weightSum = tags.size.toFloat()
        tags.forEachIndexed { index, s ->
            val tab = createTab()
            tab.setOnClickListener {
                action = ACTION_CLICK
                viewPager.currentItem = index
                performSelected(index)

            }
            tabs.add(tab)
            tab.text = s
            tab.position = index
            linearLayout.addView(
                tab,
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            )
        }
//        post {
//            performSelected(0, false)
//        }

    }

    /**
     * 设置红点是否可见
     */
    public fun setRedDotVisible(position: Int, visible: Boolean) {
        if (visible) {
            redDotSet.add(position)
        } else {
            redDotSet.remove(position)
        }
        (linearLayout[position] as TabTitle).redDotVisible = visible
    }


}

@SuppressLint("ViewConstructor")
open class TabIndicator(context: Context, private val tabs: List<TabTitle>) :
    View(context) {
    private val location = RectF()
    private val indicatorWidth =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23f, resources.displayMetrics)
    private val indicatorHeight =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)
    val paint = Paint().apply {
        color = 0xFF7B87FF.toInt()
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        val rx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
        canvas.drawRoundRect(location, rx, rx, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize(0, widthMeasureSpec),
            MeasureSpec.makeMeasureSpec(indicatorHeight.toInt(), MeasureSpec.EXACTLY)
        )
    }

    private fun TabTitle.getIndicatorX(): Float {
        return x + width / 2 - indicatorWidth / 2
    }

    fun animToTarget(position: Int) {
        if (tabs.isEmpty()) return
        val previousX: Float = location.left
        val nextX = tabs.getOrNull(position)?.getIndicatorX() ?: 0f
        animate()
            .alpha(1f)
            .setDuration(FixViewPagerIndicator.ANIM_DURATION)
            .setUpdateListener {
                val distance = nextX - previousX
                val l = previousX + distance * it.animatedFraction
                location.apply {
                    left = l
                    right = l + indicatorWidth
                    top = 0f
                    bottom = height.toFloat()
                }
                invalidate()
            }
            .start()


    }

    fun move(position: Int, percent: Float) {
        if (tabs.isEmpty()) return
        val previousX: Float = tabs.getOrNull(position)?.getIndicatorX() ?: 0f
        val nextX = tabs.getOrNull(position + 1)?.getIndicatorX() ?: 0f
        val distance = nextX - previousX
        val l = previousX + distance * percent
        location.apply {
            left = l
            right = l + indicatorWidth
            top = 0f
            bottom = height.toFloat()
        }
        invalidate()
    }


}

private inline fun log(message: () -> String) {
    Log.d("FitViewPagerIndicator", message())
}


@SuppressLint("AppCompatCustomView", "ViewConstructor")
open class TabTitle(
    context: Context,
    private val normalStyle: TitleStyle,
    private val selectedStyle: TitleStyle
) : TextView(context) {
    init {
//        setSingleLine(true)// 用这个啥都不会绘制，我也不知道为啥
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    var position: Int = -1
    var redDotVisible: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF6141.toInt()
        style = Paint.Style.FILL_AND_STROKE
    }

    init {
        gravity = Gravity.CENTER
    }

    data class TitleStyle(
        @ColorInt
        val color: Int,
        val bold: Boolean,
        @Dimension(unit = Dimension.PX)
        val textSize: Float
    )

    private val textBound = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val textSize = textSize
        val bold = paint.isFakeBoldText
        // 按照选中的测量,这个尺寸比较大
        setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedStyle.textSize)
        setBold(selectedStyle.bold)
        text.let {
            paint.getTextBounds(text.toString(), 0, text.length, textBound)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 还原设置
        paint.textSize = textSize
        setBold(bold)
    }

    private fun setBold(bold: Boolean) {
        if (paint.isFakeBoldText == bold) return
        paint.isFakeBoldText = bold
        paint.textSkewX = 0f
        paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)
        // 绘制红点
        if (redDotVisible) {
            val cx = width / 2 + textBound.width() / 2 + 5.dp2Px()
            val cy = (height / 2 - textBound.height() / 2).toFloat()
            canvas.drawCircle(cx, cy, 3.dp2Px(), dotPaint)
        }

    }

    private fun Number.dp2Px() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
    )

    private var animator: ValueAnimator? = null
    fun animTo(targetPercent: Float) {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(percent, targetPercent).apply {
            duration = FixViewPagerIndicator.ANIM_DURATION
            addUpdateListener {
                percent = it.animatedValue as Float
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    /**
     * 设置进度
     */
    var percent: Float = 0f
        set(value) {
            field = value
            setTextColor(
                ArgbEvaluatorCompat.getInstance()
                    .evaluate(percent, normalStyle.color, selectedStyle.color)
            )
            paint.textSize =
                normalStyle.textSize + percent * (selectedStyle.textSize - normalStyle.textSize)
            if (selectedStyle.bold) {
                setBold(percent > 0.5)
            }
            invalidate()
        }


}


