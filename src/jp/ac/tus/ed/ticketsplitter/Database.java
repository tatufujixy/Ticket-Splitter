package jp.ac.tus.ed.ticketsplitter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.ac.tus.ed.ticketsplitter.splitters.TicketSplitterTree;

public class Database {
	//運賃エリア(TRUNK:幹線   LOCAL:地方交通線)
	public static final int FARE_HOKKAIDO_TRUNK=1; // 北海道幹線
	public static final int FARE_HOKKAIDO_LOCAL=2; // 北海道地方交通線
	public static final int FARE_HONSYU_TRUNK=3; // 本州幹線
	public static final int FARE_HONSYU_LOCAL=4; // 本州地方交通線
	public static final int FARE_SHIKOKU_TRUNK=5; // 四国幹線
	public static final int FARE_KYUSYU_TRUNK=7; // 九州幹線
	
	//加算
	public static final int ADDITIONAL_FARE_HOKKAIDO=50;
	public static final int ADDITIONAL_FARE_SHIKOKU=51;
	public static final int ADDITIONAL_FARE_KYUSYU=52;
	
	//・・・
	public static final int FARE_YAMANOTE=11;
	public static final int FARE_OSAKA_KANJO=12;
	public static final int FARE_SPECIFIC_TOKYO=13;//東京電車特定区間
	public static final int FARE_SPECIFIC_OSAKA=13;//大阪電車特定区間
	//・・・

	//初期化はstaticイニシャライザで
	static Connection conn=null;
	static Statement statement = null;
	
	static HashMap<Integer,Station> stationMap;

