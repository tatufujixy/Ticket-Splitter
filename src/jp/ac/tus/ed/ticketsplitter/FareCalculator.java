package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.tus.ed.ticketsplitter.splitters.TicketSplitter;

public class FareCalculator {
	
	private class RouteInformation{//運賃計算に必要な情報をまとめたもの
		//経路が全て山の手線内・大阪環状線内ならそれぞれtrue
		boolean inYamanoteLine;
		boolean inOsakaKanjoLine;
		
		int specificArea;//経路が特定区間内ならその値。そうでなければ0
		
		boolean allTrunk=true;//経路が全て幹線ならtrue
		boolean containTrunk=false;//経路に幹線が含まれていればtrue
		
		
		Map<Integer,BigDecimal> areaDistance=new HashMap<Integer,BigDecimal>();
		//<エリア、距離>エリアはLineクラスのstatic変数の値
		//各エリアの運賃計算キロが入る
		
		RouteInformation(){
			areaDistance.put(Line.AREA_HOKKAIDO, BigDecimal.ZERO);
			areaDistance.put(Line.AREA_HONSYU, BigDecimal.ZERO);
			areaDistance.put(Line.AREA_SHIKOKU, BigDecimal.ZERO);
			areaDistance.put(Line.AREA_KYUSYU, BigDecimal.ZERO);
		}
		
	}
	private class FareCalculationRoute extends Route{
		String start=null,dest=null;
		
		FareCalculationRoute(Route r) {
			super(r);
		}
		
	}
	
	
	private class ShortestPathKey{
		private final Station start;
		private final Station dest;
		
		ShortestPathKey(Station start,Station dest){
			this.start=start;
			this.dest=dest;
		}
		
		@Override
		public int hashCode(){
			return start.hashCode()^(dest.hashCode()<<1);
		}
		
		@Override
		public boolean equals(Object obj){
			if(!(obj instanceof ShortestPathKey)){
				return false;
			}
			ShortestPathKey k=(ShortestPathKey)obj;
			return start.equals(k.start) && dest.equals(k.dest);
		}
	}
	
	Map<ShortestPathKey,Route> shortestPathMap=Collections.synchronizedMap(new HashMap<ShortestPathKey,Route>());
	//ダイクストラ法による最短経路を保存するMap
	
