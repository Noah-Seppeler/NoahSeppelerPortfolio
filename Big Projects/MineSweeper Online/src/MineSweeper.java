

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MineSweeper extends Application {

	private int difficulty=0;
	private TextField boardX= new TextField();
	private TextField boardY= new TextField();
	private TextField boardB= new TextField();
	private Timeline updateTime = new Timeline();
	private Timeline updateBombs = new Timeline();
	private int numRows=9;
	private int numCols=9;
	private int numB=10;
	private StackPane mainPane=new StackPane();
	MineField mineField;
	private Stage m;
	static int time = 0;
	Text timer = new Text("Time: "+time);
	Text numBombs=new Text("sum");
	
	//online
	private boolean coop = false;
	private boolean vs = false;
	private int seed;
	private TextField ipField = new TextField();
	DataInputStream in;
	DataOutputStream out;
	public static int r;
	public static int c;
	public static boolean flagClick;
	public static boolean moved;
	
	

	
	
	public void start(Stage primaryStage) {
		mainPane.getChildren().add(setStartScreen());
		Scene main = new Scene(mainPane,400,600);
		primaryStage.setScene(main);
		primaryStage.show();
		m=primaryStage;
		m.setTitle("Mine Sweeper");
		
	}

	private void updateBombs() {
		numBombs.setText("Bombs: "+mineField.getBombCountDown());
	}
	
	private void updateTime() {
		if(!MineButton.endOfGame) {
			time++;
			timer.setText("Time: "+time);
		}
	}


	private Pane setStartScreen() {
		HBox start = new HBox();
		VBox startScreen = new VBox(40);
		startScreen.getChildren().addAll(getTitle(),getDifficultyButtons(),new Text("Custom Settings:"),getCustomFields(),getStartButton(), getOnlineButton());
		startScreen.setAlignment(Pos.CENTER);
		start.setAlignment(Pos.CENTER);
		start.getChildren().add(startScreen);
		return start;
	}

	private Node getOnlineButton() {
		Button online = new Button("Go Online");
		online.setOnAction(e-> getOnlinePane());
		return online;
	}

	private void getOnlinePane() {
		mainPane.getChildren().clear();
		HBox h = new HBox();
		VBox v = new VBox(40);
		
		h.getChildren().add(v);
		h.setAlignment(Pos.CENTER);
		v.setAlignment(Pos.CENTER);
		v.getChildren().addAll(getOnlineTitle(), getIPBox(), getJoinButton(),getBackButton());
		
		mainPane.getChildren().add(v);
		
		
	}

	private Node getBackButton() {
		Button back = new Button("Back");
		back.setOnAction(e->changeDifficulty());
		
		return back;
	}

	private Node getJoinButton() {
		Button join = new Button("Join");
		join.setOnAction(e->setJoinAction());
		
		return join;
	}

	private void setJoinAction() {
		// TODO set Join properties
		
		try {
			Socket socket = new Socket(ipField.getText(),25565);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			numRows = in.readInt();
			numCols = in.readInt();
			numB = in.readInt();
			seed = in.readInt();
			
			mineField = new MineField(numRows,numCols,numB);
			
			mineField.createBoard(seed);
			
			updateTime.stop();
			updateBombs.stop();
			updateTimeAnimation();
			updateBombAnimation();
			time=0;
			m.setWidth((numCols*30)+100);
			m.setHeight((numRows*30)+100);
			createPlayArea();
			
			runGameThread();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void runGameThread() {
		Timeline n = new Timeline(
			      new KeyFrame(Duration.millis(1), e->  {
			    	  //System.out.println();
			    	  if(moved) {
							try {
								System.out.println(flagClick);
								out.writeInt(r);
								out.writeInt(c);
								out.writeBoolean(flagClick);
								flagClick = false;
								//System.out.println("Move Made");
							} catch (IOException ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
							}
							
							moved = false;
						}
			      }));
		
		n.setCycleCount(Timeline.INDEFINITE);
		n.play();
		
		new Thread(()-> {
			while(true) {
				try {
					int r = in.readInt();
					int c = in.readInt();
					boolean fT = in.readBoolean();
					
					MineButton bT = mineField.buttons[r][c];
					
					Platform.runLater(()->{
						mineField.pressButton(bT, fT);
					});
					
					
					//System.out.println(r+" "+c);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Node getIPBox() {
		VBox v = new VBox();
		HBox h = new HBox();
		
		v.setAlignment(Pos.CENTER);
		h.setAlignment(Pos.CENTER);
		
		v.getChildren().addAll(h);
		h.getChildren().addAll(new Label("Host IP: "), ipField);
		return h;
	}

	private Node getOnlineTitle() {
		Text title = new Text("Online Game");
		title.setFont(new Font(20));
		return title;
	}

	private Node getTitle() {
		Text title = new Text("Mine Sweeper");
		title.setFont(new Font(20));
		return title;
	}

	private Node getStartButton() {
		Button start = new Button("Start");
		start.setOnAction(e->startAction());
		return start;
	}


	private void startAction() {
				
		if(difficulty==3) {
			try {
				numRows = Integer.parseInt(boardX.getText());
				numCols = Integer.parseInt(boardY.getText());
				numB = Integer.parseInt(boardB.getText());
			}catch(NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Not valid numbers");
				alert.setHeaderText("Please enter valid numbers");
				alert.setContentText("Please enter valid numbers");

				alert.showAndWait();
			}
			mineField = new MineField(numRows,numCols,numB);
		}else {
			mineField = new MineField(difficulty);
		}
		mineField.createBoard();
		updateTime.stop();
		updateBombs.stop();
		updateTimeAnimation();
		updateBombAnimation();
		time=0;
		m.setWidth((numCols*30)+100);
		m.setHeight((numRows*30)+100);
		createPlayArea();
		
	}





	private void updateTimeAnimation() {
		updateTime = new Timeline(
			      new KeyFrame(Duration.millis(1000),e-> updateTime()));
		updateTime.setCycleCount(Timeline.INDEFINITE);
		updateTime.play();
		
	}

	private void updateBombAnimation() {
		updateBombs = new Timeline(
			      new KeyFrame(Duration.millis(1),e-> updateBombs()));
		updateBombs.setCycleCount(Timeline.INDEFINITE);
		updateBombs.play();
	}

	private void createPlayArea() {
		VBox playArea = new VBox();
		playArea.setAlignment(Pos.CENTER);
		mainPane.getChildren().clear();
		playArea.getChildren().addAll(topBar(),mineField.showBoard(),changeDifficultyButton());

		mainPane.getChildren().add(playArea);
		mainPane.setAlignment(Pos.CENTER);
		
	}


	private Node changeDifficultyButton() {
		Button change = new Button("Change Difficulty");
		change.setOnAction(e->changeDifficulty());
		return change;
	}


	private void changeDifficulty() {
		mainPane.getChildren().clear();
		MineButton.endOfGame=false;
		//m.setHeight(500);
		//m.setWidth(400);
		mainPane.getChildren().add(setStartScreen());
	}


	private Node topBar() {
		HBox top = new HBox(20);
		top.getChildren().addAll(timer,getResetButton(),numBombs);
		top.setAlignment(Pos.CENTER);
		return top;
	}

	private Node getResetButton() {
		Button reset = new Button("Reset");
		reset.setOnAction(e->resetGame());
		return reset;
	}
	
	private void resetGame() {
		mainPane.getChildren().clear();
		mineField.createBoard();
		MineButton.endOfGame=false;
		updateTime.stop();
		updateTimeAnimation();
		updateBombs.stop();
		updateBombAnimation();
		updateTime.play();
		updateBombs.play();
		mineField.reset();
		time=0;
		timer.setText("Time: "+0);
		createPlayArea();
	}


	private Node getCustomFields() {
		VBox field = new VBox(10);
		HBox rows = new HBox(5);
		HBox cols = new HBox(5);
		HBox bombs = new HBox(5);
		rows.getChildren().addAll(new Label("Rows:"),boardX);
		cols.getChildren().addAll(new Label("Cols:"),boardY);
		bombs.getChildren().addAll(new Label("Bombs: "),boardB);
		field.getChildren().addAll(rows,cols,bombs);
		return field;
	}


	private Node getDifficultyButtons() {
		VBox difficulty = new VBox(10);
		RadioButton easy = new RadioButton("Easy");
		RadioButton medium = new RadioButton("Medium");
		RadioButton hard = new RadioButton("Hard");
		RadioButton custom = new RadioButton("Custom");
		ToggleGroup diff = new ToggleGroup();
		easy.setToggleGroup(diff);
		medium.setToggleGroup(diff);
		hard.setToggleGroup(diff);
		custom.setToggleGroup(diff);
		EventHandler<ActionEvent> handler = e->{setDifficultyButtonActions(easy,medium,hard,custom);};
		easy.setOnAction(handler);
		medium.setOnAction(handler);
		hard.setOnAction(handler);
		custom.setOnAction(handler);
		difficulty.getChildren().addAll(easy,medium,hard,custom);
		return difficulty;
	}


	private void setDifficultyButtonActions(RadioButton e, RadioButton m, RadioButton h, RadioButton c) {
		if(e.isSelected()) {
			difficulty = 0;
			numRows = 9;
			numCols = 9;
			numB = 10;
		}else if(m.isSelected()) {
			difficulty = 1;
			numRows = 16;
			numCols = 16;
			numB = 40;
		}else if(h.isSelected()) {
			difficulty=2;
			numRows = 16;
			numCols = 30;
			numB = 99;
		}else if(c.isSelected()) {
			difficulty=3;
		}
		
	}
	
	public static int getTime() {
		return time;
	}


	public static void main(String[] args) {
		
		launch();

	}


}
