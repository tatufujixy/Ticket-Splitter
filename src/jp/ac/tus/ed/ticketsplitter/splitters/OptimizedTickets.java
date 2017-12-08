package jp.ac.tus.ed.ticketsplitter.splitters;

import java.util.List;

import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Ticket;

public class OptimizedTickets {
	private Route route;
	private List<Ticket> list;
	
	public OptimizedTickets(Route r,List<Ticket> l){
		route=r;
		list=l;
	}
	
	public Route getRoute(){
		return route;
	}
	public List<Ticket> getTicketList(){
		return list;
	}
}
