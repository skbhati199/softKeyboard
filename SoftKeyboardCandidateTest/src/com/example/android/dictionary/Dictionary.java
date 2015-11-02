package com.example.android.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Dictionary {

	private final List<String> mCurrentSuggestion;
	private final List<String> mReadData;
	private DictionaryUpdateListener mListener = null;
	private Context mContext;
	private ArrayList<String> mReadList;
	private List<String> mCustomReadList;
	private boolean mFlag;
	private List<String> mBestSuggestion;

	public Dictionary(Context context) {
		this.mCurrentSuggestion = new ArrayList<String>();
		this.mReadData = new ArrayList<String>();
		this.mReadList = new ArrayList<String>();
		this.mCustomReadList = new ArrayList<String>();
		this.mContext = context;
		this.mBestSuggestion = new ArrayList<String>();
	}

	public interface DictionaryUpdateListener {
		public void onDictionaryChange(List<String> suggestions);
	}

	public void setDictionaryUpdateListener(DictionaryUpdateListener listener) {
		mListener = listener;
	}

	public List<String> readFile() {
		// Main Dictionary
		mReadList.clear();
		BufferedReader reader = null;
		try {
			AssetManager am = mContext.getAssets();
			InputStream data = am.open("data.txt");
			InputStreamReader dataInputStream = new InputStreamReader(data);
			reader = new BufferedReader(dataInputStream);
			String each = null;
			while ((each = reader.readLine()) != null) {
				mReadList.add(each.toLowerCase());
			}
			reader.close();
			dataInputStream.close();
			data.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBuffer(reader);
		}
		return mReadList;
	}

	public List<String> customReadFile() {

		BufferedReader reader = null;
		mCustomReadList.clear();
		try {
			File file = new File(mContext.getFilesDir() + File.separator + "save.txt");
			// System.out.println("File " + file);
			if (!file.exists())
				file.createNewFile();
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				mCustomReadList.add(line);
			}
			reader.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeBuffer(reader);
		}

		return mCustomReadList;
	}

	public void writeFile(String word) {
		ArrayList<String> list = mReadList;
		for (String strGet : list) {
			if (!strGet.toLowerCase().contains(word.toLowerCase())) {
				mFlag = true;
			}
		}
		writeWord(word);
	}

	public List<String> readAll() {
		List<String> list = new ArrayList<String>();
		List<String> listA = readFile();
		List<String> listB = customReadFile();
		list.addAll(listB);
		list.addAll(listA);

		// System.out.println("current List : " + noOfElements() + " Read : " +
		// noOfReadElements()
		// + " Custom read element : " + noOfReadCustomElements());
		// System.out.println(" list : " + mCurrentSuggestion + " List Read :" +
		// listA + " Custom List " + listB);
		return list;
	}

	private void clearList() {
		mCurrentSuggestion.clear();
	}

	public int noOfElements() {
		return mCurrentSuggestion.size();
	}

	public int noOfReadElements() {
		return mReadList.size();
	}

	public int noOfReadCustomElements() {
		return mReadList.size();
	}

	public void setSuggestions(List<String> suggestions) {
		List<String> tempSuggestions = new ArrayList<String>();
		for (String s : suggestions) {
			if (!tempSuggestions.contains(s)) {
				tempSuggestions.add(s);
			}
		}
		updateCurrentSuggestions();
	}

	private void updateCurrentSuggestions() {

	}

	private void writeWord(String word) {
		if (mFlag) {
			String fileName = "save.txt";
			OutputStreamWriter osw = null;

			try {
				osw = new OutputStreamWriter(mContext.openFileOutput(fileName, Context.MODE_APPEND));
				osw.append(word.toLowerCase());
				osw.append(System.getProperty("line.separator"));
				osw.flush();
				osw.close();
			} catch (Exception e) {
				Log.e("Failed to file isn't created", "Exception", e);
			} finally {
				closeOutputBuffer(osw);
			}
		}
	}

	public boolean match(String word) {
		List<String> list = readAll();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).toLowerCase().equalsIgnoreCase(word.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private void closeOutputBuffer(OutputStreamWriter osw) {
		if (osw != null) {
			osw = null;
		}
	}

	private void closeBuffer(BufferedReader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
