package org.pdfsam.alternatemix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.output.SingleTaskOutput;
import org.sejda.model.parameter.AlternateMixMultipleInputParameters;
import org.sejda.model.parameter.base.AbstractPdfOutputParameters;
import org.sejda.model.parameter.base.SingleOutputTaskParameters;
import org.sejda.model.validation.constraint.AtLeastTwo;
import org.sejda.model.validation.constraint.SingleOutputAllowedExtensions;

@SingleOutputAllowedExtensions
public class AlternateMixInputParameters extends AbstractPdfOutputParameters
        implements SingleOutputTaskParameters {
    @Valid
    @NotNull
    private SingleTaskOutput output;

    @Valid
    private List<PdfMixInput> inputList = new ArrayList<PdfMixInput>();

    @Override
    public SingleTaskOutput getOutput() {
        return output;
    }

    @Override
    public void setOutput(SingleTaskOutput output) {
        this.output = output;
    }

    /**
     * @return an unmodifiable view of the inputList
     */
    public List<PdfMixInput> getInputList() {
        return Collections.unmodifiableList(inputList);
    }

    public void addInput(PdfMixInput input) {
        this.inputList.add(input);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(inputList).append(output).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AlternateMixInputParameters)) {
            return false;
        }
        AlternateMixInputParameters parameter = (AlternateMixInputParameters) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(inputList, parameter.inputList)
                .append(output, parameter.getOutput()).isEquals();
    }
}
