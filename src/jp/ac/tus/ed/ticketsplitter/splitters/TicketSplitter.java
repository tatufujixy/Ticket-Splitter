package jp.ac.tus.ed.ticketsplitter.splitters;

import java.util.List;

import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;
import jp.ac.tus.ed.ticketsplitter.Ticket;

public class TicketSplitter {
	class Node{
		Station sta;
		int dist;
		Node back;
	}
	
	public static List<Ticket> getOptimizedTickets(Station start,Station dest){
	//start駅からdest駅までの最安値の分割パターンの乗車券リストを返す
	//中間発表までには、ダイクストラ法で経路を求めたあと、その運賃を求めて返すこととする(戻り値のリストの長さは1)
		return null;
	}
	public static Route dijkstra(Station start,Station dest){
	//ダイクストラ法により、start駅からdest駅までの経路を求める
	//FareCalculatorから呼ばれるかも
		
		//最短距離未確定リスト
		//確定済みリスト
		
		//destnode dist0
		//未確定リストにdist0を入れる
		
		
		//SyainComparatorクラス
		//最短距離未確定リストをコスト順に並べる
		//未確定リストから、コストの最低のものを確定済みリストに移動、その隣の駅を取り出す
		//取り出した駅が確定済みリストにあれば無視、未確定リストにあれば、距離を計算しなおし、短ければ距離・直前の駅を更新
		
		//上の処理を、start駅が確定済みリストに入るまで繰り返す
		
		
		
		
		
		//処理内容、隣接している駅名をstationをもとに呼び出し、それまでのdistanceと隣接駅までの距離を足したものを
		//いれる。処理済みリストにその駅名があるならそのノードは削除。ないなら処理待ちリストに。
		//処理済みかどうかの判定にdest駅と一致しているかもいれておく。一致したら終了。
		//距離を算出。
		
		return null;
	}
	
	
}
