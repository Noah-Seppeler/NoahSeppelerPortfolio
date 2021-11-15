

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MineButton extends Button {
	private boolean isABomb;
	private boolean isFlagged;
	private int r=0;
	private int c=0;
	private boolean clicked =false;
	public static boolean endOfGame=false;
	Image[] images = new Image[11];
	
	public MineButton() {
		this.setMaxSize(30, 30);
		for(int i=0;i<images.length;i++) { 
			try {
				images[i]= new Image(new FileInputStream("MineSweeper\\"+(i)+".png"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Image" +i+ "not found.");
			}
		}
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public boolean isABomb() {
		return isABomb;
	}

	public void setABomb(boolean isABomb) {
		this.isABomb = isABomb;
	}

	public boolean isFlagged() {
		return isFlagged;
	}
	
	public void setFlagged(MineField m) {
		if(!this.isFlagged&&!this.clicked) {
			this.setGraphic(new ImageView(images[9]));
			isFlagged=true;
			m.setBombCountDown(m.getBombCountDown()-1);
		}else {
			this.setGraphic(null);
			isFlagged=false;
			m.setBombCountDown(m.getBombCountDown()+1);
		}
		
	}
	
	public void setBombImage() {
		this.setGraphic(new ImageView(images[10]));
	}
	
	public void setNumberImage(MineButton[][] b) {
		int count=0;
		int bR=r-1;
		int bC=c-1;
		int endingR=r+1;
		int endingC=c+1;
		if(bR<0) {
			bR++;
		} else if(endingR+1>b.length) {
			endingR--;
		}
		if(bC<0) {
			bC++;
		} else if(endingC+1>b[bR].length) {
			endingC--;
		}
		for(int row=bR; row<=endingR;row++) {
			for(int col=bC;col<=endingC;col++) {
				if(b[row][col].isABomb()) {
					count++;
				}
			}
		}
		clicked=true;
		if(count==0) {
			b[r][c].setGraphic(new ImageView(images[0]));
			for(int row=bR; row<=endingR;row++) {
				for(int col=bC;col<=endingC;col++) {
					if(b[row][col].clicked==false) {
						b[row][col].setNumberImage(b);
					}
				}
			}
		}
		
		this.setGraphic(new ImageView(images[count]));
	}


	public boolean getClicked() {
		
		return clicked;
	}	

}

