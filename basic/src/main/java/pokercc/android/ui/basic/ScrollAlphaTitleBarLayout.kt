package pokercc.android.ui.basic

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.absoluteValue

// 使用方式
//  <com.google.android.material.appbar.AppBarLayout
//        android:id="@+id/app_bar_layout"
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:background="#fff"
//        android:minHeight="200dp"
//        app:elevation="0px">
//
//        <com.google.android.material.appbar.CollapsingToolbarLayout
//            android:layout_width="match_parent"
//            android:layout_height="wrap_content"
//            app:layout_scrollFlags="scroll|exitUntilCollapsed"
//            app:title="会员课程">
//
//            <androidx.constraintlayout.widget.ConstraintLayout
//                android:layout_width="match_parent"
//                android:layout_height="wrap_content">
//
//                <ImageView
//                    android:layout_width="match_parent"
//                    android:layout_height="0dp"
//                    android:src="@drawable/vip_course_top_image"
//                    app:layout_constraintBottom_toBottomOf="parent"
//                    app:layout_constraintDimensionRatio="360:202"
//                    app:layout_constraintTop_toTopOf="parent" />
//            </androidx.constraintlayout.widget.ConstraintLayout>
//
//            <pokercc.android.ui.basic.ScrollAlphaTitleBarLayout
//                android:id="@+id/tool_bar"
//                android:layout_width="match_parent"
//                android:layout_height="wrap_content"
//                android:background="#fff"
//                app:layout_collapseMode="pin">
//
//                <pokercc.android.ui.basic.TitleBar
//                    android:layout_width="match_parent"
//                    android:layout_height="wrap_content"
//                    app:layout_constraintBottom_toBottomOf="parent"
//                    app:layout_constraintTop_toTopOf="parent"
//                    app:title="会员课程" />
//            </pokercc.android.ui.basic.ScrollAlphaTitleBarLayout>
//        </com.google.android.material.appbar.CollapsingToolbarLayout>
//
//    </com.google.android.material.appbar.AppBarLayout>
/**
 * 滑动渐变的标题栏目父布局
 *
 * @author pokercc
 * @date 2020-4-13 11:32:26
 */
class ScrollAlphaTitleBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        children.forEach {
            if (it is ViewGroup) {
                it.layout(l + paddingStart, t + paddingTop, r - paddingRight, b - paddingBottom)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent?.parent as? AppBarLayout)?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percent = verticalOffset.absoluteValue * 1.0f / appBarLayout.totalScrollRange
            alpha = percent
        })
    }

    /** 修改父类实现，不消费点击事件 */
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}