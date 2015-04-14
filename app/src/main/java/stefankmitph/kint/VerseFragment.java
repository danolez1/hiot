package stefankmitph.kint;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import stefankmitph.model.BookNavigator;
import stefankmitph.model.Word;

/**
 * Created by KumpitschS on 14.04.2015.
 */
public class VerseFragment extends android.support.v4.app.Fragment {

    private String book;
    private int chapter;
    private int verse;
    private Word[] words;
    private BookNavigator navigator;
    private Typeface typeface;
    private SQLiteDatabase database;
    private ActivityObjectProvider provider;

    public static VerseFragment newInstance(String book, int chapter, int verse) {
        VerseFragment fragment = new VerseFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();

        args.putString("book", book);
        args.putInt("chapter", chapter);
        args.putInt("verse", verse);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            provider = (ActivityObjectProvider) activity;
        } catch(ClassCastException e) {
            throw new RuntimeException("it ain't a Provider");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        book = getArguments().getString("book");
        chapter = getArguments().getInt("chapter");
        verse = getArguments().getInt("verse");

        database = provider.getDatabase();

        navigator = new BookNavigator(database);
        words = navigator.getVerse(book, chapter, verse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = container.getContext();

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.activity_fragment, container, false);

        AsyncTask<Object, Void, Word[]> loadTask = new AsyncTask<Object, Void, Word[]>() {
            @Override
            protected Word[] doInBackground(Object... params) {
                Word[] words = navigator.getVerse(params[0].toString(), (Integer)params[1], (Integer)params[2]);
                return words;
            }

            @Override
            protected void onPostExecute(Word[] result) {
                words = result;
            }
        };

        //loadTask.execute(book, chapter, verse);

        FlowLayout layout = new FlowLayout(context, null);
        layout.setLayoutParams(
                new FlowLayout.LayoutParams(
                        FlowLayout.LayoutParams.MATCH_PARENT,
                        FlowLayout.LayoutParams.MATCH_PARENT
                ));

        for(int i = 0; i < words.length; i++) {
            Word word = words[i];

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setPadding(10, 20, 10, 10);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

            final TextView textViewStrongs = new TextView(context);
            textViewStrongs.setText(word.getStrongs());
            textViewStrongs.setTextSize(10.0f);
            textViewStrongs.setTextColor(Color.rgb(0, 146, 242));
            //final SQLiteDatabase finalDatabase = database;
            textViewStrongs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = textViewStrongs.getText().toString();
                    String[] parts = text.split("\\&");
                    String strongs1 = "";
                    if (parts.length > 1) {

                    } else {
                        //Cursor result1 = finalDatabase.rawQuery(String.format("select * from strongs where nr = %s", text), null);
                        //while (result1.moveToNext())
                        //    strongs1 = result1.getString(2);
                    }

                    Toast toast = Toast.makeText(v.getContext(), strongs1, Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            TextView textView2 = new TextView(context);
            textView2.setText(word.getWord());
            textView2.setTypeface(typeface);
            textView2.setTextSize(16.0f);

            TextView textView3 = new TextView(context);
            textView3.setText("line3");


            TextView textViewFunctional = new TextView(context);
            textViewFunctional.setText(word.getFunctional());
            textViewFunctional.setTextSize(10.0f);
            textViewFunctional.setTextColor(Color.rgb(0, 146, 242));

            linearLayout.addView(textViewStrongs);
            linearLayout.addView(textView2);
            linearLayout.addView(textView3);
            linearLayout.addView(textViewFunctional);

            layout.addView(linearLayout);
        }
        view.addView(layout);
        return view;
    }
}