package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Route {
	
	private List<Station> stationList=new ArrayList<Station>(100);
	private List<Line> lineList=new ArrayList<Line>(100);
	private BigDecimal distance=BigDecimal.ZERO;
	
	//コンストラクタはTicketSplitter(、FareCalculator)で呼ばれる
	public Route(Route r){
		stationList.addAll(r.stationList);
		lineList.addAll(r.lineList);
		distance=r.distance;
	}
	public Route(List<Station> sta,List<Line> line){
		stationList.addAll(sta);
		lineList.addAll(line);
		
		for(int i=1;i<stationList.size();i++){
			BigDecimal between=stationList.get(i-1).getDistance(lineList.get(i-1).getId())
					.subtract(stationList.get(i).getDistance(lineList.get(i-1).getId())).abs();
			distance=distance.add(between);
		}
	}
	public Route(Station start){
		stationList.add(start);
	}
	
	
	
	// FareCalculatorで、経路を編集するために必要なメソッド    11/10
	public Route divideHead(int i){
	    return new Route(stationList.subList(0,i+1),lineList.subList(0,i));
	}
	public Route divideTail(int i){
		//先頭からi番目の駅から下車駅までの経路を取り出す
		 return new Route(stationList.subList(i,stationList.size()),lineList.subList(i,lineList.size()));
	}
	public void join(Route r){
		stationList.addAll(r.getStationsList().subList(1,r.getStationsList().size()));
		lineList.addAll(r.getLinesList());
		
		distance=distance.add(r.distance);
		//このRouteの後ろに、引数rの経路を連結する。
		//この経路の下車駅と、rの乗車駅が同じであることが条件
	}
	
	
	
	public void addRoute(Line line,Station sta){
		BigDecimal between=stationList.get(stationList.size()-1).getDistance(line.getId())
				.subtract(sta.getDistance(line.getId())).abs();
		distance=distance.add(between);
		
		stationList.add(sta);
		lineList.add(line);
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
	
	public String toString(){
		String str="";
		for(String s : via()){
			str+=s+" ";
		}
		return str;
	}
	
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
