package pokercc.android.playrecyclerview.snap

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.ui.recyclerview.BuildConfig
import pokercc.android.ui.recyclerview.R
import pokercc.android.ui.recyclerview.databinding.RecyPageSnapHelperFragmentBinding

internal class PageSnapHelperFragment : Fragment() {

    private lateinit var pageSnapHelperFragmentBinding: RecyPageSnapHelperFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pageSnapHelperFragmentBinding= RecyPageSnapHelperFragmentBinding.inflate(inflater,container,false)
        return pageSnapHelperFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageSnapHelperFragmentBinding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = ColorAdapter()
            PagerSnapHelper().attachToRecyclerView(recyclerView)
        }
    }
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
        holder.itemView.setBackgroundColor(colors[position])
        holder.tvPosition.text = position.toString()
        "onBindViewHolder".log()

    }

    override fun onViewRecycled(holder: ColorViewHolder) {
        super.onViewRecycled(holder)
        "onViewRecycled".log()

    }

}