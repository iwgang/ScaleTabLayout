package cn.iwgang.scaletablayout_demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.iwgang.scaletablayout_demp.R
import kotlinx.android.synthetic.main.frg_content.*

class ContentFragment : Fragment() {
    private lateinit var mTitle: String

    companion object {
        private const val INTENT_KEY_TITLE = "intent_key_title"

        fun newInstance(title: String): ContentFragment {
            return ContentFragment().apply {
                val bundle = Bundle()
                bundle.putString(INTENT_KEY_TITLE, title)
                arguments = bundle
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mTitle = arguments!!.getString(INTENT_KEY_TITLE)
        return LayoutInflater.from(context).inflate(R.layout.frg_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_content.text = mTitle
    }

}