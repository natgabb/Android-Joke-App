package cs534.gabbamonte.castonguayboisvert.assign2;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * This activity lets the user select the categories of jokes they would like to
 * see.
 * 
 * @author Natacha Gabbamonte 0932340
 * @author Caroline Castonguay-Boisvert 084348
 * 
 */
public class SetCategory extends Activity {

	private ArrayList<String> categories = new ArrayList<String>();
	private ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();
	private ArrayList<String> categoriesToBeDeleted = new ArrayList<String>();
	private ArrayList<View[]> views = new ArrayList<View[]>();
	private SharedPreferences prefs;
	private AsyncDBHelper database;

	private LinearLayout checkBoxesLayout = null;

	/**
	 * Sets up the listeners for the buttons and adds the categories.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setcategory);

		prefs = getSharedPreferences(MainPage.TAG, MODE_PRIVATE);
		database = MainPage.getAsyncDBHelper();

		// Save button onClickListener
		Button saveButton = (Button) findViewById(R.id.set_cat_buttonSaveChanges);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveCategories();
			}

		});

		checkBoxesLayout = (LinearLayout) findViewById(R.id.linearLayout_setcat_checkBoxes);

		// Adds the categories to the XML.
		addCategories();

		// onClickListener for the "Cancel" button.
		Button backButton = (Button) findViewById(R.id.setcat_backButton);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SetCategory.this.finish();
			}

		});
	}

	/*
	 * Adds the categories to the ListView.
	 */
	private void addCategories() {

		Cursor categoryCursor = database.getCategories();

		// If there are no categories, asks the user to create one and leaves
		// the activity.
		if (categoryCursor.getCount() <= 0) {
			categoryCursor.close();
			displayToastAndLeave(getString(R.string.setcat_nocat));
		}

		// Adds the categories to the ArrayList
		while (categoryCursor.moveToNext())
			categories.add(categoryCursor.getString(1));
		categoryCursor.close();

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Inner LinearLayout for each category
		RelativeLayout innerLayout;
		CheckBox checkBox;
		Button button;

		for (String category : categories) {
			innerLayout = (RelativeLayout) inflater.inflate(
					R.layout.setcat_checkbox, null);
			checkBox = (CheckBox) innerLayout
					.findViewById(R.id.checkBoxCategory);

			button = (Button) innerLayout
					.findViewById(R.id.buttonDeleteCategory);
			button.setOnClickListener(deleteButtonListener);

			// If the user had already selected the category, it will be
			// automatically selected
			checkBox.setText(category);
			views.add(new View[] { checkBox, button });
			if (prefs.getBoolean(category, true))
				checkBox.performClick();

			checkBoxesLayout.addView(innerLayout);
			checkBoxes.add(checkBox);
		}
	}

	/*
	 * If saving the changes means jokes will be deleted, this method is called
	 * to display a confirm dialog box.
	 */
	private void displayConfirmDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TextView message = (TextView) inflater.inflate(R.layout.menu_textview,
				null);
		message.setText(R.string.setcat_confirm_message);
		builder.setView(message);

		TextView title = (TextView) inflater.inflate(R.layout.menu_textview,
				null);
		title.setText(R.string.setcat_confirm_title);
		builder.setCustomTitle(title);

		// Deletes the category and jokes if the user clicked yes
		builder.setPositiveButton(R.string.setcat_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int button) {
						deleteCategories();
						SetCategory.this.finish();
					}
				});

		// Leaves the activity without saving the changes if the user clicks no.
		// It also displays a "Nothing was deleted" message.
		builder.setNegativeButton(R.string.setcat_no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int button) {
						Toast.makeText(SetCategory.this,
								R.string.setcat_notdeleted, Toast.LENGTH_SHORT)
								.show();
						SetCategory.this.finish();
					}
				});

		AlertDialog confirmDialog = builder.create();
		confirmDialog.show();
	}

	/*
	 * Listener for the delete buttons of the categories. Saves the category to
	 * be deleted in a list. (Does not delete the category right away from the
	 * database, but deletes the checkbox and button from the layout.)
	 */
	private OnClickListener deleteButtonListener = new OnClickListener() {
		public void onClick(View v) {

			for (View[] view : views)
				if (view[1] == v) {
					categoriesToBeDeleted.add(((CheckBox) view[0]).getText()
							.toString());
					views.remove(view);
					checkBoxesLayout.removeView((View) view[0].getParent());
					checkBoxesLayout.refreshDrawableState();
					break;
				}
		}

	};

	/*
	 * Saves the categories (selected or not selected) to the SharedPreferences.
	 */
	private void saveCategories() {
		// Saves the preferences for categories.
		SharedPreferences.Editor preferencesEditor = prefs.edit();
		for (CheckBox checkBox : checkBoxes)
			preferencesEditor.putBoolean(checkBox.getText().toString(),
					checkBox.isChecked());

		preferencesEditor.commit();
		if (!categoriesToBeDeleted.isEmpty())
			displayConfirmDeleteDialog();
		else
			finish();

	}

	/*
	 * Displays a toast and exists this activity.
	 */
	private void displayToastAndLeave(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		finish();
	}

	/*
	 * Deletes the categories that are contained in the categoriesToBeDeleted
	 * list.
	 */
	private void deleteCategories() {
		boolean noErrors = true;
		int countOfDeleted = 0;

		for (String category : categoriesToBeDeleted)
			if (!database.deleteCategory(category))
				noErrors = false;
			else
				countOfDeleted++;

		// Displays a message to the user telling them how many categories were
		// deleted.
		String message = "";
		if (countOfDeleted > 1)
			message = countOfDeleted + " "
					+ getString(R.string.setcat_deleted_plural);
		else
			message = getString(R.string.setcat_deleted_singular);

		// Displays a message to the user if there was an error during deletion.
		if (!noErrors)
			Toast.makeText(SetCategory.this, R.string.setcat_errordeleting,
					Toast.LENGTH_SHORT).show();

		Toast.makeText(SetCategory.this, message, Toast.LENGTH_SHORT).show();
	}
}
