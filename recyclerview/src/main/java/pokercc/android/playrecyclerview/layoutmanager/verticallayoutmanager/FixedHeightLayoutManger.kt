package pokercc.android.playrecyclerview.layoutmanager.verticallayoutmanager

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.ui.recyclerview.BuildConfig

/**
 * 固定高度的垂直线性布局管理器
 * 参考文章 <a href="https://blog.csdn.net/user11223344abc/article/details/78080671"></a>
 *
 */
class FixedHeightLayoutManger : RecyclerView.LayoutManager() {
    companion object {
        private val DEBUG = BuildConfig.DEBUG
        private const val TAG = "FixedHeightLayoutManger"
        private fun String.log() {
            if (DEBUG) {
                Log.d(TAG, this)
            }
        }
    }

    class LayoutParams : RecyclerView.LayoutParams {
        constructor(width: Int, height: Int) : super(width, height)
        constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: RecyclerView.LayoutParams) : super(source)

    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return lp is LayoutParams
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return LayoutParams(
            lp
        )

    }

    override fun generateLayoutParams(c: Context, attrs: AttributeSet): RecyclerView.LayoutParams {
        return LayoutParams(
            c,
            attrs
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        """onLayoutChildren(
        |recycler:$recycler,
        |state:$state
        |)
        """.trimMargin().log()

        measureItemHeight(recycler)

        fill(0, recycler)

    }

    /**
     * 假设item都是一样高的
     */
    private var itemHeight = 0

    /**
     * 内容的高度
     */
    private val contentHeight
        get() = itemHeight * itemCount


    private val visibleHeight
        get() = height - paddingTop - paddingBottom

    override fun canScrollVertically() = true

    /**
     * 滑动距离
     */
    private var scrollDistance = 0

    private fun measureItemHeight(recycler: RecyclerView.Recycler) {

        detachAndScrapAttachedViews(recycler)
        if (itemCount > 0 && itemHeight == 0) {
            val child = recycler.getViewForPosition(0)
            addView(child)
            measureChildWithMargins(child, paddingStart + paddingEnd, paddingTop + paddingEnd)
            itemHeight = getDecoratedMeasuredHeight(child)
            removeAndRecycleView(child, recycler)
        }
    }

    private fun fill(dy: Int, recycler: RecyclerView.Recycler) {
        // scrollDistance是有范围的,大于0,小于最后一个item的bottom -visibleHeight
        // 填充 scrollDistance 到 scrollDistance+visibleHeight这段区域,不在这段区域的，都回收掉
        detachAndScrapAttachedViews(recycler)

        // 布局[scrollDistance,scrollDistance+visibleHeight]这一段区域
        // 滚动内容，如果内容在可视范围内，则显示，否则回收
        val windowTop = 0
        val windowBottom = visibleHeight
        for (i in 0 until itemCount) {
            val child = recycler.getViewForPosition(i)
            val childTop = -scrollDistance + itemHeight * i
            val childBottom = childTop + itemHeight
            if (windowTop in childTop..childBottom || childTop >= windowTop && childBottom <= windowBottom || windowBottom in childTop..childBottom) {
                addView(child)
                measureChildWithMargins(child, paddingStart + paddingEnd, paddingTop + paddingEnd)

                layoutDecoratedWithMargins(
                    child, paddingStart, childTop, height - paddingEnd, childBottom
                )
            } else {
                removeAndRecycleView(child, recycler)
            }


        }
        // 偷懒的回收方式
//        removeAndRecycleAllViews(recycler)


    }

    private val maxScroll
        get() = contentHeight - height - paddingTop - paddingBottom

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {

        val consume = when {
            //
            scrollDistance + dy > maxScroll -> maxScroll - scrollDistance
            scrollDistance + dy < 0 -> -scrollDistance
            else -> dy
        }
        scrollDistance += consume
        fill(0, recycler)

        return consume
    }
}