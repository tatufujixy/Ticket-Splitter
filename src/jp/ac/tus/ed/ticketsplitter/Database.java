package jp.ac.tus.ed.ticketsplitter;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Database {
	//運賃エリア(TRUNK:幹線   LOCAL:地方交通線)
	public static final int FARE_HOKKAIDO_TRUNK=1;
	public static final int FARE_HOKKAIDO_LOCAL=2;
	public static final int FARE_HONSYU_TRUNK=3;
	public static final int FARE_HONSYU_LOCAL=4;
	//・・・
	public static final int FARE_YAMANOTE=11;
	public static final int FARE_OSAKA_KANJO=12;
	public static final int FARE_SPECIFIC_TOKYO=13;//東京電車特定区間
	public static final int FARE_SPECIFIC_OSAKA=13;//大阪電車特定区間
	//・・・

	//初期化はstaticイニシャライザで
	static Connection conn=null;
	static Statement statement = null;

	static{
		try {
			Class.forName("org.sqlite.JDBC");

			conn=DriverManager.getConnection("jdbc:sqlite::resource:"+Database.class.getClassLoader().getResource("res/database.db"));
			statement = conn.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}/* finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}*/

	}

	public static Station getStation(int id){
	//idを指定してStationインスタンスを返す
	//idは、stationテーブルにおけるid,id_stationのどちらでも良い
		Station sta=new Station();
		
		try {
			statement.setQueryTimeout(30);
			String sql = "select * from station where id = "+id;
			ResultSet rs=statement.executeQuery(sql);
			
			if(!rs.next()){//idをもつ駅が存在しない
				return null;
			}
			if(rs.getInt("id_station")!=0){
				//乗り換え可能な駅のとき
				rs=statement.executeQuery("select * from station where id_station = "+id);
			}
			rs.beforeFirst();
			while(rs.next()){
				int line=rs.getInt("line");
				
				if(rs.getInt("id_station")==0){
					sta.setStationId(rs.getInt("id"));
				}else{
					sta.setStationId(rs.getInt("id_station"));
				}
			
				sta.setStationIdOfLine(line,rs.getInt("id"));
				sta.setName(rs.getString("name"));
				sta.addNextStationId(line, rs.getInt("prev_station"));
				sta.addNextStationId(line, rs.getInt("next_station"));
				sta.setSpecificWardsAndCities(rs.getInt("wards_and_cities"));
				sta.setIsInYamanoteLine(rs.getBoolean("yamanote"));
				sta.setInOsakaKanjoLine(rs.getBoolean("osaka_kanjo"));
				
				String specificArea=rs.getString("specific_area");
				if(specificArea==null){
				}else if(specificArea.equals("東京")){
					sta.setSpecificArea(Station.SPECIFIC_TOKYO);
				}else{
					sta.setSpecificArea(Station.SPECIFIC_OSAKA);
				}
				
				String suburbArea=rs.getString("suburb_area");
				if(suburbArea==null){
				}else if(suburbArea.equals("東京")){
					sta.setSuburbArea(Station.SUBURB_TOKYO);
				}else if(suburbArea.equals("大阪")){
					sta.setSuburbArea(Station.SUBURB_OSAKA);
				}/*他の条件も後で追加*/
				
				sta.setDistance(line, new BigDecimal(rs.getString("distance")));
			}
		} catch (SQLException e){
			return null;
		}
		return sta;
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
