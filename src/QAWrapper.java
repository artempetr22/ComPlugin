import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.AbstractLayout;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class QAWrapper extends DialogWrapper {
    private ArrayList<QA> qas;
    private AnActionEvent event;
    private String filename;

    @Override
    protected void init() {
        super.init();

        setTitle("Questions in " + filename);
    }

    protected QAWrapper(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        for (int i = 0; i < qas.size() ; i++) {
            // add components to the panel
            constraints.gridy = (i*3);
            constraints.gridx = 0;
            newPanel.add(new Label(qas.get(i).getQuestion()), constraints);

            constraints.gridy = (i*3)+1;
            TextField textField = new TextField(100);
            newPanel.add(textField, constraints);

            constraints.gridy = (i*3)+2;

            Button btn = new Button("answer");

            int finalI = i;
            btn.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String answer = textField.getText();
                    qas.get(finalI).setAnswer(answer);
                    doWriteAnswerToDocument(qas.get(finalI));

                    newPanel.removeAll();
                    close(1);

                    //recreate action
                    new PopUpAction().actionPerformed(event);
                }
            });

            newPanel.add(btn, constraints);
        }

        if (qas.size()==0) {
            constraints.gridy = 0;
            constraints.gridx = 0;
            newPanel.add(new Label("No Unresolved questions"), constraints);
        }

        return newPanel;
    }

    public ArrayList<QA> getQas() {
        return qas;
    }

    public void setQas(ArrayList<QA> qas) {
        this.qas = qas;
    }

    private JComponent label(String l){
        JLabel label = new JLabel(l);

        label.setBorder(JBUI.Borders.empty(0,5,2,0));

        return label;
    }

    private void doWriteAnswerToDocument(QA qa){
        WriteCommandAction.runWriteCommandAction(qa.getProject(), () -> {
            qa.getDocument().replaceString(qa.getIndex(), qa.getIndex(), "//@Answer " + qa.getAnswer() + "\n");
        });
    }

    public AnActionEvent getEvent() {
        return event;
    }

    public void setEvent(AnActionEvent event) {
        this.event = event;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
