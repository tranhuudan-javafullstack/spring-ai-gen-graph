package vn.com.huudan.ai.temporal.activity.alphabuzz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.com.huudan.ai.service.alphabuzz.CsvGraphPopulatorService;
import vn.com.huudan.ai.temporal.constant.TemporalTaskQueues;

import io.temporal.spring.boot.ActivityImpl;

@Component
@ActivityImpl(taskQueues = TemporalTaskQueues.ALPHABUZZ_CSV_GRAPH_POPULATOR)
public class AlphabuzzCsvGraphPopulatorActivitiesImpl implements AlphabuzzCsvGraphPopulatorActivities {

    private static final Logger LOG = LoggerFactory.getLogger(AlphabuzzCsvGraphPopulatorActivitiesImpl.class);

    @Autowired
    private CsvGraphPopulatorService csvGraphPopulatorService;

    @Override
    public int populateUser() {
        try {
            return csvGraphPopulatorService.populateCsvUser();
        } catch (Exception e) {
            LOG.error("Error in populateUser", e);
            return -1;
        }
    }

    @Override
    public int populateBuzz() {
        try {
            return csvGraphPopulatorService.populateCsvBuzz();
        } catch (Exception e) {
            LOG.error("Error in populateBuzz", e);
            return -1;
        }
    }

    @Override
    public int populateRelationship_Follow() {
        try {
            return csvGraphPopulatorService.populateCsvRelationship_Follow();
        } catch (Exception e) {
            LOG.error("Error in populateRelationship_Follow", e);
            return -1;
        }
    }

    @Override
    public int populateRelationship_Publish() {
        try {
            return csvGraphPopulatorService.populateCsvRelationship_Publish();
        } catch (Exception e) {
            LOG.error("Error in populateRelationship_Publish", e);
            return -1;
        }
    }

    @Override
    public int populateRelationship_Like() {
        try {
            return csvGraphPopulatorService.populateCsvRelationship_Like();
        } catch (Exception e) {
            LOG.error("Error in populateRelationship_Like", e);
            return -1;
        }
    }

    @Override
    public int populateRelationship_Republish() {
        try {
            return csvGraphPopulatorService.populateCsvRelationship_Republish();
        } catch (Exception e) {
            LOG.error("Error in populateRelationship_Republish", e);
            return -1;
        }
    }

    @Override
    public int populateBuzzChildNodes() {
        try {
            return csvGraphPopulatorService.populateBuzzChildNodes();
        } catch (Exception e) {
            LOG.error("Error in populateBuzzChildNodes", e);
            return -1;
        }
    }

}
