package com.loan.icreditapp.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.loan.icreditapp.R
import com.loan.icreditapp.base.BaseFragment
import com.loan.icreditapp.ui.profile.fragment.AddProfile1Fragment
import com.loan.icreditapp.ui.profile.fragment.AddProfile2Fragment
import com.loan.icreditapp.ui.profile.fragment.AddProfile3Fragment

class MyProfileFragment : BaseFragment() {

    private var tabLayout : TabLayout? = null
    private var vp : ViewPager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_myprofile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout =  view.findViewById(R.id.tab_layout_myprofile)
        vp =  view.findViewById(R.id.vp_myprofile)
        initializeView()
    }

    private fun initializeView() {
        val pageAdapter = PagerAdapter(childFragmentManager) //初始化FragmentPagerAdapter
        vp?.setAdapter(pageAdapter) //设置viewPager的adapter
        tabLayout?.setupWithViewPager(vp) //将tabLayout和viewPager关联

    }


    private class PagerAdapter : FragmentPagerAdapter {

        private var profile1Fragment: AddProfile1Fragment? = null
        private var profile2Fragment: AddProfile2Fragment? = null
        private var profile3Fragment: AddProfile3Fragment? = null

        constructor(fm : FragmentManager) : super(fm){

        }

        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment {
            if (position == 0){
                if (profile1Fragment == null){
                    profile1Fragment = AddProfile1Fragment()
                    profile1Fragment!!.setShowMode()
                }
                return profile1Fragment as AddProfile1Fragment
            } else if (position == 1){
                if (profile2Fragment == null){
                    profile2Fragment = AddProfile2Fragment()
                    profile2Fragment!!.setShowMode()
                }
                return profile2Fragment as AddProfile2Fragment
            } else {
                if (profile3Fragment == null){
                    profile3Fragment = AddProfile3Fragment()
                    profile3Fragment!!.setShowMode()
                }
                return profile3Fragment as AddProfile3Fragment
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            if (position == 0){
                return "Basic info"
            } else if (position == 1){
                return "Referee info"
            } else {
                return "Details info"
            }
        }
    }
}