	public Ticket calculate(Route route){
	//rの経路を1枚のきっぷで買うときの運賃を返す
		/*System.out.print("calculate : ");
		for(Station s:route.getStationsList()){
			System.out.print(s.getName()+" ");
		}
		System.out.println();
		*/
		
		FareCalculationRoute r=getFareCalculationRoute(route);
		
		RouteInformation ri=getInformation(r);
		
		int fare=0;
		String fareCategory="";
		
		
		List<Station> stationsList=r.getStationsList();
		fare=Database.getSpecificSectionFare(stationsList.get(0),stationsList.get(stationsList.size()-1),r.getDistance());
		if(fare!=-1){
			//特定区間運賃
			fareCategory="特定区間運賃("+stationsList.get(0).getName()+"・"+stationsList.get(stationsList.size()-1).getName()+")";
		}else if(ri.inYamanoteLine){
			fare=Database.getFare(Database.FARE_YAMANOTE, r.getDistance());
			fareCategory="山手線";
		}else if(ri.inOsakaKanjoLine){
			fare=Database.getFare(Database.FARE_OSAKA_KANJO, r.getDistance());
			fareCategory="大阪環状線";
		}else if(ri.specificArea==Station.SPECIFIC_TOKYO){
			fare=Database.getFare(Database.FARE_SPECIFIC_TOKYO, r.getDistance());
			fareCategory="東京電車特定区間";
		}else if(ri.specificArea==Station.SPECIFIC_OSAKA){
			fare=Database.getFare(Database.FARE_SPECIFIC_OSAKA, r.getDistance());
			fareCategory="大阪電車特定区間";
		}else if(
				ri.areaDistance.get(Line.AREA_HOKKAIDO).compareTo(BigDecimal.ZERO)
				+ri.areaDistance.get(Line.AREA_HONSYU).compareTo(BigDecimal.ZERO)
				+ri.areaDistance.get(Line.AREA_SHIKOKU).compareTo(BigDecimal.ZERO)
				+ri.areaDistance.get(Line.AREA_KYUSYU).compareTo(BigDecimal.ZERO)
				>=2
				){
			//経路が北海道・本州・四国・九州のいずれか1エリアのみでない
			fare=Database.getFare(Database.FARE_HONSYU_TRUNK,
					ri.areaDistance.get(Line.AREA_HOKKAIDO)
					.add(ri.areaDistance.get(Line.AREA_HONSYU))
					.add(ri.areaDistance.get(Line.AREA_SHIKOKU))
					.add(ri.areaDistance.get(Line.AREA_KYUSYU)));
			fareCategory="複数エリア跨り";
			//さらに加算額もfareに追加する
			if(ri.areaDistance.get(Line.AREA_HOKKAIDO).compareTo(BigDecimal.ZERO)!=0){
				fare+=Database.getFare(Database.ADDITIONAL_FARE_HOKKAIDO,ri.areaDistance.get(Line.AREA_HOKKAIDO));
			}
			if(ri.areaDistance.get(Line.AREA_SHIKOKU).compareTo(BigDecimal.ZERO)!=0){
				fare+=Database.getFare(Database.ADDITIONAL_FARE_SHIKOKU,ri.areaDistance.get(Line.AREA_SHIKOKU));
			}
			if(ri.areaDistance.get(Line.AREA_KYUSYU).compareTo(BigDecimal.ZERO)!=0){
				fare+=Database.getFare(Database.ADDITIONAL_FARE_KYUSYU,ri.areaDistance.get(Line.AREA_KYUSYU));
			}
			
		}else if(ri.areaDistance.get(Line.AREA_HOKKAIDO).compareTo(BigDecimal.ZERO)==1){
			//北海道のみ
			if(ri.allTrunk){
				//幹線のみ
				fare=Database.getFare(Database.FARE_HOKKAIDO_TRUNK,r.getDistance());
				fareCategory="北海道幹線";
			}else if(ri.containTrunk){
				if(r.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(BigDecimal.TEN)<=0){
					//10km以下なら地方交通線運賃で計算
					fare=Database.getFare(Database.FARE_HOKKAIDO_LOCAL,r.getDistance());
					fareCategory="北海道地方交通線(幹線区間含む)";
				}else{
					//両方含む
					fare=Database.getFare(Database.FARE_HOKKAIDO_TRUNK,ri.areaDistance.get(Line.AREA_HOKKAIDO));
					fareCategory="北海道幹線";
				}
			}else{
				//地交線のみ
				fare=Database.getFare(Database.FARE_HOKKAIDO_LOCAL,r.getDistance());
				fareCategory="北海道地方交通線";
			}
		}else if(ri.areaDistance.get(Line.AREA_HONSYU).compareTo(BigDecimal.ZERO)==1){
			//本州のみ
			if(ri.allTrunk){
				fare=Database.getFare(Database.FARE_HONSYU_TRUNK,r.getDistance());
				fareCategory="本州幹線";
			}else if(ri.containTrunk){
				if(r.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(BigDecimal.TEN)<=0){
					//10km以下なら地方交通線運賃で計算
					fare=Database.getFare(Database.FARE_HONSYU_LOCAL,r.getDistance());
					fareCategory="本州地方交通線(幹線含む)";
				}else{
					fare=Database.getFare(Database.FARE_HONSYU_TRUNK,ri.areaDistance.get(Line.AREA_HONSYU));
					fareCategory="本州幹線";
				}
			}else{
				fare=Database.getFare(Database.FARE_HONSYU_LOCAL,r.getDistance());
				fareCategory="本州地方交通線";
			}
		}else if(ri.areaDistance.get(Line.AREA_SHIKOKU).compareTo(BigDecimal.ZERO)==1){
			//四国のみ
			if(ri.allTrunk){
				fare=Database.getFare(Database.FARE_SHIKOKU_TRUNK,r.getDistance());
				fareCategory="四国幹線";
			}else if(ri.containTrunk){
				//特定運賃に該当するか調べる
				fare=Database.getTrunkAndLocalSpecificFare(ri.areaDistance.get(Line.AREA_SHIKOKU), r.getDistance(), Line.AREA_SHIKOKU);
				fareCategory="特定運賃(四国幹線・地方交通線)";
				if(fare==-1){
					fare=Database.getFare(Database.FARE_SHIKOKU_TRUNK,ri.areaDistance.get(Line.AREA_SHIKOKU));
					fareCategory="四国幹線";
				}
			}else{
				fare=Database.getLocalSpecificFare(ri.areaDistance.get(Line.AREA_SHIKOKU),r.getDistance(),Line.AREA_SHIKOKU);
				fareCategory="特定運賃(四国地方交通線)";
				if(fare==-1){
					fare=Database.getFare(Database.FARE_SHIKOKU_TRUNK,ri.areaDistance.get(Line.AREA_SHIKOKU));
					fareCategory="四国幹線";
				}
			}
		}else{
			//九州のみ
			if(ri.allTrunk){
				fare=Database.getFare(Database.FARE_KYUSYU_TRUNK,r.getDistance());
				fareCategory="九州幹線";
			}else if(ri.containTrunk){
				//特定運賃に該当するか調べる
				fare=Database.getTrunkAndLocalSpecificFare(ri.areaDistance.get(Line.AREA_KYUSYU), r.getDistance(), Line.AREA_KYUSYU);
				fareCategory="特定運賃(九州幹線・地方交通線)";
				if(fare==-1){
					fare=Database.getFare(Database.FARE_KYUSYU_TRUNK,ri.areaDistance.get(Line.AREA_KYUSYU));
					fareCategory="九州幹線";
				}
			}else{
				fare=Database.getLocalSpecificFare(ri.areaDistance.get(Line.AREA_KYUSYU),r.getDistance(),Line.AREA_KYUSYU);
				fareCategory="特定運賃(九州地方交通線)";
				if(fare==-1){
					fare=Database.getFare(Database.FARE_KYUSYU_TRUNK,ri.areaDistance.get(Line.AREA_KYUSYU));
					fareCategory="九州幹線";
				}
			}
		}
		
		fare+=Database.getAdditionalFare(r);
		
		return new Ticket(r,fare,r.start,r.dest,fareCategory);
	}
	
