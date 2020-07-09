import com.google.common.collect.Maps;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.ModuleRootManagerComponent;
import com.intellij.openapi.roots.impl.ModuleRootManagerImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.text.ByteArrayCharSequence;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PopUpAction extends AnAction {
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = e.getData(CommonDataKeys.PROJECT);
        String path = project.getBasePath();

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();

        System.out.println(document.getText());

        //нахождение вопросов
        ArrayList<QA> qas = new ArrayList<>();
        doGetQasFromDocument(document, qas);

        qas.forEach(qa -> {
            System.out.println(qa.getQuestion());
            doWriteAnswerToDocument(document, qa);
        });

        System.out.println("Questions: " + qas.size());
        qas.forEach(qa -> {
            System.out.println(qa.toString());
        });

        qas.get(1);

        //запись в файл


        //@myellhome why this?
        ArrayList<VirtualFile> filesToCheck = new ArrayList<>();
        ProjectFileIndex.SERVICE.getInstance(project).iterateContent(new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile fileOrDir) {
                if (fileOrDir.getName().endsWith(".java")) filesToCheck.add(fileOrDir);
                return true;
            }
        });

        System.out.println();
        filesToCheck.forEach(file->{
            System.out.println(file.getName());
        });

        //fileExistsCheck(path);
    }

    private void doWriteAnswerToDocument(Document document, QA qa){
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(qa.getIndex(), qa.getIndex(), "//@Answer " + qa.getAnswer() + "\n");
        });
    }

    private void doGetQasFromDocument(Document document, ArrayList<QA> qas){
        String documentText = document.getText();

        int from = 0;
        while (documentText.indexOf(Configs.QUESTION_TAG, from) >= 0){
            int start = documentText.indexOf(Configs.QUESTION_TAG, from);
            int end = documentText.indexOf("?", start) + 2;
            String question = documentText.substring(start, end);

            qas.add(new QA(question, end, document));
            from = end;
        }
    }

    private void fileExistsCheck(String temp) {
        System.out.println(temp);
        File contentFile = new File(temp + "/content.txt");

        if (!contentFile.exists()){
            try {
                JsonObject content = new JsonObject().put("content", new JsonArray());
                FileWriter writer = new FileWriter(contentFile);
                writer.write(content.toString());
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
