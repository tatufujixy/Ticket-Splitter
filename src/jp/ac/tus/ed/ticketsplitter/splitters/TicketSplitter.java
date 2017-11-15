package jp.ac.tus.ed.ticketsplitter.splitters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import jp.ac.tus.ed.ticketsplitter.Database;
import jp.ac.tus.ed.ticketsplitter.FareCalculator;
import jp.ac.tus.ed.ticketsplitter.Line;
import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;
import jp.ac.tus.ed.ticketsplitter.Ticket;

public class TicketSplitter {
	
	
	public static List<Ticket> getOptimizedTickets(Station start,Station dest){
	//start駅からdest駅までの最安値の分割パターンの乗車券リストを返す
	//中間発表までには、ダイクストラ法で経路を求めたあと、その運賃を求めて返すこととする(戻り値のリストの長さは1)
		Route dijkstraroute = dijkstra(start,dest);
		Ticket ti=new FareCalculator().calculate(dijkstraroute);
		List<Ticket> list=new ArrayList<Ticket>();
		list.add(ti);
		return list;
	}
	
	public static Route dijkstra(Station start,Station dest){
		int startid = start.getStationId();
	//ダイクストラ法により、start駅からdest駅までの経路を求める
	//FareCalculatorから呼ばれるかも
		//処理中の駅
		StaNode processingStaNode;
		Station processingSta;
		List<Integer> processingstations_lineids;
		Line thisline;
		//int processing_lineid;
		Map<Integer,List<Integer>> processingstations_map;
		List<Integer> nextstations;
		//処理中の駅
		Station newstation;
		StaNode newnode;
		//駅間距離用の変数
		BigDecimal betweensta;
		BigDecimal absbetween;
		//start dest間距離用変数
		BigDecimal startdest ;
		Route route=null;
		
		//処理終了のためのフラグ
		boolean endprocess = true;
		
		
		//最短距離未確定リスト unsettled 未確定の意
		ArrayList<StaNode> unsettled = new ArrayList<StaNode>();
		//確定済みリスト committed 確定済み
		ArrayList<StaNode> committed = new ArrayList<StaNode>();
		//destnode dist0	
		//未確定リストにdist0を入れる
		 unsettled.add(new StaNode(dest,new BigDecimal("0.0"),null,null));
		
		 //ここから下をstart StaNodeが出るまでループ
		while(endprocess){
		//最短距離未確定リストをコスト順に並べる
		//ListComparatorクラスの条件に従いソート
		 Collections.sort(unsettled,new ListComparator());
		//未確定リストから、距離の最低のもの(つまり最初のもの)を確定済みリストに移動、その隣の駅を取り出す
		//取り出した駅(unsettled.get(0))が確定済みリストにあれば無視、未確定リストにあれば、距離を計算しなおし、短ければ距離・直前の駅を更新
		processingStaNode = unsettled.remove(0);
		committed.add(processingStaNode);
		//処理する駅を取り出す
		processingSta = processingStaNode.getSta();
		//その駅の路線idをとる
		processingstations_lineids = processingSta.getLineId();
		//その駅のマップを取り出す。取り出した路線idをもとに隣接駅をマップから取り出す。
		processingstations_map = processingSta.nextStationId();//戻り値がマップの形
		 
		for(int processing_lineid : processingstations_lineids){
			 //ある路線の、隣接駅をすべて抜き出しノードにする。取り出した路線idは消す。
			
			 //processing_lineid = processingstations_lineids.get(0);
			 //processingstations_lineids.remove(0);
			
			 //隣接駅を路線idをもとにマップから取り出す。当然2つ要素を持つintegerのリスト。
			 //nextstationsは両隣の駅のidを格納しているリスト。
			 nextstations = processingstations_map.get(processing_lineid);
			 thisline = Database.getLine(processing_lineid);
			 for(int newStationId : nextstations){
				//駅を作成、それをもとにノードを作成し、unsettledに入れるかどうか確認後入れる。
				newstation = Database.getStation(newStationId);
				//nextstations.remove(0);
				//駅間の距離を算出し絶対値をつける
				betweensta = ((processingStaNode.getSta()).getDistance(processing_lineid)).subtract(newstation.getDistance(processing_lineid));
				absbetween = betweensta.abs();
				newnode = new StaNode(newstation,absbetween.add(processingStaNode.getdist()),processingStaNode,thisline);
				//新しく作ったStaNodeがstart、未確定内、確定内にあるか確認。
				if(!(committed.indexOf(newnode)==-1)){
					//最短経路が確定しているリスト内にあったならばこのノードは破棄。なにもしない。
				}else if(newstation.getStationId() == startid){
					//start stationと一致しているならば処理を終了
					//求めたい距離はstartのstationに一致したノードを求めたStaNodeに書いてあるdistと、
					//そこからstartまでの距離をたしたものなので
					startdest = (absbetween).add(processingStaNode.getdist());
					//経路はbackがnullになるまでnodeを追えばわかる。未実装
							
					StaNode pointer = newnode;
					List<Line> linelist = new ArrayList<Line>();
					List<Station> stalist = new ArrayList<Station>();
					while(!(pointer.getback() == null)){
					linelist.add(pointer.getvialine());
					stalist.add(pointer.getSta());
					pointer = pointer.getback();
					}
					stalist.add(pointer.getSta());
					
					route = new Route(stalist,linelist);	
					
					endprocess = false;
					
				}else if(!(unsettled.indexOf(newnode)==-1)){
					//未確定リストにあったならば距離を確認。更新するか否か
					StaNode alreadynode = unsettled.remove(unsettled.indexOf(newnode)); 
					if((alreadynode.getdist()).compareTo(newnode.getdist()) > 0){
						// alreadyのほうが大きいならばnewnodeを格納、そうでないならalreadynodeを再格納。
						unsettled.add(newnode);
					}else{
						unsettled.add(alreadynode);
					}
				}else{
				//未確定、確定、startnodeの条件に合わない場合は単に未確定リストに追加。
					unsettled.add(newnode);
				}
			}
		 	
		}
//この下がwhileの}	
	}
		
		
		
		
		//処理が終わり次第 返し値Routeクラスのインスタンス？
	return route;
	
	
	}
}

//nodeクラス
class StaNode{
	Station sta;
	BigDecimal dist;
	StaNode back;
	Line vialine;
	//コンストラクタ
	public StaNode(Station sta,BigDecimal dist,StaNode back,Line vialine){
		this.sta = sta;
		this.dist = dist;
		this.back = back;
		this.vialine = vialine;
	}
	//距離を持ってくる関数
	public BigDecimal getdist(){
		return this.dist;
	}
	public Station getSta(){
		return this.sta;
	}
	public StaNode getback(){
		return this.back;
	}
	public Line getvialine(){
		return this.vialine;
	}
	@Override
	public boolean equals(Object o){
		if(!(o instanceof StaNode)){
			return false;
		}
		
		return sta.equals(((StaNode)o).sta);
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
        return (no1.compareTo(no2));
        
    }

}
