
package jp.ac.tus.ed.ticketsplitter.splitters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.tus.ed.ticketsplitter.Database;
import jp.ac.tus.ed.ticketsplitter.FareCalculator;
import jp.ac.tus.ed.ticketsplitter.Line;
import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;
import jp.ac.tus.ed.ticketsplitter.Ticket;

public class TicketSplitterTree {
	
	static FareCalculator calculator=new FareCalculator();
	
	public static List<Ticket> getOptimizedTickets(Station start,Station dest){
	//start駅からdest駅までの最安値の分割パターンの乗車券リストを返す
		return getTicketList(start,dest);
	}
	
	
	
	public static List<Ticket> getTicketList(Station start,Station dest){
		List<TreeStaNode> unsettledList=new ArrayList<TreeStaNode>();
		//未確定リスト
		
		Map<Station,LowestFareInformation> fareMap=new HashMap<Station,LowestFareInformation>();
		//各駅への最安値運賃のマップ
		
		unsettledList.add(new TreeStaNode(dest,null,null));
		fareMap.put(dest,new LowestFareInformation(dest, 0, new Route(dest),new ArrayList<Ticket>()));
		
		while(true){
			TreeStaNode processing=getMin(unsettledList,fareMap);
			Station processingStation=processing.sta;
			fareMap.get(processingStation).setSettled(true);
			
			System.out.println("最安値確定:"+processingStation.getName());
			
			if(processingStation.equals(start)){
				return fareMap.get(processingStation).getTicketList();
			}
			
			for(int lineId:processing.sta.getLineId()){
				for(int nextStationId:processingStation.nextStationId().get(lineId)){
					//各隣接駅について
					
					
					Station nextStation=Database.getStation(nextStationId);
					
					if(processing.back!=null && processing.back.sta.equals(nextStation)){
						//戻る経路は無視する
						continue;
					}
					
					
					LowestFareInformation nextStationInfo=fareMap.get(nextStation);
					if(nextStationInfo!=null && nextStationInfo.isSettled()){
						//すでにこの駅が確定リストにあるとき
						TreeStaNode node=new TreeStaNode(nextStation,processing,Database.getLine(lineId));
						System.out.println("確定済み："+nextStation.getName());
						
						//本当にこの場合も枝刈りできない？？？ -> ほとんどの場合で枝刈りしてよい
						/*
						//ループでないか確認
						boolean loop=false;
						TreeStaNode tmp=node.back;
						while(tmp!=null){
							if(tmp.sta.equals(node.sta)){
								loop=true;
								break;
							}
							tmp=tmp.back;
						}
						if(!loop){
							//新しく作ったノード
							unsettledList.add(node);
						}
						*/
						
					}else if(nextStationInfo!=null && !nextStationInfo.isSettled()){
						//この駅が未確定リストにあるとき
						
						TreeStaNode node=new TreeStaNode(nextStation,processing,Database.getLine(lineId));
						//新しく作ったノード
						
						unsettledList.add(node);
						
						Route searchRoute=new Route(node.sta);
						while(node.back!=null){//経路をまず求める
							searchRoute.addRoute(node.vialine,node.back.sta);
							node=node.back;
						}
						
						/*System.out.print("未確定リストにあるとき  searchRoute:");
						for(Station s:searchRoute.getStationsList()){
							System.out.print(s.getName()+" ");
						}
						System.out.println();*/
						
						Route lastRoute=new Route(searchRoute.getStationsList().get(0));
						int searchStartIndex=1;
						for(int i=1;i<nextStationInfo.getRoute().getStationsList().size();i++){
							lastRoute.addRoute(searchRoute.getLinesList().get(i-1),searchRoute.getStationsList().get(i));
							//計算の省略
							if(nextStationInfo.getRoute().getStationsList().get(i).equals(searchRoute.getStationsList().get(i))
									&& nextStationInfo.getRoute().getLinesList().get(i-1).equals(searchRoute.getLinesList().get(i-1))){
								//同じ駅と経路
								searchStartIndex=i;
							}else{
								break;
							}
							
						}
						
						for(int i=searchStartIndex+1;i<searchRoute.getStationsList().size();i++){
							lastRoute.addRoute(searchRoute.getLinesList().get(i-1),searchRoute.getStationsList().get(i));
							
							//折り返しの判定
							Route gRoute=fareMap.get(searchRoute.getStationsList().get(i)).getRoute();//残りのルート
							//System.out.println("折り返しの判定:"+gRoute.getStationsList().get(1).getName()+" "+searchRoute.getStationsList().get(i-1).getName());
							if(gRoute.getDistance().compareTo(BigDecimal.ZERO)!=0
									&& gRoute.getStationsList().get(1).equals(searchRoute.getStationsList().get(i-1))
									&& gRoute.getLinesList().get(0).equals(searchRoute.getLinesList().get(i-1))){
								//折り返しあり
								continue;
							}
							
							//ループの判定
							Set<Station> lastRouteStation=new HashSet<Station>(lastRoute.getStationsList());
							boolean loop=false;
							for(Station s:gRoute.getStationsList()){
								if(lastRouteStation.contains(s)){
									//ループあり
									loop=true;
									break;
								}
							}
							if(loop){
								continue;
							}
							/*
							System.out.print("last Ticket:");
							for(Station s:lastRoute.getStationsList()){
								System.out.print(s.getName()+" ");
							}
							System.out.println();
							*/
							Ticket lastTicket=calculator.calculate(lastRoute);
							int fare=fareMap.get(searchRoute.getStationsList().get(i)).getFare()+lastTicket.getFare();
							if(fare<nextStationInfo.getFare()
									|| (fare==nextStationInfo.getFare()
										&& fareMap.get(searchRoute.getStationsList().get(i)).getTicketList().size()+1
											< nextStationInfo.getTicketList().size() ) ){
								nextStationInfo.setFare(fare);
								nextStationInfo.setRoute(searchRoute);
								
								List<Ticket> ticketList=new ArrayList<Ticket>(fareMap.get(searchRoute.getStationsList().get(i)).getTicketList());
								ticketList.add(0,lastTicket);
								nextStationInfo.setTicketList(ticketList);
							}
							
						}
						
						
					}else{
						//この駅が未確定リストにないとき（運賃が1度も計算されていない）
						
						
						TreeStaNode node=new TreeStaNode(nextStation,processing,Database.getLine(lineId));
						//新しく作ったノード
						unsettledList.add(node);
						
						nextStationInfo=new LowestFareInformation(nextStation);
						fareMap.put(nextStation,nextStationInfo);
						
						
						Route searchRoute=new Route(node.sta);
						while(node.back!=null){//経路をまず求める
							searchRoute.addRoute(node.vialine,node.back.sta);
							node=node.back;
						}
						
						
						Route lastRoute=new Route(searchRoute.getStationsList().get(0));
						for(int i=1;i<searchRoute.getStationsList().size();i++){
							lastRoute.addRoute(searchRoute.getLinesList().get(i-1),searchRoute.getStationsList().get(i));
							
							//折り返しの判定
							Route gRoute=fareMap.get(searchRoute.getStationsList().get(i)).getRoute();//残りのルート
							if(gRoute.getDistance().compareTo(BigDecimal.ZERO)!=0
									&& gRoute.getStationsList().get(1).equals(searchRoute.getStationsList().get(i-1))
									&& gRoute.getLinesList().get(0).equals(searchRoute.getLinesList().get(i-1))){
								//折り返しあり
								continue;
							}
							
							//ループの判定
							Set<Station> lastRouteStation=new HashSet<Station>(searchRoute.getStationsList().subList(0,i));
							boolean loop=false;
							for(Station s:gRoute.getStationsList()){
								if(lastRouteStation.contains(s)){
									//ループあり
									loop=true;
									break;
								}
							}
							if(loop){
								continue;
							}
							
							Ticket lastTicket=calculator.calculate(lastRoute);
							int fare=fareMap.get(searchRoute.getStationsList().get(i)).getFare()+lastTicket.getFare();
							if(fare<nextStationInfo.getFare()
									|| (fare==nextStationInfo.getFare()
									&& fareMap.get(searchRoute.getStationsList().get(i)).getTicketList().size()+1
										< nextStationInfo.getTicketList().size() ) ){
								nextStationInfo.setFare(fare);
								nextStationInfo.setRoute(searchRoute);
								
								List<Ticket> ticketList=new ArrayList<Ticket>(fareMap.get(searchRoute.getStationsList().get(i)).getTicketList());
								ticketList.add(0,lastTicket);
								nextStationInfo.setTicketList(ticketList);
							}
							
						}
						
					}
					
					
					System.out.println("TicketSplitterTree:getTicketList :"+nextStation.getName()+" "+nextStationInfo.getFare());
					
					
				}
				
			}
		
		}
		
		
		
		//return null;
	}
	
	
	static TreeStaNode getMin(List<TreeStaNode> list,Map<Station,LowestFareInformation> fareMap){
		//最小値を取り出して、リストから削除
		int min=0;
		int minFare=Integer.MAX_VALUE;
		for(int i=0;i<list.size();i++){
			int fare=fareMap.get(list.get(i).sta).getFare();
			if(minFare>fare){
				min=i;
				minFare=fare;
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
	
	//コンストラクタ
	public TreeStaNode(Station sta,TreeStaNode back,Line vialine/*,int farefromdest,List<Ticket> ticketList*/){
		this.sta = sta;
		this.back = back;
		this.vialine = vialine;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof TreeStaNode)){
			return false;
		}
		
		return sta.equals(((TreeStaNode)o).sta);
	}
}

class LowestFareInformation{
	private Station station;
	private int fare;
	private Route route;
	private List<Ticket> ticketList;
	
	private boolean isSettled=false;
	
	public LowestFareInformation(Station sta){
		this(sta,Integer.MAX_VALUE,null,null);
	}
	public LowestFareInformation(Station sta,int fare,Route route,List<Ticket> list){
		station=sta;
		this.fare=fare;
		this.route=route;
		ticketList=list;
	}
	void setSettled(boolean b){
		isSettled=b;
	}
	boolean isSettled(){
		return isSettled;
	}
	
	void setFare(int i){
		fare=i;
	}
	void setRoute(Route r){
		route=r;
	}
	void setTicketList(List<Ticket> l){
		ticketList=l;
	}
	
	int getFare(){
		return fare;
	}
	Route getRoute(){
		return route;
	}
	List<Ticket> getTicketList(){
		return ticketList;
	}
}