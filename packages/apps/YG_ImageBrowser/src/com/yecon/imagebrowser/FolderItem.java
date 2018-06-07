package com.yecon.imagebrowser;

import java.util.ArrayList;
import java.util.List;


public class FolderItem {

	public FolderItem() {
		// TODO Auto-generated constructor stub
		folder_id = -1;
		amount = 0;
		folder = "unknown";
		files = new ArrayList<String>();
	}
	
	public int folder_id;
	public int amount;
	public String folder;
	public List<String> files;
}
