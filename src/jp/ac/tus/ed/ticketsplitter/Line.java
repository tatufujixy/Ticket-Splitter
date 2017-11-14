package jp.ac.tus.ed.ticketsplitter;

public class Line { // JR各路線データのクラス
	//地域(運賃の違いのため)
	public static final int AREA_HONSYU=1;
	public static final int AREA_HOKKAIDO=2;
	public static final int AREA_SHIKOKU=3;
	public static final int AREA_KYUSYU=4;
	
	private int id;
	private String name;
	private boolean isTrunk;
	private int area=0;
	
	//コンストラクタはDatabaseで呼ばれる
	Line(int id,String name,boolean trunk,int area){
		this.id=id;
		this.name=name;
		this.isTrunk=trunk;
		this.area=area;
	}
	
	public int getId(){
	//路線IDを返す
		
		return id;
	}
	public String getName(){
	//路線名を返す
		return name;
	}
	public boolean isTrunk(){
	//幹線ならtrue、地方交通線ならfalse
		return isTrunk;
	}
	public int getArea(){
	//地域の値を返す
		return area;
	}
}
