package edu.cmu.hcii.sugilite.model.block.special_operation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.hcii.sugilite.SugiliteData;
import edu.cmu.hcii.sugilite.dao.SugiliteScriptDao;
import edu.cmu.hcii.sugilite.model.block.SugiliteSpecialOperationBlock;
import edu.cmu.hcii.sugilite.model.block.SugiliteStartingBlock;
import edu.cmu.hcii.sugilite.model.variable.StringVariable;
import edu.cmu.hcii.sugilite.model.variable.Variable;
import edu.cmu.hcii.sugilite.pumice.PumiceDemonstrationUtil;
import edu.cmu.hcii.sugilite.ui.dialog.VariableSetValueDialog;

import static edu.cmu.hcii.sugilite.source_parsing.SugiliteScriptExpression.addQuoteToTokenIfNeeded;

/**
 * @author toby
 * @date 10/31/16
 * @time 2:07 PM
 */


public class SugiliteSubscriptSpecialOperationBlock extends SugiliteSpecialOperationBlock {

    private String subscriptName;
    private List<StringVariable> variableValues;

    public SugiliteSubscriptSpecialOperationBlock(String subscriptName){
        super();
        this.subscriptName = subscriptName;
        this.variableValues = new ArrayList<>();
        this.setDescription("Run subscript");
    }

    public SugiliteSubscriptSpecialOperationBlock(){
        super();
        this.variableValues = new ArrayList<>();
        this.setDescription("Run subscript");
    }

    public void setSubscriptName (String subscriptName){
        this.subscriptName = subscriptName;
        this.setDescription("Run subscript: " + subscriptName);
    }

    public String getSubscriptName (){
        return subscriptName;
    }

    public void setVariableValues(List<StringVariable> variableValues) {
        this.variableValues = variableValues;
    }

    public List<StringVariable> getVariableValues() {
        return variableValues;
    }

    @Override
    public void run(Context context, final SugiliteData sugiliteData, SugiliteScriptDao sugiliteScriptDao, final SharedPreferences sharedPreferences) throws Exception{
        final SugiliteStartingBlock script = sugiliteScriptDao.read(subscriptName);

        //send an agent message through pumiceDialogManager if one is available
        if (sugiliteData.pumiceDialogManager != null){
            String ParameterizedProcedureName = PumiceDemonstrationUtil.removeScriptExtension(subscriptName).replace("Procedure_", "");
            for (StringVariable variable : variableValues) {
                ParameterizedProcedureName = ParameterizedProcedureName.replace("[" + variable.getName() + "]", "[" + variable.getValue() + "]");
            }

            sugiliteData.pumiceDialogManager.sendAgentMessage("Executing the procedure: " + ParameterizedProcedureName, true, false);
        }

        if (script != null) {
            Handler mainHandler = new Handler(context.getMainLooper());
            final Context finalContext = context;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    VariableSetValueDialog variableSetValueDialog = new VariableSetValueDialog(finalContext, sugiliteData, script, sharedPreferences, sugiliteData.getCurrentSystemState(), sugiliteData.pumiceDialogManager, false);
                    if (script.variableNameDefaultValueMap.size() > 0) {
                        //has variable
                        sugiliteData.stringVariableMap.putAll(script.variableNameDefaultValueMap);

                        //process variableValues
                        Map<String, StringVariable> alreadyLoadedStringVariableMap = new HashMap<>();

                        //check if variableValue is among alternatives before adding to alreadyLoadedStringVariableMap
                        try {
                            for (StringVariable stringVariable : variableValues) {
                                //TODO: make sure the case matches in variable value
                                if (script.variableNameAlternativeValueMap.containsKey(stringVariable.getName()) && script.variableNameAlternativeValueMap.get(stringVariable.getName()).contains(stringVariable.getValue())) {
                                    alreadyLoadedStringVariableMap.put(stringVariable.getName(), stringVariable);
                                } else {
                                    throw new Exception("Can't find the loaded variable in the script");
                                }
                            }
                        } catch (Exception e) {
                            //TODO: better handle the exception
                            e.printStackTrace();
                        }
                        sugiliteData.stringVariableMap.putAll(alreadyLoadedStringVariableMap);

                        boolean needUserInput = false;
                        for (Map.Entry<String, Variable> entry : script.variableNameDefaultValueMap.entrySet()) {
                            if (entry.getValue().type == Variable.USER_INPUT && (!alreadyLoadedStringVariableMap.containsKey(entry.getKey()))) {
                                needUserInput = true;
                                break;
                            }
                        }

                        variableSetValueDialog.setAlreadyLoadedVariableMap(alreadyLoadedStringVariableMap);
                        if (needUserInput) {
                            //show the dialog to obtain user input - run getNextBlockToRun() after finish executing the current one
                            variableSetValueDialog.show(getNextBlockToRun(), sugiliteData.afterExecutionRunnable);
                        }

                        else {
                            variableSetValueDialog.executeScript(getNextBlockToRun(), sugiliteData.pumiceDialogManager, sugiliteData.afterExecutionRunnable);
                        }
                    } else {
                        //execute the script without showing the dialog - run getNextBlockToRun() after finish executing the current one
                        variableSetValueDialog.executeScript(getNextBlockToRun(), sugiliteData.pumiceDialogManager, sugiliteData.afterExecutionRunnable);
                    }
                }
            };
            mainHandler.post(myRunnable);
        }
        else {
            //ERROR: CAN'T FIND THE SCRIPT
            System.out.println("Can't find the script " + subscriptName);
            throw new Exception("Can't find the script " + subscriptName);
        }
    }

    @Override
    public String toString() {
        return "(" + "call" + " " + "run_script" + " " + addQuoteToTokenIfNeeded(subscriptName) + ")";
    }

    @Override
    public String getPumiceUserReadableDecription() {
        return String.format("Run the subscript named \"%s\".", subscriptName);
    }
}
