package jp.ac.tus.ed.ticketsplitter;

public class Ticket {
	private Route route;
	private int fare;
	
	//コンストラクタはFareCalculatorで呼ばれる
	
	public Ticket(Route r, int fare){
		route=r;
		this.fare=fare;
	}
	public Route getRoute(){
	//この乗車券のRouteを返す
		return route;
	}
	public int getFare(){
	//この乗車券の運賃を返す
		return fare;
	}
}
