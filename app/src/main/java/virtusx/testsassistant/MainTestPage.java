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

public class MainTestPage extends AppCompatActivity  {

    private ListView list;
    private ColorStateList ra;
    private ColorStateList fa;
    SharedPreferences prefs;
    private Boolean hideAnswers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode", false)) {
            setTheme(android.R.style.Theme_Material_NoActionBar);
        }
        setContentView(R.layout.activity_main_test_page);
        StartTest();
    }

    private void StartTest() {
        try {
            ra  =  ColorStateList.valueOf(getResources().getColor(R.color.colorRight));
            fa  =  ColorStateList.valueOf(getResources().getColor(R.color.colorError));
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            hideAnswers = prefs.getBoolean("blind_mode",false);
            Object startNew = getIntent().getExtras() != null ? getIntent().getExtras().get("startNew") : null;
            Object num = getIntent().getExtras() != null ? getIntent().getExtras().get("QuestionNumber") : null;
            if (QuizFile.getInstance(this.getExternalFilesDir(null)) != null) {
                if (startNew != null) {
                    ArrayList<QuizFile.Question> qsts = new ArrayList<>(QuizFile.getInstance(this.getExternalFilesDir(null)).resetQuestions());
                    if (num != null && isNumeric(num.toString()) && Integer.parseInt(num.toString()) < qsts.size()) {
                        List<QuizFile.Question> sub = prefs.getBoolean("random_order", false) ? RandomList(qsts) : qsts;
                        QuizFile.getInstance(this.getExternalFilesDir(null)).setCurrentQuestions(new ArrayList<>(sub.subList(0, Integer.parseInt(num.toString()))));
                    } else if (prefs.getBoolean("random_order", false))
                        QuizFile.getInstance(this.getExternalFilesDir(null)).setCurrentQuestions(RandomList(qsts));
                    else
                        QuizFile.getInstance(this.getExternalFilesDir(null)).setCurrentQuestions(qsts);
                    if (prefs.getBoolean("random_answers", false))
                        for (QuizFile.Question q : QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions())
                            Collections.shuffle(QuizFile.getInstance(this.getExternalFilesDir(null)).getAnswersByQuestId(q.Id));
                    QuizFile.getInstance(this.getExternalFilesDir(null)).setCurrentQuestion(0);
                }
                QuizFile.Question qst = QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion());
                ((TextView)findViewById(R.id.Question)).setMovementMethod(new ScrollingMovementMethod());
                ((TextView) findViewById(R.id.Question)).setText(String.format(getResources().getString(R.string.question_text), (QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion() + 1), QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().size(), qst.getQuestionName()));
                list = findViewById(R.id.Answers);
                if(hideAnswers)
                    findViewById(R.id.checkButton).setVisibility(View.INVISIBLE);
                AnswerAdapter answerArrayAdapter = new AnswerAdapter(this, R.layout.answer_item, QuizFile.getInstance(this.getExternalFilesDir(null)).getAnswersByQuestId(qst.Id), hideAnswers, fa, ra);
                list.setAdapter(answerArrayAdapter);
                setButtonsVisibility();
            }
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void checkAnswer(View view) {
        try {
            ListView list = findViewById(R.id.Answers);
            for (int i = 0; i < list.getCount(); i++) {
                QuizFile.Answer a = (QuizFile.Answer) list.getAdapter().getItem(i);
                int id = QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion()).Id;
                boolean answer = a.getChecked().equals(QuizFile.getInstance(this.getExternalFilesDir(null)).getAnswersByQuestId(id).get(i).getRight());
                a.setAnswered(answer);
            }
            findViewById(R.id.checkButton).setEnabled(false);
            ((AnswerAdapter)list.getAdapter()).notifyDataSetChanged();
        }
        catch (Exception e){
            Toast.makeText(this.getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        save(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StartTest();
    }

    public void save(View view) {
        try {
            QuizFile.SaveQuizFile(QuizFile.getInstance(this.getExternalFilesDir(null)), this.getExternalFilesDir(null));
            if (view != null) {
                Intent mainPage = new Intent(this, MainPage.class);
                startActivity(mainPage);
            }
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onNextClick(View view){
        try{
            if (view.getId() == R.id.backButton) previousTest();
            else if (nextTest(view)) return;
            changeTest();
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void changeTest() {
        QuizFile.Question qst = QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion());
        findViewById(R.id.Question).scrollTo(0, 0);
        ((TextView) findViewById(R.id.Question)).setText(String.format(getResources().getString(R.string.question_text), (QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion() + 1), QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().size(), qst.getQuestionName()));
        list = findViewById(R.id.Answers);
        AnswerAdapter answerArrayAdapter = new AnswerAdapter(this, R.layout.answer_item, QuizFile.getInstance(this.getExternalFilesDir(null)).getAnswersByQuestId(qst.Id), hideAnswers, fa, ra);
        list.setAdapter(answerArrayAdapter);
        setButtonsVisibility();
    }

    private boolean nextTest(View view) {
        if (!QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion()).getAnswered()) {
            ListView list = findViewById(R.id.Answers);
            int falses = 0;
            for (int i = 0; i < list.getCount(); i++) {
                QuizFile.Answer a = (QuizFile.Answer) list.getAdapter().getItem(i);
                int id = QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion()).Id;
                boolean answer = a.getChecked().equals(QuizFile.getInstance(this.getExternalFilesDir(null)).getAnswersByQuestId(id).get(i).getRight());
                if (!answer)
                    falses++;
                a.setAnswered(answer);
                QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion()).setAnswered(true);
            }
            QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion()).setRightAnswer(falses == 0);
            int curId = QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion()).Id;
            Integer answers = QuizFile.getInstance(this.getExternalFilesDir(null)).getAnswersByQuestId(curId).size();
            if (!hideAnswers)
                Snackbar.make(view, String.format(getResources().getString(R.string.question_result), answers - falses, answers), Snackbar.LENGTH_SHORT).show();
        }
        QuizFile.getInstance(this.getExternalFilesDir(null)).setCurrentQuestion(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion() + 1);
        if (QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion() >= QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().size() || QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion() < 0) {
            Intent mainPage = new Intent(this, MainPage.class);
            mainPage.putExtra("TestNumber", QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().size());
            int fan = 0;
            for (QuizFile.Question q : QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions())
                if (!q.getRightAnswer())
                    ++fan;
            mainPage.putExtra("FalseAnswers", fan);
            QuizFile.getInstance(this.getExternalFilesDir(null)).setCurrentQuestions(null);
            startActivity(mainPage);
            return true;
        }
        findViewById(R.id.checkButton).setEnabled(!QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().get(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion()).getAnswered());
        return false;
    }

    private void previousTest() {
        QuizFile.getInstance(this.getExternalFilesDir(null)).setCurrentQuestion(QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion() - 1);
        findViewById(R.id.checkButton).setEnabled(false);
    }

    private void setButtonsVisibility() {
        int current = QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestion();
        boolean endVisible = current + 1 == QuizFile.getInstance(this.getExternalFilesDir(null)).getCurrentQuestions().size();
        findViewById(R.id.end_button).setVisibility(endVisible ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.nextButton).setVisibility(!endVisible ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.backButton).setVisibility(current > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static List<QuizFile.Question> RandomList(List<QuizFile.Question> questions){
        Collections.shuffle(questions);
        return questions;
    }
}
