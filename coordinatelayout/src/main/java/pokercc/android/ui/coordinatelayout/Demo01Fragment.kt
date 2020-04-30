package pokercc.android.ui.coordinatelayout

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.math.MathUtils
import androidx.core.view.*
import androidx.fragment.app.Fragment
import pokercc.android.ui.coordinatelayout.databinding.CooDemo01FragmentBinding

/**
 * 滑动小的，带动大的
 * 1. 小的不能滑动之后大的滑动
 * 2. 大的不能滑动之后，小的滑动
 * 借鉴自:https://www.jianshu.com/p/e333f11f29aa
 * @author pokercc
 * @date 2019-11-30 16:17:20
 */


class Demo01Fragment : Fragment() {

    private lateinit var demo01Binding: CooDemo01FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        demo01Binding = CooDemo01FragmentBinding.inflate(inflater, container, false)
        return demo01Binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        demo01Binding.toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            demo01Binding.parentView.scrollingType =
                if (isChecked) ParentView.ScrollingType.AFTER_CHILDREN else ParentView.ScrollingType.BEFORE_CHILDREN
        }
        demo01Binding.toggleButton.isChecked = true
    }
}

private fun log(message: String) {
    Log.d("Demo01", message)
}

internal class ParentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {
    private val parentHelper = NestedScrollingParentHelper(this)
    var scrollingType: ScrollingType =
        ScrollingType.AFTER_CHILDREN

    /**
     * 子view在它滑动前，通知我了
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (scrollingType == ScrollingType.BEFORE_CHILDREN) {
            consumeScroll(dx, dy, consumed)
        }

    }

    /**
     * 消费滑动
     */
    private fun consumeScroll(dx: Int, dy: Int, consumed: IntArray? = null) {

        if (dx != 0 || dy != 0) {

            log("parent.consumeScroll(dx:$dx,dy:$dy)")
            // 计算越界
            // view.x应该在[0,parent.width-width],view.x=consumedX+x
            val consumedX =
                MathUtils.clamp(dx.toFloat(), -x, (parent as View).width - width - x).toInt()
            // view.y应该在[0,parent.height-height],view.y=consumedY+y

            val consumedY =
                MathUtils.clamp(dy.toFloat(), -y, (parent as View).height - height - y).toInt()
            consumed?.let {
                consumed[0] = consumedX
                consumed[1] = consumedY
            }
            if (consumedX != 0) {
                log("parent.offsetLeftAndRight($consumedX)")
                offsetLeftAndRight(consumedX)
            }
            if (consumedY != 0) {
                log("parent.offsetTopAndBottom($consumedY)")
                offsetTopAndBottom(consumedY)
            }
        }

    }

    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target, type)

    }

    /**
     * 子view问我要不要处理这个事件
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return true
    }

    /**
     * 子view告诉我，请求通过，由我处理这个事件
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    }


    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)

    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    /**
     * 子view在它滑动后，通知我了
     */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {

        if (scrollingType == ScrollingType.AFTER_CHILDREN) {
            consumeScroll(dxUnconsumed, dyUnconsumed)

        }
    }

    enum class ScrollingType {
        BEFORE_CHILDREN, AFTER_CHILDREN
    }
}

internal class ChildrenView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), NestedScrollingChild3 {
    private val nestedScrollChildrenHelper = NestedScrollingChildHelper(this)

    init {
        isNestedScrollingEnabled = true
    }

    private val consumed = IntArray(2)
    private val offsetInWindow = IntArray(2)
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        super.setNestedScrollingEnabled(enabled)
        nestedScrollChildrenHelper.isNestedScrollingEnabled = true
    }

    private var downX = 0f
    private var downY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                startNestedScroll(
                    ViewCompat.SCROLL_AXIS_HORIZONTAL or ViewCompat.SCROLL_AXIS_VERTICAL,
                    ViewCompat.TYPE_TOUCH
                )
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.x - downX).toInt()
                val dy = (event.y - downY).toInt()
                dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH)

                // 计算越界
                // view.x应该在[0,parent.width-width],view.x=consumedX+x
                val consumedX = MathUtils.clamp(
                    dx - consumed[0].toFloat(),
                    -x,
                    (parent as View).width - width - x
                ).toInt()
                // view.y应该在[0,parent.height-height],view.y=consumedY+y

                val consumedY = MathUtils.clamp(
                    dy - consumed[1].toFloat(),
                    -y,
                    (parent as View).height - height - y
                ).toInt()
                if (consumedX != 0) {
                    log("children.offsetLeftAndRight($consumedX)")
                    offsetLeftAndRight(consumedX)
                }
                if (consumedY != 0) {
                    log("children.offsetTopAndBottom($consumedY)")
                    offsetTopAndBottom(consumedY)
                }
                val dxUnconsumed = dx - consumedX
                val dyUnconsumed = dy - consumedY
                log("child:dxUnconsumed=$dxUnconsumed,dyUnconsumed=$dyUnconsumed")
                dispatchNestedScroll(
                    consumedX,
                    consumedY,
                    dxUnconsumed,
                    dyUnconsumed,
                    offsetInWindow,
                    ViewCompat.TYPE_TOUCH,
                    consumed
                )
            }
            MotionEvent.ACTION_UP -> {
                stopNestedScroll(ViewCompat.TYPE_TOUCH)

            }
        }

        return true
    }


    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return nestedScrollChildrenHelper.startNestedScroll(axes, type)
    }


    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return nestedScrollChildrenHelper.dispatchNestedPreScroll(
            dx,
            dy,
            consumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return nestedScrollChildrenHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {

        return nestedScrollChildrenHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    override fun stopNestedScroll(type: Int) {
        return nestedScrollChildrenHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return nestedScrollChildrenHelper.hasNestedScrollingParent(type)
    }


}