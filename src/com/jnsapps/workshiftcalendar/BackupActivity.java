package com.jnsapps.workshiftcalendar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.jnsapps.workshiftcalendar.db.DataBaseHelper;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class BackupActivity extends SherlockActivity {

	private static final int REQUEST_CODE = 1234;
	private static String CHOOSER_TITLE = "Select a file";
	private static final String BACKUP_PATH = "/WorkShiftCalendar";
	private static final String PREFERENCES_FILE = "preferences";
	private static final String DATABASE_FILE = "database";
	private static final String BACKUP_FILE = "backup.zip";
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	// Database
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup);
		db = DataBaseHelper.getInstance(this).getReadableDatabase();
		CHOOSER_TITLE = getString(R.string.backup_chooser_title);
	}

	public void makeBackup(View view) {
		Toast.makeText(this, R.string.backup_toast_making, Toast.LENGTH_SHORT)
				.show();
		boolean res = true;
		// Create backup root folder
		Calendar c = Calendar.getInstance();
		File sdCard = Environment.getExternalStorageDirectory();
		File backupRootFolder = new File(sdCard.getAbsolutePath() + BACKUP_PATH
				+ "/" + df.format(c.getTime()));
		backupRootFolder.mkdirs();
		// Backup preferences
		File prefFile = new File(backupRootFolder, PREFERENCES_FILE);
		res = res & saveSharedPreferencesToFile(prefFile);
		// Backup database
		File srcDBFile = new File(db.getPath());
		File dstDBFile = new File(backupRootFolder, DATABASE_FILE);
		res = res & copy(srcDBFile, dstDBFile);
		// Create zip file
		File[] filesToZip = new File[2];
		filesToZip[0] = prefFile;
		filesToZip[1] = dstDBFile;
		File backupZip = new File(backupRootFolder, BACKUP_FILE);
		res = res & zip(filesToZip, backupZip);
		// Clean
		prefFile.delete();
		dstDBFile.delete();
		// Show result
		if (res) {
			Toast.makeText(this, R.string.backup_toast_ok, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, R.string.backup_toast_fail, Toast.LENGTH_SHORT)
					.show();
		}

	}

	private boolean saveSharedPreferencesToFile(File dst) {
		boolean res = false;
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(dst));
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			output.writeObject(pref.getAll());

			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

	public boolean copy(File src, File dst) {
		boolean res = false;
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(src);
			output = new FileOutputStream(dst);
			byte[] buf = new byte[1024];
			int len;
			while ((len = input.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

	public boolean zip(File[] _files, File _zipFile) {
		boolean res = false;
		BufferedInputStream origin = null;
		ZipOutputStream out = null;
		int BUFFER = 1024;
		try {
			FileOutputStream dest = new FileOutputStream(_zipFile);
			out = new ZipOutputStream(new BufferedOutputStream(dest));

			byte data[] = new byte[BUFFER];

			for (int i = 0; i < _files.length; i++) {
				FileInputStream fi = new FileInputStream(_files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(_files[i].getName());
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
			}
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (origin != null) {
					origin.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;

	}

	public void restoreBackup(View view) {
		// Get zip file
		showFileChooser();
	}

	private void showFileChooser() {
		Intent target = FileUtils.createGetContentIntent();
		Intent intent = Intent.createChooser(target, CHOOSER_TITLE);
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.backup_toast_file_choose_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				try {
					// The URI of the selected file
					final Uri uri = data.getData();
					// Create a File from this Uri
					String filePath = getPath(this, uri);
					File zipFile = new File(filePath);
					restoreBackupFromZipFile(zipFile);

				} catch (Exception e) {
					Toast.makeText(this,
							R.string.backup_toast_file_choose_error,
							Toast.LENGTH_SHORT).show();
				}

			}
		}
	}

	public static String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = context.getContentResolver().query(uri, projection,
					null, null, null);
			int column_index = cursor.getColumnIndexOrThrow("_data");
			if (cursor.moveToFirst()) {
				return cursor.getString(column_index);
			}

		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	private void restoreBackupFromZipFile(File zipFile) {
		boolean res = true;
		// Initial check
		int extensionIndex = zipFile.getName().lastIndexOf(".");
		if (extensionIndex == -1
				|| !zipFile.getName().substring(extensionIndex).equals(".zip")) {
			Toast.makeText(this, R.string.backup_toast_wrong_file,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// Unzip
		File zipParentDir = zipFile.getParentFile();
		res = res & unzip(zipFile, zipParentDir);
		if (!res) {
			Toast.makeText(this, R.string.backup_toast_unzip_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// Restore SharedPreferences
		File prefFile = new File(zipParentDir, PREFERENCES_FILE);
		if (prefFile.exists()) {
			res = res & loadSharedPreferencesFromFile(prefFile);
			if (!res) {
				Toast.makeText(this, R.string.backup_toast_preference_error,
						Toast.LENGTH_SHORT).show();
			}
			prefFile.delete();
			setResult(Preferences.PREFERENCES_CHANGED);
		} else {
			Toast.makeText(this, R.string.backup_toast_preference_not_found,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// Restore DataBase
		File newDataBaseFile = new File(zipParentDir, DATABASE_FILE);
		if (newDataBaseFile.exists()) {
			File oldDataBaseFile = new File(db.getPath());
			res = res & copy(newDataBaseFile, oldDataBaseFile);
			if (!res) {
				Toast.makeText(this, R.string.backup_toast_database_error,
						Toast.LENGTH_SHORT).show();
			}
			newDataBaseFile.delete();
		} else {
			Toast.makeText(this, R.string.backup_toast_database_not_found,
					Toast.LENGTH_SHORT).show();
			return;
		}
		newDataBaseFile.delete();
		// Show result
		if (res) {
			Toast.makeText(this, R.string.backup_toast_restore_ok,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.backup_toast_restore_fail,
					Toast.LENGTH_SHORT).show();
		}

	}

	public boolean unzip(File _zipFile, File dst) {
		boolean res = false;
		int BUFFER = 1024;
		int size;
		byte[] buffer = new byte[BUFFER];
		FileInputStream fin = null;
		ZipInputStream zin = null;
		try {
			fin = new FileInputStream(_zipFile);
			zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			String path = "";
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					File f = new File(dst, ze.getName());
					if (!f.isDirectory()) {
						f.mkdirs();
					}
				} else {
					path = dst.getAbsolutePath() + "/" + ze.getName();
					FileOutputStream out = new FileOutputStream(path, false);
					BufferedOutputStream fout = new BufferedOutputStream(out,
							BUFFER);
					try {
						while ((size = zin.read(buffer, 0, BUFFER)) != -1) {
							fout.write(buffer, 0, size);
						}

						zin.closeEntry();
					} finally {
						fout.flush();
						fout.close();
					}
				}
			}
			res = true;
		} catch (Exception e) {
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
				if (zin != null) {
					zin.close();
				}
			} catch (Exception e) {
			}

		}
		return res;
	}

	@SuppressWarnings({ "unchecked" })
	private boolean loadSharedPreferencesFromFile(File src) {
		boolean res = false;
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(src));
			Editor prefEdit = PreferenceManager.getDefaultSharedPreferences(
					getBaseContext()).edit();
			prefEdit.clear();
			Map<String, ?> entries = (Map<String, ?>) input.readObject();
			for (Entry<String, ?> entry : entries.entrySet()) {
				Object v = entry.getValue();
				String key = entry.getKey();

				if (v instanceof Boolean)
					prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
				else if (v instanceof Float)
					prefEdit.putFloat(key, ((Float) v).floatValue());
				else if (v instanceof Integer)
					prefEdit.putInt(key, ((Integer) v).intValue());
				else if (v instanceof Long)
					prefEdit.putLong(key, ((Long) v).longValue());
				else if (v instanceof String)
					prefEdit.putString(key, ((String) v));
			}
			prefEdit.commit();
			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

}
