package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.List;

public class Route {
	//コンストラクタはTicketSplitter(、FareCalculator)で呼ばれる
	
	
	
	public List<Station> getStationsList(){
	//乗車駅から降車駅までの駅のリスト
		return null;
	}
	public List<Line> getLinesList(){
	//乗車駅から降車駅までの駅間の路線リスト
		return null;
	}
	/*
	s=getStationsList(), l=getLinesList();
	として、経由順は、
	s.get(0)駅 → l.get(0)線 → s.get(1)駅 → l.get(1)線 → ・・・ → l.get(n-1)線 → s.get(n)駅
	
	*/
	
	
	public BigDecimal getDistance(){
	//この経路の営業キロ
		return null;
	}
	public List<String> via(){
	//この経路の経由路線を文字列リストで返す(GUIから呼ばれる)
		return null;
	}
}
