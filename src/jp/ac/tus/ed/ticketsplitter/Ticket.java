package jp.ac.tus.ed.ticketsplitter;

public class Ticket {
	private Route route;
	private int fare;
	private String start;
	private String dest;
	private String fareCategory;
	
	
	
	//コンストラクタはFareCalculatorで呼ばれる
	
	public Ticket(Route r, int fare,String start,String dest){
		this(r, fare,start,dest,null);
	}
	public Ticket(Route r, int fare,String start,String dest,String category){
		route=r;
		this.fare=fare;
		
		this.start= start!=null && !start.equals("") ? start 
				: r.getStationsList().get(0).getName();
		this.dest= dest!=null && !dest.equals("") ? dest 
				: r.getStationsList().get(r.getStationsList().size()-1).getName();
		fareCategory=category;
	}
	public Route getRoute(){
	//この乗車券のRouteを返す
		return route;
	}
	public int getFare(){
	//この乗車券の運賃を返す
		return fare;
	}
	
	//乗下車駅（特定都区市内など含む）を返す
	public String getStart(){
		return start;
	}
	public String getDestination(){
		return dest;
	}
	
	public String getFareCategory(){
		return fareCategory;
	}
}
