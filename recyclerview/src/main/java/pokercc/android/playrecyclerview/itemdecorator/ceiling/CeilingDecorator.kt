package pokercc.android.playrecyclerview.itemdecorator.ceiling

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.R.attr.centerY
import android.text.StaticLayout


/**
 * 吸顶的装饰器
 * 关键点：要在当前组，最后一个item的bottom小于分类显示的时候，移动类别显示
 */
class CeilingDecorator(
    private val itemHeight: Int = 100, private val backgroundColor: Int = Color.RED,
    private val textSize: Int = 30, private val textColor: Int = Color.WHITE,
    private val marginLeft: Int = 45,
    private val itemDecider: ItemDecider
) :
    RecyclerView.ItemDecoration() {


    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        isDither = true
        color = textColor
        textSize = this@CeilingDecorator.textSize.toFloat()
        textAlign = Paint.Align.LEFT
    }
    private val backgroundPaint = Paint().apply {
        color = this@CeilingDecorator.backgroundColor
    }
    private val textBound = Rect()

    private fun drawCategory(canvas: Canvas, parent: RecyclerView, title: String, top: Int) {
        canvas.drawRect(
            parent.paddingStart.toFloat(),
            top.toFloat(),
            (parent.width - parent.paddingStart - parent.paddingEnd).toFloat(),
            top + itemHeight.toFloat(),
            backgroundPaint
        )
        if (!TextUtils.isEmpty(title)) {
            textPaint.getTextBounds(title, 0, title.length, textBound)
            val fontMetrics = textPaint.fontMetrics
            val baseline = top + (itemHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top


            canvas.drawText(
                title,
                marginLeft.toFloat(),
                baseline,
                textPaint
            )
        }

    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        parent.layoutManager?.let {
            for (i in 0 until it.childCount) {
                val child = it.getChildAt(i)!!
                val layoutParams = child.layoutParams as RecyclerView.LayoutParams
                if (itemDecider.isHeaderInGroup(layoutParams.viewAdapterPosition)) {
                    val groupName = itemDecider.getGroupName(layoutParams.viewAdapterPosition)
                    if (groupName?.isNotEmpty() == true) {
                        val saveCount = c.save()
                        c.translate(0f, (child.top - itemHeight).toFloat())
                        drawCategory(c, parent, groupName, 0)
                        c.restoreToCount(saveCount)

                    }


                }
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val saveCount = c.save()
        c.translate(0f, 0f)
        parent.layoutManager?.getChildAt(0)?.let {
            val layoutParams = it.layoutParams as RecyclerView.LayoutParams
            val groupName = itemDecider.getGroupName(layoutParams.viewLayoutPosition)

            if (groupName?.isNotEmpty() == true) {
                val bottom = it.bottom + it.translationY
                if (itemDecider.isFooterInGroup(layoutParams.viewLayoutPosition) && bottom < itemHeight) {
                    drawCategory(c, parent, groupName, (bottom - itemHeight).toInt())
                } else {
                    drawCategory(c, parent, groupName, 0)
                }
            }

        }

        c.restoreToCount(saveCount)

    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        parent.layoutManager?.let {
            val layoutParams = view.layoutParams as RecyclerView.LayoutParams
            if (itemDecider.isHeaderInGroup(layoutParams.viewLayoutPosition)) {
                outRect.top = itemHeight
            }
        }

    }

    interface ItemDecider {
        /**
         * 是否是一组的头
         */
        fun isHeaderInGroup(layoutPosition: Int): Boolean

        /**
         * 是否是一组的尾巴
         */
        fun isFooterInGroup(layoutPosition: Int): Boolean


        /**
         * 获取组名
         */
        fun getGroupName(layoutPosition: Int): String?
    }

}