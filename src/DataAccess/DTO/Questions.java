package DataAccess.DTO;

public class Questions {
    private int questionID;
    private String content;
    private String type;
    private String level;
    private String soundURL;

    public Questions() {
    }

    public Questions(int questionID, String content, String type, String level, String soundURL) {
        this.questionID = questionID;
        this.content = content;
        this.type = type;
        this.level = level;
        this.soundURL = soundURL;
    }
    public int getQuestionID() {
        return questionID;
    }
    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getSoundURL() {
        return soundURL;
    }
    public void setSoundURL(String soundURL) {
        this.soundURL = soundURL;
    }

}
