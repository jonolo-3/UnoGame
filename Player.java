import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;

public class Player {
	public String name;
	public boolean isBot;
	public boolean online;
	public Socket connection;
	public BufferedReader inFromClient;
	public DataOutputStream outToClient;
	public ArrayList<Card> hand;
	public boolean uno;
	public boolean winner = false;
	private Game game;
	
	/**
	 * Constructor for "offline" players.
	 * @param name
	 * @param isBot
	 * @param game
	 */
	public Player(String name, boolean isBot, Game game) {
		this.name = name; this.isBot = isBot; this.online = false; this.game=game;
	}
	
	/**
	 * Constructor for online players.
	 * @param name
	 * @param isBot
	 * @param connection
	 * @param inFromClient
	 * @param outToClient
	 * @param game
	 */
	public Player(String name, boolean isBot, Socket connection, BufferedReader inFromClient, DataOutputStream outToClient, Game game) {
		this.name = name; this.isBot = isBot; this.online = true; this.connection = connection;
		 this.inFromClient = inFromClient; this.outToClient = outToClient; this.game=game;
		
	}

	/**
	 * Prints hand.
	 */
	private void printHand() {
		System.out.print("Your current hand:    ");
		for(int i=0; i<hand.size(); i++) {
			System.out.print(Card.printCard(hand.get(i)));
		}			
	}

	/**
	 * Sorts the hand.
	 * @param hand
	 */
	public static void sortHand(ArrayList<Card> hand) {
		boolean swapped = true; int j=0;
		while(swapped) {
			swapped = false; j++;
			for(int i=0; i<hand.size()-j; i++) {
				if(hand.get(i+1).color < hand.get(i).color || (hand.get(i+1).color == hand.get(i).color && (hand.get(i+1).value.compareTo(hand.get(i).value)<0))) { // if(hand[i+1] < hand[i])
					Card a = hand.get(i); hand.set(i, hand.get(i+1)); hand.set(i+1, a); swapped = true; // SWAP
				}
			}			
		}
	}
	
	/**
	 * Just a print for selection for cards. 0...hand.size-1 
	 */
	private void selectCardPrint(){
			System.out.print("\nSelect cards to play: ");
			for(int i=0; i<hand.size(); i++) {
				System.out.print("\t["+i+"]");
			} 
			System.out.println();
	}
	
	/**
	 * Check if aCard can be played on lastCardPlayed.
	 * @param aCard
	 * @param lastCardPlayed
	 * @return true or false
	 */
	public boolean viableChoice(Card aCard, Card lastCardPlayed) {
		try{
			if(aCard.value.compareTo(lastCardPlayed.value)==0 || lastCardPlayed.color == aCard.color || aCard.color == Card.WILD) { //Same value, same color, or wildcard
			return true;
			}
		}
		catch (Exception e){
			System.out.println("Something went wrong...Exception: " + e.getMessage());	
		}return false;		
	}
	
	/**
	 * Basically checks if player can play and if input is valid card choices. If so send to playCards which plays.
	 * @param playedCard
	 * @return playCards(this, cardChoices, uno)
	 */
	public Card playerPlay(Card playedCard) {
		boolean viableChoice=false;
		BufferedReader br = null;
		String input = "";
		boolean uno = false;
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();


		if(canPlay(playedCard)) {
			boolean validCardChoices = false;
			String[] splitInput;
			String test = "uno";
			
			printHand();
			selectCardPrint();

			while(!validCardChoices) { //Continue until player selected valid card to play
				try {
					br = new BufferedReader(new InputStreamReader(System.in));
					input=br.readLine();
					splitInput = input.split(",");
					for(int i=0; i<splitInput.length; i++) {
						if (splitInput[i].trim().equalsIgnoreCase("uno")){
							try {
								splitInput[i].trim().matches(test);
								uno = true;
							}catch(NumberFormatException e){System.out.print("You said something other than uno...");}
						}
						else {
						cardChoices.add(Integer.parseInt(splitInput[i].trim()));
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid input! Integers or uno accepted!");

				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("You have selected an invalid card to play, try again");
				}
				validCardChoices = isValidChoices(cardChoices, playedCard);
				if (!validCardChoices){
					System.out.println("You have selected an invalid card to play, try again");
					cardChoices.clear();

				}
			}
		}
		return game.playCards(this, cardChoices, uno);
	}
	
	/**
	 * Same as playerPlay but for bot. Bots play 1 card for now.
	 * @param playedCard
	 * @return
	 */
	public Card botPlay(Card playedCard) {
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		if (canPlay(playedCard)) {
			for (int i=0; i<hand.size(); i++) {
				if(viableChoice(hand.get(i), playedCard)) { 
					cardChoices.add(i);
					break;
				}
			}
		}
		return game.playCards(this, cardChoices, uno);
	}
	
	/**
	 * Is the first selected card valid, is the other cards of same value as first?
	 * @param cardChoices
	 * @param lastCardPlayed
	 * @return
	 */
	public boolean isValidChoices(ArrayList<Integer> cardChoices, Card lastCardPlayed){
		if (cardChoices.size()==0) {
			return false;
		}
		try {
			for (int i=0; i<cardChoices.size(); i++) {
				if (!((viableChoice(hand.get(cardChoices.get(0)), lastCardPlayed) && 
						(hand.get(cardChoices.get(0)).value.compareTo(hand.get(cardChoices.get(i)).value)==0)))) {
					return false;
				}
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Same value, same color, or wildcard
	 * @param playedCard
	 * @return
	 */
	public boolean canPlay(Card playedCard) {
		for (int i=0; i<hand.size(); i++) {
			if(hand.get(i).value.compareTo(playedCard.value)==0 || playedCard.color == hand.get(i).color || hand.get(i).color == Card.WILD) { 
				return true;
			}
		}
		game.drawCard(this, 1);
		return canPlay(playedCard); //recursive draw card until there is a card the player can play	
	}
}