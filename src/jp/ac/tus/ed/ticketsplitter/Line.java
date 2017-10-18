package jp.ac.tus.ed.ticketsplitter;

public class Line {
	//地域(運賃の違いのため)
	public static final int AREA_HONSYU=1;
	public static final int AREA_HOKKAIDO=2;
	public static final int AREA_SIKOKU_KYUSYU=3;
	
	
	//コンストラクタはDatabaseで呼ばれる
	
	public int getId(){
	//路線IDを返す
		return -1;
	}
	public String getName(){
	//路線名を返す
		return null;
	}
	public boolean isTrunk(){
	//幹線ならtrue、地方交通線ならfalse
		return false;
	}
	public int getArea(){
	//地域の値を返す
		return 0;
	}
}
