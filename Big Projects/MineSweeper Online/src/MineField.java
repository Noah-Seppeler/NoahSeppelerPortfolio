

import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MineField {
	private int numBombs=0;
	private int bombCountDown=0;
	private int seed;

	public MineButton[][] buttons;
	
	public int getBombCountDown() {
		return bombCountDown;
	}
	
	public void setBombCountDown(int numBombs) {
		this.bombCountDown = numBombs;
	}


	public MineField(int s) {
		if(s==0) {
			buttons = new MineButton[9][9];
			numBombs=10;
			bombCountDown=10;
		} else if(s==1) {
			buttons=new MineButton[16][16];
			numBombs=40;
			bombCountDown=40;
		} else if(s==2) {
			buttons = new MineButton[16][30];
			numBombs=99;
			bombCountDown=99;
		}
	}
	
	public MineField(int r, int c, int nB) {
		numBombs=nB;
		bombCountDown=nB;
		buttons = new MineButton[r][c];
	}

	public void createBoard() {
		for(int r=0;r<buttons.length;r++) {
			for(int c=0;c<buttons[r].length;c++) {
				buttons[r][c]=new MineButton();
				MineButton b = buttons[r][c];
				b.setR(r);
				b.setC(c);
				buttons[r][c].setMinSize(30.0, 30.0);
				buttons[r][c].setOnMouseClicked(e->setButtonAction(e,b));
				buttons[r][c] = b;
				
			}
		}
		placeBombs();
		
	}
	
	public void createBoard(int s) {
		for(int r=0;r<buttons.length;r++) {
			for(int c=0;c<buttons[r].length;c++) {
				buttons[r][c]=new MineButton();
				MineButton b = buttons[r][c];
				b.setR(r);
				b.setC(c);
				buttons[r][c].setMinSize(30.0, 30.0);
				buttons[r][c].setOnMouseClicked(e->setButtonAction(e,b));
				buttons[r][c] = b;
				
			}
		}
		placeBombs(s);
		
	}

	private void placeBombs() {
		bombCountDown = numBombs;
		Random rand = new Random();
		int r;
		int c;
		for(int i=0;i<numBombs;i++) {
			r=rand.nextInt(buttons.length);
			c=rand.nextInt(buttons[1].length);
			if(!buttons[r][c].isABomb()) {
				buttons[r][c].setABomb(true);
			}else {
				i--;
			}
		}
		
	}
	
	private void placeBombs(int s) {
		bombCountDown = numBombs;
		Random rand = new Random(s);
		int r;
		int c;
		for(int i=0;i<numBombs;i++) {
			r=rand.nextInt(buttons.length);
			c=rand.nextInt(buttons[1].length);
			if(!buttons[r][c].isABomb()) {
				buttons[r][c].setABomb(true);
			}else {
				i--;
			}
		}
		
	}

	private void setButtonAction(MouseEvent event, MineButton b) {
		MouseButton button = event.getButton();
		MineSweeper.r = b.getR();
		MineSweeper.c = b.getC();
		if(button==MouseButton.SECONDARY) {
			System.out.println("flag");
			MineSweeper.flagClick = true;
		}
		MineSweeper.moved = true;
		//System.out.println(MineSweeper.r+""+MineSweeper.c+""+MineSweeper.moved);
		if(!MineButton.endOfGame) {
			if(button==MouseButton.SECONDARY&&!b.getClicked()) {
				b.setFlagged(this);
			} else if(button==MouseButton.PRIMARY) {
				if(!b.isFlagged()&&b.isABomb()) {
					endGame();
				}else if(!b.isFlagged()) {
					activateButton(b);
				}
			}
			testWin();
		}

		
	}
	
	public void pressButton(MineButton b, boolean flag) {
		if(!MineButton.endOfGame) {
			if(flag&&!b.getClicked()) {
				b.setFlagged(this);
			} else if(!flag) {
				if(!b.isFlagged()&&b.isABomb()) {
					endGame();
				}else if(!b.isFlagged()) {
					activateButton(b);
				}
			}
			testWin();
		}
	}

	private void testWin() {
		int winCount = 0;
		for(int r=0;r<buttons.length;r++) {
			for(int c=0;c<buttons[r].length;c++) {
				if(buttons[r][c].isABomb()&&buttons[r][c].isFlagged()) {
					winCount++;
				}
			}
		}
		if(winCount==numBombs) {
			winGame();
		}		
	}

	private void winGame() {
		MineButton.endOfGame=true;
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("YOU WIN!!!!");
		alert.setHeaderText("Your time was:");
		alert.setContentText(""+MineSweeper.getTime());

		alert.showAndWait();
		
	}

	private void activateButton(MineButton b) {
		b.setNumberImage(buttons);
		
	}

	private void endGame() {
		MineButton.endOfGame=true;
		for(int r=0;r<buttons.length;r++) {
			for(int c=0;c<buttons[r].length;c++) {
				if(buttons[r][c].isABomb()) {
					buttons[r][c].setBombImage();
				}
				
			}
		}
		
	}

	public Pane showBoard() {
		HBox b= new HBox();
		GridPane g= new GridPane();
		
		for(int r=0;r<buttons.length;r++) {
			for(int c=0;c<buttons[r].length;c++) {
				g.add(buttons[r][c], c, r);
			}
		}
		b.getChildren().add(g);
		b.setAlignment(Pos.CENTER);
		return b;
	}
	
	public void setEnabled() {
		for(int r=0;r<buttons.length;r++) {
			for(int c=0;c<buttons[r].length;c++) {
				buttons[r][c].setDisable(false);
			}
		}
	}
	
	public void setDisabled() {
		for(int r=0;r<buttons.length;r++) {
			for(int c=0;c<buttons[r].length;c++) {
				buttons[r][c].setDisable(true);
			}
		}
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	
}
