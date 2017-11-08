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
import java.util.List;

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

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
