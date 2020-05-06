package pokercc.android.playrecyclerview.mix.tantan

import android.graphics.Canvas
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TantanItemTouchCallback(private val adapter: RecyclerView.Adapter<*>, private val data: MutableList<*>) :
    ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        //设置只有第一个item可以滑动
        if (viewHolder.layoutPosition == 0) {
            return makeMovementFlags(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
            )
        }
        return 0

    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val layoutPosition = viewHolder.adapterPosition
        data.removeAt(layoutPosition)
        adapter.notifyItemRemoved(layoutPosition)
        Toast.makeText(
            viewHolder.itemView.context,
            "向${if (direction == ItemTouchHelper.LEFT) "左" else "右"}滑动",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // 默认实现滑动
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val swipeThreshold = getThreshold(recyclerView, viewHolder)
        val distance = recyclerView.width
        val percent: Float = if (distance == 0) 0.toFloat() else (swipeThreshold / distance.toFloat()).toFloat()

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)

            val position = (child.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
            if (position == 0) {
                continue
            }
            val scale: Float = 1 - 0.1f * position + 0.1f * percent
            val translationY: Float = (position * 0.1f - 0.1f * percent) * child.height.toFloat()

            child.scaleX = scale
            child.scaleY = scale
//            child.translationY = translationY

        }

    }

    private fun getThreshold(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Float {
        return recyclerView.width * getSwipeThreshold(viewHolder)
    }

}