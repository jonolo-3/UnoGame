import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;

public class Game{

	public Random rnd;
	ArrayList<Card> deck;
	ArrayList<Card> pile;
	ArrayList<Card> playedCards;
	ArrayList<Player> players;
	Player currentPlayer;
	boolean clockwise;
	int index;


	
	/**
	 * Constructor. Initializing stuff needed for the game.
	 * @param numberOfOnlineClients
	 */
	public Game(int numberOfOnlineClients) {
		deck = createDeck();
		pile = new ArrayList<Card>();
		players = createPlayers(numberOfOnlineClients);
		index = rnd.nextInt(numberOfOnlineClients +1); //total number of player Player1, Bot1 and clients. +1 cuz players index from 0-size()-1
		currentPlayer = players.get(index);
		playedCards = new ArrayList<Card>();
		clockwise = true;
	}
	
	
	/**
	 * Send annoucement out to online clients.
	 * @param announcement
	 */
	private void clientAnnouncement(String announcement){
		for(int i=0; i<players.size(); i++) {
			if(players.get(i).online) {
				try {
					players.get(i).outToClient.writeBytes(announcement + "\n");
				} catch(Exception e) {System.out.println("Something went wrong! Could not send announcements!");}
			}
		}
	}
	
	/**
	 * 
	 * @return next player.
	 * if clockwise is true, then change direction. If clockwise becomes false, changes back direction.
	 */
	public Player getNextPlayer() {
		int index = players.indexOf(currentPlayer);
		if (clockwise){index++;}
		else{index--;}
		if (index == -1){ //If last player was player0, go to last player in array
			index = players.size() -1;
		}
		else if (index == players.size()) { //IF last player was the last player in array of Players, go to first player.
			index = 0;
		}
		return players.get(index);	
	}
	
	/**
	 * Check if card on top of deck is a special card. If not, set to startCard.
	 * @return startCard.
	 */
	public Card startCard() {
		Card startCard = deck.get(0);
		while(startCard.specialCard){
			shuffle(deck);
			startCard = deck.get(0);
		}
		clientAnnouncement("Starting card: " + Card.printCard(startCard));
		System.out.println("Starting card: " + Card.printCard(startCard));
		pile.add(deck.remove(0));
		return startCard;
		
	}
	
	/**
	 * Checks if the player has 1 card in hand and forgot to say "uno". If so, draw a card.
	 * @param player
	 */
	public void forgotUno(Player player){
		if(player.hand.size() == 1 && !player.uno) {
			drawCard(player, 1);
		}
	}

	/**
	* Creating deck
	* @return shuffled deck
	*
	*/
	private ArrayList<Card> createDeck() {
		ArrayList<Card> deck = new ArrayList<Card>();
		//For each color
		for(int i=0; i<4; i++) {
			for(int j=0; j<Card.coloredcards.length; j++) {
				if(Card.coloredcards[j].equals("0")||Card.coloredcards[j].equals("1")||Card.coloredcards[j].equals("2")||Card.coloredcards[j].equals("3")
					||Card.coloredcards[j].equals("4")||Card.coloredcards[j].equals("5")||Card.coloredcards[j].equals("6")||Card.coloredcards[j].equals("7")
					 ||Card.coloredcards[j].equals("8")||Card.coloredcards[j].equals("9")){

					deck.add(new Card(i, Card.coloredcards[j]));
				}
				//Special cards
				else if(Card.coloredcards[j] == "[</>]"){
					deck.add(new ReverseCard(i));
				}
				else if (Card.coloredcards[j] == "[(X)]") {
					deck.add(new SkipCard(i));
				}
				else if (Card.coloredcards[j] == "[+2]") {
					deck.add(new DrawCard(i,2));
				}
			}
		}
		for(int i=0; i<4; i++) {
			deck.add(new WildCard(Card.WILD, 0)); //Adding WILD, WILD+4
			deck.add(new WildCard(Card.WILD, 4));
		}
		return shuffle(deck);
	}

