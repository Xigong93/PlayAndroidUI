package pokercc.android.playrecyclerview.layoutmanager.stacklayoutmanager

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StackLayoutManager : RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    class LayoutParams : RecyclerView.LayoutParams {
        constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.MarginLayoutParams) : super(source)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
        constructor(source: RecyclerView.LayoutParams?) : super(source)
    }

    /**
     * 条目的宽度
     */
    private var itemWidth = 0

    /**
     * 第一个item左边的间距
     */
    private val firstItemMarginLeft
        get() = (visibleWidth - itemWidth) shr 1
    /**
     * item的间距
     */
    private var itemMargin = 100
    /**
     * 内容的宽度
     */
    private val contentWidth: Int
        get() = firstItemMarginLeft * 2 + itemWidth * itemCount + itemMargin * (itemCount - 1)

    /**
     * 可见的宽度
     */
    private val visibleWidth
        get() = width - paddingStart - paddingEnd

    /**
     * 最大的滑动距离
     */
    private val maxScroll: Int
        get() = contentWidth - visibleWidth


    /**
     * 滑动距离
     */
    private var scrollDistance = 0


    /**
     * 第一个可见的条目
     */
    private var firstVisibleItem = 0

    /**
     * 最后一个可见的条目
     */
    private var lastVisibleItem = 0

    /**
     * 选中的item
     */
    private val selectedItem: Int
        get() = Math.round(scrollDistance.toFloat() / (itemWidth + itemMargin))
    /**
     * 最大的缩放比例
     */
    private var maxScale = 1.25f

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        fill(recycler)

    }

    private fun fill(recycler: RecyclerView.Recycler) {
        // 确定itemWidth
        if (itemWidth == 0) {
            detachAndScrapAttachedViews(recycler)
            val child = recycler.getViewForPosition(0)
            addView(child)
            measureChildWithMargins(child, paddingStart + paddingEnd, paddingTop + paddingBottom)
            itemWidth = getDecoratedMeasuredWidth(child)
            detachAndScrapView(child, recycler)
        }
        detachAndScrapAttachedViews(recycler)

        firstVisibleItem = 0
        lastVisibleItem = 0
        // 开始进行布局

        // 定位点
        val anchorX = (visibleWidth - itemWidth) shr 1
        for (i in 0 until itemCount) {
            val childLeft = -scrollDistance + firstItemMarginLeft + (itemWidth + itemMargin) * i
            if (childLeft + itemWidth > 0 && childLeft < visibleWidth) {
                if (firstVisibleItem == 0) {
                    firstVisibleItem = i
                }
                lastVisibleItem = i

                val child = recycler.getViewForPosition(i)

                // z轴顺序,setZ()api 有版本限制
                if (childLeft < anchorX) {
                    addView(child)
                } else {
                    addView(child, 0)
                }
                if (i == selectedItem) {
                    child.bringToFront()
                }


                measureChildWithMargins(child, paddingStart + paddingEnd, paddingTop + paddingBottom)
                val itemHeight = getDecoratedMeasuredHeight(child)
                val top = (height - paddingTop - paddingBottom - itemHeight) shr 1
                layoutDecoratedWithMargins(child, childLeft, top, childLeft + itemWidth, top + itemHeight)


//                 执行动画
                var percent: Float = 1 - Math.abs(childLeft - anchorX).toFloat() / (itemWidth + itemMargin)
                percent = 1 + (percent) * (maxScale - 1)
                child.scaleX = percent
                child.scaleY = percent
            }

        }

        // 回收
        recycler.scrapList.forEach {
            removeAndRecycleView(it.itemView, recycler)
        }

    }

    override fun canScrollHorizontally() = true
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        // 向右滑动了，dx 是负值,向左是正值
        val consume = when {
            scrollDistance + dx < 0 -> scrollDistance
            scrollDistance + dx > maxScroll -> maxScroll - scrollDistance
            else -> dx
        }
        scrollDistance += consume

        fill(recycler)
        return consume
    }


    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                // 滚动到固定位置
                val position = scrollDistance.toFloat() / (itemWidth + itemMargin)
                if (position / 1 != 0.0f) {
                    scrollToPosition(Math.round(position), true)
                }
            }
        }

    }

    private var animator: ValueAnimator? = null
    private fun cancelAnim() {

    }

    override fun scrollToPosition(position: Int) {
        scrollToPosition(position, false)

    }

    private fun scrollToPosition(position: Int, anim: Boolean) {
        val offset = position * (itemWidth + itemMargin)
        animator = ValueAnimator.ofInt(scrollDistance, offset)
            .apply {
                addUpdateListener {
                    scrollDistance = it.animatedValue as Int
                    requestLayout()
                }
                duration = 300
                start()
            }

    }


    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        super.smoothScrollToPosition(recyclerView, state, position)
        scrollToPosition(position, true)
    }


}