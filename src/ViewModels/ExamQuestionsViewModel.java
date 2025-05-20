package ViewModels;

import BussinessLogic.ExamsBLL;
import BussinessLogic.QuestionsBLL;
import BussinessLogic.AnswersBLL;
import BussinessLogic.ExamQuestionsBLL;
import DataAccess.DTO.Exams;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Answers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ExamQuestionsViewModel {
    private ExamsBLL examsBLL = new ExamsBLL();
    private QuestionsBLL questionsBLL = new QuestionsBLL();
    private AnswersBLL answersBLL = new AnswersBLL();
    private ExamQuestionsBLL examQuestionsBLL = new ExamQuestionsBLL();

    private List<ExamWithQuestionsVM> examWithQuestionsList;

    public void loadData() throws Exception {
        // Lấy dữ liệu exams, questions, answers, examQuestions
        List<List<Object>> rawExams = examsBLL.getExams();
        List<List<Object>> rawQuestions = questionsBLL.getQuestions();
        List<List<Object>> rawAnswers = answersBLL.getAnswers();
        List<List<Object>> rawExamQuestions = examQuestionsBLL.getExamQuestions();

        // Map ExamID -> Exams
        LinkedHashMap<Integer, Exams> examsMap = new LinkedHashMap<>();
        for (List<Object> row : rawExams) {
            Exams e = new Exams();
            e.setExamID((Integer) row.get(0));
            // Có thể là setTitle hoặc setExamName tuỳ DTO của bạn
            if (row.size() > 1) {
                if (row.get(1) instanceof String) {
                    e.setTitle((String) row.get(1));
                }
            }
            examsMap.put(e.getExamID(), e);
        }

        // Map QuestionID -> Questions
        LinkedHashMap<Integer, Questions> questionsMap = new LinkedHashMap<>();
        for (List<Object> row : rawQuestions) {
            Questions q = new Questions();
            q.setQuestionID((Integer) row.get(0));
            if (row.size() > 1) {
                q.setContent((String) row.get(1));
            }
            questionsMap.put(q.getQuestionID(), q);
        }

        // Map QuestionID -> List<Answers>
        LinkedHashMap<Integer, List<Answers>> answersMap = new LinkedHashMap<>();
        for (List<Object> row : rawAnswers) {
            Answers a = new Answers();
            a.setAnswersID((Integer) row.get(0));
            a.setQuestionID((Integer) row.get(1));
            a.setContent((String) row.get(2));
            a.setCorrect((Boolean) row.get(3));
            answersMap.computeIfAbsent(a.getQuestionID(), k -> new ArrayList<>()).add(a);
        }

        // Map ExamID -> List<QuestionID> (giữ thứ tự xuất hiện)
        LinkedHashMap<Integer, List<Integer>> examToQuestionIDs = new LinkedHashMap<>();
        for (List<Object> row : rawExamQuestions) {
            Integer examId = (Integer) row.get(0);
            Integer questionId = (Integer) row.get(1);
            examToQuestionIDs.computeIfAbsent(examId, k -> new ArrayList<>()).add(questionId);
        }

        examWithQuestionsList = new ArrayList<>();

        for (Integer examId : examsMap.keySet()) {
            Exams exam = examsMap.get(examId);
            List<Integer> questionIDs = examToQuestionIDs.getOrDefault(examId, new ArrayList<>());
            List<Questions> examQuestions = new ArrayList<>();
            List<List<Answers>> answersList = new ArrayList<>();
            for (Integer qid : questionIDs) {
                Questions q = questionsMap.get(qid);
                if (q != null) {
                    examQuestions.add(q);
                    List<Answers> ans = answersMap.getOrDefault(qid, new ArrayList<>());
                    answersList.add(ans);
                }
            }
            ExamWithQuestionsVM vm = new ExamWithQuestionsVM();
            vm.setExam(exam);
            vm.setQuestions(examQuestions);
            vm.setAnswersList(answersList);
            examWithQuestionsList.add(vm);
        }
    }

    public List<ExamWithQuestionsVM> getExamWithQuestionsList() {
        return examWithQuestionsList;
    }
}
