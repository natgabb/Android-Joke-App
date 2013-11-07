package cs534.gabbamonte.castonguayboisvert.assign2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity displays the jokes in the database, one at a time. It also lets
 * the users delete a joke from the database.
 * 
 * @author Natacha Gabbamonte 0932340
 * @author Caroline Castonguay-Boisvert 084348
 * 
 */
public class ShowJoke extends Activity {

	private TextView jokeView = null;
	private TextView answerView = null;

	// For the options menu
	private static final int CHANGE_CATEGORY_ID = 0;

	private Handler handler = new Handler();
	private AsyncDBHelper dbHelper;

	private ArrayList<CharSequence> allCategories;
	private boolean[] areChecked;

	private Cursor currentJokes = null;
	private Iterator<String[]> jokesIterator;
	private ArrayList<String[]> jokesArray;
	private String[] currentJoke = null;

	// Used to know the index of each inside the jokesIterator and jokesArray
	private static final int INDEX_OF_ID = 0;
	private static final int INDEX_OF_QUESTION = 1;
	private static final int INDEX_OF_ANSWER = 2;

	private boolean isCategorySelected = false;

	/**
	 * Displays the first joke in the list according to the categories that were
	 * previously selected and sets up the listeners for the buttons.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showjoke);

		dbHelper = MainPage.getAsyncDBHelper();
		getJokes();

		jokeView = (TextView) findViewById(R.id.textViewQuestion);
		answerView = (TextView) findViewById(R.id.textViewAnswer);

		// If no category was selected, will not try and shuffle the empty joke
		// list.
		if (isCategorySelected) {
			saveAndShuffle(currentJokes);
			showNextJoke();
		}

		// Shows the answer to the current joke
		Button showAnswerButton = (Button) findViewById(R.id.buttonShowAnswer);
		showAnswerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stopTimer();
				showAnswer();
			}
		});

		// Shows the next joke and hides the answer
		Button nextJokeButton = (Button) findViewById(R.id.buttonNextJoke);
		nextJokeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showNextJoke();

			}
		});

		// Deletes the current joke
		Button deleteJokeButton = (Button) findViewById(R.id.buttonDeleteJoke);
		deleteJokeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				deleteJoke();

			}
		});

		// onClickListener for the "Go Back" button
		Button backButton = (Button) findViewById(R.id.showjoke_backButton);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShowJoke.this.finish();
			}
		});
	}

	/**
	 * Adds the Set Category option to the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, CHANGE_CATEGORY_ID, Menu.NONE,
				getString(R.string.showjoke_changecat));
		getMenuInflater().inflate(R.menu.activity_splash, menu);
		return true;
	}

	/**
	 * Attempts to set the categories as selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CHANGE_CATEGORY_ID:
			showCategoriesDialog();
			break;
		}
		return false;
	}

	/*
	 * Displays the categories dialog when the user clicks on the
	 * "Change category" item in the options menu.
	 */
	private void showCategoriesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Contains the boolean for each category for whether or not the
		// categories are checked. They are unchecked by default.
		areChecked = new boolean[allCategories.size()];
		for (int i = 0; i < areChecked.length; i++)
			areChecked[i] = false;

