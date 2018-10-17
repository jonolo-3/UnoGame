public class Card {
	public static int RED = 0;
	public static int GREEN = 1;
	public static int YELLOW = 2;
	public static int BLUE = 3;
	public static int WILD = 4;
	public int color;
	public String value;
	public boolean specialCard;

	public static String[] CARDCOLOR = new String[] //{"\u001B[101m\033[30m\u001B[1m", //RED BACKGROUND, BLACK TEXT, BOLD
									 				 //"\u001B[102m\033[30m\u001B[1m", //GREEN BACKGROUND, BLACK TEXT, BOLD
													 //"\u001B[103m\033[30m\u001B[1m", //YELLOW BACKGROUND, BLACK TEXT, BOLD
													 //"\u001B[106m\033[30m\u001B[1m", //BLUE BACKGROUND, BLACK TEXT, BOLD
													 //"\u001B[47m\033[30m\u001B[1m"}; //WILD: GRAY BACKGROUND, BLACK TEXT, BOLD
													  {"RED_", "GREEN_", "YELLOW_", "BLUE_", "WILD_"}; //CODE FOR WINDOWS COMMAND PROMPT
	public static String RESET = ""; // RESET TO NORMAL \u001B[0m
	public static String[] coloredcards = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
											   "1", "2", "3", "4", "5", "6", "7", "8", "9", 
											   		  "[</>]", "[(X)]", "[+2]",
											   		  "[</>]", "[(X)]", "[+2]"}; //REVERSE, SKIP, +2

	public static String[] wildcards = new String[] {"(?)", "+4"}; // WILD, WILD+4


	public Card(int color, String value) {
		this.color = color; this.value = value; this.specialCard = false;
	}
	public Card(String card) {
		String[] cardArray = card.split(",");
		try {
			this.color = Integer.parseInt(cardArray[0]);
			this.value = cardArray[1];
		} catch(Exception e) {System.out.println("Something went wrong... Exception: " + e.getMessage());}
		this.specialCard = false;
	}
	public String toString() {
		return ""+color+","+value;
	}
	public static String printCard(Card aCard) {
		return "\t"+CARDCOLOR[aCard.color] + " " + aCard.value + " " + RESET;
	}
	/**
	 * Serves as an execute.
	 * Cards who extends Game will have an own "execute".
	 * @param game
	 */
	public void cardOnPlay(Game game){
	}	
}