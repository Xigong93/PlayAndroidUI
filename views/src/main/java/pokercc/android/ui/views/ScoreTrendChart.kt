package pokercc.android.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.TypeVariable


private const val DEBUG = true
internal val STAGES = arrayOf(
    "5.0-",
    "5.0",
    "5.5",
    "6.0",
    "6.5",
    "7.0",
    "7.5"
)

private const val COLOR_PRIMARY = 0xffffcf64.toInt()


/**
 * 分数趋势图
 * Created by pokercc on 19-9-25.
 */
class ScoreTrendChart
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    private data class TestPoint(
        /**
         * 当前的阶段
         */
        val stage: Int,
        val x: Float,
        val y: Float,
        var previous: TestPoint? = null,
        var next: TestPoint? = null
    ) {
        /**
         * 在点上面显示标签
         */
        val showLabelUp: Boolean
            get() = previous == null || next == null || (previous != null && next != null && previous!!.stage < stage && next!!.stage < stage)
    }


    private val stages = STAGES
    /**
     * 点到label的距离
     */
    private var labelMarginPoint: Int = 0

    /**
     * 标签的尺寸
     */
    private var labelWidth: Int = 0
    private var labelHeight: Int = 0
    /**
     * 条目的间距
     */
    private var itemHorizontalDistance = 0
    private var itemVerticalDistance = 0

    /**
     * 滑动的范围
     */
    private var scrollRange: Int = 0
    /**
     * 当前滑动的距离
     */
    private var scrollOffset: Int = 0


    /**
     * 设置测试记录
     */
    fun setTestRecorders(
        recorders: List<TestRecorder>,
        onLabelClickListener: OnLabelClickListener? = null
    ) = post {
        if (!recorders.isNullOrEmpty()) {


            // 计算布局参数

            labelMarginPoint = 15.dp2Px(context).toInt()
            labelWidth = 60.dp2Px(context).toInt()
            labelHeight = 38.dp2Px(context).toInt()

            itemHorizontalDistance = 100.dp2Px(context).toInt()

            itemVerticalDistance =
                (height - 2 * (labelHeight + labelMarginPoint) - paddingTop - paddingBottom) / (stages.size - 1)
            // 内容的宽度=条目数量的间距+一个label的宽度
            val contentWidth = (recorders.size - 1) * itemHorizontalDistance + labelWidth
            scrollRange = (contentWidth - width).coerceAtLeast(0)
            // 初始化，直接滑动到最右边
            scrollOffset = scrollRange

            // 参数映射
            val points = recorders
                .mapIndexed { index, testRecorder ->
                    val stage = stages.indexOf(testRecorder.stageName)
                    TestPoint(
                        stage = stage,
                        x = (paddingLeft + labelWidth / 2 + index * itemHorizontalDistance).toFloat(),
                        y = (height - (labelHeight + labelMarginPoint) - stage * itemVerticalDistance).toFloat()
                    )
                }
                .toList()
                .also {
                    it.forEachIndexed { index, _ ->
                        it[index].apply {
                            previous = it.getOrNull(index - 1)
                            next = it.getOrNull(index + 1)
                        }
                    }
                }

            // 刷新view
            val chartLayoutManager = ChartLayoutManager(points)
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            addItemDecoration(DrawChartItemDecoration(points))
            layoutManager = chartLayoutManager
            adapter = ChartAdapter(ArrayList(recorders), onLabelClickListener)

        }
    }


    /**
     * 布局label
     */
    private inner class ChartLayoutManager(private val points: List<TestPoint>) :
        RecyclerView.LayoutManager() {


        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }


        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
            relayout(recycler)
        }

        private fun relayout(recycler: RecyclerView.Recycler) {
            //1. 离屏缓存
            detachAndScrapAttachedViews(recycler)

            for (i in 0 until itemCount) {
                val p = points[i]
                // 计算坐标
                val itemLeft = (p.x - labelWidth / 2 + -scrollOffset).toInt()
                val itemTop = (if (p.showLabelUp) {
                    p.y - labelMarginPoint - labelHeight
                } else {
                    p.y + labelMarginPoint
                }).toInt()
                val itemRight = itemLeft + labelWidth
                val itemBottom = itemTop + labelHeight

                if (itemRight < 0 || itemLeft > width) {
                    continue
                }
                val view = recycler.getViewForPosition(i)
                // 2.添加View
                addView(view)
                // 3.测量
                measureChildWithMargins(view, 0, 0)
                log("measureChildWithMargins: index=$i view:${view}")
                // 4.布局
                layoutDecoratedWithMargins(view, itemLeft, itemTop, itemRight, itemBottom)
                log("layoutDecoratedWithMargins: index=$i view:${view}")
            }
        }


        override fun scrollHorizontallyBy(
            dx: Int,
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State?
        ): Int {

            // dx 大于0 ，是往左边，小于0，是往右边
            log("scrollHorizontallyBy:dx=$dx ")
            val s = when {
                dx + scrollOffset < 0 -> -scrollOffset
                dx + scrollOffset > scrollRange -> scrollRange - scrollOffset
                else -> dx
            }
            log("scrollHorizontallyBy: dx=$dx s=$s scrollOffset=$scrollOffset scrollRange=$scrollRange childrenCount=$childCount")

            scrollOffset += s
            if (s != 0) {
                relayout(recycler)
            }
            return s
        }

        override fun canScrollHorizontally(): Boolean = true


    }

    /**
     * 绘制曲线，点和阴影
     */
    private inner class DrawChartItemDecoration(
        private val points: List<TestPoint>
    ) :
        RecyclerView.ItemDecoration() {
        private val innerPointPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).also {
            it.color = COLOR_PRIMARY
        }

        private val outerPointPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).also {
            it.color = Color.WHITE
        }
        private val linePath = Path()
        private val backgroundPath = Path()
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).also {
            it.color = COLOR_PRIMARY
            it.strokeWidth = 6.0f
            it.style = Paint.Style.STROKE
            it.shader = LinearGradient(
                0f,
                0f,
                300f,
                0f,
                COLOR_PRIMARY,
                0xFFFF7D39.toInt(),
                Shader.TileMode.MIRROR
            )

        }

        private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).also {
            it.color = 0x30ffcf64
            it.style = Paint.Style.FILL
            it.shader = LinearGradient(
                0f,
                0f,
                0f,
                height.toFloat(),
                COLOR_PRIMARY,
                Color.TRANSPARENT,
                Shader.TileMode.MIRROR
            )
        }
        val fl = 0.4f
        val fl1 = 0.6f

        init {
            if (points.isNotEmpty()) {
                linePath.also {
                    it.reset()
                    it.moveTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val x = points[i].x
                        val y = points[i].y
                        val x2 = points[i + 1].x
                        val y2 = points[i + 1].y
                        it.cubicTo(
                            x + itemHorizontalDistance * fl,
                            y,
                            x + itemHorizontalDistance * fl1,
                            y2,
                            x2,
                            y2
                        )

                    }
                }
                // 背景
                backgroundPath.also {
                    it.reset()
                    it.set(linePath)
                    it.lineTo(
                        points.last().x,
                        height.toFloat()
                    )
                    it.lineTo(points.first().x, height.toFloat())
                    it.close()

                }
            }
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val save = c.save()
            c.translate(-scrollOffset.toFloat(), 0f)
            if (points.isNotEmpty()) {

                // 绘制阴影
                c.drawPath(backgroundPath, shadowPaint)
                // 绘制线
                c.drawPath(linePath, linePaint)
                // 绘制点
                points.forEach {
                    c.drawCircle(it.x, it.y, 14.0f, outerPointPaint)
                    c.drawCircle(it.x, it.y, 10.0f, innerPointPaint)
                }
            }
            c.restoreToCount(save)
        }
    }


}

