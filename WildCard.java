public class WildCard extends Card{
	int nDraw;
	
	/**
	 * Sets fitting values, as well and changing colors.
	 * @param color
	 * @param nDraw
	 */
	public WildCard(int color, int nDraw){
		//if no-argument constructor
		super(color, "0");

		if (nDraw == 0){
			this.value = "(?)";
		}
		else if (nDraw == 4) {
			this.value = "[+4]";
		}
		this.nDraw = nDraw;
		this.specialCard = true;
	}

	@Override
	public void cardOnPlay(Game game){
		game.drawCard(game.getNextPlayer(), nDraw);
		//if it was a Wild card, change to new color
		if (this.color == WILD){
			game.changePlayColor();
		}
	}
}