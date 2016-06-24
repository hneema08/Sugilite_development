package edu.cmu.hcii.sugilite.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;

import edu.cmu.hcii.sugilite.model.block.SugiliteBlock;
import edu.cmu.hcii.sugilite.model.block.SugiliteOperationBlock;
import edu.cmu.hcii.sugilite.model.block.SugiliteStartingBlock;
import edu.cmu.hcii.sugilite.model.operation.SugiliteOperation;
import edu.cmu.hcii.sugilite.model.operation.SugiliteSetTextOperation;

/**
 * @author toby
 * @date 6/21/16
 * @time 4:03 PM
 */
public class ReadableDescriptionGenerator {
    private Map<String, String> packageNameReadableNameMap;
    private PackageManager packageManager;
    public ReadableDescriptionGenerator(Context applicationContext){
        packageNameReadableNameMap = new HashMap<>();
        setupPackageNameReadableNameMap();
        packageManager = applicationContext.getPackageManager();
    }
    public String generateReadableDescription(SugiliteBlock block){
        String message = "";
        if(block instanceof SugiliteStartingBlock)
            return "<b>STARTING BLOCK</b>";
        /**
         * structure: [OPERATION] + "the button/textbox/object" + [IDENTIFIER] + "that has [VIEWID]" + at [LOCATION] + in [PACKAGE]
         */
        if(block instanceof SugiliteOperationBlock){
            SugiliteOperation operation = ((SugiliteOperationBlock) block).getOperation();

            switch (operation.getOperationType()){
                case SugiliteOperation.CLICK:
                    message += setColor("Click ", "#ffa500") + "on ";
                    break;
                case SugiliteOperation.SELECT:
                    message += setColor("Select ", "#ffa500");
                    break;
                case SugiliteOperation.SET_TEXT:
                    message += setColor("Set Text ", "#ffa500") + "to \"" + setColor(((SugiliteSetTextOperation)((SugiliteOperationBlock) block).getOperation()).getText(), "#ff0000") + "\" for ";
                    break;
                case SugiliteOperation.LONG_CLICK:
                    message += setColor("Long click ", "#ffa500") + "on ";
                    break;
            }

            if(((SugiliteOperationBlock) block).getElementMatchingFilter().getClassName() != null){
                switch (((SugiliteOperationBlock) block).getElementMatchingFilter().getClassName()){
                    case "android.widget.ImageButton":
                    case "android.widget.Button":
                    case "android.widget.TextView":
                    case "android.widget.ImageView":
                        message += setColor("the button ", "blue");
                        break;
                    case "android.widget.EditText":
                        message += setColor("the textbox ", "blue");
                        break;
                    default:
                        message += setColor("the object ", "blue");
                }
            }

            if(((SugiliteOperationBlock) block).getElementMatchingFilter().getText() != null){
                message += "\"" + setColor(((SugiliteOperationBlock) block).getElementMatchingFilter().getText(), "#006400")  + "\" ";
            }
            else if (((SugiliteOperationBlock) block).getElementMatchingFilter().getContentDescription() != null){
                message += "\"" + setColor(((SugiliteOperationBlock) block).getElementMatchingFilter().getContentDescription(), "#006400") + "\" ";
            }
            else if (((SugiliteOperationBlock) block).getElementMatchingFilter().getChildFilter()!= null && ((SugiliteOperationBlock) block).getElementMatchingFilter().getChildFilter().getText() != null){
                message += "\"" + setColor(((SugiliteOperationBlock) block).getElementMatchingFilter().getChildFilter().getText(), "#006400") + "\" ";
            }
            else if (((SugiliteOperationBlock) block).getElementMatchingFilter().getChildFilter()!= null && ((SugiliteOperationBlock) block).getElementMatchingFilter().getChildFilter().getContentDescription() != null){
                message += "\"" + setColor(((SugiliteOperationBlock) block).getElementMatchingFilter().getChildFilter().getContentDescription(), "#006400") + "\" ";
            }

            if(((SugiliteOperationBlock) block).getElementMatchingFilter().getViewId() != null){
                message += "that has the view ID \"" + setColor(((SugiliteOperationBlock) block).getElementMatchingFilter().getViewId(), "#800080") + "\" ";
            }

            if(((SugiliteOperationBlock) block).getElementMatchingFilter().getBoundsInScreen() != null){
                message += "at the screen location (" + setColor(((SugiliteOperationBlock) block).getElementMatchingFilter().getBoundsInScreen(), "#006400") + ") ";
            }

            if(((SugiliteOperationBlock) block).getElementMatchingFilter().getBoundsInParent() != null){
                message += "at the parent location (" + setColor(((SugiliteOperationBlock) block).getElementMatchingFilter().getBoundsInParent(), "#006400") + ") ";
            }

            if(((SugiliteOperationBlock) block).getElementMatchingFilter().getPackageName() != null)
                message += "in " + setColor(getReadableName(((SugiliteOperationBlock) block).getElementMatchingFilter().getPackageName()), "#ff00ff") + " ";
            return message;
        }



        return "NULL";
    }

    private void setupPackageNameReadableNameMap(){
        packageNameReadableNameMap.put("com.google.android.googlequicksearchbox", "Home Screen");
    }

    /**
     * get readable app name from package name
     * @param packageName
     * @return
     */
    public String getReadableName(String packageName){
        ApplicationInfo applicationInfo;
        try{
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        }
        catch (Exception e){
            applicationInfo = null;
        }
        if(packageNameReadableNameMap.containsKey(packageName))
            return packageNameReadableNameMap.get(packageName);
        else if (applicationInfo != null)
            return (String)packageManager.getApplicationLabel(applicationInfo);
        else
            return packageName;
    }

    private String setColor(String message, String color){
        return "<font color=\"" + color + "\"><b>" + message + "</b></font>";
    }

}
