package ViewModels;

import DataAccess.DTO.Exams;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Answers;
import java.util.List;

public class ExamWithQuestionsVM {
    private Exams exam;
    private List<Questions> questions;
    private List<List<Answers>> answersList;

    public ExamWithQuestionsVM() {}

    public ExamWithQuestionsVM(Exams exam, List<Questions> questions, List<List<Answers>> answersList) {
        this.exam = exam;
        this.questions = questions;
        this.answersList = answersList;
    }

    public Exams getExam() {
        return exam;
    }

    public void setExam(Exams exam) {
        this.exam = exam;
    }

    public List<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Questions> questions) {
        this.questions = questions;
    }

    public List<List<Answers>> getAnswersList() {
        return answersList;
    }

    public void setAnswersList(List<List<Answers>> answersList) {
        this.answersList = answersList;
    }
}
