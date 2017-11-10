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
import java.util.HashMap;
import java.util.List;

import jp.ac.tus.ed.ticketsplitter.Database;
import jp.ac.tus.ed.ticketsplitter.Station;

public class FareWeightGraphCreator {
	
	static Connection conn = null;
	static Statement statement = null;
	
	static{
		try{
			Class.forName("org.sqlite.JBBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+"res/faregraph.db");
			statement = conn.createStatement();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/* public static giveData(start_id, dest_id, fare){
		
	} */

	public static void main(String[] args) throws IOException{
		// TODO 自動生成されたメソッド・スタブ
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		Station start=null;
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
		
		// List<Fare> list = (切符1枚で買った場合の値段を出すプログラミング名);
		
		
		
	}

}
