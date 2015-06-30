package org.openmhealth.shim.withings.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.openmhealth.schema.domain.omh.*;
import org.openmhealth.shim.common.mapper.JsonNodeDataPointMapper;

import java.util.List;
import java.util.UUID;

import static org.openmhealth.schema.domain.omh.DataPointModality.*;
import static org.openmhealth.schema.domain.omh.DataPointModality.SENSED;


/**
 * Created by Chris Schaefbauer on 6/29/15.
 */
public abstract class WithingsDataPointMapper<T> implements JsonNodeDataPointMapper<T> {
    protected String TIME_ZONE_PROPERTY = "timezone";
    protected String RESOURCE_API_SOURCE_NAME = "Withings Resource API";
    public abstract List<DataPoint<T>> asDataPoints(List<JsonNode> responseNodes);

    protected <T extends Measure> DataPoint<T> newDataPoint(T measure, String sourceName, Long externalId,
            Boolean sensed) {

        DataPointAcquisitionProvenance.Builder provenanceBuilder =
                new DataPointAcquisitionProvenance.Builder(sourceName);

        if (sensed != null ) {
            if(sensed){
                provenanceBuilder.setModality(SENSED);
            }
            else{
                provenanceBuilder.setModality(SELF_REPORTED);
            }

        }

        DataPointAcquisitionProvenance acquisitionProvenance = provenanceBuilder.build();

        // TODO discuss the name of the external identifier, to make it clear it's the ID used by the source
        if (externalId != null) {
            acquisitionProvenance.setAdditionalProperty("external_id", externalId);
        }

        DataPointHeader header = new DataPointHeader.Builder(UUID.randomUUID().toString(), measure.getSchemaId())
                .setAcquisitionProvenance(acquisitionProvenance)
                .build();

        return new DataPoint<>(header, measure);
    }

}
