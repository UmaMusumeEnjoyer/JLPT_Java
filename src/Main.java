import BussinessLogic.ExamsBLL;
import DataAccess.DAL.ExamsDAL;
import DataAccess.DbConnect;
import ViewModels.QuestionAnswersViewModel;
import ViewModels.QuestionWithAnswersVM;
import DataAccess.DTO.Answers;

import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        QuestionAnswersViewModel questionAnswersViewModel = new QuestionAnswersViewModel();
        try{
            questionAnswersViewModel.loadData();
            for(QuestionWithAnswersVM q : questionAnswersViewModel.getQuestionAnswerList()) {
                //System.out.println("Question ID: " + q.getQuestion().getQuestionID());
                System.out.println("Question Content: " + q.getQuestion().getContent());
                for (Answers a : q.getAnswers()) {
                    //System.out.println("Answer ID: " + a.getAnswersID());
                    System.out.print(a.getContent() + " ");
                    System.out.println(a.isCorrect());
                }
                System.out.println();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }
}