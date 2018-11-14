package edu.cmu.hcii.sugilite.model.operation.unary;

import edu.cmu.hcii.sugilite.model.value.SugiliteValue;
import edu.cmu.hcii.sugilite.ontology.SerializableOntologyQuery;

/**
 * @author toby
 * @date 11/13/18
 * @time 11:46 PM
 */
public class SugiliteResolveBoolExpOperation extends SugiliteUnaryOperation<String> implements SugiliteValue<Boolean> {
    private String text;
    public SugiliteResolveBoolExpOperation(){
        super();
        this.setOperationType(RESOLVE_BOOLEXP);
    }

    @Override
    public String getParameter0() {
        return text;
    }

    @Override
    public void setParameter0(String value) {
        this.text = value;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean containsDataDescriptionQuery() {
        return false;
    }

    @Override
    public SerializableOntologyQuery getDataDescriptionQueryIfAvailable() {
        return null;
    }

    @Override
    public Boolean evaluate() {
        //TODO: this should actually execute the query to get the result
        return null;
    }
}
