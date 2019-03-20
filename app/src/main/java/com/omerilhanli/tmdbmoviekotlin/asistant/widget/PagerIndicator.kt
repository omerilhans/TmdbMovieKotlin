/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omerilhanli.tmdbmoviekotlin.asistant.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.omerilhanli.tmdbmoviekotlin.R

import java.util.ArrayList

/**
 * 专为 ViewPager 定制的页面指示器
 */
class PagerIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    HorizontalScrollView(context, attrs) {
    private var currentPosition: Int = 0    //当前位置
    private var lastOffset: Int = 0
    private var lastScrollX = 0
    private var currentPositionOffset: Float = 0.toFloat()    //当前位置偏移量
    private var start: Boolean = false
    private var allowWidthFull: Boolean = false    // 内容宽度无法充满时，允许自动调整Item的宽度以充满
    private var disableViewPager: Boolean = false   // 禁用ViewPager
    private var slidingBlockDrawable: Drawable? = null    //滑块
    private var viewPager: ViewPager? = null    //ViewPager
    private var tabsLayout: ViewGroup? = null    //标题项布局
    private var onPageChangeListener: OnPageChangeListener? = null    //页面改变监听器
    private var onClickTabListener: OnClickTabListener? = null
    private var onDoubleClickTabListener: OnDoubleClickTabListener? = null
    private var tabViews: MutableList<View>? = null
    private var disableTensileSlidingBlock: Boolean = false // 禁止拉伸滑块图片
    private var tabViewFactory: TabViewFactory? = null
    private var bottomLinePaint: Paint? = null
    private var bottomLineColor = -1
    private var bottomLineHeight = -1
    private val pageChangedListener = PageChangedListener()
    private val tabViewClickListener = TabViewClickListener()
    private val setSelectedTabListener = SetSelectedTabListener()
    private val tabViewDoubleClickGestureDetector: DoubleClickGestureDetector

    /**
     * 获取Tab总数
     */
    val tabCount: Int
        get() {
            val tabsLayout = getTabsLayout()
            return tabsLayout?.childCount ?: 0
        }

    init {
        isHorizontalScrollBarEnabled = false    //隐藏横向滑动提示条
        removeAllViews()
        if (attrs != null) {
            val attrsTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicator)
            if (attrsTypedArray != null) {
                allowWidthFull = attrsTypedArray.getBoolean(R.styleable.PagerIndicator_pi_allowWidthFull, false)
                slidingBlockDrawable = attrsTypedArray.getDrawable(R.styleable.PagerIndicator_pi_slidingBlock)
                disableViewPager = attrsTypedArray.getBoolean(R.styleable.PagerIndicator_pi_disableViewPager, false)
                disableTensileSlidingBlock =
                        attrsTypedArray.getBoolean(R.styleable.PagerIndicator_pi_disableTensileSlidingBlock, false)
                bottomLineColor = attrsTypedArray.getColor(R.styleable.PagerIndicator_pi_bottomLineColor, -1)
                bottomLineHeight =
                        attrsTypedArray.getDimension(R.styleable.PagerIndicator_pi_bottomLineHeight, -1f).toInt()
                attrsTypedArray.recycle()
            }
        }
        tabViewDoubleClickGestureDetector = DoubleClickGestureDetector(context)

