package ViewModels;
import BussinessLogic.AnswersBLL;
import BussinessLogic.QuestionsBLL;
import DataAccess.DTO.Answers;
import DataAccess.DTO.Questions;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class QuestionAnswersViewModel {
    private QuestionsBLL questionBLL = new QuestionsBLL();
    private AnswersBLL answerBLL = new AnswersBLL();

    private List<QuestionWithAnswersVM> questionAnswerList;

    public void loadData() throws Exception {
        List<List<Object>> rawQuestions = questionBLL.getQuestions();
        List<List<Object>> rawAnswers = answerBLL.getAnswers();

        List<Questions> questions = new ArrayList<>();
        for (List<Object> row : rawQuestions) {
            Questions q = new Questions();
            q.setQuestionID((Integer) row.get(0));
            q.setContent((String) row.get(1));
            questions.add(q);
        }

        List<Answers> answers = new ArrayList<>();
        for (List<Object> row : rawAnswers) {
            Answers a = new Answers();
            a.setAnswersID((Integer) row.get(0));
            a.setQuestionID((Integer) row.get(1));
            a.setContent((String) row.get(2));
            a.setCorrect((Boolean) row.get(3));
            answers.add(a);
        }

        questionAnswerList = new ArrayList<>();

        for (Questions q : questions) {
            List<Answers> relatedAnswers = answers.stream()
                    .filter(a -> a.getQuestionID() == q.getQuestionID())
                    .collect(Collectors.toList());

            QuestionWithAnswersVM item = new QuestionWithAnswersVM();
            item.setQuestion(q);
            item.setAnswers(relatedAnswers);

            questionAnswerList.add(item);
        }
    }

    public List<QuestionWithAnswersVM> getQuestionAnswerList() {
        return questionAnswerList;
    }

}
