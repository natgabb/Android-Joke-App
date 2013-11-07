package cs534.gabbamonte.castonguayboisvert.assign2;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This class links the activities to the DBHelper but AsyncTask.
 * 
 * @author Natacha Gabbamonte 0932340
 * @author Caroline Castonguay-Boisvert 084348
 * 
 */
public class AsyncDBHelper {

	private DBHelper database = null;

	private static final String CATEGORY = "CATEGORY";
	private static final String JOKE = "JOKE";
	private static final String TREAT_AS_INT = "TREAT_AS_INT";
	private static final String TREAT_AS_STRING = "TREAT_AS_STRING";

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The context.
	 */
	public AsyncDBHelper(Context context) {
		database = new DBHelper(context);
	}

	/**
	 * Returns the jokes from all the categories.
	 * 
	 * @return A cursor with all the jokes.
	 */
	public Cursor getJokes() {
		try {
			return new AsyncQuery().execute().get();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return null;

	}

	/**
	 * Returns the jokes from specific categories.
	 * 
	 * @param categories
	 *            An array of the categories of jokes to be included.
	 * @return A cursor containing the jokes.
	 */
	public Cursor getJokesInCategories(String[] categories) {
		try {
			return new AsyncQuery().execute(categories).get();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return null;
	}

	/**
	 * Returns all the categories.
	 * 
	 * @return A cursor with all the categories.
	 */
	public Cursor getCategories() {
		try {
			String[] temp = null;
			return new AsyncQuery().execute(temp).get();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return null;
	}

	/**
	 * Adds a new joke to the database.
	 * 
	 * @param joke
	 *            The joke string.
	 * @param answer
	 *            The answer string.
	 * @param category
	 *            The joke's category.
	 * @return The row ID of the newly inserted row, or -1 if an error occurred,
	 *         or -2 if an exception occurred.
	 */
	public long addNewJoke(String joke, String answer, String category) {

		try {
			return new AsyncInsert().execute(joke, answer, category).get()
					.longValue();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return -1;
	}

	/**
	 * Adds a new category to the database.
	 * 
	 * @param category
	 *            The category string.
	 * @return The row ID of the newly inserted row, or -1 if an error occurred
	 *         or -2 if an exception occurred.
	 */
	public long addNewCategory(String category) {

		try {
			return new AsyncInsert().execute(category).get().longValue();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return -2;
	}

	/**
	 * Deletes the joke with the corresponding ID from the database.
	 * 
	 * @param id
	 *            The id of the joke.
	 * @return Whether the deletion was successful or not.
	 */
	public boolean deleteJoke(int id) {

		try {
			return new AsyncDelete().execute(JOKE, String.valueOf(id)).get();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return false;
	}

	/**
	 * Deletes the category with the corresponding ID and all its jokes from the
	 * database.
	 * 
	 * @param id
	 *            The id of the category.
	 * @return Whether the deletion was successful.
	 */
	public boolean deleteCategory(int id) {

		try {
			return new AsyncDelete().execute(CATEGORY, TREAT_AS_INT,
					String.valueOf(id)).get();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return false;
	}

	/**
	 * Deletes the category with the corresponding name and all its jokes from
	 * the database.
	 * 
	 * @param category
	 *            The category name.
	 * @return Whether the deletion was successful.
	 */
	public boolean deleteCategory(String category) {

		try {
			return new AsyncDelete().execute(CATEGORY, TREAT_AS_STRING,
					category).get();
		} catch (InterruptedException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e(MainPage.TAG, e.getClass() + " " + e.getMessage());
		}
		return false;
	}

	/*
	 * The AsyncTask for querying.
	 */
	private class AsyncQuery extends AsyncTask<String[], Void, Cursor> {

		@Override
		protected Cursor doInBackground(String[]... params) {
			// Get the categories
			if (params[0] == null) {
				return database.getCategories();
			}
			// Get all jokes.
			else if (params.length == 0) {
				return database.getJokes();
			}
			// Get some jokes
			else {
				return database.getJokesInCategories(params[0]);
			}
		}
	}

	/*
	 * The AsyncTask for inserting.
	 */
	private class AsyncInsert extends AsyncTask<String, Void, Long> {

		@Override
		protected Long doInBackground(String... params) {
			// Add a category
			if (params.length == 1) {
				return database.addNewCategory(params[0]);
			}
			// Add a joke
			else if (params.length == 3) {
				return database.addNewJoke(params[0], params[1], params[2]);
			}
			// This should never happen (unless we were lost when calling it)
			else
				return Long.valueOf(-1);
		}
	}

	/*
	 * The AsyncTask for deleting.
	 */
	private class AsyncDelete extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			if (params[0].equals(CATEGORY)) {
				// Delete a category from the ID
				if (params[1].equals(TREAT_AS_INT))
					return database.deleteCategory(Integer.parseInt(params[2]));
				// Delete a category from the name
				else if (params[1].equals(TREAT_AS_STRING))
					return database.deleteCategory(params[2]);
				// This should never happen
				else
					return false;

			} else if (params[0].equals(JOKE)) {
				return database.deleteJoke(Integer.parseInt(params[1]));
			}
			// This should never happen
			else
				return false;
		}
	}
}
