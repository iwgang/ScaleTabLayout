package cn.iwgang.scaletablayout

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView


/**
 * ScaleTabLayout
 * Created by iWgang on 18/9/8.
 * https://github.com/iwgang/ScaleTabLayout
 */
class ScaleTabLayout : HorizontalScrollView {
    private lateinit var mLlContainer: LinearLayout
    private lateinit var mViewPager: ViewPager

    private var mUnSelTextColor: Int = 0
    private var mSelTextColor: Int = 0
    private var mUnSelTextSize: Float = 0.0f
    private var mSelTextSize: Float = 0.0f
    private var mTabSpacing: Float = 0.0f
    private var mLeftMargin: Float = 0.0f
    private var mRightMargin: Float = 0.0f
    private var isSelTextBold: Boolean = true

    private var mCurrentPagerPosition: Int = 0
    private var mSelTextScale: Float = 1.3f
    private var isClickAnimation: Boolean = false

    companion object {
        const val DEF_UN_SEL_TEXT_COLOR = Color.GRAY
        const val DEF_SEL_TEXT_COLOR = Color.BLACK
    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttr(attrs)
        initViews()
    }

    private fun initAttr(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleTabLayout)
        mUnSelTextColor = ta.getColor(R.styleable.ScaleTabLayout_stl_unSelTextColor, DEF_UN_SEL_TEXT_COLOR)
        mSelTextColor = ta.getColor(R.styleable.ScaleTabLayout_stl_selTextColor, DEF_SEL_TEXT_COLOR)
        mUnSelTextSize = ta.getDimension(R.styleable.ScaleTabLayout_stl_unSelTextSize, resources.getDimension(R.dimen.stl_un_sel_text_size))
        mSelTextSize = ta.getDimension(R.styleable.ScaleTabLayout_stl_selTextSize, resources.getDimension(R.dimen.stl_sel_text_size))
        mTabSpacing = ta.getDimension(R.styleable.ScaleTabLayout_stl_tabSpacing, resources.getDimension(R.dimen.stl_tab_spacing))
        val defLeftRightMargin = resources.getDimension(R.dimen.stl_left_right_margin)
        mLeftMargin = ta.getDimension(R.styleable.ScaleTabLayout_stl_leftMargin, defLeftRightMargin)
        mRightMargin = ta.getDimension(R.styleable.ScaleTabLayout_stl_rightMargin, defLeftRightMargin)
        isSelTextBold = ta.getBoolean(R.styleable.ScaleTabLayout_stl_isSelTextBold, true)

        mSelTextScale = mSelTextSize / mUnSelTextSize
        ta.recycle()
    }

    private fun initViews() {
        mLlContainer = LinearLayout(context)
        mLlContainer.orientation = LinearLayout.HORIZONTAL
        mLlContainer.gravity = Gravity.CENTER_VERTICAL
        addView(mLlContainer)
    }

    private fun clickTab(position: Int) {
        if (mCurrentPagerPosition == position) return

        val curPagerPosition = mCurrentPagerPosition

        mViewPager.currentItem = position
        isClickAnimation = true

        ValueAnimator.ofFloat(1.0f, 0f).setDuration(200).apply {
            addUpdateListener {
                val offsetValue = it.animatedValue as Float
                inScale(position, offsetValue)
                outScale(curPagerPosition, offsetValue)
            }
        }.start()
    }

    private fun inScale(position: Int, offsetValue: Float) {
        (mLlContainer.getChildAt(position) as TextView).apply {
            val scale = mSelTextScale + (1.0f - mSelTextScale) * offsetValue
            scaleX = scale
            scaleY = scale
            setTextColor(argbEvaluate(offsetValue, mSelTextColor, mUnSelTextColor))
        }
    }

    private fun outScale(position: Int, offsetValue: Float) {
        (mLlContainer.getChildAt(position) as TextView).apply {
            val scale = 1.0f + (mSelTextScale - 1.0f) * offsetValue
            scaleX = scale
            scaleY = scale
            setTextColor(argbEvaluate(offsetValue, mUnSelTextColor, mSelTextColor))
        }
    }

    fun setViewPager(viewPager: ViewPager) {
        mViewPager = viewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (isClickAnimation) return

                inScale(position, positionOffset)

                if (position + 1 < mLlContainer.childCount) {
                    outScale(position + 1, positionOffset)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    isClickAnimation = false
                }
            }

            override fun onPageSelected(position: Int) {
                val curTvTab = mLlContainer.getChildAt(position) as TextView
                if (isSelTextBold) curTvTab.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                smoothScrollTo(((curTvTab.left + curTvTab.width / 2) - width * 0.5).toInt(), 0)

                if (isSelTextBold) {
                    (mLlContainer.getChildAt(mCurrentPagerPosition) as TextView).apply {
                        typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    }
                }

                mCurrentPagerPosition = position
            }
        })

        for (pos in 0 until viewPager.adapter!!.count) {
            val tvTab = TextView(context).apply {
                text = viewPager.adapter!!.getPageTitle(pos)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnSelTextSize)
                setTextColor(mUnSelTextColor)
                layoutParams = LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    when (pos) {
                        0 -> {
                            leftMargin = mLeftMargin.toInt()
                            rightMargin = mTabSpacing.toInt()
                            if (isSelTextBold) typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        }
                        viewPager.adapter!!.count - 1 -> rightMargin = mRightMargin.toInt()
                        else -> rightMargin = mTabSpacing.toInt()
                    }
                }
                setOnClickListener { clickTab(pos) }
            }
            mLlContainer.addView(tvTab)
        }
    }


    private fun argbEvaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = (startValue shr 24 and 0xff) / 255.0f
        var startR = (startValue shr 16 and 0xff) / 255.0f
        var startG = (startValue shr 8 and 0xff) / 255.0f
        var startB = (startValue and 0xff) / 255.0f

        val endA = (endValue shr 24 and 0xff) / 255.0f
        var endR = (endValue shr 16 and 0xff) / 255.0f
        var endG = (endValue shr 8 and 0xff) / 255.0f
        var endB = (endValue and 0xff) / 255.0f

        // convert from sRGB to linear
        startR = Math.pow(startR.toDouble(), 2.2).toFloat()
        startG = Math.pow(startG.toDouble(), 2.2).toFloat()
        startB = Math.pow(startB.toDouble(), 2.2).toFloat()

        endR = Math.pow(endR.toDouble(), 2.2).toFloat()
        endG = Math.pow(endG.toDouble(), 2.2).toFloat()
        endB = Math.pow(endB.toDouble(), 2.2).toFloat()

        // compute the interpolated color in linear space
        var a = startA + fraction * (endA - startA)
        var r = startR + fraction * (endR - startR)
        var g = startG + fraction * (endG - startG)
        var b = startB + fraction * (endB - startB)

        // convert back to sRGB in the [0..255] range
        a *= 255.0f
        r = Math.pow(r.toDouble(), 1.0 / 2.2).toFloat() * 255.0f
        g = Math.pow(g.toDouble(), 1.0 / 2.2).toFloat() * 255.0f
        b = Math.pow(b.toDouble(), 1.0 / 2.2).toFloat() * 255.0f

        return Math.round(a) shl 24 or (Math.round(r) shl 16) or (Math.round(g) shl 8) or Math.round(b)
    }
}