package pokercc.android.playrecyclerview.itemtouchhelper

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.ui.recyclerview.R
import pokercc.android.ui.recyclerview.databinding.RecySampleItemTouchHelperFragmentBinding

/**
 * 侧滑删除+长按拖拽
 */
internal class SampleItemTouchHelperFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = RecySampleItemTouchHelperFragmentBinding.inflate(inflater, container, false)
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val data = (0 until 50).toList()
        val sampleAdapter = SampleAdapter(data)
        viewBinding.  recyclerView.adapter = sampleAdapter
        viewBinding.  recyclerView.addItemDecoration(MarginItemDecorator())
        ItemTouchHelper(SampleCallback(sampleAdapter, data)).attachToRecyclerView(viewBinding.recyclerView)
        return viewBinding.root
    }
}

private class MarginItemDecorator : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        parent.adapter?.let {
            if (parent.getChildAdapterPosition(view) != it.itemCount - 1) {
                outRect.bottom = 30
            }
        }
        outRect.left = 30
        outRect.right = 30

    }
}

private class SampleViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(viewGroup.context).inflate(
        R.layout.recy_sample_itemtouchhelper_item,
        viewGroup,
        false
    )
) {
    val tvTitle: TextView by lazy { itemView.findViewById<TextView>(R.id.tv_title) }
}

private class SampleAdapter(val data: List<Int>) : RecyclerView.Adapter<SampleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.tvTitle.text = "第${data[position] + 1}条"
    }

}