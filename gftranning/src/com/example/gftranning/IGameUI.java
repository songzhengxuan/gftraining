package com.example.gftranning;

public interface IGameUI {
	/**
	 * display the ui of factor
	 * @param result
	 */
	public void display(int result, boolean isPreparaing);

	/**
	 * hide the ui of last factor
	 */
	public void hide();

	/**
	 * display the succeed result of the lastResult
	 * @param lastResult
	 */
	public void onSucceed(int lastResult);

	/**
	 * display the errors result of errorResult, it should be excepted
	 * @param excepted 
	 * @param errorResult
	 */
	public void onError(int excepted, int errorResult);
	
	/**
	 *
	 */
	public void onAlreadyMarked();

	public void onGameEnd();
}
