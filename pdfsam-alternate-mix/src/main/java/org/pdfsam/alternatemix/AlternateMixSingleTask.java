package org.pdfsam.alternatemix;

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.support.io.IOUtils.createTemporaryBuffer;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.sambox.component.PdfAlternateMixer;
import org.sejda.model.exception.TaskException;
import org.sejda.model.task.BaseTask;
import org.sejda.model.task.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAMBox implementation of the AlternateMix task performing the mix of two given {@link org.sejda.model.input.PdfMixInput}s.
 * 
 * @author Andrea Vacondio
 */
public class AlternateMixSingleTask extends BaseTask<AlternateMixInputParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AlternateMixSingleTask.class);

    private PdfAlternateMixer mixer = null;
    private SingleOutputWriter outputWriter;

    @Override
    public void before(AlternateMixInputParameters parameters, TaskExecutionContext executionContext)
            throws TaskException {
        super.before(parameters, executionContext);
        mixer = new PdfAlternateMixer();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.getExistingOutputPolicy(), executionContext);
    }

    @Override
    public void execute(AlternateMixInputParameters parameters) throws TaskException {

        LOG.debug("Starting alternate mix of {} input documents", parameters.getInputList().size());
        mixer.mix(parameters.getInputList(), executionContext());
        mixer.setVersionOnPDDocument(parameters.getVersion());
        mixer.setCompress(parameters.isCompress());

        File tmpFile = createTemporaryBuffer(parameters.getOutput());
        outputWriter.taskOutput(tmpFile);
        LOG.debug("Temporary output set to {}", tmpFile);
        mixer.savePDDocument(tmpFile);
        nullSafeCloseQuietly(mixer);

        parameters.getOutput().accept(outputWriter);

        LOG.debug("Alternate mix of {} files completed", parameters.getInputList().size());
    }

    @Override
    public void after() {
        nullSafeCloseQuietly(mixer);
    }

}
