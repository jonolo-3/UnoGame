import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class TestsHomeExam {
	
	static Game game;
	
	@Before
	public void setUp() throws Exception {
		game = new Game(0);
	}
	
	/**
	 * When game initializes it creates the players and deals 7 cards.
	 * So here we test if they actually have 7 cards.
	 */
	@Test
	public void requirementOne() {
		assertEquals(7,game.currentPlayer.hand.size());
		assertEquals(7, game.getNextPlayer().hand.size());
	}
	
	/**
	 * Deck is made when Game object initializes as well.
	 * So after method startCard, deck size should be 1 cards less?
	 * Also checked if the card gets added to the pile.
	 */
	@Test
	public void requirementTwo() {
		int oldDeck = game.deck.size();
		int oldPile = game.pile.size();
		game.startCard();
		assertEquals(oldDeck-1, game.deck.size());
		assertEquals(oldPile+1, game.pile.size());
	}
	
	/**
	 * Check if returned card is special card. False if not and everything is alright!
	 */
	@Test
	public void requirementThree() {
		assertEquals(false, game.startCard().specialCard);
	}
	
	/**
	 * test viableChoice(aCard, lastCardPlayed)
	 * Tested Same color, same value, invalid card(neither color or value)
	 * and if wildscards can be played on any card.
	 */
	@Test
	public void requirementFour() {		
		
		Card testCard = new Card(Card.GREEN, "3");
		Card testCard2 = new Card(Card.GREEN, "8");
		Card testCard3 = new Card(Card.RED, "3");
		Card testCard4 = new Card(Card.YELLOW, "[+2]");
		Card testCard5 = new Card(Card.BLUE, "[+2]");
		Card testWildCard = new Card(Card.WILD, "(?)");
		Card testWildCard2 = new Card(Card.WILD, "+4");
		
		assertEquals(true, game.currentPlayer.viableChoice(testCard3, testCard)); //Same value
		assertEquals(true, game.currentPlayer.viableChoice(testCard2, testCard)); //Same color
		assertEquals(false, game.currentPlayer.viableChoice(testCard4, testCard)); //Invalid choice.
		assertEquals(true, game.currentPlayer.viableChoice(testCard5, testCard4)); //Same special value.
		
		assertEquals(true, game.currentPlayer.viableChoice(testWildCard, testCard)); //Testing
		assertEquals(true, game.currentPlayer.viableChoice(testWildCard2, testCard4));//Wildcards. 
		
	}
	
	/**
	 * Assigning hand with handCards and making them my cardChoices.
	 * Testing if I can play them on testCard (see if they are valid choices.)
	 */
	@Test
	public void requirementFive() {
		Card testCard = new Card(Card.RED, "3");
		Card handCard = new Card(Card.RED, "9");
		Card handCard2 = new Card(Card.GREEN, "9");
		Card handCard3 = new Card(Card.BLUE, "9");
		
		ArrayList<Card> hand = game.currentPlayer.hand;
		hand.clear();
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		
		hand.add(handCard);
		hand.add(handCard2);
		hand.add(handCard3);
		
		for (int i=0; i<hand.size(); i++) {
			cardChoices.add(i);
		}
		assertEquals(true, game.currentPlayer.isValidChoices(cardChoices, testCard));
	}
	
	/**
	 * Requirement 6 is divided into 4 tests, one for each special card.
	 */
	@Test
	public void requirementSixWild() {
		WildCard wildCard = new WildCard(Card.BLUE,4); //Chose another color than Wild to not have to choose one.
		
		ArrayList<Card> hand = game.currentPlayer.hand;
		hand.clear();
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		
		//WildCard
		hand.add(wildCard);
		hand.add(wildCard);
		int handNextplayerBefore = game.getNextPlayer().hand.size();
		for (int i=0; i<hand.size(); i++) {
			cardChoices.add(i);
		}
		game.playCards(game.currentPlayer, cardChoices, false);
		int handNextplayerAfter = game.getNextPlayer().hand.size();
		assertEquals(handNextplayerBefore + 8, handNextplayerAfter);	
	}
	@Test
	public void requirementSix2plus() {
		DrawCard plus2Card = new DrawCard(Card.BLUE, 2);
		
		ArrayList<Card> hand = game.currentPlayer.hand;
		hand.clear();
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		
		//+2 cards
		hand.add(plus2Card);
		hand.add(plus2Card);
		int handNextplayerBefore = game.getNextPlayer().hand.size();
		for (int i=0; i<hand.size(); i++) {
			cardChoices.add(i);
		}
		game.playCards(game.currentPlayer, cardChoices, false);
		int handNextplayerAfter = game.getNextPlayer().hand.size();
		assertEquals(handNextplayerBefore + 4, handNextplayerAfter);		
	}
	@Test
	public void requirementSixSkipCard() {
		SkipCard skipCard = new SkipCard(Card.BLUE);
		
		Player testPlayer = new Player("Test1", false,game);
		Player testPlayer2 = new Player("Test2", false,game);
		game.players.add(testPlayer);
		game.players.add(testPlayer2);
		
		ArrayList<Card> hand = game.currentPlayer.hand;
		hand.clear();
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		
		//skipcards
		hand.add(skipCard);
		hand.add(skipCard);
		for (int i=0; i<hand.size(); i++) {
			cardChoices.add(i);
		}
		game.playCards(game.currentPlayer, cardChoices, false);
		/**
		 * 	Skip 2 times when 4 players should result in Player4's next turn, which it does.
		 * 	Might not look like it in this test, but I'm testing playCards method.
		 *	which will set current player to Player 3 
		 * 	because (currentPlayer = nextPlayer) x2 times. Another currentPlayer = nextPlayer
		 *  happens at the end of the turn => Player4's turn next!
		 */
		assertEquals(game.players.get(2), game.currentPlayer);	
	}
	@Test
	public void requirementSixReverseCard() {
		ReverseCard reverseCard = new ReverseCard(Card.BLUE);
		
		ArrayList<Card> hand = game.currentPlayer.hand;
		hand.clear();
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		
		//reversecards
		hand.add(reverseCard);
		hand.add(reverseCard);
		for (int i=0; i<hand.size(); i++) {
			cardChoices.add(i);
		}
		game.playCards(game.currentPlayer, cardChoices, false);
		// clockwise should be true since reverse 2x times = same direction.
		assertEquals(true, game.clockwise);	
	}
	
	
	/**
	 * Check if hand after canPlay on a card(testCard) is greater or equal the hand before canPlay.
	 * If is it then the player drew cards until it had a viable card. 
	 * If the hand size were equally large then the player could play on that card
	 * without having to draw cards.
	 */
	@Test
	public void requirementSeven() {
		int playerHandBefore = game.currentPlayer.hand.size();
		Card testCard = new Card(Card.BLUE, "3");
		game.currentPlayer.canPlay(testCard);
		int playerHandAfter = game.currentPlayer.hand.size();
		assertEquals(true, playerHandAfter >= playerHandBefore);
	}
	
	/**
	 * Test if player or bot plays valid cards or follows rule 6.
	 * If pile is not empty they played a valid card(s). Players is always playing their turn.
	 *  
	 */
	@Test
	public void requirementEight() {
		Card playedCard = new Card(Card.GREEN, "3");
		Card testCard2 = new Card(Card.YELLOW, "3");
		Card testCard3 = new Card(Card.RED, "3");
		Card testWildCard = new Card(Card.WILD, "+4");
		ArrayList<Card> hand = game.currentPlayer.hand;
		hand.clear();
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		
		
		//See if played valid cards
		hand.add(testCard2);
		hand.add(testCard3);
		boolean validCardChoices = false;
		while(!validCardChoices) {
			for (int i=0; i<hand.size(); i++) {
				cardChoices.add(i);
			}
			validCardChoices = game.currentPlayer.isValidChoices(cardChoices, playedCard);
			if(validCardChoices) {
				game.pile.clear();
				game.playCards(game.currentPlayer, cardChoices, false);
				boolean pileEmpty = game.pile.isEmpty();
				assertEquals(false, pileEmpty);
			}
		}
		//Test for rule 6
		hand.clear();
		hand.add(testWildCard);
		hand.add(testWildCard);
		boolean validCardChoices1 = false;
		while(!validCardChoices1) {
			for (int i=0; i<hand.size(); i++) {
				cardChoices.add(i);
			}
			validCardChoices1 = game.currentPlayer.isValidChoices(cardChoices, playedCard);
			if(validCardChoices1) {
				game.pile.clear();
				game.playCards(game.currentPlayer, cardChoices, false);
				boolean pileEmpty1 = game.pile.isEmpty();
				assertEquals(false, pileEmpty1);
			}
		}
		
	}
	
	/**
	 * Test if deck is empty, then test if empty after running method for requirement 9.
	 */
	@Test
	public void requirementNine() {
		Card testCard = new Card(Card.RED,"3"); //For pile not to be empty.
		Card testCard2 = new Card(Card.BLUE, "9"); // Need 2 cards since discard top of pile.
		game.pile.add(testCard);
		game.pile.add(testCard2);
		
		game.deck.clear();
		assertEquals(true, game.deck.isEmpty());
		game.isDeckEmpty();
		assertEquals(false, game.deck.isEmpty());
	}
	
	@Test
	public void requirementTen() {
	/**
	 * Nothing to test here. I have no code allowing people to trade cards.	
	 */
	
	}
	
	/**
	 * This requirement 11 contradicts requirement 13.
	 * However bot always say "uno" when played second last card. 
	 * So the testing for bot here only.
	 */
	@Test
	public void requirementEleven() {
		Card card = new Card(Card.BLUE,"3");
		Card card2 = new Card(Card.RED,"3");
		Card card3 = new Card(Card.GREEN,"5");
		ArrayList<Card> hand = game.players.get(1).hand;
		
		hand.clear();
		hand.add(card);
		hand.add(card3);
		//Has two cards, uno false.
		assertEquals(false, game.players.get(1).uno);
		game.players.get(1).botPlay(card);
		//Played one card, one left in hand. Did the bot say uno?
		assertEquals(true, game.players.get(1).uno);	
	}
	
	/**
	 * The player, the bot in this case, started of with 2 cards and played 1 of them.
	 * 
	 */
	@Test
	public void requirementTwelve() {
		Card card = new Card(Card.BLUE,"3");
		Card card2 = new Card(Card.RED,"3");
		Card card3 = new Card(Card.GREEN,"5");
		ArrayList<Card> hand = game.players.get(1).hand;
		
		hand.clear();
		hand.add(card2);
		hand.add(card);
		//Hand size = 2
		assertEquals(2, game.players.get(1).hand.size());
		game.players.get(1).botPlay(card);
		
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		for (int i=0; i<hand.size(); i++) {
			cardChoices.add(i);
		}
		//Check hand size 1, if last card is valid aad if it said uno, if so => winner
		assertEquals(1, game.players.get(1).hand.size());
		assertEquals(true, game.players.get(1).isValidChoices(cardChoices, card));
		assertEquals(true, game.players.get(1).uno);
			
		//This will say that Bot1 is the winner, but the system exits then, so idk how to test.
		game.playCards(game.players.get(1), cardChoices, game.players.get(1).uno);
		assertEquals(true, game.players.get(1).winner);	
	}
	
	/**
	 * 
	 * ForgotUno gets called for currentPlayer at the start of each turn.
	 * Player had 1 card but forgot to say uno, gets punished and have to draw a card.
	 * 
	 */
	@Test
	public void requirementThirteen() {
		Card card3 = new Card(Card.GREEN,"5");
		ArrayList<Card> hand = game.currentPlayer.hand;
		
		hand.clear();
		hand.add(card3);
		
		assertEquals(1, game.currentPlayer.hand.size());
		game.forgotUno(game.currentPlayer);
		assertEquals(2,game.currentPlayer.hand.size());
		
	}
	/**
	 * A player played his last card, leaving his hand size to 0.
	 * And he won the game. 
	 * 
	 */
	@Test
	public void requirementFourteen(){
		Card card3 = new Card(Card.GREEN,"5");
		ArrayList<Card> hand = game.currentPlayer.hand;
		hand.clear();
		hand.add(card3);
		
		ArrayList<Integer> cardChoices = new ArrayList<Integer>();
		for (int i=0; i<hand.size(); i++) {
			cardChoices.add(i);
		}
		assertEquals(true, game.currentPlayer.isValidChoices(cardChoices, card3));
		game.playCards(game.currentPlayer, cardChoices, true);
		assertEquals(0, game.currentPlayer.hand.size());
		
	}
	
	//@Test
	//public void requirementFifteen() {
		
	//}
	
	//@Test
	//public void requirementSixteen() {
		
	//}
	
	//@Test
	//public void requirementSeventeen() {
		
	//}
}
