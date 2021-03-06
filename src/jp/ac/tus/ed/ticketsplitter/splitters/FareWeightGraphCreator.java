package jp.ac.tus.ed.ticketsplitter.splitters;


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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.tus.ed.ticketsplitter.Database;
import jp.ac.tus.ed.ticketsplitter.FareCalculator;
import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;

public class FareWeightGraphCreator {
	
	static Connection conn = null;
	static Statement statement = null;
	
	//Set<Station> stationSet = new HashSet<Station>(Database.getAllStations().values());
	//List<Station> stationList = new ArrayList<Station>(stationSet);
	
	static void createDatabase(){
		//最初に呼ぶメソッド
		Set<Station> stationSet = new HashSet<Station>(Database.getAllStations().values());
		List<Station> stationList = new ArrayList<Station>(stationSet);
		
		/*
		stationListの全ての2駅の組み合わせについて、
		・TicketSplitter.dijkstraメソッドで最短経路を求める
		・FareCalculator.calculateで運賃を求める
		・2駅と運賃をDBに入れる
		*/
	
		for(int i=0;i<stationList.size();i++){
			for(int j=0;j<stationList.size();j++){
				if(i != j){
					Station start = stationList.get(i);
					Station dest = stationList.get(j);
					Route dijkstraroute = TicketSplitter.dijkstra(start, dest);
					int fare = new FareCalculator().calculate(dijkstraroute).getFare();
					giveData(start.getStationId(), dest.getStationId(), fare);
				}else{
					
				}
			}
		}
		
	}
	
	public static void giveData(int start, int dest, int fare){
		try{
			Class.forName("org.sqlite.JBBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+"res/faregraph.db");
			statement = conn.createStatement();
			statement.executeUpdate("insert into fare values(" + start + "," + dest + "," + fare + ")");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	//データの消去(念のため)
	public static void DataDelete(int start, int dest, int fare){
		try{
			Class.forName("org.sqlite.JBBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+"res/faregraph.db");
			statement = conn.createStatement();
			statement.executeUpdate("delete from fare where start_id = " + start + "and dest_id = " + dest);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	// mainクラス
	public static void main(){
		// TODO 自動生成されたメソッド・スタブ
		
		createDatabase();

	}

}
