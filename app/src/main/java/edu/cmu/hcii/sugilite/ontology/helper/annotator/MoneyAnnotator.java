package edu.cmu.hcii.sugilite.ontology.helper.annotator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.hcii.sugilite.ontology.SugiliteRelation;
import edu.cmu.hcii.sugilite.ontology.helper.annotator.SugiliteTextParentAnnotator.AnnotatingResult;

/**
 * @author toby
 * @date 1/18/18
 * @time 12:24 PM
 */
public class MoneyAnnotator implements SugiliteTextAnnotator {
    private Map<String, List<AnnotatingResult>> cache;

    MoneyAnnotator(){
        cache = new HashMap<>();
    }

    @Override
    public List<AnnotatingResult> annotate(String text) {
        text = text.replaceAll("[\\u00A0\\u2007\\u202F]+", " ");

        if (cache.containsKey(text)){
            return cache.get(text);
        }

        List<AnnotatingResult> results = new ArrayList<>();
        String regex = "[$€£¥] *[0-9]+(\\.[0-9][0-9]?)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String matchedString = text.substring(matcher.start(), matcher.end());
            String numericValue = matchedString.replace("$", "")
                    .replace("€", "")
                    .replace("£", "")
                    .replace("¥", "").trim();
            Double value = null;
            try {
                value = Double.valueOf(numericValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AnnotatingResult result = new AnnotatingResult(RELATION, text.substring(matcher.start(), matcher.end()), matcher.start(), matcher.end(), value);
            results.add(result);
        }

        cache.put(text, results);
        return results;
    }

    private static final SugiliteRelation RELATION = SugiliteRelation.CONTAINS_MONEY;

    public static void main(String[] args) {
        MoneyAnnotator moneyAnnotator = new MoneyAnnotator();
        List<AnnotatingResult> results = moneyAnnotator.annotate("$2.45 or ¥43234");
        System.out.println(results.size());
        System.out.println(results.get(0).getNumericValue().doubleValue());
        System.out.println(results.get(1).getNumericValue().doubleValue());
    }

}
