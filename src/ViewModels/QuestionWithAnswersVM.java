package ViewModels;
import java.util.List;

import DataAccess.DTO.Answers;
import DataAccess.DTO.Questions;

public class QuestionWithAnswersVM {
    private Questions question;
    private List<Answers> answers;

    public QuestionWithAnswersVM() {}

    public QuestionWithAnswersVM(Questions question, List<Answers> answers) {
        this.question = question;
        this.answers = answers;
    }
    public Questions getQuestion() {
        return question;
    }
    public void setQuestion(Questions question) {
        this.question = question;
    }
    public List<Answers> getAnswers() {
        return answers;
    }
    public void setAnswers(List<Answers> answers) {
        this.answers = answers;
    }

}
