package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		int additionFare=0;//加算運賃があればここに累積して加算される
		
		RouteInformation(){
			areaDistance.put(Line.AREA_HOKKAIDO, BigDecimal.ZERO);
			areaDistance.put(Line.AREA_HONSYU, BigDecimal.ZERO);
			areaDistance.put(Line.AREA_SIKOKU_KYUSYU, BigDecimal.ZERO);
		}
		
	}
	
	
	public int calculate(Route r){
	//rの経路を1枚のきっぷで買うときの運賃を返す
		RouteInformation ri=getClassfier(r);
		
		int fare=0;
		if(ri.inYamanoteLine){
			fare=Database.getFare(Database.FARE_YAMANOTE, r.getDistance());
		}else if(ri.inOsakaKanjoLine){
			fare=Database.getFare(Database.FARE_OSAKA_KANJO, r.getDistance());
		}else if(ri.specificArea==Station.SPECIFIC_TOKYO){
			fare=Database.getFare(Database.FARE_SPECIFIC_TOKYO, r.getDistance());
		}else if(ri.specificArea==Station.SPECIFIC_OSAKA){
			fare=Database.getFare(Database.FARE_SPECIFIC_OSAKA, r.getDistance());
		}else if(
				(ri.areaDistance.get(Line.AREA_HOKKAIDO).compareTo(BigDecimal.ZERO)*
				ri.areaDistance.get(Line.AREA_HONSYU).compareTo(BigDecimal.ZERO)==1) ||
				(ri.areaDistance.get(Line.AREA_HOKKAIDO).compareTo(BigDecimal.ZERO)*
				ri.areaDistance.get(Line.AREA_SIKOKU_KYUSYU).compareTo(BigDecimal.ZERO)==1) ||
				(ri.areaDistance.get(Line.AREA_HONSYU).compareTo(BigDecimal.ZERO)*
						ri.areaDistance.get(Line.AREA_SIKOKU_KYUSYU).compareTo(BigDecimal.ZERO)==1)){
			//経路が北海道・本州・四国九州のいずれか1エリアのみでない
			fare=Database.getFare(Database.FARE_HONSYU_TRUNK,
					ri.areaDistance.get(Line.AREA_HOKKAIDO)
					.add(ri.areaDistance.get(Line.AREA_HONSYU))
					.add(ri.areaDistance.get(Line.AREA_SIKOKU_KYUSYU)));
			
			//!!!!!!さらに加算額もfareに追加する!!!!!!!
		}else{
			//経路が北海道・本州・四国九州のいずれか1エリアのみ
			
			/*if(ri.allTrunk){//経路が全て幹線
				//ここは要らない！(下の条件だけあればよい)
				
			}else */if(ri.containTrunk || ri.areaDistance.get(Line.AREA_SIKOKU_KYUSYU).compareTo(BigDecimal.ZERO)==1){
				//経路が幹線を含む、またはエリアが四国九州
				if(ri.areaDistance.get(Line.AREA_HOKKAIDO).compareTo(BigDecimal.ZERO)!=0){
					fare=Database.getFare(Database.FARE_HOKKAIDO_TRUNK, ri.areaDistance.get(Line.AREA_HOKKAIDO));
				}else if(ri.areaDistance.get(Line.AREA_HONSYU).compareTo(BigDecimal.ZERO)!=0){
					fare=Database.getFare(Database.FARE_HONSYU_TRUNK, ri.areaDistance.get(Line.AREA_HOKKAIDO));
				}/*else{//!!!!!!!!!!!!!ここはあとで実装!!!!!!!!!!!!!
					fare=Database.getFare(Database.FARE_SHIKOKU_TRUNK, ri.areaDistance.get(Line.AREA_HOKKAIDO));
				}*/
			}else{
				//経路が地方交通線のみ
				if(ri.areaDistance.get(Line.AREA_HOKKAIDO).compareTo(BigDecimal.ZERO)!=0){
					fare=Database.getFare(Database.FARE_HOKKAIDO_LOCAL, r.getDistance());
				}else if(ri.areaDistance.get(Line.AREA_HONSYU).compareTo(BigDecimal.ZERO)!=0){
					fare=Database.getFare(Database.FARE_HONSYU_LOCAL, r.getDistance());
				}/*else{//!!!!!!!!!!!!!ここはあとで実装!!!!!!!!!!!!!
					fare=Database.getFare(Database.FARE_SHIKOKU_LOCAL, r.getDistance());
				}*/
			}
			
		}
		
		return fare+ri.additionFare;
	}
	
	RouteInformation getClassfier(Route r){
		
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
			if(ri.specificArea!=sta.getSpecificArea()){
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
			
		}
		
		return ri;
	}
}
