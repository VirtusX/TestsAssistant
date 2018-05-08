package virtusx.testsassistant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /*try{
           SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
           db.execSQL("CREATE TABLE IF NOT EXISTS Files (FilePath TEXT, FileName TEXT)");
           Cursor query = db.rawQuery("SELECT * FROM Files;", null);
           List<QuizFile> quizFiles = new ArrayList<>();
           if(query.moveToFirst()){
               do {
                   String file = query.getString(0);
                   String name = query.getString(1);
                   quizFiles.add(new QuizFile(file,name));
               }
               while (query.moveToNext());
           }
           if(quizFiles.size()>0){
               ListView list = findViewById(R.id.FilesList);
               QuizAdapter quizAdapter = new QuizAdapter(this,R.layout.file_item,quizFiles);
               list.setAdapter(quizAdapter);
           }
           query.close();
           db.close();
       }
       catch (Exception e){
           Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
       }*/
    }
    public void LoadTest(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String file = null;
            List<String> files = new ArrayList<>();
            if (data != null) {
                try {
                    if(data.getData()!= null) file = readTextFromUri(data.getData());
                    else if(data.getClipData()!= null) {
                        for (int i = 0; i<data.getClipData().getItemCount();i++)
                            files.add(readTextFromUri(data.getClipData().getItemAt(i).getUri()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(file!= null || files.size()>0)
            {
                try{
                    QuizFile quiz = file!= null ? new QuizFile(file) : new QuizFile(files);
                    Intent testPage = new Intent(this,TestPage.class);
                    testPage.putExtra("QuizFile",quiz);
                    startActivity(testPage);
                }
                catch (Exception e){
                    Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream),"Windows-1251"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if(!line.trim().equals(""))
                stringBuilder.append(line).append("\r\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }
}
