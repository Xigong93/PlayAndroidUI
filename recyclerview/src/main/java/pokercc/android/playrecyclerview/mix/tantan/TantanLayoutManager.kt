package pokercc.android.playrecyclerview.mix.tantan

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min
import kotlin.math.pow

class TantanLayoutManager : RecyclerView.LayoutManager() {
    /**
     * 显示的条目数量
     */
    private val showingItemCount = 3
    /**
     * 条目的缩放比例
     */
    private val itemScale = 0.9f

    private val itemTranslateY = 80

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    class LayoutParams : RecyclerView.LayoutParams {
        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.MarginLayoutParams?) : super(source)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
        constructor(source: RecyclerView.LayoutParams?) : super(source)
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        detachAndScrapAttachedViews(recycler)
        val showingItemCount = min(this.showingItemCount, itemCount)
        // 倒叙添加View
        for (i in showingItemCount - 1 downTo 0) {
            val child = recycler.getViewForPosition(i)
            addView(child)
            measureChildWithMargins(child, paddingStart + paddingEnd, paddingTop + paddingBottom)
            val itemHeight = getDecoratedMeasuredHeight(child)
            val itemWidth = getDecoratedMeasuredWidth(child)
            val left = (width - itemWidth) shr 1
            val top = (height - itemHeight) shr 1
            layoutDecoratedWithMargins(child, left, top, left + itemWidth, top + itemHeight)
            val scale = 1 - 0.1f * i
            child.scaleX = scale
            child.scaleY = scale
            child.translationY = i * child.height * 0.1f

        }

    }
}