package virtusx.testsassistant;

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
        setQuestions(file);
    }

    private void setQuestions(String file) {
        String[] split = file.split("\r\n\\?");
        ArrayList<String> list = new ArrayList<>( Arrays.asList(split));
        Name = list.get(0);
        list.remove(0);
        Questions = new ArrayList<>();
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
            for (int j = 0; j < answrs.size(); j++) {
                if(!answrs.get(j).replace("\r\n","").trim().equals(""))
                    quest.Answers.add(new Answer(j,answrs.get(j).replace("\r\n","").substring(1).trim(),answrs.get(j).contains("+")));
            }
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
        private Boolean Answered = false;

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
            return Answered;
        }

        public void setAnswered(Boolean answered) {
            Answered = answered;
        }
    }

    public class Answer implements Serializable{
        private String AnswerText;
        private Boolean isRight;
        private Boolean Checked = false;
        private ColorStateList Answered;
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
            return Checked;
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
class AnswerAdapter extends ArrayAdapter<QuizFile.Answer> {
    private LayoutInflater inflater;
    private int layout;
    private List<QuizFile.Answer> answers;

    AnswerAdapter(Context context, int resource, List<QuizFile.Answer> answers) {
        super(context, resource, answers);
        this.answers = answers;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
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

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        view = inflater.inflate(this.layout, parent, false);
        CheckBox checkBox = view.findViewById(R.id.answer);
        checkBox.setOnClickListener(view1 -> answers.get(position).setChecked(((CheckBox)view1).isChecked()));
        QuizFile.Answer answer = answers.get(position);
        if(answer.getAnswered()!= null){
            checkBox.setButtonTintList(answer.getAnswered());
            checkBox.setEnabled(false);
        }
        checkBox.setChecked(answer.getChecked());
        checkBox.setText(answer.getAnswerText());
        checkBox.setId(answer.Id);
        return view;
    }
}

/*class QuizAdapter extends ArrayAdapter<QuizFile>{
    private LayoutInflater inflater;
    private int layout;
    private List<QuizFile> quizFiles;

    QuizAdapter(Context context, int resource, List<QuizFile> quizFiles) {
        super(context, resource, quizFiles);
        this.quizFiles = quizFiles;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        view = inflater.inflate(this.layout, parent, false);
        try {
            TextView textView = view.findViewById(R.id.FileName);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    try {
                        String file = readTextFromUri(Environment.getExternalStorageDirectory().getPath()+quizFiles.get(position).getPath(),view.getContext());
                        QuizFile quiz = new QuizFile(file);
                        Intent testPage = new Intent(view.getContext(),TestPage.class);
                        testPage.putExtra("QuizFile",quiz);
                        view.getContext().startActivity(testPage);
                    } catch (IOException e) {
                        Toast.makeText(view.getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            QuizFile quizFile = quizFiles.get(position);
            textView.setText(quizFile.getFileName());
        }
        catch (Exception e){
            e.toString();
        }
        return view;
    }
    private String readTextFromUri(String uri, Context cont) throws IOException {
        InputStream inputStream = new FileInputStream( uri);
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
}*/