	RouteInformation getInformation(Route r){
		List<Station> stations=r.getStationsList();
		List<Line> lines=r.getLinesList();
		
		RouteInformation ri=new RouteInformation();
		
		
		ri.inYamanoteLine=stations.get(0).isInYamanoteLine();
		ri.inOsakaKanjoLine=stations.get(0).isInOsakaKanjoLine();
		ri.specificArea=stations.get(0).getSpecificArea();
		
		for(int i=1;i<stations.size();i++){
			Station sta=stations.get(i);
			Station back=stations.get(i-1);
			ri.inYamanoteLine=ri.inYamanoteLine && sta.isInYamanoteLine();
			ri.inOsakaKanjoLine=ri.inOsakaKanjoLine && sta.isInOsakaKanjoLine();
			if(ri.specificArea!=0 &&ri.specificArea!=sta.getSpecificArea()){
				ri.specificArea=0;
			}
			
			Line l=lines.get(i-1);
			ri.allTrunk=ri.allTrunk && l.isTrunk();
			ri.containTrunk=ri.containTrunk || l.isTrunk();
			
			BigDecimal distance;//隣の駅との換算・擬制キロ
			if(l.isTrunk()){
				distance=sta.getDistance(l.getId()).subtract(back.getDistance(l.getId())).abs();
			}else{
				distance=
						sta.getDistance(l.getId())
							.multiply(new BigDecimal("1.1")).setScale(1, BigDecimal.ROUND_HALF_UP)
						.subtract(back.getDistance(l.getId())
							.multiply(new BigDecimal("1.1")).setScale(1, BigDecimal.ROUND_HALF_UP))
						.abs();
				
			}
			
			//各エリアの距離を更新
			ri.areaDistance.put(l.getArea(),ri.areaDistance.get(l.getArea()).add(distance));
			//System.out.println(ri.areaDistance.get(l.getArea()));
		}
		
		return ri;
	}
	
	
	
