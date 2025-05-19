import BussinessLogic.AnswersBLL;
import BussinessLogic.ExamsBLL;
import BussinessLogic.QuestionsBLL;
import DataAccess.DAL.ExamsDAL;
import DataAccess.DbConnect;

import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        QuestionsBLL questionsBLL = new QuestionsBLL();
        ExamsBLL examsBLL = new ExamsBLL();
        AnswersBLL answersBLL = new AnswersBLL();
        try {
            // Lấy danh sách câu hỏi
            List<List<Object>> questions = answersBLL.getAnswers();
            System.out.println("Danh sách câu hỏi:");
            for (List<Object> question : questions) {
                System.out.println(question);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}