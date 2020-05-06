package pokercc.android.playrecyclerview.expand

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.playrecyclerview.itemanimator.BaseItemAnimator
import pokercc.android.ui.recyclerview.BuildConfig
import pokercc.android.ui.recyclerview.R
import pokercc.android.ui.recyclerview.databinding.RecyExpandInItemFragmentBinding

/**
 * 在item内部，有展开的行为,获取是动态添加view
 * 1. 使用CoordinateLayout 协调布局
 * 2. 使用NestedScrollView
 * 3. 使用recyclerView
 * 4. 使用普通的布局,LinearLayout,或者是FrameLayout之类
 */
internal class ExpandInItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = RecyExpandInItemFragmentBinding.inflate(inflater, container, false)
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.recyclerView.adapter = ColorAdapter()
        return viewBinding.root
    }

    private class ColorViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        val tvPosition: TextView by lazy { item.findViewById<TextView>(R.id.tv_position) }


    }

    private class ColorAdapter : RecyclerView.Adapter<ColorViewHolder>() {
        companion object {
            private val DEBUG = BuildConfig.DEBUG
            private const val TAG = "ColorAdapter"
            private fun String.log() {
                if (DEBUG) {
                    Log.d(TAG, this)
                }
            }
        }

        private val colors = listOf(
            Color.BLACK, Color.DKGRAY, Color.GRAY,
            Color.LTGRAY, Color.WHITE, Color.RED,
            Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.CYAN, Color.MAGENTA, 0xFF00FFFF.toInt(),
            0xFFFF00FF.toInt(), Color.DKGRAY, Color.GRAY,
            0xFF00FF00.toInt(), 0xFF800000.toInt(), 0xFF000080.toInt()

        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            "onCreateViewHolder".log()
            return ColorViewHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.recy_fixed_color_item, parent, false)
            )
        }

        override fun getItemCount() = colors.size

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            holder.itemView.translationY = position.toFloat()
            holder.itemView.setBackgroundColor(colors[position])
            holder.tvPosition.text = position.toString()
            holder.itemView.setOnClickListener { itemView ->
                itemView.layoutParams.height = itemView.height * 2
                itemView.requestLayout()
                notifyItemChanged(position)
            }
            "onBindViewHolder".log()

        }

        override fun onViewRecycled(holder: ColorViewHolder) {
            super.onViewRecycled(holder)
            "onViewRecycled".log()

        }

    }
}

/**
 * 折叠动画
 */
private class ExpandAnimator : BaseItemAnimator() {


    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {

        ViewCompat.animate(holder.itemView)
            .alpha(1f)
            .setDuration(removeDuration * 3)
            .setInterpolator(mInterpolator)
            .setListener(DefaultRemoveVpaListener(holder))
            .setStartDelay(getRemoveDelay(holder))
            .start()

    }

    override fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder) {
        super.preAnimateRemoveImpl(holder)


    }

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
        super.preAnimateAddImpl(holder)


    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {

        ViewCompat.animate(holder.itemView)
            .alpha(1f)
            .setDuration(addDuration * 3)
            .setInterpolator(mInterpolator)
            .setListener(DefaultAddVpaListener(holder))
            .setStartDelay(getAddDelay(holder))

            .start()


    }

    override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
        super.onAnimationFinished(viewHolder)
    }

}