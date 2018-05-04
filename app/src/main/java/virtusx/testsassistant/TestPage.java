package virtusx.testsassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class TestPage extends AppCompatActivity {

    private QuizFile quiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);
        quiz = (QuizFile) Objects.requireNonNull(getIntent().getExtras()).get("QuizFile");
        if(getIntent().getExtras().get("TestNumber")!= null){
            Integer all = getIntent().getExtras().getInt("TestNumber");
            Integer wrong = getIntent().getExtras().getInt("FalseAnswers");
            ((TextView)findViewById(R.id.testResult)).setText(String.format(getResources().getString(R.string.test_result),all-wrong,all));
        }
        ((TextView)findViewById(R.id.TestName)).setText(quiz.getName());
        ((EditText)findViewById(R.id.selectQuestion)).setHint("1 - "+quiz.getQuestions().size());
    }

    public void onAllTestClick(View view){
        try{
            Intent testPage = new Intent(this,MainTestPage.class);
            testPage.putExtra("QuizFile",quiz);
            startActivity(testPage);
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    public void onSelectedTestClick(View view){
        try{
            Intent testPage = new Intent(this,MainTestPage.class);
            testPage.putExtra("QuizFile",quiz);
            testPage.putExtra("QuestionNumber",((EditText)findViewById(R.id.selectQuestion)).getText());
            startActivity(testPage);
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}