	static{
		try {
			Class.forName("org.sqlite.JDBC");
			//System.out.println(Database.class.getClassLoader().getResource("res/database.db"));
			conn=DriverManager.getConnection("jdbc:sqlite:"+"res/database.db");
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
		
		
		stationMap=getAllStations();

	}
	
	public static Station getStation(int id){
		return stationMap.get(id);
	}
	private static Station getStationFromDb(int id){
	//idを指定してStationインスタンスを返す
	//idは、stationテーブルにおけるid,id_stationのどちらでも良い
		Station sta=new Station();
		//System.out.println(id);
		try {
			statement.setQueryTimeout(30);
			String sql = "select * from station where id="+id;
			ResultSet rs=statement.executeQuery(sql);
			
			if(!rs.next()){//idをもつ駅が存在しない
				System.out.println("not exist");
				return null;
			}
			if(rs.getInt("id_station")!=0){
				//乗り換え可能な駅のとき
				rs=statement.executeQuery("select * from station where id_station="+rs.getInt("id_station"));
				rs.next();
			}
			do{
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
				//sta.setSpecificWardsAndCities(rs.getInt("wards_and_cities"));
				sta.setIsInYamanoteLine(rs.getBoolean("yamanote"));
				sta.setInOsakaKanjoLine(rs.getBoolean("osaka_kanjo"));
				
				String specificWardsAndCities=rs.getString("wards_and_cities");
				if(specificWardsAndCities==null){
				}else if(specificWardsAndCities.equals("東京")){
					sta.setSpecificWardsAndCities(Station.CITY_TOKYO);
				}else if(specificWardsAndCities.equals("横浜")){
					sta.setSpecificWardsAndCities(Station.CITY_YOKOHAMA);
				}//！！！他の特定都区市内も記述する！！！
				
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
			}while(rs.next());
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}
		return sta;
	}
	
	//データベース内のすべての駅情報をリストで返す
	public static HashMap<Integer,Station> getAllStations(){
		if(stationMap!=null){
			return stationMap;
		}
		
		try {
			String sql = "select * from station";
			ResultSet rs=statement.executeQuery(sql);
			
			// メモの内容を記述：最初にすべての駅データを取り出す
			stationMap=new HashMap<Integer,Station>();
			
			List<Integer> idList=new ArrayList<Integer>(1000);
			while(rs.next()){
				
				idList.add(rs.getInt("id"));
			}
			
			for(int id:idList){
				//int id =  rs.getInt("id");
				//rs.getString("name");
				Station st = getStationFromDb(id);
				for(int i : st.getStationIdOfLine()){
					stationMap.put(i, st);
				}
				
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		return stationMap;
	}
	
	public static Station getStation(String name){
	//駅名nameの駅のStationインスタンスを返す。なければnullを返す
	//ほぼgetStation(int id)のコピー
	
	//データ取り出しのやり方は中間発表後に考え直す
		Station sta = new Station();
		
		try {
			statement.setQueryTimeout(30);
			String sql = "select * from station where name = '"+name+"'"; // 文字列で＝は使える？
			ResultSet rs=statement.executeQuery(sql);
			
			if(!rs.next()){//idをもつ駅が存在しない
				return null;
			}
			/*if(rs.getInt("id_station")!=0){
				//乗り換え可能な駅のとき
				rs=statement.executeQuery("select * from station where name = "+name);
			}*/
			do{
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
			}while(rs.next());
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}
		return sta;
	}
		
	
	
	public static Line getLine(int id){
	//路線IDがidのLineを返す
		String sql = null;
		ResultSet rs;
		
		sql = "select * from line where id = " + id;
		try {
			rs=statement.executeQuery(sql);
			if(rs.next()){ //idを持つlineが存在しないとき
				if(rs.getString("area").equals("本州")){
					return new Line(rs.getInt("id"),rs.getString("name"),rs.getBoolean("trunk"),Line.AREA_HONSYU);
				}else if(rs.getString("area").equals("北海道")){
					return new Line(rs.getInt("id"),rs.getString("name"),rs.getBoolean("trunk"),Line.AREA_HOKKAIDO);
				}else if(rs.getString("area").equals("四国")){
					return new Line(rs.getInt("id"),rs.getString("name"),rs.getBoolean("trunk"),Line.AREA_SHIKOKU);
				}else if(rs.getString("area").equals("九州")){
					return new Line(rs.getInt("id"),rs.getString("name"),rs.getBoolean("trunk"),Line.AREA_KYUSYU);
				}
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}
		
	public static int getSpecificSectionFare(Station s1,Station s2,BigDecimal distance){
		//s1とs2の間、距離distanceで特定区間運賃が設定されていればその運賃を返す
		//設定されていなければ-1を返す
		
		
		
		
		return -1;
	}
	
	public static Station getCentralStationOfWardsAndCities(int area){
		//引数は、Stationクラスのstatic変数（CITY_エリア名）
		//特定都区市内の中心駅を返す
		
		String areaString=null;
		switch(area){
		case Station.CITY_SAPPORO:
			areaString="札幌";
			break;
		case Station.CITY_TOKYO:
			areaString="東京";
			break;
		case Station.CITY_YOKOHAMA:
			areaString="横浜";
			break;
		//ほかの特定都区市内も
		}
		
		String sql="select * from central_station_specific_wards_and_cities where wards_and_cities='"+areaString+"'";
		Station sta=null;
		try {
			ResultSet rs=statement.executeQuery(sql);
			if(rs.next()){
				sta=getStation(rs.getInt("id"));
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		return sta;
	}
	public static Station getCentralStationOfYamanoteLine(){
		//山手線内の中心駅（東京駅）を返す
		int id_CentralOfYamanote;
		Station centralOfYamanote = new Station();
		String Yamanote = "山手線";
		try{
			statement.setQueryTimeout(30);
			String sql = "select * from central_station_specific_wards_and_cities where wards_and_cities = '" + Yamanote+"'";
			ResultSet rs=statement.executeQuery(sql);
			
			if(!rs.next()){//idをもつ駅が存在しない
				System.out.println("not exist");
				return null;
			}
			
			id_CentralOfYamanote = rs.getInt("id");
			
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
		
		centralOfYamanote = getStation(id_CentralOfYamanote);
		return centralOfYamanote;
	}
	
	public static int getAdditionalFare(Route r){
		//経路rの加算運賃を返す
		
		
		return 0;
	}
	public static int getTrunkAndLocalSpecificFare(BigDecimal fareCalcKilos,BigDecimal operatingKilos,int area){
		//運賃計算キロがfareCalcKilos,営業キロがoparatingKilos、エリアがarea(Lineクラスのstatic変数AREA_(エリア名))のときの、
		//幹線と地方交通線を連続して利用するときの特定運賃を返す
		//当てはまらなければ-1を返す
		
		return -1;
	}
	public static int getLocalSpecificFare(BigDecimal convertedKilos,BigDecimal operatingKilos,int area){
		//擬制キロがconvertedKilos,営業キロがoparatingKilos、エリアがarea(Lineクラスのstatic変数AREA_(エリア名))のときの、
		//地方交通線のみ利用するときの特定運賃を返す
		//当てはまらなければ-1を返す
		
		return -1;
	}
	
	public static int getFare(int area, BigDecimal distance){
	//運賃エリアareaの、距離distanceでの運賃を返す
	//エリアの指定とdistanceの小数点以下を切り上げ
		//System.out.println("getFare : "+area+" "+distance.toPlainString());
		
		BigDecimal bd = distance.setScale(0, BigDecimal.ROUND_UP); // distanceの小数点以下を切り上げ
		int fare = 0;
		String sql = null;
	//select * from fare where min<=(distance整数値) and max<=(distance整数値) and (エリア指定)
		switch(area){
			case FARE_HOKKAIDO_TRUNK:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='北海道幹線'";
				break;
			case FARE_HOKKAIDO_LOCAL:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='北海道地方交通線'";
				break;
			case FARE_HONSYU_TRUNK:
				sql = "select * from fare where min<=" + bd +" and max>=" + bd + " and area='本州幹線'";
				break;
			case FARE_HONSYU_LOCAL:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='本州地方交通線'";
				break;
			case FARE_SHIKOKU_TRUNK:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='四国幹線'";
				break;
			case FARE_KYUSYU_TRUNK:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='九州幹線'";
				break;
			case FARE_SPECIFIC_TOKYO:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='東京電車特定区間'";
				break;
			case FARE_YAMANOTE:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='山手線'";
				break;
			case FARE_OSAKA_KANJO:
				sql = "select * from fare where min<=" + bd + " and max>=" + bd + " and area='大阪環状線'";
				break;
			
			//他の運賃表の場合も追加する！！
		}
		//System.out.println(sql);
		try {
			ResultSet rs = statement.executeQuery(sql);
			if(rs.next()){
				fare = rs.getInt("fare");
			}else{
				System.out.println("運賃が見つからない :area="+area+", distance="+distance);
			}
			
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		
		return fare;
	}


	//特定区間運賃とかは中間発表後に実装すれば良いかと
	
	public static void main(String args[]) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		Station start=null;
		while(start==null){
			System.out.println("乗車駅を入力:");
			String str=in.readLine();
			start=Database.getStation(str);
		}
		Station dest=null;
		while(dest==null){
			System.out.println("降車駅を入力:");
			String str=in.readLine();
			dest=Database.getStation(str);
		}
		
		List<Ticket> list=TicketSplitterTree.getOptimizedTickets(start, dest);
		
		for(Ticket t:list){
			Route r=t.getRoute();
			System.out.println(t.getStart()+" -> "+t.getDestination());
			System.out.println("運賃:"+t.getFare()+"円 ("+t.getFareCategory()+")");
			System.out.print("経路 : ");
			for(String str : r.via()){
				System.out.print(str+"  ");
			}
			System.out.println("\n");
		}
		
	}
}