        viewTreeObserver.addOnGlobalLayoutListener(setSelectedTabListener)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (allowWidthFull && tabsLayout != null) {
            var childView: View
            var w = 0
            val size = tabsLayout!!.childCount
            while (w < size) {
                childView = tabsLayout!!.getChildAt(w)
                val params = childView.layoutParams
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                childView.layoutParams = params
                w++
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!allowWidthFull) {
            return
        }
        val tabsLayout = getTabsLayout() ?: return
        if (tabsLayout.childCount <= 0) {
            return
        }

        if (tabViews == null) {
            tabViews = ArrayList()
        } else {
            tabViews!!.clear()
        }
        for (w in 0 until tabsLayout.childCount) {
            tabViews!!.add(tabsLayout.getChildAt(w))
        }

        adjustChildWidthWithParent(
            tabViews!!,
            measuredWidth - tabsLayout.paddingLeft - tabsLayout.paddingRight,
            widthMeasureSpec,
            heightMeasureSpec
        )

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 调整views集合中的View，让所有View的宽度加起来正好等于parentViewWidth
     *
     * @param views                   子View集合
     * @param parentViewWidth         父Vie的宽度
     * @param parentWidthMeasureSpec  父View的宽度规则
     * @param parentHeightMeasureSpec 父View的高度规则
     */
    private fun adjustChildWidthWithParent(
        views: MutableList<View>,
        parentViewWidth: Int,
        parentWidthMeasureSpec: Int,
        parentHeightMeasureSpec: Int
    ) {
        var parentViewWidth = parentViewWidth
        // 先去掉所有子View的外边距
        for (view in views) {
            if (view.layoutParams is ViewGroup.MarginLayoutParams) {
                val lp = view.layoutParams as LinearLayout.LayoutParams
                parentViewWidth -= lp.leftMargin + lp.rightMargin
            }
        }

        // 去掉宽度大于平均宽度的View后再次计算平均宽度
        var averageWidth = parentViewWidth / views.size
        var bigTabCount = views.size
        while (true) {
            val iterator = views.iterator()
            while (iterator.hasNext()) {
                val view = iterator.next()
                if (view.measuredWidth > averageWidth) {
                    parentViewWidth -= view.measuredWidth
                    bigTabCount--
                    iterator.remove()
                }
            }
            if (bigTabCount <= 0) {
                break
            }
            averageWidth = parentViewWidth / bigTabCount
            var end = true
            for (view in views) {
                if (view.measuredWidth > averageWidth) {
                    end = false
                }
            }
            if (end) {
                break
            }
        }

        // 修改宽度小于新的平均宽度的View的宽度
        for (view in views) {
            if (view.measuredWidth < averageWidth) {
                val layoutParams = view.layoutParams as LinearLayout.LayoutParams
                layoutParams.width = averageWidth
                view.layoutParams = layoutParams
                // 再次测量让新宽度生效
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    measureChildWithMargins(view, parentWidthMeasureSpec, 0, parentHeightMeasureSpec, 0)
                } else {
                    measureChild(view, parentWidthMeasureSpec, parentHeightMeasureSpec)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bottomLineColor != -1 && bottomLineHeight != -1) {
            if (bottomLinePaint == null) {
                bottomLinePaint = Paint()
                bottomLinePaint!!.color = bottomLineColor
            }
            canvas.drawRect(
                0f,
                (bottom - bottomLineHeight).toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                bottomLinePaint!!
            )
        }

        if (disableViewPager) return
        /* 绘制滑块 */
        val tabsLayout = getTabsLayout()
        if (tabsLayout != null && tabsLayout.childCount > 0 && slidingBlockDrawable != null) {
            val currentTab = tabsLayout.getChildAt(currentPosition)
            if (currentTab != null) {
                var slidingBlockLeft = currentTab.left.toFloat()
                var slidingBlockRight = currentTab.right.toFloat()
                if (currentPositionOffset > 0f && currentPosition < tabsLayout.childCount - 1) {
                    val nextTab = tabsLayout.getChildAt(currentPosition + 1)
                    if (nextTab != null) {
                        val nextTabLeft = nextTab.left.toFloat()
                        val nextTabRight = nextTab.right.toFloat()
                        slidingBlockLeft = currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) *
                                slidingBlockLeft
                        slidingBlockRight = currentPositionOffset * nextTabRight + (1f - currentPositionOffset) *
                                slidingBlockRight
                    }
                }

                // 不拉伸
                if (disableTensileSlidingBlock) {
                    val center = (slidingBlockLeft + (slidingBlockRight - slidingBlockLeft) / 2).toInt()
                    slidingBlockLeft = (center - slidingBlockDrawable!!.intrinsicWidth / 2).toFloat()
                    slidingBlockRight = (center + slidingBlockDrawable!!.intrinsicWidth / 2).toFloat()
                }

                slidingBlockDrawable!!.setBounds(
                    slidingBlockLeft.toInt(),
                    height - slidingBlockDrawable!!.intrinsicHeight,
                    slidingBlockRight.toInt(),
                    height
                )
                slidingBlockDrawable!!.draw(canvas)
            }
        }
    }

    /**
     * 获取布局
     */
    private fun getTabsLayout(): ViewGroup? {
        if (tabsLayout == null) {
            if (childCount > 0) {
                tabsLayout = getChildAt(0) as ViewGroup
            } else {
                removeAllViews()
                val tabsLayout = LinearLayout(context)
                tabsLayout.gravity = Gravity.CENTER_VERTICAL
                this.tabsLayout = tabsLayout
                addView(
                    tabsLayout,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL
                    )
                )
            }
        }
        return tabsLayout
    }

    /**
     * 重置，清除所有tab
     */
    fun reset() {
        if (tabViewFactory != null) {
            val tabViewGroup = getTabsLayout()
            tabViewFactory!!.addTabs(tabViewGroup, if (viewPager != null) viewPager!!.currentItem else 0)
            setTabClickEvent()
        }
    }

