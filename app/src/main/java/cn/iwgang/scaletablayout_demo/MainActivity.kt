package cn.iwgang.scaletablayout_demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import cn.iwgang.scaletablayout_demp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mTabs: List<String> by lazy {
        arrayListOf("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6", "Tab7", "Tab8", "Tab9", "Tab10")
    }
    private val mFragments: List<Fragment> by lazy {
        arrayListOf<Fragment>().apply { mTabs.forEach { add(ContentFragment.newInstance(it)) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        vp_content.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int) = mFragments[position]

            override fun getCount() = mFragments.size

            override fun getPageTitle(position: Int) = mTabs[position]
        }
        cv_scaleTabLayout.setViewPager(vp_content)
    }

}
