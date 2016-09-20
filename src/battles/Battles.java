package battles;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
// The main class here - The GameCanvas class.
// Draws most stuff in the game.
class GameCanvas extends JComponent {
	// Declares all variables.
	public int gameGoing = 1;
	public int upgradeable = 1;
	public int soldier_x = 283;
	public int soldier_y = 350;
	public double soldier_speed = 1;
	public int soldier_health = 3;
	public int soldier2_health = 3;
	public int cannon_health = 1;
	public int cannonball_x = 283;
	public int cannonball_y = 0;
	public int moneh = 0;
	public boolean canAffordUpgrade = false;
	public int upgradeCost;
	public int firepressed = 0;
	public int bullet_speed = 6;
	public int regenerativePower = 0;
	public int soldiersKilled = 0;
	public int levelUpEligible = 0;
	public int isLoseMessageDisplayed = 1;
	public int currentLevel = 1;
	public int updatedUpgradeCost = 0;
	public int level6Used = 0;
	public int gameLost = 0;
	protected void paintComponent(Graphics g) {
		if(gameGoing == 1) {
		if(updatedUpgradeCost == 0) {
			setLevelCost();
		}
		paintHunk(g, 0);
		paintHunk(g, 80);
		paintHunk(g, 160);
		paintHunk(g, 240);
		paintHunk(g, 320);
		paintHunk(g, 400);
		paintHunk(g, 480);
		paintHunk(g, 560); // Draws the map.
		// Declares the images.
		BufferedImage soldier = null;
		BufferedImage soldiers_car = null;
		BufferedImage cannon = null;
		BufferedImage cannon_lives = null;
		BufferedImage chest = null;
		BufferedImage cannonball = null;
		BufferedImage sold_health_1 = null;
		BufferedImage sold_health_2 = null;
		BufferedImage sold_health_3 = null;
		
		try {
			// Reads images from file.
			soldier = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\soldier.png"));
			//soldiers_car = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\soldier_car.png"));
			sold_health_1 = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\health1.png"));
			sold_health_2 = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\health2.png"));
			sold_health_3 = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\health3.png"));
			cannon = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\cannon.png"));
			cannon_lives = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\life.png"));
			chest = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\chest.png"));
			cannonball = ImageIO.read(new File(getCurrentWorkingDir() + "\\images\\cannonball.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Draws the appropriate visualizer for the soldier's health.
		if(soldier_health == 3) {
			g.drawImage(sold_health_1, soldier_x - 10, soldier_y - 30, null);
		} else if(soldier_health == 2) {
			g.drawImage(sold_health_2, soldier_x - 10, soldier_y - 30, null);
		} else if(soldier_health == 1) {
			g.drawImage(sold_health_3, soldier_x - 10, soldier_y - 30, null);
		} else if(soldier_health == 0) {
			kill_soldier();
		}
		// Draws a car instead of a soldier if he is going fast enough.
		if((!(soldier_speed >= 10))) {
			g.drawImage(soldier, soldier_x, soldier_y, null);
		} else if(soldier_speed >= 10) {
			g.drawImage(soldier, soldier_x, soldier_y, null);
		}
		// Draws all the other images.
		g.drawImage(cannonball, cannonball_x, cannonball_y, null);
		g.drawImage(cannon, 255, 0, null);
		g.drawImage(cannon_lives, 0, 130, null);
		g.drawImage(chest, 0, 0, null);
		Rectangle soldier_hitBox = new Rectangle(soldier_x, soldier_y, 90, 168);
		Rectangle cannonball_hitBox = new Rectangle(cannonball_x, cannonball_y, 50, 51);
		// Checks to see if the soldier hit the cannonball. If so, take out one of
		// his health and redraw the cannonball at the top.
		if(collisionCheck(soldier_hitBox, cannonball_hitBox)) {
			soldier_health--;
			fire_again();
		}
		// Checks to see if the soldier's y is less than or equal to 60.
		// If so, take out one of the cannon's health. Also, if the cannon's
		// health is 0, turn off the game. 
		if(soldier_y <= 60) {
			cannon_health--;
			if(cannon_health == 0) {
				try {
					turnOffGame();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Battles.clip.stop();
				if(isLoseMessageDisplayed == 1) {
					JOptionPane.showMessageDialog(this, "YOU LOSE!!!");
				}
				Battles.gamesPlayed++;
				gameLost = 1;
			}
		}
		// Checks to see if you can afford an upgrade. If so, then set
		// canAffordUpgrade to true. If not, then set it to false.
		if(moneh >= upgradeCost) {
			canAffordUpgrade = true;
	    } else {
	    	canAffordUpgrade = false;
	    }
		soldier_move();
		cannonball_move();
		re_paint();
		// Draws the soldiers killed
	    g.setFont(new Font("Garamond", Font.PLAIN, 50));
	     
	    g.setColor(Color.white);
	    
	    g.drawString(Integer.toString(moneh), 140, 80);
	    g.drawString(Integer.toString(cannon_health), 130, 190);
	    g.setFont(new Font("Garamond", Font.PLAIN, 30));
	    g.drawString(Integer.toString(soldiersKilled) + " soldiers killed", 0, 255);
	    g.drawString("Level " + Integer.toString(currentLevel), 0, 285);
	    updatedUpgradeCost = 1;
		} else {
			clearCanvas(g);
		}
		re_paint();
	}
	// Method to paint grass blocks
	protected void paintGrass(Graphics g, int x, int y) {
		g.setColor(new Color(102, 204, 0));
		g.drawRect(x, y, 80, 80);
		g.fillRect(x, y, 80, 80);
	}
	// Method to paint path blocks
	protected void paintPath(Graphics g, int x, int y) {
		g.setColor(Color.GRAY);
		g.drawRect(x, y, 80, 80);
		g.fillRect(x, y, 80, 80);
	}
	// Paints hunk of grass with matching path at given y
	protected void paintHunk(Graphics g, int y) {
		paintGrass(g, 0, y);
		paintGrass(g, 80, y);
		paintGrass(g, 160, y);
		paintPath(g, 240, y);
		paintPath(g, 320, y);
		paintGrass(g, 400, y);
		paintGrass(g, 480, y);
		paintGrass(g, 560, y);
	}
	// Sets the appropriate cost for upgrades
	public void setLevelCost() {
		if(currentLevel == 1) upgradeCost = 5;
		if(currentLevel == 2) upgradeCost = 6;
		if(currentLevel == 3) upgradeCost = 7;
	}
	// Method to move the soldier
	public void soldier_move() {
		for(double i = 0; i < soldier_speed; i++) {
			soldier_y--;
		}
		repaint();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// Method to move the cannonball
	public void cannonball_move() {
		for(int i = 0; i < bullet_speed; i++) {
			cannonball_y++;
		}
		repaint();
	}
	// Recursively repaints the canvas
	public void re_paint() {
		repaint();
	}
	// Method to reset the cannonball
	public void fire_again() {
		cannonball_x = 283;
		cannonball_y = 0;
	}
	// Method to reset the soldier
	public void kill_soldier() {
		soldier_x = 283;
		soldier_y = 400;
		soldier_health = 3;
		soldier_speed++;
		soldiersKilled++;
		moneh++;
		if(regenerativePower == 1) {
			cannon_health++;
		}
		if(regenerativePower == 2) {
			cannon_health++;
			cannon_health++;
		}
		if(regenerativePower > 2) {
			for(int i = 0; i < regenerativePower; i++) {
				cannon_health++;
			}
		}
		System.out.println("Soldiers killed: " + soldiersKilled);
	}
	// Method that turns off the game and resets all variables
	public void turnOffGame() throws Exception {
		Graphics g = Battles.canvas.getGraphics();
		clearCanvas(g);
		gameGoing = 0;
		upgradeable = 1;
		soldier_x = 283;
		soldier_y = 350;
		soldier_speed = 1;
		soldier_health = 3;
		soldier2_health = 3;
		cannon_health = 1;
		cannonball_x = 283;
		cannonball_y = 0;
		moneh = 0;
		canAffordUpgrade = false;
		upgradeCost = 5;
		firepressed = 0;
		bullet_speed = 6;
		regenerativePower = 0;
		levelUpEligible = 0;
		updatedUpgradeCost = 0;
		gameLost = 0;
		//Battles.clip.stop();
		Battles.playButton.setVisible(true);
		Battles.statsButton.setVisible(true);
		Battles.resetStatsButton.setVisible(true);
		Battles.exitButton.setVisible(true);
		Battles.playLabel.setVisible(true);
		Battles.copyrightLabel.setVisible(true);
		Battles.upgrade.setVisible(false);
		Battles.backButton.setVisible(false);
		clearCanvas(g);
	}
	// Method to clear the canvas
	public void clearCanvas(Graphics g) {
		g.setColor(Color.white);
		g.drawRect(0, 0, 647, 534);
	}
	// Method to upgrade the cannon with the appropriate upgrade
	public void nextUpgrade() throws Exception {
		if(upgradeable == 1) {
		moneh -= upgradeCost;
		int upgraded = 0;
		if(currentLevel == 1) {
		if(upgradeCost == 5) {
			cannon_health = 5;
			JOptionPane.showMessageDialog(this, "Cannon upgraded! It now has 5 health.");
			upgradeCost = 7;
			upgraded = 1;
		}
		if(upgradeCost == 7 && upgraded == 0) {
			cannon_health = 10;
			bullet_speed = 8;
			JOptionPane.showMessageDialog(this, "Cannon upgraded! It now has 10 health and bullet speed is 8 instead of 6.");
			upgradeCost = 8;
			upgraded = 1;
		}
		if(upgradeCost == 8 && upgraded == 0) {
			cannon_health = 10;
			bullet_speed = 10;
			regenerativePower = 1;
			JOptionPane.showMessageDialog(this, "Cannon upgraded! It now has regenerative power and bullet speed is 10 instead of 8.");
			upgradeCost = 14;
			upgraded = 1;
		}
		if(upgradeCost == 14 && upgraded == 0) {
			cannon_health = 20;
			bullet_speed = 10;
			regenerativePower = 2;
			JOptionPane.showMessageDialog(this, "Cannon upgraded! Regenerative power x2, and bullet speed is 10 instead of 8.");
			upgradeCost = 9;
			levelUpEligible = 1;
			upgraded = 1;
		}
		if(upgradeCost == 9 && upgraded == 0) {
			isLoseMessageDisplayed = 0;
			JOptionPane.showMessageDialog(this, "Level up!");
			gameGoing = 0;
			currentLevel = 2;
			try {
				Battles.updateCurrentLevelFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			turnOffGame();
		}
		} else if(currentLevel == 2) {
			if(upgradeCost == 6) {
				if(level6Used == 0) {
				cannon_health = 4;
				JOptionPane.showMessageDialog(this, "Cannon upgraded! It now has 4 health.");
				upgradeCost = 5;
				level6Used = 1;
				System.out.println(level6Used);
				upgraded = 1;
				} else {
					System.out.println(level6Used);
					cannon_health = 15;
					regenerativePower = 2;
					JOptionPane.showMessageDialog(this, "Cannon health regenerated to 15. Regeneration = 2 health/soldier killed.");
					upgradeCost = 7;
					upgraded = 1;
				}
			}
			if(upgradeCost == 5 && upgraded == 0) {
				cannon_health = 8;
				regenerativePower = 1;
				JOptionPane.showMessageDialog(this, "Cannon health regenerated to 8. Regeneration = 1 health/soldier killed.");
				upgradeCost = 10;
				upgraded = 1;
			}
			if(upgradeCost == 10 && upgraded == 0) {
				cannon_health = 20;
				regenerativePower = 3;
				JOptionPane.showMessageDialog(this, "Cannon health regenerated to 20. Regeneration = 3 health/soldier killed.");
				upgradeCost = 15;
				upgraded = 1;
			}
			if(upgradeCost == 15 && upgraded == 0) {
				cannon_health = 10;
				regenerativePower = 4;
				JOptionPane.showMessageDialog(this, "Cannon health regenerated to 10. Regeneration = 4 health/soldier killed.");
				upgradeCost = 11;
				levelUpEligible = 1;
				upgraded = 1;
			}
			if(upgradeCost == 11 && upgraded == 0) {
				isLoseMessageDisplayed = 0;
				JOptionPane.showMessageDialog(this, "Level up!");
				gameGoing = 0;
				currentLevel = 3;
				try {
					Battles.updateCurrentLevelFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
				turnOffGame();
			}
		} else if(currentLevel == 3) {
			if(upgradeCost == 7) {
				cannon_health = 3;
				JOptionPane.showMessageDialog(this, "Cannon health is now 6.");
				upgradeCost = 3;
				upgraded = 1;
			}
			if(upgradeCost == 3 && upgraded == 0) {
				cannon_health = 8;
				JOptionPane.showMessageDialog(this, "Cannon health is now 8.");
				upgradeCost = 5;
				upgraded = 1;
			}
			if(upgradeCost == 5 && upgraded == 0) {
				cannon_health = 10;
				regenerativePower = 1;
				JOptionPane.showMessageDialog(this, "UPGRADE DETAILS: Cannon health: 10. Regeneration: 1/soldier killed.");
				upgradeCost = 11;
				upgraded = 1;
			}
			if(upgradeCost == 11 && upgraded == 0) {
				cannon_health = 15;
				regenerativePower = 2;
				JOptionPane.showMessageDialog(this, "UPGRADE DETAILS: Cannon health: 15. Regeneration: 2/soldier killed.");
				upgradeCost = 9;
				upgraded = 1;
			}
			if(upgradeCost == 9 && upgraded == 0) {
				cannon_health = 15;
				regenerativePower = 3;
				JOptionPane.showMessageDialog(this, "UPGRADE DETAILS: Cannon health: 15. Regeneration: 3/soldier killed.");
				upgradeCost = 13;
				upgraded = 1;
			}
		}
		}
	}
	// (borrowed code) Main method to check for collision
	boolean collisionCheck(Rectangle rect1, Rectangle rect2) {
		 return hitCheck(rect1.x, rect1.y, rect2) ||
		        hitCheck(rect1.x+rect1.width, rect1.y, rect2) ||
		        hitCheck(rect1.x, rect1.y+rect1.height, rect2) ||
		        hitCheck(rect1.x+rect1.width,
		                 rect1.y+rect1.height, rect2);
	}
	// (borrowed code) Base method to check for collision
	boolean hitCheck(int x, int y, Rectangle rect) {
		 return (x >= rect.x && x <= rect.x+rect.width) &&
		        (y >= rect.y && y <= rect.y+rect.height);
	}
	// A very important method. Returns the current working directory.
	public static String getCurrentWorkingDir() throws Exception {
		return GameCanvas.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
	}
	// Method to play soundtrack (used once below to play the background track).
	static void playSound(String soundFile) throws Exception {
	    File f = new File(soundFile);
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
	    Clip clip = AudioSystem.getClip();
	    clip.open(audioIn);
	    clip.start();
	}
}
// The Battles program class, containing the JFrame and all the buttons, labels, etc.
public class Battles extends JFrame {
	// Declares all buttons, labels, etc.
	public static GameCanvas canvas = new GameCanvas();
	public static JFrame gameFrame;
	public static JButton upgrade;
	public static JButton playButton;
	public static Clip clip;
	public static JLabel playLabel;
	public static JToggleButton statsButton;
	public static Font segoe_def;
	public static JLabel statsLabel;
	public static JButton backButton;
	public static JLabel statsLabel2;
	public static int gamesPlayed = 0;
	public static JLabel copyrightLabel;
	public static JButton exitButton;
	public static JButton resetStatsButton;
	public static void main(String[] args) throws Exception {
		// Initializes the UI.
		segoe_def = new Font("Segoe UI", Font.PLAIN, 26);
		gameFrame = new JFrame("Battles v1.8.8b");
		gameFrame.setLayout(null);
		gameFrame.getContentPane().setBackground(Color.WHITE);
		gameFrame.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e) {
				try {
					updateSoldiersKilledFile();
					if(canvas.gameGoing == 1) {
						gamesPlayed++;
					}
					updateGamesPlayedFile();
				} catch (Exception e1) {
		            System.err.println("There was a problem: " + e1);
		        }
		    	System.exit(0);
		    }
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {
    		e.printStackTrace();
    	}
		canvas.gameGoing = 0;
		canvas.currentLevel = currentLevel();
		canvas.setSize(647, 534);
		canvas.setLocation(0, 0);
		upgrade = new JButton("Upgrade - " + canvas.upgradeCost + " coins");
		upgrade.setFont(new Font("Arial", Font.PLAIN, 26));
		upgrade.setSize(upgrade.getPreferredSize());
		upgrade.setLocation(400, 0);
		upgrade.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  try{canvas.nextUpgrade();}catch(Exception e1){e1.printStackTrace();}
				  if(canvas.levelUpEligible == 1) {
					  canvas.isLoseMessageDisplayed = 1;
				  }
			  } 
		});
		upgrade.setVisible(false);
		playButton = new JButton("Play Game");
		playButton.setFont(segoe_def);
		playButton.setSize(upgrade.getPreferredSize());
		playButton.setLocation(200, 40);
		playButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  try {
					/*  File f = new File(GameCanvas.getCurrentWorkingDir() + "\\sounds\\background_music.wav");
					  AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
					  clip = AudioSystem.getClip();
					  clip.open(audioIn);
					  clip.start();*/
				  } catch (Exception e1) {
					e1.printStackTrace();
				  }	
				  upgrade.setVisible(true);
				  backButton.setVisible(true);
				  playButton.setVisible(false);
				  statsButton.setVisible(false);
				  statsButton.setSelected(false);
				  resetStatsButton.setVisible(false);
				  exitButton.setVisible(false);
				  statsLabel.setVisible(false);
				  statsLabel2.setVisible(false);
				  copyrightLabel.setVisible(false);
				  playLabel.setVisible(false);
				  canvas.gameGoing = 1;
			  } 
		});
		statsButton = new JToggleButton("Stats");
		statsButton.setFont(segoe_def);
		statsButton.setSize(upgrade.getPreferredSize());
		statsButton.setLocation(200, 80);
		statsButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
			   if(ev.getStateChange()==ItemEvent.SELECTED){
				try {
					statsLabel.setText("Soldiers killed: " + soldiersKilled());
					statsLabel2.setText("Games played: " + gamesPlayed());
				} catch (Exception e) {
					e.printStackTrace();
				}
				   statsLabel.setVisible(true);
				   statsLabel2.setVisible(true);
			   } else if(ev.getStateChange()==ItemEvent.DESELECTED){
				   statsLabel.setVisible(false);
				   statsLabel2.setVisible(false);
			   }
			}
		});
		resetStatsButton = new JButton("Reset Stats");
		resetStatsButton.setFont(segoe_def);
		resetStatsButton.setSize(upgrade.getPreferredSize());
		resetStatsButton.setLocation(200, 120);
		resetStatsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int reply = JOptionPane.showConfirmDialog(gameFrame, "Are you sure you want to reset stats?", "Are you sure?", JOptionPane.YES_NO_OPTION);
				int erroredOut = 0;
				if(reply == JOptionPane.YES_OPTION) {
					try {
						clearFiles();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(erroredOut == 0) {
					JOptionPane.showMessageDialog(gameFrame, "Stats were reset.");
					}
				} else {
					JOptionPane.showMessageDialog(gameFrame, "Nothing was changed.");
				}
			}
		});
		exitButton = new JButton("Exit Game");
		exitButton.setFont(segoe_def);
		exitButton.setSize(upgrade.getPreferredSize());
		exitButton.setLocation(200, 160);
		exitButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
					if(canvas.gameGoing == 1) {
						gamesPlayed++;
					}
					update();
					canvas.soldiersKilled = 0;
			    	System.exit(0);
			  } 
		});
		backButton = new JButton("Back");
		backButton.setFont(segoe_def);
		backButton.setLocation(0, 489);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					updateSoldiersKilledFile();
					updateGamesPlayedFile();
					canvas.turnOffGame();
					canvas.soldiersKilled = 0;
					canvas.gameGoing = 0;
					copyrightLabel.setVisible(true);
					exitButton.setVisible(true);
					resetStatsButton.setVisible(true);
					gamesPlayed++;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		backButton.setVisible(false);
		playLabel = new JLabel("Battles v1.8.8b");
		playLabel.setFont(segoe_def);
		playLabel.setSize(playLabel.getPreferredSize());
		playLabel.setLocation(230, 0);
		statsLabel = new JLabel("Soldiers killed: " + soldiersKilled());
		statsLabel.setFont(segoe_def);
		statsLabel.setSize(statsLabel.getPreferredSize());
		statsLabel.setLocation(210, 210);
		statsLabel.setVisible(false);
		statsLabel2 = new JLabel("Games played: " + gamesPlayed());
		statsLabel2.setFont(segoe_def);
		statsLabel2.setSize(statsLabel2.getPreferredSize());
		statsLabel2.setLocation(210, 250);
		statsLabel2.setVisible(false);
		copyrightLabel = new JLabel("Copyright (c) 2016 H.");
		copyrightLabel.setFont(segoe_def);
		copyrightLabel.setSize(copyrightLabel.getPreferredSize());
		copyrightLabel.setLocation(0, 497);
		Icon playIcon = new ImageIcon(GameCanvas.getCurrentWorkingDir() + "\\images\\play.gif");
		Icon statsIcon = new ImageIcon(GameCanvas.getCurrentWorkingDir() + "\\images\\stats.gif");
		Icon backIcon = new ImageIcon(GameCanvas.getCurrentWorkingDir() + "\\images\\back.gif");
		Icon exitIcon = new ImageIcon(GameCanvas.getCurrentWorkingDir() + "\\images\\exit.gif");
		Icon resetStatsIcon = new ImageIcon(GameCanvas.getCurrentWorkingDir() + "\\images\\reset_stats.gif");
		playButton.setIcon(playIcon);
		statsButton.setIcon(statsIcon);
		resetStatsButton.setIcon(resetStatsIcon);
		exitButton.setIcon(exitIcon);
		backButton.setIcon(backIcon);
		backButton.setSize(backButton.getPreferredSize());
		gameFrame.add(playButton);
		gameFrame.add(statsButton);
		gameFrame.add(resetStatsButton);
		gameFrame.add(exitButton);
		gameFrame.add(backButton);
		gameFrame.add(playLabel);
		gameFrame.add(statsLabel);
		gameFrame.add(statsLabel2);
		gameFrame.add(copyrightLabel);
		gameFrame.add(upgrade);
		gameFrame.getContentPane().add(canvas);
		try {
			ImageIcon img = new ImageIcon(GameCanvas.getCurrentWorkingDir() + "\\images\\icon.png");
			gameFrame.setIconImage(img.getImage());	
		} catch(Exception e) {}
		gameFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		gameFrame.setSize(647, 574);
		gameFrame.setResizable(false);
		gameFrame.setVisible(true);
		recursive_call(); // Calls the recursive method.
	}
	// The recursive method in the Battles class.
	// It is used for lots of stuff, mainly relating
	// to the canvas and cannon upgrades.
	public static void recursive_call() throws Exception {
		if(canvas.levelUpEligible == 0) {
			upgrade.setText("Upgrade - " + canvas.upgradeCost + " coins");
		} else {
			upgrade.setText("Level up - " + canvas.upgradeCost + " coins");
		}
		if(canvas.canAffordUpgrade) {
			upgrade.setSize(upgrade.getPreferredSize());
			upgrade.setEnabled(true);
		} else {
			upgrade.setEnabled(false);
		}
		Thread.sleep(200);
		recursive_call();
	}
	// Method to return soldiers killed from file.
	public static String soldiersKilled() throws Exception {
		String soldiersKilled = "0";
		Scanner soldierScanner = new Scanner(new FileReader(GameCanvas.getCurrentWorkingDir() + "\\saved_info\\soldiersKilled.txt"));
		while(soldierScanner.hasNextLine()){
			soldiersKilled = soldierScanner.nextLine();
		}
		soldierScanner.close();
		return soldiersKilled;
	}
	// The same type of method, but for games played.
	public static String gamesPlayed() throws Exception {
		String gamesPlayed = "0";
		Scanner gameScanner = new Scanner(new FileReader(GameCanvas.getCurrentWorkingDir() + "\\saved_info\\gamesPlayed.txt"));
		while(gameScanner.hasNextLine()){
			gamesPlayed = gameScanner.nextLine();
		}
		gameScanner.close();
		return gamesPlayed;
	}
	// Another similar method for current level.
	public static int currentLevel() throws Exception {
		int currentLevel = 0;
		Scanner currentLevelScanner = new Scanner(new FileReader(GameCanvas.getCurrentWorkingDir() + "\\saved_info\\currentLevel.txt"));
		while(currentLevelScanner.hasNextLine()){
			currentLevel = Integer.parseInt(currentLevelScanner.nextLine());
		}
		currentLevelScanner.close();
		return currentLevel;
	}
	// The below three methods update their respective files.
	public static void updateSoldiersKilledFile() throws Exception {
		writeToFile(Integer.toString(add(Integer.parseInt(soldiersKilled()), canvas.soldiersKilled)), GameCanvas.getCurrentWorkingDir() + "\\saved_info\\soldiersKilled.txt");
	}
	public static void updateGamesPlayedFile() throws Exception {
		writeToFile(Integer.toString(add(Integer.parseInt(gamesPlayed()), gamesPlayed)), GameCanvas.getCurrentWorkingDir() + "\\saved_info\\gamesPlayed.txt");
	}
	public static void updateCurrentLevelFile() throws Exception {
		writeToFile(Integer.toString(canvas.currentLevel), GameCanvas.getCurrentWorkingDir() + "\\saved_info\\currentLevel.txt");
	}
	// Clears out files. Used in the resetStatsButton action listener only.
	public static void clearFiles() throws Exception {
		writeToFile("0", GameCanvas.getCurrentWorkingDir() + "\\saved_info\\soldiersKilled.txt");
		writeToFile("0", GameCanvas.getCurrentWorkingDir() + "\\saved_info\\gamesPlayed.txt");
	}
	// Method for writing to files. Used in the above four methods.
	public static void writeToFile(String toWrite, String filePath) {
			File fout = new File(filePath);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(fout);

				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				bw.write(toWrite);
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	// Switches to the menu view by showing and hiding elements.
	public static void switchToMenu() {
		Battles.playButton.setVisible(true);
		Battles.statsButton.setVisible(true);
		Battles.playLabel.setVisible(true);
		Battles.upgrade.setVisible(false);
		Battles.backButton.setVisible(false);
	}
	// Updates all files (excluding the current level file).
	public static void update() {
		try {
			updateSoldiersKilledFile();
			updateGamesPlayedFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// A simple method for adding two numbers.
	public static int add(int num1, int num2) {return num1 + num2;}
}