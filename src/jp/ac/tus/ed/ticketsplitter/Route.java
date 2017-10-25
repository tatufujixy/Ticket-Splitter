package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Route {
	
	private List<Station> stationList=new ArrayList<Station>();
	private List<Line> lineList=new ArrayList<Line>();
	private BigDecimal distance=BigDecimal.ZERO;
	
	//コンストラクタはTicketSplitter(、FareCalculator)で呼ばれる
	public Route(Station start){
		stationList.add(start);
	}
	
	void addRoute(Line line,Station sta){
		stationList.add(sta);
		lineList.add(line);
		
		BigDecimal between=stationList.get(stationList.size()-1).getDistance(line.getId())
				.subtract(sta.getDistance(line.getId())).abs();
		distance=distance.add(between);
	}
	
	public List<Station> getStationsList(){
	//乗車駅から降車駅までの駅のリスト
		return stationList;
	}
	public List<Line> getLinesList(){
	//乗車駅から降車駅までの駅間の路線リスト
		return lineList;
	}
	/*
	s=getStationsList(), l=getLinesList();
	として、経由順は、
	s.get(0)駅 → l.get(0)線 → s.get(1)駅 → l.get(1)線 → ・・・ → l.get(n-1)線 → s.get(n)駅
	
	*/
	
	
	public BigDecimal getDistance(){
	//この経路の営業キロ
		return distance;
	}
	public List<String> via(){
	//この経路の経由路線を文字列リストで返す(GUIから呼ばれる)
		
		List<String> list=new ArrayList<String>();
		
		String via=null;
		
		for(int i=0;i<lineList.size();i++){
			if(via==null || !via.equals(lineList.get(i).getName())){
				list.add(stationList.get(i).getName());
				list.add(lineList.get(i).getName());
				via=lineList.get(i).getName();
			}
		}
		list.add(stationList.get(stationList.size()-1).getName());
		return list;
	}
}
