package jp.ac.tus.ed.ticketsplitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import jp.ac.tus.ed.ticketsplitter.splitters.TicketSplitter;
import jp.ac.tus.ed.ticketsplitter.splitters.TicketSplitterTree;

public class Debug {

	public static void main (String[] args) throws Exception{
		// TODO 自動生成されたメソッド・スタブ
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		/*
		Station st=Database.getStation("尾久");
		for(int i:st.getLineId()){
			System.out.println(i+" "+Database.getLine(i).getName());
			
		}
		*/
		
		System.out.println("dijkstraのデバッグ");
		
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
		
		Route route=TicketSplitter.dijkstra(start, dest);
		
		for(Station s:route.getStationsList()){
			System.out.print(s.getName()+" ");
		}
	}

}
