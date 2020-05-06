package pokercc.android.playrecyclerview.itemdecorator.ceiling

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import pokercc.android.ui.recyclerview.R
import pokercc.android.ui.recyclerview.databinding.RecyCeilingFragmentBinding


/**
 * 悬浮吸顶的activity
 */
class CeilingFragment : Fragment() {
    private lateinit var ceilingFragmentBinding: RecyCeilingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        ceilingFragmentBinding = RecyCeilingFragmentBinding.inflate(inflater, container, false)

        return ceilingFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ceilingFragmentBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val cities = getCities(requireContext())
            adapter = CityAdapter(cities)
            addItemDecoration(
                CeilingDecorator(
                    textSize = 40,
                    itemDecider = object : CeilingDecorator.ItemDecider {
                        override fun isHeaderInGroup(layoutPosition: Int): Boolean {
                            return cities[layoutPosition].let {
                                it.province?.cities?.indexOf(it) == 0
                            }
                        }

                        override fun isFooterInGroup(layoutPosition: Int): Boolean {
                            return cities[layoutPosition].let {
                                it.province!!.cities.indexOf(it) == it.province!!.cities.size - 1
                            }
                        }

                        override fun getGroupName(layoutPosition: Int): String? {
                            return cities[layoutPosition].province?.name
                        }

                    })
            )
        }
    }

}

private fun getCities(context: Context): List<City> {

    val provinces = listOf<Province>(
        Province(
            listOf(
                City("郑州市"),
                City("开封市"),
                City("平顶山市"),
                City("安阳市"),
                City("鹤壁市"),
                City("新乡市"),
                City("焦作市"),
                City("濮阳市"),
                City("许昌市"),
                City("漯河市"),
                City("三门峡市"),
                City("南阳市"),
                City("商丘市"),
                City("信阳市"),
                City("周口市"),
                City("驻马店市")
            ), "河南省"
        ),
        Province(
            listOf(
                City("石家庄市"),
                City("唐山市"),
                City("秦皇岛市"),
                City("邯郸市"),
                City("遵化市"),
                City("邢台市"),
                City("涿州市"),
                City("保定市"),
                City("晋州市"),
                City("武安市"),
                City("鹿泉市"),
                City("新乐市"),
                City("辛集市"),
                City("迁安市")
            ), "河北省"
        )
        , Province(
            listOf(
                City("成都市"),
                City("自贡市"),
                City("攀枝花市"),
                City("泸州市"),
                City("德阳市"),
                City("绵阳市"),
                City("广元市"),
                City("遂宁市"),
                City("内江市"),
                City("乐山市"),
                City("南充市"),
                City("眉山市"),
                City("宜宾市")
            ), "四川省"
        )
    )
    val cities = ArrayList<City>()
    for (province in provinces) {
        province.cities.forEach {
            it.province = province
            cities.add(it)
        }
    }

    return cities
}

private class CityViewHolder(viewGroup: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(viewGroup.context).inflate(R.layout.recy_ceiling_item, viewGroup, false)
    ) {
    val textView: TextView by lazy(LazyThreadSafetyMode.NONE) { itemView.findViewById<TextView>(R.id.title) }

}

data class Province(
    val cities: List<City>,
    val name: String
)

data class City(
    val name: String,
    var province: Province? = null
)


private class CityAdapter(val cities: List<City>) : Adapter<CityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(parent)


    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.textView.text = cities[position].name.trim()
    }

}