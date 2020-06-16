package edu.cmu.hcii.sugilite.pumice.dao;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.cmu.hcii.sugilite.R;
import edu.cmu.hcii.sugilite.SugiliteData;
import edu.cmu.hcii.sugilite.pumice.kb.PumiceKnowledgeManager;
import edu.cmu.hcii.sugilite.ui.dialog.SugiliteProgressDialog;

/**
 * @author toby
 * @date 2/14/19
 * @time 10:54 AM
 */
public class PumiceKnowledgeDao {
    Context context;
    private File knowledgeDir;
    private static final String fileName = "data.PumiceKnowledge";

    public PumiceKnowledgeDao(Context context, SugiliteData sugiliteData){
        this.context = context;
        try {
            File rootDataDir = context.getFilesDir();
            knowledgeDir = new File(rootDataDir.getPath() + "/pumice_knowledge");
            if (!knowledgeDir.exists() || !knowledgeDir.isDirectory())
                knowledgeDir.mkdir();
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
            //TODO: error handling
        }
    }

    public PumiceKnowledgeManager getPumiceKnowledgeManagerFromFile() throws IOException, ClassNotFoundException {
        //read the script out from the file, and put it into the cache
        SugiliteProgressDialog progressDialog = new SugiliteProgressDialog(SugiliteData.getAppContext(), R.string.loading_kb_message);
        progressDialog.show();

        FileInputStream fin = null;
        ObjectInputStream ois = null;
        PumiceKnowledgeManager pumiceKnowledge = null;
        try {
            fin = new FileInputStream(knowledgeDir.getPath() + "/" + fileName);
            ois = new ObjectInputStream(new BufferedInputStream(fin));
            pumiceKnowledge = (PumiceKnowledgeManager) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return null;
        } finally {
            if (fin != null)
                fin.close();
            if (ois != null)
                ois.close();
        }
        progressDialog.dismiss();
        return pumiceKnowledge;
    }

    public void savePumiceKnowledge(PumiceKnowledgeManager pumiceKnowledge) throws IOException, ClassNotFoundException{
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(knowledgeDir.getPath() + "/" + fileName);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(pumiceKnowledge);
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
            //TODO: error handling
        }
        finally {
            if(oos != null)
                oos.close();
            if(fout != null)
                fout.close();
        }
    }

    public PumiceKnowledgeManager getPumiceKnowledgeOrANewInstanceIfNotAvailable(boolean toAddDefaultContentForNewInstance, boolean toAddBuiltInQueries) throws IOException, ClassNotFoundException {
        PumiceKnowledgeManager pumiceKnowledgeManager = getPumiceKnowledgeManagerFromFile();
        if (pumiceKnowledgeManager == null){
            pumiceKnowledgeManager = new PumiceKnowledgeManager();

            //add the default test content
            if (toAddDefaultContentForNewInstance) {
                pumiceKnowledgeManager.initForTesting();
            }

            if (toAddBuiltInQueries) {
                pumiceKnowledgeManager.initWithBuiltInKnowledge();
            }
        }
        return pumiceKnowledgeManager;
    }



}
