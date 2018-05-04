package virtusx.testsassistant;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainTestPage extends AppCompatActivity  {

    private QuizFile quiz;
    private ListView list;
    private Integer currentQuestion;
    private List<QuizFile.Question> questions = new ArrayList<>();
    private Integer falseAnswer = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_test_page);
            quiz = (QuizFile) Objects.requireNonNull(getIntent().getExtras()).get("QuizFile");
            Object num = getIntent().getExtras().get("QuestionNumber");
            if (quiz != null) {
                if(num!= null){
                    Random randomGenerator = new Random();
                    List<QuizFile.Question> qsts = quiz.getQuestions();
                    for(int i = 0; i<Integer.parseInt(num.toString());i++){
                        int index = randomGenerator.nextInt(qsts.size());
                        questions.add(qsts.get(index));
                        qsts.remove(index);
                    }
                }else questions = quiz.getQuestions();
                currentQuestion = 0;
                QuizFile.Question qst = questions.get(currentQuestion);
                ((TextView)findViewById(R.id.Question)).setText((currentQuestion+1)+"/"+questions.size()+" "+qst.getQuestionName());
                list = findViewById(R.id.Answers);
                AnswerAdapter answerArrayAdapter = new AnswerAdapter(this,R.layout.answer_item,qst.getAnswers());
                list.setAdapter(answerArrayAdapter);
            }
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public void onCheckClick(View view){
        try {
            ListView list = findViewById(R.id.Answers);
            int falses = 0;
            for (Integer i = 0; i< list.getCount(); i++ ) {
                QuizFile.Answer a = (QuizFile.Answer) list.getAdapter().getItem(i);
                Integer id = a.Id;
                Boolean answer = a.getChecked().equals(questions.get(currentQuestion).getAnswer(id).getRight());
                if(!answer)
                    falses++;
                setAnswer(a,answer);
            }
            if(falses!= 0)
                falseAnswer++;
            ((AnswerAdapter)list.getAdapter()).notifyDataSetChanged();
            findViewById(R.id.checkButton).setEnabled(false);
            questions.get(currentQuestion).setAnswered(true);
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void onNextClick(View view){
        try{
            if(view.getId()==R.id.backButton) --currentQuestion;
            else
            {
                if(!questions.get(currentQuestion).getAnswered()){
                    ListView list = findViewById(R.id.Answers);
                    int falses = 0;
                    for (Integer i = 0; i< list.getCount(); i++ ) {
                        QuizFile.Answer a = (QuizFile.Answer) list.getAdapter().getItem(i);
                        Integer id = a.Id;
                        Boolean answer = a.getChecked().equals(questions.get(currentQuestion).getAnswer(id).getRight());
                        if(!answer)
                            falses++;
                        setAnswer(a,answer);
                    }
                    if(falses!= 0)
                        falseAnswer++;
                    questions.get(currentQuestion).setAnswered(true);
                }
                ++currentQuestion;
            }
            if(currentQuestion >= questions.size() || currentQuestion<0){
                Intent testPage = new Intent(this,TestPage.class);
                testPage.putExtra("QuizFile",quiz);
                testPage.putExtra("TestNumber",questions.size());
                testPage.putExtra("FalseAnswers",falseAnswer);
                startActivity(testPage);
                return;
            }
            findViewById(R.id.checkButton).setEnabled(!questions.get(currentQuestion).getAnswered());
            QuizFile.Question qst = questions.get(currentQuestion);
            ((TextView)findViewById(R.id.Question)).setText((currentQuestion+1)+"/"+questions.size()+" "+qst.getQuestionName());
            list = findViewById(R.id.Answers);
            AnswerAdapter answerArrayAdapter = new AnswerAdapter(this,R.layout.answer_item,qst.getAnswers());
            list.setAdapter(answerArrayAdapter);
            findViewById(R.id.end_button).setVisibility(currentQuestion+1==questions.size() ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.nextButton).setVisibility(currentQuestion+1!= questions.size() ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.backButton).setVisibility(currentQuestion>0 ? View.VISIBLE : View.INVISIBLE);
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void setAnswer(QuizFile.Answer a, Boolean answer){
        if(a!= null)
            a.setAnswered(answer ? ColorStateList.valueOf(getResources().getColor(R.color.colorRight)) : ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
    }
}
