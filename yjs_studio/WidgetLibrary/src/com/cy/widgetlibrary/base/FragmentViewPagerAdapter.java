package com.cy.widgetlibrary.base;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * zqc
 *<p>2015-2-10</p>
 *<p>Fragment列表的适配器</p>
 */
public class FragmentViewPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Fragment> mFragmentsList = null;

	public FragmentViewPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragmentsList) {
		super(fm);
		// TODO Auto-generated constructor stub
		mFragmentsList = fragmentsList;
	}

	@Override
	public int getCount() {
		return mFragmentsList.size();
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentsList.get(position);
	}

}
