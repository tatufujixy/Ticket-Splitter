
package jp.ac.tus.ed.ticketsplitter.splitters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import jp.ac.tus.ed.ticketsplitter.Database;
import jp.ac.tus.ed.ticketsplitter.FareCalculator;
import jp.ac.tus.ed.ticketsplitter.Line;
import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;
import jp.ac.tus.ed.ticketsplitter.Ticket;

public class TicketSplitterTree {
	
	
	public static List<Ticket> getOptimizedTickets(Station start,Station dest){
	//start駅からdest駅までの最安値の分割パターンの乗車券リストを返す
	//中間発表までには、ダイクストラ法で経路を求めたあと、その運賃を求めて返すこととする(戻り値のリストの長さは1)
		Route dijkstraroute = dijkstra(start,dest);
	
		//Ticket ti=new FareCalculator().calculate(dijkstraroute);
		
		List<Ticket> list=new ArrayList<Ticket>();
		list.add(null);
		return list;
	}
	
	
	
	public static List<Ticket> getTicketList(Station start,Station dest){
		int startid = start.getStationId();
		
		List<TreeStaNode> unsettledList=new ArrayList<TreeStaNode>();
		//未確定リスト
		
		List<TreeStaNode> settledList=new ArrayList<TreeStaNode>();
		//確定リスト
		
		unsettledList.add(new TreeStaNode(dest,null,null,0,null));
		
		while(true){
			TreeStaNode processing=getMin(unsettledList);
			Station processingStation=processing.sta;
			
			for(int lineId:processing.sta.getLineId()){
				for(int nextStationId:processingStation.nextStationId().get(lineId)){
					//各隣接駅について
					Station nextStation=Database.getStation(nextStationId);
					int n;
					if(settledList.contains(new TreeStaNode(nextStation,null,null,0,null))){
						//すでにこの駅が確定リストにあるとき何もしない
					}else if((n=unsettledList.indexOf(new TreeStaNode(nextStation,null,null,0,null)))!=-1){
						//すでにこの駅が未確定リストにあるとき
						
						TreeStaNode thisStationNode=unsettledList.remove(n);
						//すでにリストにある、その駅ノード
						
						//processingStationまでの経路にnextStationを追加した経路について、分割最安値を求め、unsettledListにすでにある最安値と比べる
						TreeStaNode nodeInRoute=processing;//経路中の駅ノード
						Route lastRoute=new Route(dest);//最後の1枚のきっぷの経路
						//int minFare=Integer.MAX_VALUE;//最安値
						while(true){
							lastRoute.addRoute(Database.getLine(lineId),nodeInRoute.sta);
							Ticket lastTicket=new FareCalculator().calculate(lastRoute);
							int fare=nodeInRoute.farefromdest+lastTicket.getFare();//lastRouteを最後の1枚にした時の最安値
							
							if(thisStationNode.farefromdest>fare){
								//より安いパターンが見つかったとき
								thisStationNode.farefromdest=fare;
								thisStationNode.back=processing;
								thisStationNode.vialine=Database.getLine(lineId);
								thisStationNode.ticketList=new ArrayList<Ticket>(nodeInRoute.ticketList);
								thisStationNode.ticketList.add(lastTicket);
								
							}
							
							lastRoute.addRoute(nodeInRoute.vialine,nodeInRoute.back.sta);
							nodeInRoute=nodeInRoute.back;
							
							if(nodeInRoute==null){
								break;
							}
						}
						
						unsettledList.add(thisStationNode);
						
						
					}else{
						
					}
					
					
					
					
				}
			}
		
		}
		
		
		
		return null;
	}
	
	
	static TreeStaNode getMin(List<TreeStaNode> list){
		//最小値を取り出して、リストから削除
		Comparator<TreeStaNode> com=new ListComparatorfare();
		int min=0;
		for(int i=0;i<list.size();i++){
			if(com.compare(list.get(min),list.get(i))>0){
				min=i;
			}
		}
		return list.remove(min);
	}
	
}

//nodeクラス
class TreeStaNode{
	Station sta;
	//BigDecimal dist;
	TreeStaNode back;
	Line vialine;
	
	int farefromdest;
	//dest駅からここまでの最少運賃格納用
	
	List<Ticket> ticketList=new ArrayList<Ticket>();
	//この駅までの分割きっぷのリスト
	
	//コンストラクタ
	public TreeStaNode(Station sta,TreeStaNode back,Line vialine,int farefromdest,List<Ticket> ticketList){
		this.sta = sta;
		//this.dist = dist;
		this.back = back;
		this.vialine = vialine;
		this.farefromdest = farefromdest;
		this.ticketList.addAll(ticketList);
	}
	//距離を持ってくる関数
	/*public BigDecimal getdist(){
		return this.dist;
	}*/
	/*public Station getSta(){
		return this.sta;
	}
	public TreeStaNode getback(){
		return this.back;
	}
	public Line getvialine(){
		return this.vialine;
	}
	public int getfare(){
		return this.farefromdest;
	}*/
	@Override
	public boolean equals(Object o){
		if(!(o instanceof TreeStaNode)){
			return false;
		}
		
		return sta.equals(((TreeStaNode)o).sta);
	}
}

//リスト並べ替えのためのクラス
class ListComparatorfare implements Comparator<TreeStaNode> {

    //比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
    public int compare(TreeStaNode a, TreeStaNode b) {
        int no1 = a.farefromdest;
        int no2 = b.farefromdest;

        //こうすると昇順でソートされる
        //no1>no2 なら正
        //no1<no2 なら負 no1=no2 なら0が返値になる
        return no1-no2;
        
    }

}