	private FareCalculationRoute getFareCalculationRoute(Route r){
		//特定都区市内・山手線内による運賃計算経路を求める
		List<Station> stationsList=r.getStationsList();
		
		//まず特定都区市内から
		
		//乗車駅・下車駅側で特定都区市内が適用されうるならtrue
		boolean startAreaPass=stationsList.get(0).getSpecificWardsAndCities()!=0
				&& !existsPassage(r,stationsList.get(0).getSpecificWardsAndCities());
		boolean destAreaPass=stationsList.get(stationsList.size()-1).getSpecificWardsAndCities()!=0
				&& !existsPassage(r,stationsList.get(stationsList.size()-1).getSpecificWardsAndCities());
		
		
		
		//エリアの	出口駅・入口駅を求める
		int start_i=0,dest_i=stationsList.size()-1;//出口駅・入口駅のインデックス
		Station start = null,dest=null;//乗下車駅のエリアの中心駅
		Route startAreaRoute=null,destAreaRoute=null;//中心駅から出入り口駅までのルート
		if(startAreaPass){
			//System.out.println("startAreaPass :"+stationsList.get(0).getName());
			int area=stationsList.get(0).getSpecificWardsAndCities();
			for(int i=1;i<stationsList.size();i++){
				if(stationsList.get(i).getSpecificWardsAndCities()==area){
					start_i=i;
				}else{
					break;
				}
			}
			if(start_i==stationsList.size()-1){
				//経路がすべてエリア内に収まる
				startAreaPass=destAreaPass=false;
			}else{
				start=Database.getCentralStationOfWardsAndCities(stationsList.get(0).getSpecificWardsAndCities());
				startAreaRoute=getShortestPath(start,stationsList.get(start_i));
			}
		}
		if(destAreaPass){
			//System.out.println("destAreaPass :"+stationsList.get(stationsList.size()-1).getName());
			int area=stationsList.get(stationsList.size()-1).getSpecificWardsAndCities();
			for(int i=stationsList.size()-2;i>=0;i--){
				if(stationsList.get(i).getSpecificWardsAndCities()==area){
					dest_i=i;
				}else{
					break;
				}
			}
			dest=Database.getCentralStationOfWardsAndCities(stationsList.get(stationsList.size()-1).getSpecificWardsAndCities());
			destAreaRoute=getShortestPath(stationsList.get(dest_i), dest);
		}
		/*if(start_i>=dest_i){
			//経路がすべてエリア内に収まる
			startAreaPass=destAreaPass=false;
		}*/
		
		if(startAreaPass && destAreaPass){
			//乗車駅・下車駅の両側が特定都区市内による経路変更の対象
			Route devided=r.divideHead(dest_i).divideTail(start_i);
			
			Route route=new Route(startAreaRoute);
			route.join(devided);
			route.join(destAreaRoute);
			
			if(route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("201"))>=0){
				FareCalculationRoute returnRoute=new FareCalculationRoute(route);
				returnRoute.start=Station.getSpecificWardsAndCitiesString(start.getSpecificWardsAndCities());
				returnRoute.dest=Station.getSpecificWardsAndCitiesString(dest.getSpecificWardsAndCities());
				return returnRoute;
			}
		}
		if(startAreaPass){
			Route devided=r.divideTail(start_i);
			
			Route route=new Route(startAreaRoute);
			route.join(devided);
			
			if(route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("201"))>=0){
				FareCalculationRoute returnRoute=new FareCalculationRoute(route);
				returnRoute.start=Station.getSpecificWardsAndCitiesString(start.getSpecificWardsAndCities());
				return returnRoute;
			}
		}
		if(destAreaPass){
			Route devided=r.divideHead(dest_i);
			
			Route route=new Route(devided);
			route.join(destAreaRoute);
			
			if(route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("201"))>=0){
				FareCalculationRoute returnRoute=new FareCalculationRoute(route);
				returnRoute.dest=Station.getSpecificWardsAndCitiesString(dest.getSpecificWardsAndCities());
				return returnRoute;
			}
		}
		
		
		
		//!!!!!!!!!!!!!次に山手線内!!!!!!!!!!!!!!
		
		//乗車駅・下車駅側で山手線内が適用されうるならtrue
		startAreaPass=stationsList.get(0).isInYamanoteLine()
				&& !existsPassage(r,yamanote);
		destAreaPass=stationsList.get(stationsList.size()-1).isInYamanoteLine()
				&& !existsPassage(r,yamanote);
		
		//エリアの	出口駅・入口駅を求める
		start_i=0;
		dest_i=stationsList.size()-1;//出口駅・入口駅のインデックス
		start = null;
		dest=null;//乗下車駅のエリアの中心駅
		startAreaRoute=null;
		destAreaRoute=null;//中心駅から出入り口駅までのルート
		if(startAreaPass){
			for(int i=1;i<stationsList.size();i++){
				if(stationsList.get(i).isInYamanoteLine()){
					start_i=i;
				}else{
					break;
				}
			}
			if(start_i==stationsList.size()-1){
				//経路がすべてエリア内に収まる
				startAreaPass=destAreaPass=false;
			}else{
				start=Database.getCentralStationOfYamanoteLine();
				startAreaRoute=getShortestPath(start,stationsList.get(start_i));
			}
		}
		if(destAreaPass){
			for(int i=stationsList.size()-2;i>=0;i--){
				if(stationsList.get(i).isInYamanoteLine()){
					dest_i=i;
				}else{
					break;
				}
			}
			dest=Database.getCentralStationOfYamanoteLine();
			destAreaRoute=getShortestPath(stationsList.get(dest_i), dest);
		}
		/*
		if(start_i>=dest_i){
			//経路がすべてエリア内に収まる
			startAreaPass=destAreaPass=false;
		}
		*/
		String inYamanote="東京山手線内";
		if(startAreaPass && destAreaPass){
			//乗車駅・下車駅の両側が山手内による経路変更の対象
			Route devided=r.divideHead(dest_i).divideTail(start_i);
			
			Route route=new Route(startAreaRoute);
			route.join(devided);
			route.join(destAreaRoute);
			
			if(route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("101"))>=0 
					&& route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("200"))<=0){
				FareCalculationRoute returnRoute=new FareCalculationRoute(route);
				returnRoute.dest=returnRoute.start=inYamanote;
				return returnRoute;
			}
		}
		if(startAreaPass){
			Route devided=r.divideTail(start_i);
			
			Route route=new Route(startAreaRoute);
			route.join(devided);
			
			if(route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("101"))>=0 
					&& route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("200"))<=0){
				FareCalculationRoute returnRoute=new FareCalculationRoute(route);
				returnRoute.start=inYamanote;
				return returnRoute;
			}
		}
		if(destAreaPass){
			Route devided=r.divideHead(dest_i);
			
			Route route=new Route(devided);
			route.join(destAreaRoute);
			
			if(route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("101"))>=0 
					&& route.getDistance().setScale(0, BigDecimal.ROUND_UP).compareTo(new BigDecimal("200"))<=0){
				FareCalculationRoute returnRoute=new FareCalculationRoute(route);
				returnRoute.dest=inYamanote;
				return returnRoute;
			}
		}
		
		
		return new FareCalculationRoute(r);
	}
	
	
	private static final int yamanote=-1;
	private boolean existsPassage(Route route,int area){
		//経路が指定の都区市内・山手線内を通過していればtrue
		//引数areaは、特定都区市内なら、Stationの定数、山手線内なら上で定義した値
		
		List<Station> list=route.getStationsList();
		
		boolean a=false,b=false,c=false;
		
		
		for(int i=0;i<list.size();i++){
			switch(area){
				case yamanote:
					a=a || !list.get(i).isInYamanoteLine();
					b=b || (a && list.get(i).isInYamanoteLine());
					c=c || (b && !list.get(i).isInYamanoteLine());
					break;
				default:
					a=a || list.get(i).getSpecificWardsAndCities()!=area;
					b=b || (a && list.get(i).getSpecificWardsAndCities()==area);
					c=c || (b && list.get(i).getSpecificWardsAndCities()!=area);
					break;
			}
			
			if(c){
				return true;
			}
		}
		
		return false;
	}
	
	
	private Route getShortestPath(Station start,Station dest){
		
		ShortestPathKey key=new ShortestPathKey(start,dest);
		Route r=shortestPathMap.get(key);
		if(r!=null){
			return r;
		}
		
		r= TicketSplitter.dijkstra(start, dest);
		shortestPathMap.put(key,r);
		return r;
	}
	
}
