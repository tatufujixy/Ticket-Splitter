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
import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;

public class FareWeightGraphCreator {
	
	static Connection conn = null;
	static Statement statement = null;
	
	//int start_id, dest_id, fare;
	
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
				try{
					ResultSet rs1 = statement.executeQuery("select * from station where id = " + i);
					ResultSet rs2 = statement.executeQuery("select * from station where id = " + j);
					//Station start = rs1.getStation("name");
					//Station dest = rs2.getStation("name");
					if(i != j){
						//Route dijkstraroute = TicketSplitter.dijkstra(start, dest);
						//fare = FareCalculator.calculator(dijkstraroute);
					}else{
						return;
					}
				}catch(SQLException e){
					e.printStackTrace();
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
	public static void main(String[] args) throws IOException{
		// TODO 自動生成されたメソッド・スタブ
		
		//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		/*
		Station start = null;
		while(start==null){
			System.out.println("乗車駅を入力:");
			String str = in.readLine();
			start = Database.getStation(str);
		}
		
		Station dest = null;
		while(dest==null){
			System.out.println("降車駅を入力:");
			String str = in.readLine();
			dest = Database.getStation(str);
		}
		*/
		
		// List<Fare> list = (切符1枚で買った場合の値段を出すプログラミング名);
		
		
		
	}

}
