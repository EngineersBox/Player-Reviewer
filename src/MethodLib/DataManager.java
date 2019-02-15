package MethodLib;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public interface DataManager {

	//General Methods
	public default List<Integer> stringToInt(List<String> stringlist) {
		List<Integer> retList = new ArrayList<Integer>();
		for (String cStr : stringlist) {
			retList.add(Integer.parseInt(cStr));
		}
		return retList;
	}
	
	public default List<Float> stringToFlt(List<String> stringlist) {
		List<Float> retList = new ArrayList<Float>();
		for (String cStr : stringlist) {
			retList.add(Float.parseFloat(cStr));
		}
		return retList;
	}
	
	public default List<String> IntegerToStr(List<Integer> intlist) {
		List<String> retList = new ArrayList<String>();
		for (Integer cInt : intlist) {
			retList.add(cInt.toString());
		}
		return retList;
	}
	
	//DataSet Methods
	public String getName();
	public String getRank();
	public List<Float> getCriteria();
	public Location getPlotLoc();
	public Integer getTotalRatings();
	public List<String> getRatings();
	
	//PlotSet Methods
	
}
