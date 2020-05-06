package pokercc.android.playrecyclerview.layoutmanager.viewpagerlayoutmanager

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * ViewPager效果
 */
class ViewPagerLayoutManager : RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
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
     * item的宽度
     */
    private val itemWidth
        get() = visibleWidth

    /**
     * 内容的宽度
     */
    private val contentWidth
        get() = itemWidth * itemCount

    /**
     * 可见宽度
     */
    private val visibleWidth
        get() = width - paddingStart - paddingEnd

    /**
     * 最大的滑动距离
     */
    private val maxScrollDistance
        get() = contentWidth - visibleWidth

    /**
     * 当前的滑动距离
     */
    private var scrollDistance = 0

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        fill(recycler)

    }

    private fun fill(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)

        for (i in 0 until itemCount) {
            val childLeft = -scrollDistance + itemWidth * i
            val childRight = childLeft + itemWidth
            if (childLeft > paddingStart + visibleWidth || childRight < paddingStart) {
                // 说明View是不可见的
                continue
            }
            //布局View
            val child = recycler.getViewForPosition(i)
            addView(child)
            measureChildWithMargins(child, paddingStart + paddingEnd, paddingTop + paddingBottom)
            layoutDecoratedWithMargins(child, childLeft, paddingTop, childRight, height - paddingEnd)
        }
        // 回收
        for (viewHolder in recycler.scrapList) {
            recycler.recycleView(viewHolder.itemView)
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {

        // dx 向左为正，向右为负
        val consume = when {
            scrollDistance + dx < 0 -> scrollDistance
            scrollDistance + dx > maxScrollDistance -> maxScrollDistance - scrollDistance
            else -> dx
        }
        scrollDistance += consume
        fill(recycler)
        return consume
    }


    private lateinit var animator: ValueAnimator

    private fun cancelAnim() {
        if (this::animator.isInitialized) {
            this.animator.cancel()
        }
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: RecyclerView.Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        cancelAnim()
    }

    private fun scrollToPosition(position: Int, anim: Boolean) {
        val targetX = position * itemWidth
        if (anim) {
            cancelAnim()
            animator = ValueAnimator.ofInt(scrollDistance, targetX)
            animator.addUpdateListener {
                scrollDistance = it.animatedValue as Int
                requestLayout()
            }
            animator.duration = 200
            animator.start()
        } else {
            scrollDistance = targetX
            requestLayout()
        }

    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            val selectedPosition = (scrollDistance.toFloat() / itemWidth.toFloat()).roundToInt()
            scrollToPosition(selectedPosition, true)

        }
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
        scrollToPosition(position, false)
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        super.smoothScrollToPosition(recyclerView, state, position)
        scrollToPosition(position, true)
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

}