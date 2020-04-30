package pokercc.android.scrolling

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Keep
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import kotlin.math.absoluteValue

@Keep
class BottomSlideBehavior @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    enum class State {
        FOLD, SLIDING, EXPAND
    }

    companion object {

        /**
         * 滑动触发事件的距离
         */
        private const val SCROLL_TRIGGER_DISTANCE = 180
    }


    private val headerHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        65f,
        context.resources.displayMetrics
    )

    private var eventInChildren = false
    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        //1 .在滑动布局折叠时，拦截时间，使得滑动布局可以拖动
        eventInChildren =
            (ev.x in child.x..(child.x + child.width)) and (ev.y in child.y..(child.y + child.height))
        return eventInChildren && ev.actionMasked == MotionEvent.ACTION_DOWN && state == State.FOLD
    }

    override fun blocksInteractionBelow(parent: CoordinatorLayout, child: View): Boolean {
        return true
    }

    private var downY: Int = 0

    private var downTranslateY = 0

    private var state = State.FOLD

    private var downState: State = State.FOLD
    override fun onTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {

        val topY = -child.top.toFloat()
        val bottomY = parent.height - headerHeight - child.top

        log("滑动范围:topY=${topY},bottomY=${bottomY}")
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downState = state
                child.animate().cancel()
                downY = ev.y.toInt()
                downTranslateY = child.translationY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val dY = ev.y - downY
                //child.y=[0,parent.height-headerHeight]=top+translateY
                child.translationY = MathUtils.clamp(downTranslateY + dY, topY, bottomY)
                state = State.SLIDING
            }

            MotionEvent.ACTION_UP -> {
                // 大于50，展开
                log("手指抬起,translationY=${child.translationY}")
                if ((downState == State.FOLD && (child.translationY - downTranslateY).absoluteValue > SCROLL_TRIGGER_DISTANCE)
                    or (downState == State.EXPAND && (child.translationY - downTranslateY).absoluteValue < SCROLL_TRIGGER_DISTANCE)
                ) {
                    log("手指抬起,展开")
                    child.animate()
                        .translationY(topY)
                        .withEndAction {
                            state = State.EXPAND
                        }
                        .start()
                } else {
                    log("手指抬起,折叠")
                    child.animate()
                        .translationY(bottomY)
                        .withEndAction {
                            state = State.FOLD
                        }
                        .start()
                }
                // 小于50,回去

            }
            else -> {
            }
        }
        return true
    }


    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        val top = (parent.measuredHeight - headerHeight).toInt()
        val left = parent.paddingStart
        val right = parent.measuredWidth - parent.paddingEnd
        val bottom = top + child.measuredHeight
        child.layout(left, top, right, bottom)
        log("child.layout($left,$top,$right,$bottom)")
        return true
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        // 不处理fling事件
        return type == ViewCompat.TYPE_TOUCH
    }

    override fun onNestedScrollAccepted(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ) {
        child.animate().cancel()

        super.onNestedScrollAccepted(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)


        // 在滑动布局展开的时候，如果下拉了滑动布局，然后往回塞
        if (state == State.EXPAND) {
            if (dy > 0) {

                val consumedY = MathUtils
                    .clamp(
                        -dy.toFloat(),
                        -child.y,
                        coordinatorLayout.height - headerHeight - child.y
                    )
                    .toInt()

                consumed[1] = -consumedY
                consumedY
                    .takeIf { it != 0 }
                    ?.let {
                        log("offsetTopAndBottom($it)")
                        ViewCompat.offsetTopAndBottom(child, it)
                    }
            }

        }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        // child 不是当前这个view 的话，让上，我往下

        log("onNestedScroll( child=[$child] target=[$target] dxConsumed = [${dxConsumed}], dyConsumed = [${dyConsumed}], dxUnconsumed = [${dxUnconsumed}], dyUnconsumed = [${dyUnconsumed}], type = [${type}])")

        if (state == State.FOLD) {
            // 滑动的范围,hostView.y=offsetTopAndBottom+oldY=[parent.measuredHeight - headerHeight,parent.height]
            MathUtils
                .clamp(
                    dyConsumed.toFloat(),
                    coordinatorLayout.height - headerHeight - child.y,
                    coordinatorLayout.height.toFloat() - child.y
                )
                .toInt()
                .takeIf { it != 0 }
                ?.let {
                    ViewCompat.offsetTopAndBottom(child, it)
                }
        } else if (state == State.EXPAND) {
            // 滑动的范围,hostView.y=offsetTopAndBottom+oldY=[0, parent.height-headerHeight]
            if (dyUnconsumed < 0) {
                MathUtils
                    .clamp(
                        -dyUnconsumed.toFloat(),
                        -child.y,
                        coordinatorLayout.height - headerHeight - child.y
                    )
                    .toInt()
                    .takeIf { it != 0 }
                    ?.let {
                        log("offsetTopAndBottom($it)")
                        ViewCompat.offsetTopAndBottom(child, it)
                    }

            }

        }

    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)

    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        type: Int
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)

        log("onStopNestedScroll")

        if (state == State.EXPAND) {
            if (child.y > SCROLL_TRIGGER_DISTANCE) {
                log("嵌套滑动结束,折叠")
                val bottomY = coordinatorLayout.height - headerHeight - child.top
                child.animate()
                    .translationY(bottomY)
                    .withEndAction {
                        state = State.FOLD
                    }
                    .start()
            } else {
                log("嵌套滑动结束,展开")
                val topY = -child.top.toFloat()

                child.animate()
                    .translationY(topY)
                    .withEndAction {
                        state = State.EXPAND
                    }
                    .start()
            }


        }


    }

}

private fun log(message: String) = Log.d("BossZhiPin", message)
