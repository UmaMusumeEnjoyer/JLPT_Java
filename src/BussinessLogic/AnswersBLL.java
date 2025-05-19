package BussinessLogic;
import DataAccess.DAL.AnswersDAL;
import java.util.List;

public class AnswersBLL {
    private AnswersDAL answersDAL;

    public AnswersBLL() {
        answersDAL = new AnswersDAL();
    }

    // Lấy toàn bộ dữ liệu từ bảng Answers
    public List<List<Object>> getAnswers() throws Exception {
        return answersDAL.getAnswers();
    }

//    // Thêm một câu trả lời mới
//    public void addAnswer(String answerText, int questionId) throws Exception {
//        answersDAL.addAnswer(answerText, questionId);
//    }
//
//    // Cập nhật một câu trả lời
//    public void updateAnswer(int answerId, String answerText) throws Exception {
//        answersDAL.updateAnswer(answerId, answerText);
//    }
//
//    // Xóa một câu trả lời
//    public void deleteAnswer(int answerId) throws Exception {
//        answersDAL.deleteAnswer(answerId);
//    }
}
