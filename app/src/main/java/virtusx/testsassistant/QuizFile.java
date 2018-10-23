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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizFile implements Serializable {
    private String Name;
    private List<Question> Questions;
    private Integer Id;

    QuizFile(String file) {
        Questions = new ArrayList<>();
        setQuestions(file);
    }
    QuizFile(List<String> files) {
        Questions = new ArrayList<>();
        for (String file : files) setQuestions(file);
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
                    quest.Answers.add(new Answer(j,
                            answrs.get(j).substring(1).trim().replace("\r\n", "").trim(),
                            answrs.get(j).charAt(0) == '+'));
            Questions.add(quest);
        }
    }

    public String getName() {
        return Name;
    }

    public List<Question> getQuestions() {
        return Questions;
    }

    public Integer getId() { return Id;}

    public void setId(Integer id) { Id = id;}

    public class Question implements Serializable{
        private String QuestionName;
        private List<Answer> Answers;
        Integer Id;
        private transient Boolean Answered = false;
        private transient Boolean RightAnswer = false;

        Question(){
            Answers = new ArrayList<>();
        }

        public List<Answer> getAnswers() {
            return Answers;
        }

        public void setAnswers(List<Answer> answers) {
            Answers = answers;
        }

        public String getQuestionName() {
            return QuestionName;
        }

        public Answer getAnswer(Integer id){
            return Answers.get(id);
        }

        public Boolean getAnswered() {
            return Answered !=null ? Answered : false;
        }

        public void setAnswered(Boolean answered) {
            Answered = answered;
        }

        public Boolean getRightAnswer() {
            return RightAnswer != null ? RightAnswer : false;
        }

        public void setRightAnswer(Boolean rightAnswer) {
            RightAnswer = rightAnswer;
        }
    }

    public class Answer implements Serializable{
        private String AnswerText;
        private Boolean isRight;
        private transient Boolean Checked = false;
        private transient ColorStateList Answered;
        public Integer Id;

        Answer(int Id, String text, boolean isRight) {
            this.Id = Id;
            setAnswerText(text);
            setRight(isRight);
        }

        public String getAnswerText() {
            return AnswerText;
        }

        void setAnswerText(String answerText) {
            AnswerText = answerText;
        }

        public Boolean getRight() {
            return isRight;
        }

        public void setRight(Boolean right) {
            isRight = right;
        }

        public Boolean getChecked() {
            return Checked != null ? Checked : false;
        }

        public void setChecked(Boolean checked) {
            Checked = checked;
        }

        public ColorStateList getAnswered() {
            return Answered;
        }

        public void setAnswered(ColorStateList answered) {
            Answered = answered;
        }
    }

}
class AnswerAdapter extends ArrayAdapter<QuizFile.Answer> implements Serializable{
    private LayoutInflater inflater;
    private int layout;
    private List<QuizFile.Answer> answers;
    private boolean hideAnswer;
    AnswerAdapter(Context context, int resource, List<QuizFile.Answer> answers, Boolean hideAnswers) {
        super(context, resource, answers);
        this.answers = answers;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        hideAnswer = hideAnswers;
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
            if(!hideAnswer) checkBox.setButtonTintList(answer.getAnswered());
            checkBox.setEnabled(false);
        }
        checkBox.setChecked(answer.getChecked());
        checkBox.setText(answer.getAnswerText());
        checkBox.setId(answer.Id);
        return view;
    }
}