	/**
	* Shuffling deck or pile.
	* @param deck
	* @return shuffled deck or pile.
	*/
	private ArrayList<Card> shuffle(ArrayList<Card> deck) {
		rnd = ThreadLocalRandom.current();
		for(int i=deck.size()-1; i>0; i--) {
			int index = rnd.nextInt(i+1);
			Card a = deck.get(index); deck.set(index, deck.get(i)); deck.set(i, a); // SWAP
		}
		return deck;	
	}
	
	/**
	 * Gets called if deck is empty. Discards top of played cards aka pile and shuffles it into a new deck.
	 */
	public void isDeckEmpty() {  //req. 9
		pile.remove(pile.size()-1);
		ArrayList<Card>temp = deck;
		deck = shuffle(pile);
		pile = temp;
		pile.clear();
	}
	
	/**
	 * Draws nDraw cards for player.
	 * @param player
	 * @param nDraw
	 */
	public void drawCard(Player player, int nDraw) {
		for (int i=0; i<nDraw; i++) {
			if (deck.isEmpty()) {
				isDeckEmpty();
			}
			player.hand.add(deck.remove(0));
			Player.sortHand(player.hand);
			player.uno=false;
		}
	}
	
	/**
	 * Changes the color. Choose between RED, GREEN, YELLOW, BLUE.
	 */
	public void changePlayColor(){

		if (currentPlayer.isBot) {
			lastCardPlayed().color = rnd.nextInt(4); //Just set random color for now, could probably be improved
		}
		else {
			String input;
			int color;
			BufferedReader br = null;
			System.out.println("Choose color: \t" + Card.CARDCOLOR[0] + " 0 " + Card.RESET + "\t" + //Red
				    								Card.CARDCOLOR[1] + " 1 " + Card.RESET + "\t" + //Green
				    								Card.CARDCOLOR[2] + " 2 " + Card.RESET + "\t" + //Yellow
				    								Card.CARDCOLOR[3] + " 3 " + Card.RESET + "\t"); //Blue
			try {
				br = new BufferedReader(new InputStreamReader(System.in));
				input = br.readLine();
				color = Integer.parseInt(input);
				if(isValidColor(color)){
					lastCardPlayed().color = color;
					return;
				}
				else {
					System.out.println("Invalid Input!");
					changePlayColor();
				}
			}
			catch(Exception e){
				System.out.println("Invalid Input!");
				changePlayColor();	
			}
		}
	}
	
	/**
	 * Checks if the color of the card is valid. Either Red, green, yellow or blue.
	 * @param color
	 * @return true or false
	 */
	public boolean isValidColor(int color) {
		if (color == Card.RED || color == Card.GREEN || color == Card.YELLOW || color == Card.BLUE) {
			return true;
		}
		else {return false;}		
	}
	
	/**
	 * Deals 7 cards to the player. Gets called at the start of a game. 
	 * @param player
	 */
	public void dealCards(Player player){
		ArrayList<Card> hand = new ArrayList<Card>();
		for(int i=0; i<7; i++) { //Deal 7 cards to the Player
			hand.add(deck.remove(0));
		}
		Player.sortHand(hand);
		player.hand = hand;
	}
	
	/**
	 * 
	 * @return last card played which is the card you will play after.
	 */
	public Card lastCardPlayed() {
		return pile.get(pile.size()-1);
	}
	
