package com.autochips.dvr;

import java.util.Comparator;

public class SortComparator implements Comparator<Object>{
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		String s1 = tools.getOnlyFile((String)arg0);
		String s2 = tools.getOnlyFile((String)arg1);
		return s1.compareTo(s2);
	}
}
