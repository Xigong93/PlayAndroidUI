自定义LayoutManager 的步骤
1. 重写 generateLayoutParams()的几个重载方法
2. 重写 onLayoutChildren 进行布局
3. 重写 canScrollVertically 返回true ,表示可以纵向滚动,重写scrollVerticallyBy方法，处理滚动的逻辑

怎么样才能复用View
1. 先计算可见范围
2. 计算可见范围可以容纳的Item数量(Item 是否是固定高度的)
3. 只布局可见范围内的item

滑动处理：
1. 处理滑动
2. 处理回收