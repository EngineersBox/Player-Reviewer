package MethodLib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

public class DataSet implements DataManager {

	private String name;
	private String rank;
	private List<Float> criteria;
	private Location plotloc;
	private Integer totalratings;
	private List<String> ratings;
	
	public DataSet(String name, String rank, List<Float> criteria, Location plotloc, Integer totalratings, List<String> ratings) {
		this.name = name;
		this.rank = rank;
		this.criteria = criteria;
		this.plotloc = plotloc;
		this.totalratings = totalratings;
		this.ratings = ratings;
	}
	
	public DataSet(ResultSet result) throws SQLException {
		result.next();
		this.name = result.getString("Name");
		this.rank = result.getString("rank");
		this.plotloc = Lib.getLoc(result.getString("plotloc"));
		this.totalratings = Integer.parseInt(result.getString("totalratings"));
		this.ratings = Lib.ratingListCreator(result.getString("ratinglist"));
		
		int cStart = result.findColumn("rank") + 1;
		int cEnd = result.findColumn("totalratings");
		this.criteria = new ArrayList<Float>();
		for (int i = cStart ; i < cEnd; i++) {
			this.criteria.add(result.getFloat(i));
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String newname) {
		this.name = newname;
	}
	
	@Override
	public String getRank() {
		return this.rank;
	}
	
	public void setRank(String newrank) {
		this.rank = newrank;
	}
	
	@Override
	public List<Float> getCriteria() {
		return this.criteria;
	}
	
	public List<String> getCriteriaString() {
		List<String> retval = new ArrayList<String>();
		for (Float cCriteria : this.criteria) {
			retval.add(Float.toString(cCriteria));
		}
		return retval;
	}
	
	public void setCriteria(List<Float> newcriteria) {
		this.criteria = newcriteria;
	}
	
	@Override
	public Location getPlotLoc() {
		return this.plotloc;
	}
	
	public Map<String, Object> getLocParted() {
		Map<String, Object> locParted = new HashMap<>();
		locParted.put("world", this.plotloc.getWorld());
		locParted.put("x", this.plotloc.getX());
		locParted.put("y", this.plotloc.getY());
		locParted.put("z", this.plotloc.getZ());
		return locParted;
	}
	
	public String getLocString() {
		String locString = ":w:" + this.plotloc.getWorld().toString() + ":x:" + this.plotloc.getX() + ":y:" + this.plotloc.getY() + ":z:" + this.plotloc.getZ();
		return locString;
	}
	
	public void setPlotLoc(Location newloc) {
		this.plotloc = newloc;
	}
	
	@Override
	public Integer getTotalRatings() {
		return this.totalratings;
	}
	
	public void setTotalRatings(Integer newTotal) {
		this.totalratings = newTotal;
	}
	
	@Override
	public List<String> getRatings() {
		return this.ratings;
	}
	
	public void setRatings(List<String> newratings) {
		this.ratings = newratings;
	}
	
}