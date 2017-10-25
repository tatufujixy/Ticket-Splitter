package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Station {
	//特定都区市内
	public static final int CITY_SAPPORO=1;
	public static final int CITY_TOKYO=2;
	public static final int CITY_YOKOHAMA=3;
	//・・・
	
	//特定区間
	public static final int SPECIFIC_TOKYO=20;
	public static final int SPECIFIC_OSAKA=21;
	
	//近郊区間
	public static final int SUBURB_TOKYO=31;
	public static final int SUBURB_OSAKA=32;
	//・・・
	
	
	private String name;
	private int id;
	private Map<Integer,Integer> stationIdOfLine=new HashMap<Integer,Integer>();//<line_id,station_id>
	private Map<Integer,List<Integer>> nextStationId=new HashMap<Integer,List<Integer>>();
	private int specificWardsAndCities=0;
	private boolean inYamanoteLine;
	private boolean inOsakaKanjoLine;
	private int specificArea=0;
	private int suburbArea=0;
	private Map<Integer,BigDecimal> distance=new HashMap<Integer,BigDecimal>();
	
	
	
	//コンストラクタはDatabaseで呼ばれる
	
	void setName(String s){
		name=s;
	}
	public String getName(){
		return name;
	}
	void setStationId(int i){
		id=i;
	}
	public int getStationId(){
	//駅ID。乗換可能駅であればその共通のID、すなわちDBのid_station
		return id;
	}
	void setStationIdOfLine(int line,int station){
		stationIdOfLine.put(line, station);
	}
	public int getStationIdOfLine(int lineId){
	//乗換可能駅での、路線IDがlineIdのレコードでのID
	//単独駅では必ずgetStationId()と同じ値になる
		return stationIdOfLine.get(lineId);
	}
	void addNextStationId(int line,int nextStation){
		if(nextStation==0){
			return;
		}
		if(!nextStationId.containsKey(line)){
			nextStationId.put(line, new ArrayList<Integer>(2));
		}
		nextStationId.get(line).add(nextStation);
	}
	public Map<Integer,List<Integer>> nextStationId(){
	//隣の駅への路線idと駅idの組
	//次の駅に行くのに経由する路線idをKeyに、次の駅の駅idをValueにして入れている。すなわち、Map<路線id,駅id>
		return nextStationId;
	}
	void setSpecificWardsAndCities(int s){
		specificWardsAndCities=s;
	}
	public int getSpecificWardsAndCities(){
	//特定都区市内ならその値を返す。そうでないなら0を返す
		return specificWardsAndCities;
	}
	void setIsInYamanoteLine(boolean b){
		inYamanoteLine=b;
	}
	public boolean isInYamanoteLine(){
	//山の手線内ならtrue
		return inYamanoteLine;
	}
	void setInOsakaKanjoLine(boolean b){
		inOsakaKanjoLine=b;
	}
	public boolean isInOsakaKanjoLine(){
	//大阪環状線内ならtrue
		return inOsakaKanjoLine;
	}
	void setSpecificArea(int i){
		specificArea=i;
	}
	public int getSpecificArea(){
	//特定区間内ならその値を返す。そうでないなら0を返す
		return specificArea;
	}
	void setSuburbArea(int i){
		suburbArea=i;
	}
	public int getSuburbArea(){
	//近郊区間内ならその値を返す。そうでないなら0を返す
		return suburbArea;
	}
	void setDistance(int line, BigDecimal bd){
		distance.put(line, bd);
	}
	public BigDecimal getDistance(int line){
	//路線idがlineの路線の、起点駅からこの駅までの営業キロを返す
		return distance.get(line);
	}
	public List<Integer> getLineId(){
	//この駅が属する路線idのリストを返す
		return new ArrayList<Integer>(stationIdOfLine.keySet());
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Station)){
			return false;
		}
		Station sta=(Station)o;
		return sta.id==this.id;
	}
}