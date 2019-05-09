package virtusx.testsassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizFile implements Serializable {
    private static QuizFile current;

    static void setCurrent(QuizFile quizFile) {
        current = quizFile;
    }

    static QuizFile getInstance(File directory) {
        if (current == null)
            current = LoadQuizFile(directory);
        return current;
    }

    private List<Question> currentQuestions;

    List<Question> getCurrentQuestions() {
        return currentQuestions;
    }

    void setCurrentQuestions(List<Question> questions) {
        currentQuestions = questions;
    }

    private int currentQuestion;

    int getCurrentQuestion() {
        return currentQuestion;
    }

    void setCurrentQuestion(int q) {
        currentQuestion = q;
    }

    private List<Answer> currentAnswers;

    List<Answer> getAnswersByQuestId(int id) {
        List<Answer> res = new ArrayList<>();
        for (Answer a : currentAnswers)
            if (a.QuestId == id)
                res.add(a);
        return res;
    }
    private String Name;
    private List<Question> Questions;
    private Integer Id;

    QuizFile(String file) {
        Questions = new ArrayList<>();
        currentAnswers = new ArrayList<>();
        setQuestions(file);
    }
    QuizFile(List<String> files) {
        Questions = new ArrayList<>();
        for (String file : files) setQuestions(file);
    }

    static void initInstance() {
    }

    List<Question> resetQuestions() {
        for (Question q : getQuestions()) q.setAnswered(false);
        for (Answer a : currentAnswers) {
            a.setChecked(false);
            a.setAnswered(null);
        }
        return getQuestions();
    }
    private void setQuestions(String file) {
        String[] split = file.split("\r\n\\?");
        ArrayList<String> list = new ArrayList<>( Arrays.asList(split));
        Name = Name == null ? list.get(0).trim() : Name+", "+list.get(0).trim() ;
        list.remove(0);
        for (Integer i = 0; i<list.size(); i++){
            ArrayList<String> answers = new ArrayList<>(Arrays.asList(list.get(i).split("\r\n")));
            ArrayList<String> answrs = new ArrayList<>();
            for (String a : answers){
                if(!a.trim().equals(""))
                    answrs.add(a);
            }
            Question quest = new Question();
            quest.Id = i;
            quest.QuestionName = answrs.get(0).replace("\r\n","").trim();
            answrs.remove(0);
            for (int j = 0; j < answrs.size(); j++)
                if (!answrs.get(j).replace("\r\n", "").trim().equals(""))
                    currentAnswers.add(new Answer(j, i, answrs.get(j).substring(1).trim().replace("\r\n", "").trim(),
                            answrs.get(j).charAt(0) == '+'));
            Questions.add(quest);
        }
    }

    public String getName() {
        return Name;
    }

    List<Question> getQuestions() {
        return Questions;
    }

    public Integer getId() { return Id;}

    public void setId(Integer id) { Id = id;}

    class Question implements Serializable {
        private String QuestionName;
        Integer Id;
        private Boolean Answered = false;
        private Boolean RightAnswer = false;

        String getQuestionName() {
            return QuestionName;
        }

        Boolean getAnswered() {
            return Answered !=null ? Answered : false;
        }

        void setAnswered(Boolean answered) {
            Answered = answered;
        }

        Boolean getRightAnswer() {
            return RightAnswer != null ? RightAnswer : false;
        }

        void setRightAnswer(Boolean rightAnswer) {
            RightAnswer = rightAnswer;
        }
    }

    class Answer implements Serializable {
        private String AnswerText;
        private Boolean isRight;
        private Boolean Checked = false;
        private Boolean Answered;
        Integer Id;
        Integer QuestId;

        Answer(int Id, int questId, String text, boolean isRight) {
            this.Id = Id;
            this.QuestId = questId;
            setAnswerText(text);
            setRight(isRight);
        }

        String getAnswerText() {
            return AnswerText;
        }

        void setAnswerText(String answerText) {
            AnswerText = answerText;
        }

        Boolean getRight() {
            return isRight;
        }

        void setRight(Boolean right) {
            isRight = right;
        }

        Boolean getChecked() {
            return Checked != null ? Checked : false;
        }

        void setChecked(Boolean checked) {
            Checked = checked;
        }

        Boolean getAnswered() {
            return Answered;
        }

        void setAnswered(Boolean answered) {
            Answered = answered;
        }
    }

    static void SaveQuizFile(QuizFile quiz, File directory) {
        ObjectOutput out;
        try {
            File outFile = new File(directory, "appSaveQuiz.data");
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(quiz);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static QuizFile LoadQuizFile(File directory) {
        ObjectInput in;
        QuizFile res = null;
        try {
            FileInputStream fileIn = new FileInputStream(directory.getPath() + File.separator + "appSaveQuiz.data");
            in = new ObjectInputStream(fileIn);
            res = (QuizFile) in.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static int HasPreviousQuiz(File directory) {
        try {
            File file = new File(directory.getPath() + File.separator + "appSaveQuiz.data");
            return file.exists() ? 0 : 4;
        } catch (Exception e) {
            return 4;
        }
    }
}
class AnswerAdapter extends ArrayAdapter<QuizFile.Answer> implements Serializable{
    private LayoutInflater inflater;
    private int layout;
    private List<QuizFile.Answer> answers;
    private boolean hideAnswer;
    private ColorStateList falseColor;
    private ColorStateList rightColor;

    AnswerAdapter(Context context, int resource, List<QuizFile.Answer> answers, Boolean hideAnswers, ColorStateList fa, ColorStateList ra) {
        super(context, resource, answers);
        this.answers = answers;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        hideAnswer = hideAnswers;
        falseColor = fa;
        rightColor = ra;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        view = inflater.inflate(this.layout, parent, false);
        CheckBox checkBox = view.findViewById(R.id.answer);
        QuizFile.Answer answer = answers.get(position);
        checkBox.setOnClickListener(view1 -> answers.get(position).setChecked(((CheckBox) view1).isChecked()));
        if(answer.getAnswered()!= null){
            if (!hideAnswer)
                checkBox.setButtonTintList(answer.getAnswered() ? rightColor : falseColor);
            checkBox.setEnabled(false);
        }
        checkBox.setChecked(answer.getChecked());
        checkBox.setText(answer.getAnswerText());
        checkBox.setId(answer.Id);
        return view;
    }
}
