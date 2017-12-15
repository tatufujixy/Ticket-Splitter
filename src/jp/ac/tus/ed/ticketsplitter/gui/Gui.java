package jp.ac.tus.ed.ticketsplitter.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jp.ac.tus.ed.ticketsplitter.Database;
import jp.ac.tus.ed.ticketsplitter.FareCalculator;
import jp.ac.tus.ed.ticketsplitter.Route;
import jp.ac.tus.ed.ticketsplitter.Station;
import jp.ac.tus.ed.ticketsplitter.Ticket;
import jp.ac.tus.ed.ticketsplitter.splitters.OptimizedTickets;
import jp.ac.tus.ed.ticketsplitter.splitters.TicketSplitterTree;




	class Gui extends JFrame implements ActionListener{

		JLabel testlabel = new JLabel();
		 JTextField tex1 = new JTextField(20);
		    JTextField tex2 = new JTextField(20);

	  public static void main(String args[]){
	    Gui frame = new Gui("経路探索");
	    frame.setVisible(true);
	  }

	  Gui(String title){
	    setTitle(title);
	    setBounds(100, 100, 300, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    JLabel label1 = new JLabel("乗車駅");
	    JLabel label2 = new JLabel("降車駅");



	    JPanel p = new JPanel();

	    JButton button = new JButton("実行");


	    p.add(label1);

	    p.add(tex1);

	    p.add(label2);

	    p.add(tex2);

	    p.add(button);
	    button.addActionListener(this);

	    Container contentPane = getContentPane();
	    contentPane.add(p, BorderLayout.CENTER);
	    contentPane.add(testlabel, BorderLayout.SOUTH);
	  }



	  public void actionPerformed(ActionEvent e){
		 //   testlabel.setText(tex1.getText() + " " +tex2.getText());

		    Station start = Database.getStation(tex1.getText());
		    Station dest = Database.getStation(tex2.getText());

		    System.out.print(start+" "+dest);

		    if(start == null || dest == null ) {
		testlabel.setText("駅名が正しくありません");
		    }else {
		    Result result= new Result(start,dest);
		    	//result.setVisible(true);
		    }

		  }
}

	class Result extends JFrame{
		Station start ;
		Station dest;

		public Result (Station start,Station dest) {
			this.start =start;
			this.dest = dest;
			  //JFrame frame = new JFrame
				setTitle("結果");
			 // frame.
			  setBounds(200, 200, 600, 800);

			  

			    JPanel p = new JPanel();
			    JTextArea area1 = new JTextArea(47,52);
			    area1.setLineWrap(true);
			    JScrollPane scrollpane1 = new JScrollPane(area1);
			    p.add(scrollpane1);
			    Container contentPane = getContentPane();
			    contentPane.add(p, BorderLayout.CENTER);
			    StringBuilder resl = null ;
			    try {
					resl = Calculation();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			    area1.setText(new String(resl));
			    setVisible(true);

		}


		StringBuilder Calculation() throws IOException {
			StringBuilder buff = new StringBuilder();

		long time=System.currentTimeMillis();
		OptimizedTickets tickets=TicketSplitterTree.getOptimizedTickets(start, dest);
		time=System.currentTimeMillis()-time;


		System.out.println("\n-----通常運賃-----");
		buff.append("\n-------------通常運賃--------------\n");

		Route r=tickets.getRoute();
		Ticket singleTicket=new FareCalculator().calculate(r);
		System.out.println(singleTicket.getStart()+" -> "+singleTicket.getDestination());
		buff.append(singleTicket.getStart()+" -> "+singleTicket.getDestination() + "\n");

		System.out.println("運賃:"+singleTicket.getFare()+"円 ("+singleTicket.getFareCategory()+")");
		buff.append("運賃:"+singleTicket.getFare()+"円 ("+singleTicket.getFareCategory()+")" + "\n");

		System.out.print("経路 : ");
		buff.append("経路 : ");

		for(String str : r.via()){
			System.out.print(str+"  ");
			buff.append(str+"  ");
		}

		System.out.println("\n\n-----分割運賃-----");
		buff.append("\n\n-------------分割運賃--------------\n");


		int sumFare=0;
		for(Ticket t:tickets.getTicketList()){
			r=t.getRoute();
			System.out.println(t.getStart()+" -> "+t.getDestination());
			buff.append("\n" + t.getStart()+" -> "+t.getDestination() + "\n");

			System.out.println("運賃:"+t.getFare()+"円 ("+t.getFareCategory()+")");
			buff.append("運賃:"+t.getFare()+"円 ("+t.getFareCategory()+")" + "\n");

			System.out.print("経路 : ");
			buff.append("経路 : ");

			for(String str : r.via()){
				System.out.print(str+"  ");
				buff.append(str+"  ");
			}
			System.out.println("\n");
			buff.append("\n");
			sumFare+=t.getFare();
		}
		System.out.println("合計"+sumFare+"円\n");
		buff.append("\n合計"+sumFare+"円\n");
		System.out.println("getOptimizedTickets実行時間:"+time+"ms");
		buff.append("\ngetOptimizedTickets実行時間:"+time+"ms");

		return buff;
		}
	}

