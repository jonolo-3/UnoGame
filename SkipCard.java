public class SkipCard extends Card{
	/**
	 * Sets fitting value
	 * @param color
	 */
	public SkipCard(int color){
		super(color, "[(X)]");
		this.specialCard = true;
	}



	@Override
	public void cardOnPlay(Game game){
		//skips a players turn
		game.currentPlayer = game.getNextPlayer();
	}


}