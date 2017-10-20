package jp.ac.tus.ed.ticketsplitter.splitters;

import java.util.List;

import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;
import jp.ac.tus.ed.ticketsplitter.Ticket;

public class TicketSplitter {
	public static List<Ticket> getOptimizedTickets(Station start,Station dest){
	//start駅からdest駅までの最安値の分割パターンの乗車券リストを返す
	//中間発表までには、ダイクストラ法で経路を求めたあと、その運賃を求めて返すこととする(戻り値のリストの長さは1)
		return null;
	}
	public static Route dijkstra(Station start,Station dest){
	//ダイクストラ法により、start駅からdest駅までの経路を求める
	//FareCalculatorから呼ばれるかも
		return null;
	}
	
	
}