		// Contains all the categories as checkable text fields, and sets the
		// checked value on every click
		builder.setMultiChoiceItems(
				allCategories.toArray(new CharSequence[] {}), areChecked,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						areChecked[which] = isChecked;
					}
				});

		TextView titleTextView = (TextView) inflater.inflate(
				R.layout.menu_textview, null);
		titleTextView.setText(R.string.showjoke_setcat_title);
		builder.setCustomTitle(titleTextView);

		// If the user clicks on yes, will try to change the categories (if
		// there are no jokes in the selected categories, or if no categories
		// are selected, it will not apply the changes)
		builder.setPositiveButton(R.string.showjoke_OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<String> checkedCats = new ArrayList<String>();
						for (int i = 0; i < areChecked.length; i++)
							if (areChecked[i])
								checkedCats
										.add(allCategories.get(i).toString());

						// No categories were selected.
						if (checkedCats.isEmpty())
							Toast.makeText(ShowJoke.this,
									R.string.showjoke_setcat_notchanged,
									Toast.LENGTH_LONG).show();

						else
							setJokesForCurrentIteration(checkedCats);
					}
				});

		builder.setNegativeButton(R.string.addjoke_cancel, null);

		builder.create().show();
	}

	/*
	 * Sets the jokes for the current iteration with the categories that were
	 * selected in the options menu.
	 */
	private void setJokesForCurrentIteration(ArrayList<String> categories) {
		Cursor newJokes = dbHelper.getJokesInCategories(categories
				.toArray(new String[0]));

		if (newJokes != null && newJokes.getCount() >= 1) {
			saveAndShuffle(newJokes);
			showNextJoke();
			currentJokes = newJokes;
		}
		// There are no jokes in the selected categories.
		else
			Toast.makeText(this, R.string.showjoke_setcat_nojokes,
					Toast.LENGTH_LONG).show();
	}

	/*
	 * Gets the jokes for the currently selected categories from the database.
	 */
	private void getJokes() {

		// If no category was selected, or the ones selected are all empty,
		// displays a message asking the user to select one and leaves the
		// activity.
		SharedPreferences prefs = getSharedPreferences(MainPage.TAG,
				MODE_PRIVATE);

		Cursor categoryCursor = dbHelper.getCategories();

		ArrayList<String> prefCategories = new ArrayList<String>();
		allCategories = new ArrayList<CharSequence>();
		String category = null;

		// Adds the categories that were chosen by the user to the list.
		while (categoryCursor.moveToNext()) {
			category = categoryCursor.getString(1);
			allCategories.add(category);
			if (prefs.getBoolean(category, true))
				prefCategories.add(category);
		}

		categoryCursor.close();

		if (!prefCategories.isEmpty()) {
			Cursor prefsJokes = dbHelper.getJokesInCategories(prefCategories
					.toArray(new String[0]));
			if (prefsJokes != null) {
				if (prefsJokes.getCount() >= 1) {
					isCategorySelected = true;
					currentJokes = prefsJokes;
				} else
					displayToastAndLeave(getString(R.string.showjoke_categoryempty));
			} else
				displayToastAndLeave(getString(R.string.showjoke_retrievingerror));
		} else
			displayToastAndLeave(getString(R.string.showjoke_nocategory));
	}

	/*
	 * Displays a toast and exits this activity.
	 */
	private void displayToastAndLeave(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		isCategorySelected = false;
		this.finish();
	}

	/*
	 * Saves the jokes to an array and shuffles them.
	 */
	private void saveAndShuffle(Cursor jokes) {
		jokesArray = new ArrayList<String[]>();

		while (jokes.moveToNext()) {
			jokesArray.add(new String[] { "" + jokes.getInt(INDEX_OF_ID),
					jokes.getString(INDEX_OF_QUESTION),
					jokes.getString(INDEX_OF_ANSWER) });
		}
		shuffleJokes();
	}

	/*
	 * Shuffles the jokes and recreates the iterator.
	 */
	private void shuffleJokes() {
		Collections.shuffle(jokesArray);
		jokesIterator = jokesArray.iterator();
	}

	/*
	 * Shows the next joke and hides the answer.
	 */
	private void showNextJoke() {
		stopTimer();

		if (!jokesIterator.hasNext())
			shuffleJokes();

		currentJoke = jokesIterator.next();

		jokeView.setText(currentJoke[INDEX_OF_QUESTION]);
		answerView.setVisibility(TextView.INVISIBLE);
		answerView.setText(currentJoke[INDEX_OF_ANSWER]);

		handler.postDelayed(showAnswerRunnable, 3500);
	}

	/*
	 * Shows the answer to the current joke.
	 */
	private void showAnswer() {
		answerView.setVisibility(TextView.VISIBLE);
	}

	/*
	 * Deletes the joke that is currently shown.
	 */
	private void deleteJoke() {
		jokesIterator.remove();

		if (dbHelper.deleteJoke(Integer.parseInt(currentJoke[INDEX_OF_ID]))) {
			Toast.makeText(this, getString(R.string.showjoke_deleted),
					Toast.LENGTH_SHORT).show();

			// If the person deleted all the jokes from the selected categories,
			// displays a message and leaves the activity.
			if (jokesArray.isEmpty())
				displayToastAndLeave(getString(R.string.showjoke_emptiedcategories));
			else
				showNextJoke();
		} else
			Toast.makeText(this, getString(R.string.showjoke_deletionerror),
					Toast.LENGTH_SHORT).show();
	}

	/*
	 * Stops the "Show Answer" timer.
	 */
	private void stopTimer() {
		handler.removeCallbacks(showAnswerRunnable);
	}

	Runnable showAnswerRunnable = new Runnable() {
		public void run() {
			showAnswer();
		}
	};
}
