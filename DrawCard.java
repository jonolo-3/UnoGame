public class DrawCard extends Card{
	int nDraw;
	/**
	 * Draws cards, sets fitting value to Card.
	 * @param color
	 * @param nDraw
	 */
	public DrawCard(int color, int nDraw){
		//if no-argument constructor
		super(color, "0");
		
		if (nDraw == 2) {
			this.value = "[+2]";
		}
		this.nDraw = nDraw;
		this.specialCard = true;
	}

	@Override
	public void cardOnPlay(Game game){
		game.drawCard(game.getNextPlayer(), nDraw);
	}
}