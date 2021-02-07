package com.example.safenotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class NoteList extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private final Context context = this;
	boolean a = true;
	
	ImageView del, can, sel;
	TextView noNote, title, date;
	ImageButton add;
	CheckBox check;
	
	private NotesDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_list);
		
		getActionBar().setCustomView(R.layout.custom_title);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
				// Définir une barre d'action (en haut) personnalisée.
		
		noNote = (TextView) findViewById(R.id.nonote);
		title = (TextView) findViewById(R.id.title);
		date = (TextView) findViewById(R.id.date);
		
		del = (ImageView) findViewById(R.id.imageView2);
		can = (ImageView) findViewById(R.id.imageView3);
		sel = (ImageView) findViewById(R.id.imageView4);
		
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		
		encryptDecrypt(1); // déchiffrer le titre et la date après l'identification.
		fillData(); // Lecture de la BD et affichage de la liste des notes. 
		
		registerForContextMenu(getListView());
		
		add = (ImageButton) findViewById(R.id.imageButton1);
				// Bouton ajouter nouvelle note.
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createNote();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.reset: // Réinitialiser l'application.
			
			DialogInterface.OnClickListener dialogClickListener 
				= new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					
					 case DialogInterface.BUTTON_NEGATIVE:
						
						LayoutInflater li = LayoutInflater.from(context);
						View promptsView = li.inflate(R.layout.activity_reset, null);
						
						final EditText editusr = (EditText) promptsView
								.findViewById(R.id.editusr);
						final EditText editpass = (EditText) promptsView
								.findViewById(R.id.editpass);
						
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
						alertDialogBuilder.setView(promptsView);
						alertDialogBuilder.setCancelable(false).setNegativeButton("Réinitialiser",
								new DialogInterface.OnClickListener() {
									
									@SuppressLint("SdCardPath")
									public void onClick(DialogInterface dialog, int id) {
										try {
											
											if (FirstLogIn.SHA1(FirstLogIn.SHA1(editusr.getText().toString())
												+FirstLogIn.SHA1(editpass.getText().toString()))
												.equals(readFromFile("string.xml")) == true) {
														// Vérification de nom d'utilisateur et de mot de passe.
													
													File f = new File("/data/data/"+NoteList.this
															.getPackageName()+"/files/string.xml");
													f.delete(); // Suppression de fichier de hash.
													finish();
													
											} else
												Toast.makeText(NoteList.this, "Le nom d'utilisateur ou " +
													"le mot de passe est incorrect!",Toast.LENGTH_SHORT).show();
										
										} catch (NoSuchAlgorithmException e) {
											e.printStackTrace();
										} catch (UnsupportedEncodingException e) {
											e.printStackTrace();
										}
									}
								})
						.setPositiveButton("Annuler", null);
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
						break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Toutes les données de l'application seront effacées, continuer?")
			.setPositiveButton("Non", dialogClickListener)
			.setNegativeButton("Oui", dialogClickListener).show();
			return true;
		
		case R.id.select: // Sélectionner les éléments de la liste.
			
			if (getListView().getCount() == 0) // Si la liste est vide...
				Toast.makeText(NoteList.this, "Pas d'éléments à sélectionner!",
						Toast.LENGTH_SHORT).show();
			else { // Sinon..
				visibility(3);
				
				Cursor notesCursor = mDbHelper.fetchAllNotes();
						// Curseur pour la lecture de BD. 
				String[] from = new String[] { NotesDbAdapter.KEY_TITLE };
				int[] to = new int[] { R.id.text22 };
				
				final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
				final SimpleCursorAdapter notes2 = new SimpleCursorAdapter(
						this, R.layout.notes_row2, notesCursor, from, to);
						// Adaptateur de curseur.
				
				setListAdapter(notes2);
						// Adapter la liste au adaptateur notes2.

				del.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						a = false;
						a: for (int i = getListView().getCount() - 1; i >= 0; i--) {
								// Vérifier la sélection d'au moins un élément.
							View vw = getListView().getChildAt(i);
							if (vw != null){
							check = (CheckBox) vw.findViewById(R.id.chkbx1);
							if (check.isChecked()) {
								a = true;
								break a;
							}
							}
						}
						
						if (a) {
							DialogInterface.OnClickListener dialogClickListener 
								= new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									switch (which) {
									case DialogInterface.BUTTON_NEGATIVE:
										
										for (int i = getListView().getCount() - 1; i >= 0; i--) {
											
											View vw = getListView().getChildAt(i);
											if (vw != null){
											check = (CheckBox) vw.findViewById(R.id.chkbx1);
											if (check.isChecked()) // Si l'élément est coché..
												mDbHelper.deleteNote(getListView().getItemIdAtPosition(i));
														// Supprimer-le de la BD.
											}											
										}
										
										visibility(4);
										fillData();
										Toast.makeText(NoteList.this,"Elément(s) supprimé(s)!"
														,Toast.LENGTH_SHORT).show();
										break;
									}
								}
							};
							builder1.setMessage("Etes-vous sûr de vouloir supprimer le(s)" +
												" élément(s) sélectionné(s)?")
							.setPositiveButton("Non", dialogClickListener)
							.setNegativeButton("Oui", dialogClickListener).show();
						
						} else
							Toast.makeText(NoteList.this,"Sélectionnez au moins un élément!"
											,Toast.LENGTH_SHORT).show();
					}
				});
				
				can.setOnClickListener(new View.OnClickListener() {
						// Bouton annuler la sélection.
					@Override
					public void onClick(View v) {
						visibility(4);
						fillData();
					}
				});
				
				sel.setOnClickListener(new View.OnClickListener() {
						// Bouton sélectionner tous.
					@Override
					public void onClick(View v) {
						
						for (int i = 0 ; i <= getListView().getCount(); i++) {
							View vw = getListView().getChildAt(i);
							if (vw != null){
							check = (CheckBox) vw.findViewById(R.id.chkbx1);
							check.setChecked(a);
							}
						}
						a = !a;
												
					}
				});
			}
			return true;
		
		case R.id.propos: // A propos.
			
			TextView msg = new TextView(this);
			msg.setText("Université de Batna\n3ème année Licence STIC\nAnnée universitaire (2013-2014)"
					+ "\n\nPFE réalisé par :\nMERAD FOUAD FOUZI\nBOUKHALFA TAREK\n\nEncadré par :\n"
					+ "M. BOUBICHE DJALLAL EDDINE");
			msg.setPadding(10, 10, 10, 10);
			msg.setGravity(Gravity.CENTER);
			msg.setTextColor(this.getResources().getColor(R.color.gray));
			Builder alert = new AlertDialog.Builder(NoteList.this);
			alert.setTitle("SafeNotes");
			alert.setView(msg);
			alert.setPositiveButton("OK", null);
			alert.show();
			return true;
		
		case R.id.exit: // Quitter l'application.
			exit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void exit() { // Confirmation avant de quitter.
		DialogInterface.OnClickListener dialogClickListener 
			= new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_NEGATIVE:
					finish();
					break;
				}
			}
			};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Voulez-vous vraiment quitter l'application?")
				.setPositiveButton("Non", dialogClickListener)
				.setNegativeButton("Oui", dialogClickListener).show();
	}

	public void onBackPressed() {
			// Si la touche retour est appuyée.
		exit();
	}

	private void visibility(int i) {
			// Configuration de la visibilité des éléments de l'activité.
	  switch (i) {
		
		case 1:
			noNote.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			date.setVisibility(View.VISIBLE);
			getListView().setVisibility(View.VISIBLE);
			break;
		
		case 2:
			noNote.setVisibility(View.VISIBLE);
			title.setVisibility(View.GONE);
			date.setVisibility(View.GONE);
			getListView().setVisibility(View.GONE);
			break;
		
		case 3:
			add.setVisibility(View.GONE);
			del.setVisibility(View.VISIBLE);
			can.setVisibility(View.VISIBLE);
			sel.setVisibility(View.VISIBLE);
			title.setVisibility(View.GONE);
			date.setVisibility(View.GONE);
			break;
		
		case 4:
			add.setVisibility(View.VISIBLE);
			del.setVisibility(View.GONE);
			can.setVisibility(View.GONE);
			sel.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			date.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void createNote() {
			// Démarrer l'éditeur pour une nouvelle note.
		Intent i = new Intent(this, NoteEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, NoteEdit.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@SuppressWarnings("deprecation")
	public void fillData() {
		 // Synchroniser la BD avec la liste des notes à travers le curseur.
		Cursor notesCursor = mDbHelper.fetchAllNotes();

		String[] from = new String[] { NotesDbAdapter.KEY_TITLE,
				NotesDbAdapter.KEY_DATE };
		int[] to = new int[] { R.id.text11, R.id.date_row11 };
		
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.notes_row, notesCursor, from, to);
		
		setListAdapter(notes);

		if (getListView().getCount() == 0)
			visibility(2);
		else {
			visibility(1);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
			// Spécification des éléments du menu contextuel.
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Options");
		menu.add(0, v.getId(), 0, "Editer");
		menu.add(0, v.getId(), 0, "Renommer");
		menu.add(0, v.getId(), 0, "Supprimer");

	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
				// Définition des actions à exécuter selon le choix sélectionné.
		if (item.getTitle() == "Editer") {
				// Démarrer l'éditeur pour une nouvelle note.
			Intent i = new Intent(this, NoteEdit.class);
			i.putExtra(NotesDbAdapter.KEY_ROWID,
					((AdapterContextMenuInfo) item.getMenuInfo()).id);
			startActivityForResult(i, ACTIVITY_EDIT);
		}

		else if (item.getTitle() == "Renommer") {
				// Renommer la note.
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.dialog_input, null);
			final EditText userInput = (EditText) promptsView.findViewById(R.id.EditT);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setView(promptsView);
			alertDialogBuilder.setCancelable(false).setNegativeButton("Renommer",
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {

					if (userInput.getText().toString().equals("")) 
						Toast.makeText(NoteList.this,"Nom vide!", Toast.LENGTH_SHORT).show();
					else {
						mDbHelper.rename(((AdapterContextMenuInfo) item.getMenuInfo()).id,
										userInput.getText().toString());
						fillData();
					}
				}
				})
			.setPositiveButton("Annuler", null);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.setTitle("Renommer");
			alertDialog.show();
		}

		else if (item.getTitle() == "Supprimer") {
				// Supprimer la note.
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					switch (which) {
					case DialogInterface.BUTTON_NEGATIVE:
						mDbHelper.deleteNote(((AdapterContextMenuInfo) item.getMenuInfo()).id);
						Toast.makeText(NoteList.this, "Elément supprimé!",
										Toast.LENGTH_SHORT).show();
						fillData();
						break;
					}
				
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Supprimer vraiment cet élément?")
			.setPositiveButton("Non", dialogClickListener)
			.setNegativeButton("Oui", dialogClickListener).show();
		} else
			return false;

		return true;
	}

	public String readFromFile(String fileName) {
			// Lecture de texte à partir d'un fichier.
		String ret = "", b = null;
		
		try {
			InputStream inputStream = openFileInput(fileName);
        			// 	Flux d'entrée.
			
			if ( inputStream != null ) {
				
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				
				while ((b = bufferedReader.readLine()) != null){
						// Tant qu'il ya une nouvelle ligne non nulle.
					ret += b;
				}
			}
		}
		catch (FileNotFoundException e) {
			Log.e("Login activity", "Fichier non trouvé: " + e.toString());
		} 
		catch (IOException e) {
			Log.e("Login activity", "Echec de lecture de fichier: " + e.toString());
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
			// Les actions à exécuter si l'activité est terminée.
		super.onDestroy();
		encryptDecrypt(2);
	}

	protected void encryptDecrypt(int i) {
			/* Uilisé pour déchiffrer (titre + date) après l'identification
			 * et les rechiffrer après la fermeture de l'application.*/
		Cursor cur = mDbHelper.fetchAllNotes();
		String a = null, b = null;

		while (cur.moveToNext()) {
			try {
				switch (i) {
				case 1:
					a = AESEncryptAdapter.decrypt(cur.getString(cur
							.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
					b = AESEncryptAdapter.decrypt(cur.getString(cur
							.getColumnIndexOrThrow(NotesDbAdapter.KEY_DATE)));
					break;

				case 2:
					a = AESEncryptAdapter.encrypt(cur.getString(cur
							.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
					b = AESEncryptAdapter.encrypt(cur.getString(cur
							.getColumnIndexOrThrow(NotesDbAdapter.KEY_DATE)));
					break;
				}
				mDbHelper.updateList(cur.getLong(cur.getColumnIndexOrThrow
									(NotesDbAdapter.KEY_ROWID)), a, b);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent intent) {super.onActivityResult(requestCode, resultCode, intent);
			// Retourner "requastedCode" avant de se terminer.
		fillData();
	}
}
