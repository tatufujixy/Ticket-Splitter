package jp.ac.tus.ed.ticketsplitter.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


	
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
		    testlabel.setText(tex1.getText() + tex2.getText());
		  }
	  
	}


