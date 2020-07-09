import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

public class QA {
    private String question;
    private String answer;
    private int index;
    private Document document;
    private Project project;

    public QA(String question, int index, Document document, Project project) {
        this.question = question;
        this.index = index;
        this.document = document;
        this.answer = "idk";
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "QA{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", index=" + index +
                ", document=" + document +
                '}';
    }
}
