package pokercc.android.playrecyclerview.mix.tantan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.ui.recyclerview.R
import pokercc.android.ui.recyclerview.databinding.RecyTantanFragmentBinding

private val data = listOf(
    R.drawable.img_avatar_01,
    R.drawable.img_avatar_02,
    R.drawable.img_avatar_03,
    R.drawable.img_avatar_04,
    R.drawable.img_avatar_05,
    R.drawable.img_avatar_06,
    R.drawable.img_avatar_07
)
    .mapIndexed { index, i -> index to i }
    .toList()


/**
 * 仿探探效果
 */
internal class TantanFragment : Fragment() {
    private lateinit var tantanFragmentBinding: RecyTantanFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tantanFragmentBinding = RecyTantanFragmentBinding.inflate(inflater, container, false)
        return tantanFragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tantanFragmentBinding.apply {
            recyclerView.layoutManager = TantanLayoutManager()

            setupRecyclerView()
            resetButton.setOnClickListener {
                setupRecyclerView()
            }
        }

    }

    private var itemTouchHelper: ItemTouchHelper? = null
    private fun setupRecyclerView() {
        tantanFragmentBinding.apply {
            val tempData = data.toMutableList()
            val adapter = MyAdapter(tempData)
            recyclerView.adapter = adapter
            itemTouchHelper?.attachToRecyclerView(null)
            itemTouchHelper = ItemTouchHelper(TantanItemTouchCallback(adapter, tempData))
                .also { it.attachToRecyclerView(recyclerView) }
        }


    }

}

private class MyAdapter(private val data: List<Pair<Int, Int>>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recy_tantan_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val avatarImageView = holder.avatarImageView
        avatarImageView.setImageResource(data[position].second)
        holder.tvName.text = "小姐姐 ${data[position].first + 1}"
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var avatarImageView: ImageView
        var likeImageView: ImageView
        var dislikeImageView: ImageView
        val tvName by lazy { itemView.findViewById<TextView>(R.id.tv_name) }

        init {
            avatarImageView = itemView.findViewById(R.id.iv_avatar) as ImageView
            likeImageView = itemView.findViewById(R.id.iv_like) as ImageView
            dislikeImageView = itemView.findViewById(R.id.iv_dislike) as ImageView
        }

    }
}