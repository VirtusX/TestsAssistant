package virtusx.testsassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainTestPage extends AppCompatActivity  {

    private QuizFile quiz;
    private ListView list;
    private Integer currentQuestion;
    private List<QuizFile.Question> questions = new ArrayList<>();
    private ColorStateList ra;
    private ColorStateList fa;
    SharedPreferences prefs;
    private Boolean hideAnswers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_test_page);
            ra  =  ColorStateList.valueOf(getResources().getColor(R.color.colorRight));
            fa  =  ColorStateList.valueOf(getResources().getColor(R.color.colorError));
            quiz = (QuizFile) Objects.requireNonNull(getIntent().getExtras()).get("QuizFile");
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            hideAnswers = prefs.getBoolean("blind_mode",false);
            Object num = getIntent().getExtras().get("QuestionNumber");
            if (quiz != null) {
                List<QuizFile.Question> qsts = new ArrayList<>(quiz.getQuestions());
                if(num!= null && isNumeric(num.toString()) && Integer.parseInt(num.toString()) <quiz.getQuestions().size())
                    questions = RandomList(qsts).subList(0, Integer.parseInt(num.toString()));
                else if(prefs.getBoolean("random_order",false))
                    questions = RandomList(qsts);
                else  questions = qsts;
                if(prefs.getBoolean("random_answers",false)) for (QuizFile.Question q : questions) Collections.shuffle(q.getAnswers());
                currentQuestion = 0;
                QuizFile.Question qst = questions.get(currentQuestion);
                ((TextView)findViewById(R.id.Question)).setMovementMethod(new ScrollingMovementMethod());
                ((TextView)findViewById(R.id.Question)).setText(String.format(getResources().getString(R.string.question_text),(currentQuestion+1),questions.size(),qst.getQuestionName()));
                list = findViewById(R.id.Answers);
                if(hideAnswers)
                    findViewById(R.id.checkButton).setVisibility(View.INVISIBLE);
                AnswerAdapter answerArrayAdapter = new AnswerAdapter(this,R.layout.answer_item,qst.getAnswers(),hideAnswers);
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
            for (Integer i = 0; i< list.getCount(); i++ ) {
                QuizFile.Answer a = (QuizFile.Answer) list.getAdapter().getItem(i);
                Boolean answer = a.getChecked().equals(questions.get(currentQuestion).getAnswer(i).getRight());
                a.setAnswered(answer ? ra : fa);
            }
            findViewById(R.id.checkButton).setEnabled(false);
            ((AnswerAdapter)list.getAdapter()).notifyDataSetChanged();
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void onNextClick(View view){
        try{
            if(view.getId()==R.id.backButton) {
                --currentQuestion;
                findViewById(R.id.checkButton).setEnabled(false);
            }
            else
            {
               if(!questions.get(currentQuestion).getAnswered()){
                   ListView list = findViewById(R.id.Answers);
                   int falses = 0;
                   for (Integer i = 0; i< list.getCount(); i++ ) {
                       QuizFile.Answer a = (QuizFile.Answer) list.getAdapter().getItem(i);
                       Boolean answer = a.getChecked().equals(questions.get(currentQuestion).getAnswer(i).getRight());
                       if(!answer)
                           falses++;
                       a.setAnswered(answer ? ra : fa);
                       questions.get(currentQuestion).setAnswered(true);
                   }
                   questions.get(currentQuestion).setRightAnswer(falses== 0);
                   Integer answers = questions.get(currentQuestion).getAnswers().size();
                   if(!hideAnswers)
                        Snackbar.make(view, String.format(getResources().getString(R.string.question_result),answers-falses,answers), Snackbar.LENGTH_SHORT).show();
               }
                ++currentQuestion;
                if(currentQuestion >= questions.size() || currentQuestion<0){
                    Intent testPage = new Intent(this,TestPage.class);
                    testPage.putExtra("QuizFile",quiz);
                    testPage.putExtra("TestNumber",questions.size());
                    int fan = 0;
                    for (QuizFile.Question q :
                            questions) {
                        if(!q.getRightAnswer())
                            ++fan;
                    }
                    testPage.putExtra("FalseAnswers",fan);
                    startActivity(testPage);
                    return;
                }
                findViewById(R.id.checkButton).setEnabled(!questions.get(currentQuestion).getAnswered());
            }
            QuizFile.Question qst = questions.get(currentQuestion);
            findViewById(R.id.Question).scrollTo(0,0);
            ((TextView)findViewById(R.id.Question)).setText(String.format(getResources().getString(R.string.question_text),(currentQuestion+1),questions.size(),qst.getQuestionName()));
            list = findViewById(R.id.Answers);
            AnswerAdapter answerArrayAdapter = new AnswerAdapter(this,R.layout.answer_item,qst.getAnswers(), hideAnswers);
            list.setAdapter(answerArrayAdapter);
            findViewById(R.id.end_button).setVisibility(currentQuestion+1==questions.size() ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.nextButton).setVisibility(currentQuestion+1!= questions.size() ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.backButton).setVisibility(currentQuestion>0 ? View.VISIBLE : View.INVISIBLE);
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static List<QuizFile.Question> RandomList(List<QuizFile.Question> questions){
        Collections.shuffle(questions);
        return questions;
    }
}
