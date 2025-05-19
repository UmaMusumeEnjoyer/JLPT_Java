package DataAccess.DTO;

import java.time.LocalDateTime;

public class Exams {
    private int examID;
    private String title;
    private String level;
    private LocalDateTime createDate;

    public Exams() {
    }

    public Exams(int examID, String title, String level, LocalDateTime createDate) {
        this.examID = examID;
        this.title = title;
        this.level = level;
        this.createDate = createDate;
    }

    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
