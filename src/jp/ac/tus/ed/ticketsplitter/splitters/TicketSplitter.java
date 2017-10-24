package jp.ac.tus.ed.ticketsplitter.splitters;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.math.BigDecimal;

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
		
		//最短距離未確定リスト unsettled 未確定の意
		ArrayList<StaNode> unsettled = new ArrayList<StaNode>();
		//確定済みリスト committed 確定済み
		ArrayList<StaNode> committed = new ArrayList<StaNode>();
		//destnode dist0	
		//未確定リストにdist0を入れる
		 unsettled.add(new StaNode(dest,new BigDecimal("0,0"),null));
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

//nodeクラス
class StaNode{
	Station sta;
	BigDecimal dist;
	StaNode back;
	//コンストラクタ
	public StaNode(Station sta,BigDecimal dist,StaNode back){
		this.sta = sta;
		this.dist = dist;
		this.back = back;
	}
	//距離を持ってくる関数
	public BigDecimal getdist(){
		return this.dist;
	}
	
}

//リスト並べ替えのためのクラス
class ListComparator implements Comparator<StaNode> {

    //比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
    public int compare(StaNode a, StaNode b) {
        BigDecimal no1 = a.getdist();
        BigDecimal no2 = b.getdist();

        //こうすると昇順でソートされる
        //no1>no2 なら1
        //no1<no2 なら-1 no1=no2 なら0が返値になる
        return no1.compareTo(no2);
        
    }

}