	/**
	 * Creates players. Always Player1 and Bot1 if no param. Else numberOfOnlineClients + 2 
	 * @param numberOfOnlineClients
	 * @return players.
	 */
	public ArrayList<Player> createPlayers(int numberOfOnlineClients) {
		ArrayList<Player> players = new ArrayList<Player>();

		Player player1 = new Player("Player 1", false, this);
		dealCards(player1);
		players.add(player1);
		Player bot1 = new Player("Bot 1", true, this);
		dealCards(bot1);
		players.add(bot1);
		
		try{
			ServerSocket aSocket = new ServerSocket(2060);
			for (int onlineClient=0; onlineClient<numberOfOnlineClients; onlineClient++) {
				Socket connectionSocket = aSocket.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				boolean isBot = Boolean.parseBoolean(inFromClient.readLine());
				Player onlinePlayer = new Player((isBot?"Bot ":"Player ")+ Integer.toString(onlineClient+2), isBot, connectionSocket, inFromClient, outToClient, this);
				dealCards(onlinePlayer);
				players.add(onlinePlayer);
				System.out.println("Connected to " + (isBot?"Bot":"Player") + " ID: " + (onlineClient+2));
			}

		}catch (Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return players;
	}
	
	/**
	 * Plays the cards(cardChoices the player made). Checks if player said uno and if the hand is empty.
	 * If so, gets announced winner and game ends.
	 * @param player
	 * @param cardChoices
	 * @param hasSaidUno
	 * @return
	 */
	public Card playCards(Player player, ArrayList<Integer> cardChoices, boolean hasSaidUno){
		playedCards.clear();
		for(int i=0; i<cardChoices.size(); i++){
			playedCards.add(player.hand.get((int)cardChoices.get(i)));
		}
		Collections.sort(cardChoices);
		try{
			for(int i=cardChoices.size()-1; i>=0; i--) {
				pile.add(player.hand.get(cardChoices.get(i)));
				player.hand.get(cardChoices.get(i)).cardOnPlay(this);
				player.hand.remove((int)cardChoices.get(i));
				
				//Bot says uno. This is fine since bot only plays 1 card at the time for now.
				if(player.isBot && player.hand.size() == 1) {
					player.uno = true;
				}
				if((player.hand.size()==0) && hasSaidUno) {
					player.winner = true;
					isWinner(player, player.winner);	
				}
			}
			if((player.hand.size()==1) && hasSaidUno) {
				player.uno = true;
			}
			
		}catch(Exception e){System.out.println("Something went wrong... Couldn't play the cards. Exception: " + e.getMessage());}	
		return lastCardPlayed();
	}
	
	/**
	 * Gets called if we have a winner. Send string to announcer and end game.
	 * @param player
	 */
	private void isWinner(Player player, boolean winner){
		if(winner){
			System.out.println(player.name + " wins!");
			clientAnnouncement(player.name + " wins!");
			clientAnnouncement("END");
		}
	}
	
	/**
	 * Starts game. Loop should never end since it's the Game, if not currentPlayer won the game. Loop breaks, game breaks.
	 */
	private void startGame() {
		startCard();
		String playedCardString = "";
		String announcement ="";
		String name = "";
		try{
			while (true) {
				name = currentPlayer.name;
				forgotUno(currentPlayer);

				if(currentPlayer.isBot){
					currentPlayer.botPlay(lastCardPlayed());
				}
				else{
					currentPlayer.playerPlay(lastCardPlayed());
				}

				for (int i=0; i<playedCards.size(); i++) {
					playedCardString += Card.printCard(playedCards.get(i));
				}
				announcement = name + " played: " + playedCardString + (currentPlayer.uno?" and has said uno":"");
				System.out.println(announcement);
				clientAnnouncement(announcement);
				playedCardString = "";
				announcement = "";
				try{
					if(currentPlayer.winner == true){
						System.exit(0);
					}	
				}catch(Exception e){}
				currentPlayer = getNextPlayer();
			}
		}catch (Exception e){System.out.println("Something went wrong when playing the game... Exception: " + e.getMessage());}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int numberOfOnlineClients = 0;
		try {
			if (args.length!=0) {
				numberOfOnlineClients = Integer.parseInt(args[0]);
			}
			else {
				numberOfOnlineClients = 0;
			}
		}
		catch(Exception e) {
			System.out.println("Invalid argument! Must be an Integer.");
			System.exit(1);
		}
		Game game = new Game(numberOfOnlineClients);
		game.startGame();
	}


}