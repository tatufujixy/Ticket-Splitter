package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Station {
	//特定都区市内
	public static final int CITY_SAPPORO=1;
	public static final int CITY_TOKYO=2;
	public static final int CITY_YOKOHAMA=3;
	//・・・
	
	//特定区間
	public static final int SPECIFIC_TOKYO=20;
	public static final int SPECIFIC_OSAKA=21;
	
	//近郊区間
	public static final int SUBURB_TOKYO=31;
	public static final int SUBURB_OSAKA=32;
	//・・・

	
	//コンストラクタはDatabaseで呼ばれる
	
	
	public int getStationId(){
	//駅ID。乗換可能駅であればその共通のID、すなわちDBのid_station
		return -1;
	}
	public int getStationIdOfLine(int lineId){
	//乗換可能駅での、路線IDがlineIdのレコードでのID
	//単独駅では必ずgetStationId()と同じ値になる
		return -1;
	}
	public Map<Integer,Integer> nextStationId(){
	//隣の駅への路線idと駅idの組
	//次の駅に行くのに経由する路線idをKeyに、次の駅の駅idをValueにして入れている。すなわち、Map<路線id,駅id>
		return null;
	}
	public int getSpecificWardsAndCities(){
	//特定都区市内ならその値を返す。そうでないなら0を返す
		return -1;
	}
	public boolean isInYamanoteLine(){
	//山の手線内ならtrue
		return false;
	}
	public boolean isInOsakaKanjoLine(){
	//大阪環状線内ならtrue
		return false;
	}
	public int getSpecificArea(){
	//特定区間内ならその値を返す。そうでないなら0を返す
		return -1;
	}
	public int getSuburbArea(){
	//近郊区間内ならその値を返す。そうでないなら0を返す
		return -1;
	}
	public BigDecimal getDistance(int line){
	//路線idがlineの路線の、起点駅からこの駅までの営業キロを返す
		return null;
	}
	public List<Integer> getLineId(){
	//この駅が属する路線idのリストを返す
		return null;
	}
}