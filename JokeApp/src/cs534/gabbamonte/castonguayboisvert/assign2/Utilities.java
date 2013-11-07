package cs534.gabbamonte.castonguayboisvert.assign2;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.os.Handler;

/**
 * Contains a utility method that could be used in multiple activities.
 * 
 * @author Natacha Gabbamonte 0932340
 * @author Caroline Castonguay-Boisvert 084348
 */
public class Utilities {

	/**
	 * Switches the color of a button for half a second, and then switched it
	 * back right before launching the new activity. The arguments are final
	 * because they are used in an inner class.
	 * 
	 * @param activity
	 *            the calling activity
	 * @param toLaunch
	 *            the class to launch
	 * @param button
	 *            the button that was clicked
	 */
	public static void changeButtonOnClick(final Activity mainActivity,
			final Class<? extends Activity> toLaunch, final Button button) {
		// Changes the colour of the button onClick
		button.setBackgroundColor(mainActivity.getResources().getColor(
				R.color.mainBackground));
		button.setTextColor(mainActivity.getResources().getColor(R.color.black));

		Handler handler = new Handler();

		// Changes the button back to it's original colour after .45 seconds
		handler.postDelayed(new Runnable() {
			public void run() {
				button.setBackgroundColor(mainActivity.getResources().getColor(
						R.color.buttonBackground));
				button.setTextColor(mainActivity.getResources().getColor(
						R.color.buttonText));
			}
		}, 450);

		// To let the time for the onClick to change back the colour of
		// the button
		handler.postDelayed(new Runnable() {
			public void run() {
				mainActivity.startActivity(new Intent(mainActivity, toLaunch));
			}
		}, 455);
	}
}