    private fun setTabClickEvent() {
        val tabViewGroup = getTabsLayout()
        if (tabViewGroup != null && tabViewGroup.childCount > 0) {
            //给每一个tab设置点击事件，当点击的时候切换Pager
            for (w in 0 until tabViewGroup.childCount) {
                val itemView = tabViewGroup.getChildAt(w)
                itemView.tag = w
                itemView.setOnClickListener(tabViewClickListener)
                itemView.setOnTouchListener(tabViewDoubleClickGestureDetector)
            }
        }
    }

    /**
     * 获取Tab
     *
     * @param position 位置
     * @return Tab的View
     */
    fun getTab(position: Int): View? {
        return if (tabsLayout != null && tabsLayout!!.childCount > position) {
            tabsLayout!!.getChildAt(position)
        } else {
            null
        }
    }

    /**
     * 滚动到指定的位置
     */
    private fun scrollToChild(position: Int, offset: Int) {
        val tabsLayout = getTabsLayout()
        if (tabsLayout != null && tabsLayout.childCount > 0 && position < tabsLayout.childCount) {
            val view = tabsLayout.getChildAt(position)
            if (view != null) {
                //计算新的X坐标
                var newScrollX = view.left + offset - getLeftMargin(view)
                if (position > 0 || offset > 0) {
                    newScrollX -= width / 2 - getOffset(view.width) / 2
                }

                //如果同上次X坐标不一样就执行滚动
                if (newScrollX != lastScrollX) {
                    lastScrollX = newScrollX
                    scrollTo(newScrollX, 0)
                }
            }
        }
    }

    private fun getLeftMargin(view: View): Int {
        val params = view.layoutParams
        return (params as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0
    }

    private fun getRightMargin(view: View): Int {
        val params = view.layoutParams
        return (params as? ViewGroup.MarginLayoutParams)?.rightMargin ?: 0
    }

    /**
     * 获取偏移量
     */
    private fun getOffset(newOffset: Int): Int {
        if (lastOffset < newOffset) {
            if (start) {
                lastOffset += 1
                return lastOffset
            } else {
                start = true
                lastOffset += 1
                return lastOffset
            }
        }
        if (lastOffset > newOffset) {
            if (start) {
                lastOffset -= 1
                return lastOffset
            } else {
                start = true
                lastOffset -= 1
                return lastOffset
            }
        } else {
            start = true
            lastOffset = newOffset
            return lastOffset
        }
    }

    /**
     * 选中指定位置的TAB
     */
    private fun selectedTab(newSelectedTabPosition: Int) {
        val tabsLayout = getTabsLayout()
        if (newSelectedTabPosition > -1 && tabsLayout != null && newSelectedTabPosition < tabsLayout.childCount) {
            var w = 0
            val size = tabsLayout.childCount
            while (w < size) {
                val tabView = tabsLayout.getChildAt(w)
                tabView.isSelected = w == newSelectedTabPosition
                w++
            }
        }
    }

    /**
     * 设置ViewPager
     *
     * @param viewPager ViewPager
     */
    fun setViewPager(viewPager: ViewPager) {
        if (disableViewPager) return
        this.viewPager = viewPager
        this.viewPager!!.addOnPageChangeListener(pageChangedListener)
        setTabClickEvent()
        requestLayout()
    }

    /**
     * 设置Page切换监听器
     *
     * @param onPageChangeListener Page切换监听器
     */
    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener
    }

    /**
     * 设置是否充满屏幕
     *
     * @param allowWidthFull true：当内容的宽度无法充满屏幕时，自动调整每一个Item的宽度以充满屏幕
     */
    fun setAllowWidthFull(allowWidthFull: Boolean) {
        this.allowWidthFull = allowWidthFull
        requestLayout()
    }

    /**
     * 设置滑块图片
     */
    fun setSlidingBlockDrawable(slidingBlockDrawable: Drawable) {
        this.slidingBlockDrawable = slidingBlockDrawable
        requestLayout()
    }

    /**
     * 获取滑块图片
     */
    fun getSlidingBlockDrawable(): Drawable? {
        return slidingBlockDrawable
    }

    /**
     * 设置是否禁止拉伸滑块图片
     *
     * @param disableTensileSlidingBlock 是否禁止拉伸滑块图片
     */
    fun setDisableTensileSlidingBlock(disableTensileSlidingBlock: Boolean) {
        this.disableTensileSlidingBlock = disableTensileSlidingBlock
        invalidate()
    }

