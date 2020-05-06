package pokercc.android.playrecyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pokercc.android.fragment.getNavigator
import pokercc.android.playrecyclerview.expand.ExpandInItemFragment
import pokercc.android.playrecyclerview.itemdecorator.ceiling.CeilingFragment
import pokercc.android.playrecyclerview.itemdecorator.letter.LetterFragment
import pokercc.android.playrecyclerview.itemdecorator.timeline.TimeLineFragment
import pokercc.android.playrecyclerview.itemtouchhelper.SampleItemTouchHelperFragment
import pokercc.android.playrecyclerview.layoutmanager.stacklayoutmanager.StackLayoutFragment
import pokercc.android.playrecyclerview.layoutmanager.verticallayoutmanager.FixedHeightLayoutFragment
import pokercc.android.playrecyclerview.layoutmanager.viewpagerlayoutmanager.ViewPagerLayoutFragment
import pokercc.android.playrecyclerview.mix.tantan.TantanFragment
import pokercc.android.playrecyclerview.snap.LinearSnapHelperFragment
import pokercc.android.playrecyclerview.snap.PageSnapHelperFragment
import pokercc.android.ui.basic.ExpandableAdapter
import pokercc.android.ui.recyclerview.R
import pokercc.android.ui.recyclerview.databinding.RecyRecyclerViewFragmentBinding

private val data = listOf(
    Category(
        "LayoutManager",
        listOf(
            SubItem(
                "VerticalLinearLayoutManager(垂直布局)",
                FixedHeightLayoutFragment()
            ),
            SubItem(
                "StackLayoutManager(焦点布局)",
                StackLayoutFragment()
            ),
            SubItem(
                "ViewPagerLayoutManager(ViewPager效果)",
                ViewPagerLayoutFragment()
            )
        )

    ),
    Category(
        "ItemDecorator",
        listOf(
            SubItem(
                "TimeLineDecorator(时间轴)",
                TimeLineFragment()
            ),
            SubItem(
                "CeilingDecorator(吸顶)",
                CeilingFragment()
            ),
            SubItem(
                "LetterDecorator(通讯录)",
                LetterFragment()
            )
        )
    ),
    Category(
        "ItemTouchHelper",
        listOf(
            SubItem(
                "SampleItemTouchHelper(侧滑删除+长按拖拽)",
                SampleItemTouchHelperFragment()
            )
        )
    ),
    Category(
        "SnapHelper",
        listOf(

            SubItem(
                "PagerSnapHelper",
                PageSnapHelperFragment()
            ),
            SubItem(
                "LinearSnapHelper",
                LinearSnapHelperFragment()
            )
        )
    ),
    Category(
        "Expand",
        listOf(

            SubItem(
                "ExpandInItem",
                ExpandInItemFragment()
            )
        )
    ),
    Category(
        "Mix",
        listOf(

            SubItem(
                "Tantan(探探滑动效果)",
                TantanFragment()
            )
        )
    )
)

class RecyclerViewFragment : Fragment() {

    private val categoryAdapter by lazy {
        CategoryAdapter(data.map { ExpandableAdapter.Group(it, it.subItems) }).apply {
            setOnChildrenItemClickListener { t, _, _ ->
                getNavigator().navigateTo(t.fragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerViewFragmentBinding = RecyRecyclerViewFragmentBinding.inflate(layoutInflater)
        recyclerViewFragmentBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        recyclerViewFragmentBinding.recyclerView.adapter = categoryAdapter
        return recyclerViewFragmentBinding.root
    }

}

class SubItem(val name: String, val fragment: Fragment)
class Category(val name: String, val subItems: List<SubItem>)
class CategoryViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(viewGroup.context).inflate(
        R.layout.recy_home_category_item,
        viewGroup,
        false
    )
) {
    val tvName: TextView by lazy { itemView.findViewById<TextView>(R.id.tv_name) }
    val ivExpand: ImageView by lazy { itemView.findViewById<ImageView>(R.id.iv_expand) }
}

class SubItemViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(viewGroup.context).inflate(
        R.layout.recy_home_sub_item,
        viewGroup,
        false
    )
) {
    val tvName: TextView by lazy { itemView.findViewById<TextView>(R.id.tv_name) }
}

class CategoryAdapter(private val datas: List<Group<Category, SubItem>>) :
    ExpandableAdapter<Category, SubItem>(datas) {
    override fun onCreateParentViewHolder(viewGroup: ViewGroup): RecyclerView.ViewHolder {
        return CategoryViewHolder(viewGroup)
    }

    override fun onCreateChildrenViewHolder(viewGroup: ViewGroup): RecyclerView.ViewHolder {
        return SubItemViewHolder(viewGroup)

    }

    override fun onBindChildrenViewHolder(
        holder: RecyclerView.ViewHolder,
        children: SubItem,
        group: Int,
        childrenPosition: Int
    ) {
        (holder as SubItemViewHolder).apply {
            tvName.text = children.name
        }

    }

    override fun onBindParentViewHolder(
        holder: RecyclerView.ViewHolder,
        parent: Category,
        groupPosition: Int,
        expand: Boolean
    ) {
        (holder as CategoryViewHolder).apply {
            tvName.text = parent.name
            ivExpand.rotation = -90.0f

        }

    }

    override fun onBindParentViewHolderExpandChange(
        holder: RecyclerView.ViewHolder,
        parent: Category,
        groupPosition: Int,
        expand: Boolean
    ) {
        val ivExpand = (holder as CategoryViewHolder).ivExpand
        if (expand) {
            ivExpand.animate()
                .rotation(0f)
                .start()
        } else {
            ivExpand.animate()
                .rotation(-90.0f)
                .start()
        }

    }


}