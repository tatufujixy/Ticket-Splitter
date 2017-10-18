package jp.ac.tus.ed.ticketsplitter;

import java.math.BigDecimal;
import java.util.List;

public class Database {
	//運賃エリア(TRUNK:幹線   LOCAL:地方交通線)
	public static final int FARE_HOKKAIDO_TRUNK=1;
	public static final int FARE_HOKKAIDO_LOCAL=2;
	public static final int FARE_HONSYU_TRUNK=3;
	public static final int FARE_HONSYU_LOCAL=4;
	//・・・
	public static final int FARE_YAMANOTE=11;
	public static final int FARE_SPECIFIC_TOKYO=12;//東京電車特定区間
	//・・・
	
	//初期化はstaticイニシャライザで
	
	public static Station getStation(int id){
	//idを指定してStationインスタンスを返す
	//idは、stationテーブルにおけるid,id_stationのどちらでも良い
		return null;
	}
	public static List<Station> getAllStations(){
	//データベース内のすべての駅情報をリストで返す
		return null;
	}
	public static Station getStation(String name){
	//駅名nameの駅のStationインスタンスを返す。なければnullを返す
		return null;
	}
	public static Line getLine(int id){
	//路線IDがidのLineを返す
		return null;
	}
	public static int getFare(int area, BigDecimal distance){
	//運賃エリアareaの、距離distanceでの運賃を返す
		return -1;
	}
	
	
	//特定区間運賃とかは中間発表後に実装すれば良いかと
}
