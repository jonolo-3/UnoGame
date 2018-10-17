public class ReverseCard extends Card{
	/**
	 * Sets fitting value
	 * @param color
	 */
	public ReverseCard(int color){
		super(color, "[</>]");
		this.specialCard = true;
	}

	@Override
	public void cardOnPlay(Game game){
		//change direction
		game.clockwise = !game.clockwise;
	}


}