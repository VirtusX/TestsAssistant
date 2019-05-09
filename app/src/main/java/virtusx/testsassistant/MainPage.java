package virtusx.testsassistant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainPage extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode",false)){
            setTheme(android.R.style.Theme_Material_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("TestNumber") != null) {
            Integer all = getIntent().getExtras().getInt("TestNumber");
            Integer wrong = getIntent().getExtras().getInt("FalseAnswers");
            ((TextView)findViewById(R.id.testResult)).setText(String.format(getResources().getString(R.string.test_result),all-wrong,all));
        }
        findViewById(R.id.LoadPrevious).setVisibility(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions() != null ? View.VISIBLE : View.INVISIBLE);
        ((TextView) findViewById(R.id.TestName)).setText(QuizFile.getInstance(this.getExternalFilesDir(null)).getName());
        ((EditText) findViewById(R.id.selectQuestion)).setHint("1 - " + QuizFile.getInstance(this.getExternalFilesDir(null)).getQuestions().size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.settings){
            Intent intent = new Intent(this,Settings.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.exit) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("*/*");
            startActivityForResult(intent,READ_REQUEST_CODE);
        }
        return false;
    }
    public void onAllTestClick(View view){
        try{
            Intent testPage = new Intent(this, MainTestPage.class);
            testPage.putExtra("startNew", true);
            startActivity(testPage);
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    public void onSelectedTestClick(View view){
        try{
            Intent testPage = new Intent(this, MainTestPage.class);
            testPage.putExtra("QuestionNumber",((EditText)findViewById(R.id.selectQuestion)).getText());
            testPage.putExtra("startNew", true);
            startActivity(testPage);
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void continueTest(View view) {
        try {
            Intent testPage = new Intent(this, MainTestPage.class);
            testPage.putExtra("Continue", true);
            startActivity(testPage);
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
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
                    QuizFile.setCurrent(file != null ? new QuizFile(file) : new QuizFile(files));
                    Intent testPage = new Intent(this, MainPage.class);
                    QuizFile.SaveQuizFile(QuizFile.getInstance(this.getExternalFilesDir(null)), this.getExternalFilesDir(null));
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
