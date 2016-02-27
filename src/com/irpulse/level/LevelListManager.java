package com.irpulse.level;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.util.Log;

public class LevelListManager {
	public static ArrayList<LevelListData> list = new ArrayList<LevelListData>();
	static boolean isLoaded = false;
	public static int ListCount = 0;
	public static int levelCount = 0;
	// must be inited
	public static String baseDir = null;
	public static synchronized void loadLevelListData() {
		if (isLoaded)
			return;
		//
		
//		for(int d : drawables){
//			LevelListData l = new LevelListData(d);
//			list.add(l);
//		}
		// LevelListData level6 = new LevelListData(R.drawable.bg5);
		// level6.addButton((220), (120), (140), (200));
		//
		// list.add(level6);
		//
		// LevelListData level5 = new LevelListData(R.drawable.bg4, 2);
		//
		// level5.addButton((125), (755), (120), (115));
		// level5.addButton((600), (560), (185), (215));
		// level5.addButton((820), (375), (170), (155));
		// level5.addButton((610.), (55), (230), (215));
		//
		// list.add(level5);
		//
		// LevelListData level4 = new LevelListData(R.drawable.bg3, 2);
		//
		// level4.addButton((620.), (680.), (215.), (210.));
		// level4.addButton((620.), (360.), (215.), (210.));
		//
		// level4.addButton((620.), (30.), (215.), (210.));
		// list.add(level4);
		//
		// LevelListData level3 = new LevelListData(R.drawable.bg2);
		// level3.addButton((160.), (160.), (250.), (250.));
		// list.add(level3);
		//
		// LevelListData level2 = new LevelListData(R.drawable.bg1);
		// level2.addButton((488.), (26.), (270.), (334.));
		// list.add(level2);
		//
		// LevelListData level1 = new LevelListData(R.drawable.bg0);
		// list.add(level1);

		// TODO undo the comment
		loadFromFile();

		isLoaded = true;

	}
	
	public static int getLastAnsweredPlace(){
		int res = 0;
		
		
		for(int i = 0 ; i <  list.size() ; i++){
			LevelListData levelListData = list.get(i);
			for(int levelid : levelListData.levelIDs)
				if(levelid == LevelManager.getSolvedCount())
					return i;
		}
		
		
		return list.size() - 1;
	}

	public static void loadFromFile() {

		File baseFile = new File(baseDir);
		int max = 0;
		for (String l : baseFile.list()) {
			if (l.contains("bg")) {
				Integer intTemp = Integer.parseInt(l.substring(3, l.length() -4 ));
				if (intTemp > max)
					max = intTemp;
			}
		}


		int last = max + 1;
		int i = ListCount + 1;
		for (; i < last; i++) {
			String path = baseDir;
			LevelListData temp = new LevelListData(path);
			list.add(temp);
		}

	}
}
