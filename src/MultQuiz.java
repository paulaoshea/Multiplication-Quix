
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class MultQuiz extends Application {

	private VBox primaryVBox;
	private VBox topScoreVBox;
	private VBox championScoreVBox;

	private TextField textTimes;
	private TextField textQues;
	private Label labelMessage;
	private Label topScoreMessage;
	private Label labelName;
	private Label labelTimes;
	private Label labelQues;
	private Label labelClock;
	private Label timedMessage;
	private RadioButton timedRadioButtonYes;
	private RadioButton timedRadioButtonNo;
	private ComboBox<String> nameComboBox;
	private ToggleGroup group;
	private Button goAgainButton;
	private Button topScoresButton;
	private Button mistakeButton;
	private Button championScoresButton;
	private TextArea topScoresText;
	private TextArea championScoresText;
	private TextArea mistakeText;

	private HBox nameHBox;
	private HBox timesHBox;
	private HBox timedHBox;
	private HBox topScoresHBox;
	private HBox mistakeHBox;

	private String returnedName;
	private String returnedTimes;
	private String returnedAnswer;

	private int intReturnedTimes;
	private int randomNumber;
	private int correct;
	private int questionNumber;
	private int intReturnedAnswer;

	private List<User> topScoreList;
	private List<User> championScoreList;

	// *********************************************
	private final static int HOW_MANY_GOES = 10;
	// *********************************************

	private ArrayList<User> userList;
	private Set<String> nameSet;
	private Set<String> mistakeSet;


	private LocalTime startTime;

	Image logoImage = new Image("logo.jpg", 200, 200, true, true);
	ImageView logoImageView = new ImageView(logoImage);

	Scanner fileScan;
	PrintWriter out;

	Random generator = new Random();

	// *******************************************************************************************
	// *******************************************************************************************

	@Override
	public void start(Stage primaryStage) {

		userList = new ArrayList<>();
		nameSet = new TreeSet<>();
		mistakeSet = new TreeSet<>();

		returnedName = "";
		returnedTimes = "";
		intReturnedTimes = 0;
		randomNumber = 0;
		correct = 0;
		questionNumber = 1;
		
		Font font2 = Font.font("Times", 33);

		Scanner lineScan;

		//Read in all the previous user attempts
		try {
			fileScan = new Scanner(new FileInputStream("multiplicationData.txt"));
			
			while (fileScan.hasNext()) { // outer loop reads one file line
				String oneLine = fileScan.nextLine();

				lineScan = new Scanner(oneLine);
				lineScan.useDelimiter(",");

				String name = lineScan.next();
				String timesTable = lineScan.next();
				String bestScore = lineScan.next();
				String timeTaken = lineScan.next();
				String dayDone = lineScan.next();
				/* IMPORTANT NOTE! see above- this only works with perfectly formed data */

				User user = new User(name, Integer.parseInt(timesTable), Integer.parseInt(bestScore),
						Integer.parseInt(timeTaken), LocalDate.parse(dayDone));
				userList.add(user);
				nameSet.add(name);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File multiplicationData.txt cannot be found");
		} finally {
			fileScan.close();
		}

		Collections.sort(userList);

	//	for (User user : userList) {
	//		System.out.println("user = " + user);
	//	}

		group = new ToggleGroup();

		goAgainButton = new Button("Press to play again!");
		goAgainButton.setVisible(false);
		goAgainButton.setOnAction(this::processReturnToDoAgain);

		nameComboBox = new ComboBox<>();
		nameComboBox.getItems().addAll(nameSet);
		nameComboBox.setEditable(true);
		nameComboBox.setValue("");

		primaryVBox = new VBox();
		primaryVBox.setAlignment(Pos.CENTER);
		primaryVBox.setSpacing(20);
		primaryVBox.setPadding(new Insets(20, 20, 20, 20));
		primaryVBox.setStyle("-fx-background-color: SteelBlue");

		labelMessage = new Label("");
		labelMessage.setFont(font2);

		primaryVBox.getChildren().add(labelMessage);

		topScoreMessage = new Label("");
		topScoreMessage.setFont(font2);

	//	primaryVBox.getChildren().add(topScoreMessage);

		labelName = new Label("Enter your name:");
		labelName.setFont(font2);

		labelTimes = new Label("Which times table?:");
		labelTimes.setFont(font2);

		labelClock = new Label("Would you like to be Timed?:");
		labelClock.setFont(font2);

		timedMessage = new Label();
		timedMessage.setFont(font2);

		textTimes = new TextField();
		textTimes.setFont(font2);
		textTimes.setPrefWidth(300);
		textTimes.setAlignment(Pos.CENTER);
		textTimes.setOnAction(this::processReturn);

		textQues = new TextField();
		textQues.setFont(font2);
		textQues.setPrefWidth(300);
		textQues.setAlignment(Pos.CENTER);
		textQues.setOnAction(this::processReturn2);

		labelQues = new Label("");
		labelQues.setFont(font2);

		timedRadioButtonYes = new RadioButton("Timed");
		timedRadioButtonYes.setToggleGroup(group);
		timedRadioButtonYes.setSelected(true);
		timedRadioButtonNo = new RadioButton("Not Timed");
		timedRadioButtonNo.setToggleGroup(group);

		timedRadioButtonYes.setOnAction(this::processReturn);
		timedRadioButtonNo.setOnAction(this::processReturn);

		nameHBox = new HBox(labelName, nameComboBox);
		nameHBox.setSpacing(30);
		nameHBox.setAlignment(Pos.CENTER);
		nameHBox.setPadding(new Insets(40, 0, 0, 0));

		timesHBox = new HBox(labelTimes, textTimes);
		timesHBox.setSpacing(30);
		timesHBox.setAlignment(Pos.CENTER);
		timesHBox.setPadding(new Insets(40, 0, 0, 0));

		timedHBox = new HBox(labelClock, timedRadioButtonYes, timedRadioButtonNo);
		timedHBox.setSpacing(30);
		timedHBox.setAlignment(Pos.CENTER);
		timedHBox.setPadding(new Insets(40, 40, 60, 0));

		primaryVBox.getChildren().add(nameHBox);
		primaryVBox.getChildren().add(timesHBox);
		primaryVBox.getChildren().add(timedHBox);

		primaryVBox.getChildren().add(logoImageView);

		Scene scene = new Scene(primaryVBox, 1100, 900);
		primaryStage.setScene(scene);
		primaryStage.setTitle(" Pooface@s.sfusd.edu Rocks ");
		primaryStage.show();

	}

	//
	// --------------------------------------------------------------------
	public void doTest() {

		// Generate a random number between 1 and 12
		// Random generator = new Random();
		randomNumber = generator.nextInt(12) + 1;

		labelQues.setText(intReturnedTimes + " * " + randomNumber + " = ");
	}

	// --------------------------------------------------------------------
	//
	// --------------------------------------------------------------------
	public void processReturn(ActionEvent event) {
		// Play the game!

		correct = 0;
		questionNumber = 1;

		labelMessage.setText("");
		topScoreMessage.setText("");

		returnedName = nameComboBox.getValue();

		returnedTimes = textTimes.getText();
		textTimes.clear();

		topScoresButton = new Button("Press to see " + returnedName + "'s Top Scores!");
		topScoresButton.setVisible(false);
		topScoresButton.setOnAction(this::processShowTopScores);

		championScoresButton = new Button("Press to see Champion Scores!");
		championScoresButton.setVisible(false);
		championScoresButton.setOnAction(this::processShowChampionScores);
		
		mistakeButton = new Button("Press to see where you went wrong");
		mistakeButton.setVisible(false);
		mistakeButton.setOnAction(this::processShowMistakes);

		try {
			// checking valid integer using parseInt() method
			intReturnedTimes = Integer.parseInt(returnedTimes);
		} catch (NumberFormatException e) {
			intReturnedTimes = 0;
			returnedTimes = "";
		}

		if ((returnedName.length() > 0) && (returnedTimes.length() > 0) && intReturnedTimes != 0) {
			labelMessage.setText("Great - Let's Play!");

			primaryVBox.getChildren().clear();

			doTest();

			primaryVBox.getChildren().add(timedMessage);
			primaryVBox.getChildren().add(labelQues);
			primaryVBox.getChildren().add(textQues);
			primaryVBox.getChildren().add(goAgainButton);
			primaryVBox.getChildren().add(labelMessage);
			primaryVBox.getChildren().add(topScoreMessage);

			topScoreVBox = new VBox();
			topScoreVBox.setAlignment(Pos.CENTER);
			topScoreVBox.setSpacing(20);
			topScoreVBox.setPadding(new Insets(20, 20, 20, 20));

			championScoreVBox = new VBox();
			championScoreVBox.setAlignment(Pos.CENTER);
			championScoreVBox.setSpacing(20);
			championScoreVBox.setPadding(new Insets(20, 20, 20, 20));

			topScoresText = new TextArea();
			topScoresText.setEditable(false);
			topScoresText.setText("");
			topScoresText.setVisible(false);
			topScoresText.setStyle("-fx-text-inner-color: red;");

			championScoresText = new TextArea();
			championScoresText.setEditable(false);
			championScoresText.setText("");
			championScoresText.setVisible(false);
			championScoresText.setStyle("-fx-text-inner-color: red;");

			topScoreVBox.getChildren().add(topScoresButton);
			topScoreVBox.getChildren().add(topScoresText);

			championScoreVBox.getChildren().add(championScoresButton);
			championScoreVBox.getChildren().add(championScoresText);

			topScoresHBox = new HBox(topScoreVBox, championScoreVBox);
			topScoresHBox.setSpacing(30);
			topScoresHBox.setAlignment(Pos.CENTER);
			topScoresHBox.setPadding(new Insets(20, 20, 20, 20));

			primaryVBox.getChildren().add(topScoresHBox);
			
			mistakeText = new TextArea();
			mistakeText.setEditable(false);
			mistakeText.setText("");
			mistakeText.setVisible(false);
			mistakeText.setStyle("-fx-text-inner-color: red;");
			
			mistakeHBox = new HBox(mistakeButton, mistakeText);
			mistakeHBox.setSpacing(30);
			mistakeHBox.setAlignment(Pos.CENTER);
			mistakeHBox.setPadding(new Insets(20, 20, 20, 20));

			primaryVBox.getChildren().add(mistakeHBox);
			
			goAgainButton.setVisible(false);
			topScoresButton.setVisible(false);
			mistakeButton.setVisible(false);
			championScoresButton.setVisible(false);
			textQues.setVisible(true);
			labelQues.setVisible(true);

			startTime = LocalTime.now();
		//	System.out.println("starting at : " + startTime);

		} else {
			labelMessage.setText("Name or number of times is invalid");

		}

	}

	// --------------------------------------------------------------------
	//
	// --------------------------------------------------------------------
	public void processShowTopScores(ActionEvent event) {
		//Show top scores if the top scores button is pressed
		topScoresText.setVisible(true);

		String topScoreString = "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, u");
		String formattedDate;
		for (User u : topScoreList) {
			String plural = "";
			if (u.getTimeTaken() == 1) {
				plural = " second";
			} else {
				plural = " seconds";
			}
			formattedDate = u.getDayDone().format(formatter);

			topScoreString = topScoreString + "You got a score of " + u.getBestScore() + " in " + u.getTimeTaken()
					+ plural + " on " + formattedDate + "\n";
		}
		topScoresText.setText(topScoreString);

	}

	// --------------------------------------------------------------------
	//
	// --------------------------------------------------------------------
	public void processShowChampionScores(ActionEvent event) {
		//Show champion scores if the top champion button is pressed
		championScoresText.setVisible(true);
		String championScoreString = "";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, u");
		String formattedDate;
		for (User u : championScoreList) {
			String plural = "";
			if (u.getTimeTaken() == 1) {
				plural = " second";
			} else {
				plural = " seconds";
			}
			formattedDate = u.getDayDone().format(formatter);

			championScoreString = championScoreString + u.getName() + " got a score of " + u.getBestScore() + " in "
					+ u.getTimeTaken() + plural + " on " + formattedDate + "\n";
		}
		championScoresText.setText(championScoreString);

	}

	// --------------------------------------------------------------------
	//
	// --------------------------------------------------------------------
	public void processReturn2(ActionEvent event) {
		//User presses enter after answering question to get here ...
		int result = 0;
		boolean topUserScore = false;
		boolean topChampionScore = false;

		returnedAnswer = textQues.getText();

		// System.out.println("returnedAnswer = " + returnedAnswer);

		result = intReturnedTimes * randomNumber;
		try {
			intReturnedAnswer = Integer.parseInt(returnedAnswer);
		} catch (NumberFormatException e) {
			intReturnedAnswer = 0;
		}
		// System.out.println("result = " + result);
		// System.out.println("returned answer = " + intReturnedAnswer);
		if (result == intReturnedAnswer) {
			correct++;
		} else {
			mistakeSet.add(intReturnedTimes + " * " + randomNumber +
					" = " + result + "\t(but you said " 
					+ intReturnedTimes + " * " + randomNumber + " = " +
					+ intReturnedAnswer + " )");
		}
		doTest();
		// System.out.println("question number before : " + questionNumber);
		questionNumber++;
		// System.out.println("question number after : " + questionNumber);
		textQues.setText("");
		
		//----------------------------------------------------------------
		if (questionNumber > HOW_MANY_GOES) {

			LocalTime endTime = LocalTime.now();
		//	System.out.println("ending at : " + endTime);
			long numSecs = ChronoUnit.SECONDS.between(startTime, endTime);
		//	System.out.println("number of secs : " + numSecs);

			if (timedRadioButtonYes.isSelected()) {
				timedMessage.setText("That took only : " + numSecs + " seconds ");
			} else {
				numSecs = 0;
			}

			textQues.setVisible(false);
			labelQues.setVisible(false);

			goAgainButton.setVisible(true);
			topScoresButton.setVisible(true);
			championScoresButton.setVisible(true);
			
			if (HOW_MANY_GOES > correct) {
				mistakeButton.setVisible(true);
			}

		//	System.out.println("end of game!");
		//	System.out.println("you got : " + correct);
			// labelMessage.setText("You got : " + correct + " Out of " + HOW_MANY_GOES + "
			// - Well done!");
			labelMessage.setText("You got : " + correct + " Out of " + HOW_MANY_GOES);

			int intSeconds = 0;
			LocalDate dayDone = LocalDate.now();
			try {
				out = new PrintWriter(new FileOutputStream("multiplicationData.txt", true));
				intSeconds = (int) numSecs;
				out.print(returnedName + "," + intReturnedTimes + "," + correct + "," + intSeconds + "," + dayDone
						+ "\n");
			} catch (FileNotFoundException e) {
				System.out.println("File multiplicationData.txt cannot be found");
			} finally {
				out.close();
			}

			String name = returnedName;
			int timesTable = intReturnedTimes;
			int bestScore = correct;
			int timeTaken = intSeconds;

			User user = new User(name, timesTable, bestScore, timeTaken, dayDone);
			userList.add(user);

		//	System.out.println("\nThe top 5 results for this user are");
			// userList.stream()
			// .filter( theUser -> theUser.getName().equals(returnedName) &&
			// theUser.getTimesTable() == intReturnedTimes )
			// .sorted()
			// .limit(5)
			// .forEach(System.out::println);

			topScoreList = userList.stream()
					.filter(theUser -> theUser.getName().equals(returnedName)
							&& theUser.getTimesTable() == intReturnedTimes)
					.sorted().limit(5).collect(Collectors.toList());

			for (User u : topScoreList) {
				System.out.println(u);
			}

			if (user.equals(topScoreList.get(0))) {
				topUserScore = true;
			}

//			System.out.println("\nThe top 5 top Scores");
//			userList.stream()
//						.filter( theUser ->  theUser.getTimesTable() == intReturnedTimes )
//						.sorted( (user1, user2) -> {
//									if (user1.getBestScore() == user2.getBestScore()) {
//										return Integer.compare(user1.getTimeTaken(), user2.getTimeTaken());
//									} else {
//										return Integer.compare(user2.getBestScore(), user1.getBestScore());
//									} })
//						.limit(5)
//						.forEach(System.out::println);

			championScoreList = userList.stream()
					.filter(theUser -> theUser.getTimesTable() == intReturnedTimes && theUser.getTimeTaken() != 0)
					.sorted((user1, user2) -> {
						if (user1.getBestScore() == user2.getBestScore()) {
							return Integer.compare(user1.getTimeTaken(), user2.getTimeTaken());
						} else {
							return Integer.compare(user2.getBestScore(), user1.getBestScore());
						}
					}).limit(5).collect(Collectors.toList());

			if (user.equals(championScoreList.get(0))) {
				topChampionScore = true;
			}

			if (topChampionScore) {
				topScoreMessage.setText("This is the top score EVER on the " + timesTable + " times table!");
			} else if (topUserScore) {
				topScoreMessage.setText("This is your best score ever on the " + timesTable + " times table!");
			} else {
				topScoreMessage.setText("");
			}

	//		for (User u : championScoreList) {
	//			System.out.println(u);
	//		}

			correct = 0;
			questionNumber = 0;
		} //End of the IF
		//----------------------------------------------------------------
	}

	// --------------------------------------------------------------------
	//
	// --------------------------------------------------------------------
	public void processReturnToDoAgain(ActionEvent event) {

		primaryVBox.getChildren().clear();

		timedMessage.setText("");
		labelMessage.setText("");
		topScoreMessage.setText("");
		championScoresText.setText("");
		mistakeText.setText("");
		topScoresText.setText("");
		
		mistakeSet.clear();

		primaryVBox.getChildren().add(nameHBox);
		primaryVBox.getChildren().add(timesHBox);
		primaryVBox.getChildren().add(timedHBox);
		primaryVBox.getChildren().add(labelMessage);
		primaryVBox.getChildren().add(logoImageView);
		primaryVBox.getChildren().add(topScoreMessage);

	}
	// --------------------------------------------------------------------
	//
	// --------------------------------------------------------------------
	public void processShowMistakes(ActionEvent event) {
		String mistakeTextString = "";
		mistakeText.setVisible(true);
		
		Iterator<String> mistakeSetIterator = mistakeSet.iterator();
		while (mistakeSetIterator.hasNext()) {
			mistakeTextString = mistakeTextString + mistakeSetIterator.next() + "\n";
		}
		
		mistakeText.setText(mistakeTextString);
		
	}
	// --------------------------------------------------------------------
	//
	// --------------------------------------------------------------------
	public static void main(String[] args) {
		launch(args);

	}

}
