
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

import java.net.*;
import java.awt.event.ActionEvent;

public class BroadcastStoppuhr extends JFrame {

	private JPanel contentPane;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BroadcastStoppuhr frame = new BroadcastStoppuhr();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	Thread thread;
	Thread lisen;
	public static int seconds = 0;
	public static boolean running = false;
	public BroadcastStoppuhr() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{440, 0};
		gbl_contentPane.rowHeights = new int[]{0, 40, 67, 0, 61, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		contentPane.add(panel_1, gbc_panel_1);
		
		JLabel lab_sec = new JLabel("0", SwingConstants.CENTER);
		lab_sec.setFont(new Font("Dialog", Font.BOLD, 20));
		GridBagConstraints gbc_lab_sec = new GridBagConstraints();
		gbc_lab_sec.anchor = GridBagConstraints.NORTH;
		gbc_lab_sec.fill = GridBagConstraints.HORIZONTAL;
		gbc_lab_sec.insets = new Insets(0, 0, 5, 0);
		gbc_lab_sec.gridx = 0;
		gbc_lab_sec.gridy = 1;
		contentPane.add(lab_sec, gbc_lab_sec);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		contentPane.add(panel, gbc_panel);
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 3;

		
		JButton btnNewButton = new JButton("Start");
		contentPane.add(btnNewButton, gbc_btnNewButton);
		
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				byte[] strStart = "start".getBytes();
				byte[] strStop = "stop".getBytes();
				DatagramSocket socet = new DatagramSocket();
				socet.setBroadcast(true);
				if(running) {
					
						running = false;
						
						DatagramPacket pac = new DatagramPacket(strStop, strStop.length, InetAddress.getByName("255.255.255.255"), 4444);
						socet.send(pac);
						socet.send(pac);
						socet.send(pac);
						socet.send(pac);
					
					
					
					thread.suspend();;
					
				}
				else {
					running = true;
					DatagramPacket pac = new DatagramPacket(strStart, strStart.length, InetAddress.getByName("255.255.255.255"), 4444);
					socet.send(pac);
					socet.send(pac);
					socet.send(pac);
					socet.send(pac);
					
					if(!thread.isAlive()) {
						
						thread.start();
					}else {
						thread.resume();
					}
					
					
				}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		
		thread = new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
						
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								lab_sec.setText(String.valueOf(++seconds));
							}
						});
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		
		lisen = new Thread(new Runnable() {
			public void run() {
				
				try {
					DatagramSocket socket = new DatagramSocket(4444);
					byte[] buffer = new byte[255];
					while(true) {
						DatagramPacket pacet = new DatagramPacket(buffer, buffer.length);
						socket.receive(pacet);
						
						
						
						if(new String(pacet.getData(), 0,5).equalsIgnoreCase("start")) {
							
							if(thread.isAlive()) {
								thread.resume();
							}
							else {
								if(running) continue;
								thread.start();
							}
						}
						else if (new String(pacet.getData(), 0,4).equalsIgnoreCase("stop")) {
							thread.suspend();
						}
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		lisen.start();
		
		
	}

}