    /**
     * 设置Tab点击监听器
     *
     * @param onClickTabListener Tab点击监听器
     */
    fun setOnClickTabListener(onClickTabListener: OnClickTabListener) {
        this.onClickTabListener = onClickTabListener
    }

    /**
     * 设置TAB双击监听器
     *
     * @param onDoubleClickTabListener TAB双击监听器
     */
    fun setOnDoubleClickTabListener(onDoubleClickTabListener: OnDoubleClickTabListener) {
        this.onDoubleClickTabListener = onDoubleClickTabListener
    }

    /**
     * 设置不使用ViewPager
     *
     * @param disableViewPager 不使用ViewPager
     */
    fun setDisableViewPager(disableViewPager: Boolean) {
        this.disableViewPager = disableViewPager
        if (viewPager != null) {
            viewPager!!.removeOnPageChangeListener(onPageChangeListener!!)
            viewPager = null
        }
        requestLayout()
    }

    /**
     * 设置TabView生成器
     *
     * @param tabViewFactory TabView生成器
     */
    fun setTabViewFactory(tabViewFactory: TabViewFactory) {
        this.tabViewFactory = tabViewFactory

        reset()

        viewTreeObserver.addOnGlobalLayoutListener(setSelectedTabListener)
    }

    /**
     * 设置底线的颜色
     *
     * @param bottomLineColor 底线的颜色
     */
    fun setBottomLineColor(bottomLineColor: Int) {
        this.bottomLineColor = bottomLineColor
        if (bottomLinePaint != null) {
            bottomLinePaint!!.color = bottomLineColor
        }
        postInvalidate()
    }

    /**
     * 设置底线的高度
     *
     * @param bottomLineHeight 底线的高度
     */
    fun setBottomLineHeight(bottomLineHeight: Int) {
        this.bottomLineHeight = bottomLineHeight
        postInvalidate()
    }

    /**
     * Tab点击监听器
     */
    interface OnClickTabListener {
        fun onClickTab(tab: View, index: Int)
    }

    /**
     * Tab双击监听器
     */
    interface OnDoubleClickTabListener {
        fun onDoubleClickTab(view: View?, index: Int)
    }

    /**
     * TabView生成器
     */
    interface TabViewFactory {
        /**
         * 添加tab
         *
         * @param parent              父View
         * @param currentItemPosition 当前选中的位置
         */
        fun addTabs(parent: ViewGroup?, currentItemPosition: Int)
    }

    private inner class PageChangedListener : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            selectedTab(position)
            if (onPageChangeListener != null) {
                onPageChangeListener!!.onPageSelected(position)
            }
        }

        override fun onPageScrolled(nextPagePosition: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val tabsLayout = getTabsLayout()
            if (nextPagePosition < tabsLayout!!.childCount) {
                val view = tabsLayout.getChildAt(nextPagePosition)
                if (view != null) {
                    currentPosition = nextPagePosition
                    currentPositionOffset = positionOffset
                    scrollToChild(
                        nextPagePosition,
                        (positionOffset * (view.width + getLeftMargin(view) + getRightMargin(view))).toInt()
                    )
                    invalidate()
                }
            }
            if (onPageChangeListener != null) {
                onPageChangeListener!!.onPageScrolled(nextPagePosition, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(arg0: Int) {
            if (onPageChangeListener != null) {
                onPageChangeListener!!.onPageScrollStateChanged(arg0)
            }
        }
    }

    private inner class TabViewClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            val index = v.tag as Int
            if (onClickTabListener != null) {
                onClickTabListener!!.onClickTab(v, index)
            }
            if (viewPager != null) {
                viewPager!!.setCurrentItem(index, true)
            }
        }
    }

    private inner class SetSelectedTabListener : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                viewTreeObserver.removeGlobalOnLayoutListener(this)
            } else {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

            val tabViewGroup = getTabsLayout()
            if (tabViewGroup != null) {
                currentPosition = if (viewPager != null) viewPager!!.currentItem else 0
                if (!disableViewPager) {
                    scrollToChild(currentPosition, 0)
                    selectedTab(currentPosition)
                }
            }
        }
    }

    private inner class DoubleClickGestureDetector(context: Context) : GestureDetector.SimpleOnGestureListener(),
        View.OnTouchListener {
        private val gestureDetector: GestureDetector
        private var currentView: View? = null

        init {
            gestureDetector = GestureDetector(context, this)
            gestureDetector.setOnDoubleTapListener(this)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (onDoubleClickTabListener != null) {
                onDoubleClickTabListener!!.onDoubleClickTab(currentView, currentView!!.tag as Int)
                return true
            } else {
                return false
            }
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            currentView = v
            return gestureDetector.onTouchEvent(event)
        }
    }
}