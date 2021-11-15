import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MineSweeperServer extends Application{
	
	private TextArea output = new TextArea();
	private StackPane mainPane = new StackPane();
	private TextField height = new TextField();
	private TextField width = new TextField();
	private TextField bombs = new TextField();
	private TextField seed = new TextField();
	private Stage m;
	private boolean coop = false;
	private boolean vs = false;
	private int heightNum;
	private int widthNum;
	private int bombsNum;
	private int seedNum;
	
	DataInputStream player1In;
	DataOutputStream player1Out;
	DataInputStream player2In;
	DataOutputStream player2Out;

	public static void main(String[] args) {
		launch(args);

	}
	
	

	@Override
	public void start(Stage mainStage) throws Exception {
		
		showServerSetup();
		
		Scene mainScene = new Scene(mainPane, 300, 300);
		
		m = mainStage;
		
		mainStage.setScene(mainScene);
		
		mainStage.show();
		
	}
	
	private void showServerSetup() {
		HBox h = new HBox();
		VBox v = new VBox(20);
		
		Button run = new Button("Start Server");
		
		run.setOnAction(e->runServer());
		
		v.getChildren().addAll(getModeButtons(),getCustomFields(), run);
		h.getChildren().addAll(v);
		
		mainPane.getChildren().add(h);
		
		v.setAlignment(Pos.CENTER);
		h.setAlignment(Pos.CENTER);
		
	}



	private Node getModeButtons() {
		VBox v = new VBox(20);
		HBox coopH = new HBox();
		HBox vsH = new HBox();
		
		coopH.setAlignment(Pos.CENTER);
		vsH.setAlignment(Pos.CENTER);
		
		RadioButton coop = new RadioButton();
		RadioButton vs = new RadioButton();
		ToggleGroup mode = new ToggleGroup();
		
		coop.setToggleGroup(mode);
		vs.setToggleGroup(mode);
		
		EventHandler<ActionEvent> handler = e->setModeButtonsAction(coop, vs);
		
		coop.setOnAction(handler);
		vs.setOnAction(handler);
		
		coopH.getChildren().addAll(coop, new Label("Co-op"));
		vsH.getChildren().addAll(vs,new Label("VS"));
		
		v.getChildren().addAll(coopH, vsH);
		return v;
	}
	
	private void setModeButtonsAction(RadioButton c, RadioButton v) {
		if(c.isSelected()) {
			coop = true;
		}else if(v.isSelected()) {
			vs = true;
		}
	}



	private Node getCustomFields() {
		VBox main = new VBox(5);
		
		HBox heightBox = new HBox();
		HBox widthBox = new HBox();
		HBox bombBox = new HBox();
		HBox seedBox = new HBox();
		
		heightBox.getChildren().addAll(new Label("Height:"),height);
		widthBox.getChildren().addAll(new Label("Width:"),width);
		bombBox.getChildren().addAll(new Label("Bombs:"),bombs);
		seedBox.getChildren().addAll(new Label("Seed:"), seed);
		
		main.getChildren().addAll(heightBox, widthBox, bombBox, seedBox);
		
		return main;
	}



	private void runServer() {
		
		try {
			heightNum = Integer.parseInt(height.getText());
			widthNum = Integer.parseInt(width.getText());
			bombsNum = Integer.parseInt(bombs.getText());
			seedNum = Integer.parseInt(seed.getText());
		}catch(NumberFormatException ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Numbers");
			alert.setHeaderText("Invalid Numbers");
			alert.setContentText("Please enter correct numbers next time");
			alert.showAndWait();
			System.exit(0);
		}
		
		
		Pane p = new Pane();
		
		p.getChildren().add(output);
		
		output.setWrapText(true);
		output.setEditable(false);
		output.setMinHeight(400);
		output.setMinWidth(600);
		
		m.setHeight(400);
		m.setWidth(600);
		
		runJoinThread();
		
		mainPane.getChildren().clear();
		
		mainPane.getChildren().addAll(p);
	}

	private void runJoinThread() {
		new Thread(()->{
			try {
				ServerSocket serverSocket = new ServerSocket(25565);
				Platform.runLater(()->{
					output.appendText("Server Started\n");
				});
				
				Socket player1 = serverSocket.accept();
				Platform.runLater(()->{
					output.appendText("Player1 Joined\n");
				});
				
				player1In = new DataInputStream(player1.getInputStream());
				player1Out = new DataOutputStream(player1.getOutputStream());
				
				Socket player2 = serverSocket.accept();
				Platform.runLater(()->{
					output.appendText("Player2 Joined\n");
				});
				
				player2In = new DataInputStream(player2.getInputStream());
				player2Out = new DataOutputStream(player2.getOutputStream());
				
				Platform.runLater(()->{
					output.appendText("All Players Joined. Building map . . .\n");
				});
				
				
				player1Out.writeInt(heightNum);
				player1Out.writeInt(widthNum);
				player1Out.writeInt(bombsNum);
				player1Out.writeInt(seedNum);
				
				player2Out.writeInt(heightNum);
				player2Out.writeInt(widthNum);
				player2Out.writeInt(bombsNum);
				player2Out.writeInt(seedNum);
				
				
				Platform.runLater(()->{
					output.appendText("Map built. Making player threads . . .\n");
				});
				
				makePlayerThreads();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
	}



	private void makePlayerThreads() {
		new Thread(()-> {
			while(true) {
				try {
					//System.out.println("Finding data");
					int r = player1In.readInt();
					//System.out.println("r found");
					int c = player1In.readInt();
					//System.out.println("Found data");
					boolean f = player1In.readBoolean();
					
					player2Out.writeInt(r);
					player2Out.writeInt(c);
					player2Out.writeBoolean(f);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		new Thread(()-> {
			while(true) {
				try {
					int r = player2In.readInt();
					int c = player2In.readInt();
					boolean f = player2In.readBoolean();
					
					player1Out.writeInt(r);
					player1Out.writeInt(c);
					player1Out.writeBoolean(f);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}

}