interface OnLabelClickListener {
    fun onLabelClick(view: View, testRecorder: TestRecorder, position: Int)
}


/**
 * 评测记录
 */
data class TestRecorder(
    val id: String,
    /**
     * 评测的阶段名
     */
    val stageName: String,
    /**
     * 评测的词汇量
     */
    val wordCount: String,
    /**
     * 评测日期
     */
    val date: String
)


@SuppressLint("AppCompatCustomView")
private class WordCountView(context: Context?) : TextView(context) {
    init {

        val shapeDrawable = ShapeDrawable(
            RoundRectShape(
                floatArrayOf(12f, 12f, 12f, 12f, 12f, 12f, 12f, 12f),
                null,
                null
            )
        )
        shapeDrawable.paint.color = COLOR_PRIMARY
        setBackgroundDrawable(shapeDrawable)
    }
}

class Container(context: Context?) : LinearLayout(context) {
    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        alpha = if (pressed) .5f else 1f
    }
}

private class ChartViewHolder(
    itemView: View,
    val wordCount: TextView = (itemView as ViewGroup)[0] as TextView,
    val date: TextView = (itemView as ViewGroup)[1] as TextView
) : RecyclerView.ViewHolder(itemView)

private class ChartAdapter(
    private val recorders: List<TestRecorder>,
    private val onLabelClickListener: OnLabelClickListener?
) :
    RecyclerView.Adapter<ChartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        return ChartViewHolder(Container(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            addView(WordCountView(context).also {
                it.gravity = Gravity.CENTER
                it.setTextColor(0xff2a2a2a.toInt())
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f)
                it.layoutParams = ViewGroup.LayoutParams(0, 0).also { p ->
                    p.width = 56f.dp2Px(context).toInt()
                    p.height = 21f.dp2Px(context).toInt()
                }
            })
            addView(TextView(context).also {
                it.setTextColor(Color.WHITE)
                it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f)
                it.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })
        })
    }

    override fun getItemCount(): Int = recorders.size

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        recorders[position].let {
            holder.wordCount.text = it.wordCount
            holder.date.text = it.date
            holder.itemView.setOnClickListener { view ->
                onLabelClickListener?.onLabelClick(view, it, position)
            }
        }


    }

}

private fun Number.dp2Px(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    )
}

private fun log(message: String) {
    @Suppress("ConstantConditionIf")
    if (DEBUG) {
        Log.d("ScoreTrendChart", message)
    }
}
