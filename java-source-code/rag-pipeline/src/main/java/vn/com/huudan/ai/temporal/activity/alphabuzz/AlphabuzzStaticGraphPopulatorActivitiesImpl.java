package vn.com.huudan.ai.temporal.activity.alphabuzz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.com.huudan.ai.service.alphabuzz.StaticGraphPopulatorService;
import vn.com.huudan.ai.temporal.constant.TemporalTaskQueues;

import io.temporal.spring.boot.ActivityImpl;

@Component
@ActivityImpl(taskQueues = TemporalTaskQueues.ALPHABUZZ_STATIC_GRAPH_POPULATOR)
public class AlphabuzzStaticGraphPopulatorActivitiesImpl implements AlphabuzzStaticGraphPopulatorActivities {

    @Autowired
    private StaticGraphPopulatorService staticGraphPopulatorService;

    @Override
    public void populateUser() {
        staticGraphPopulatorService.populateStaticUser();
    }

    @Override
    public void populateBuzz() {
        staticGraphPopulatorService.populateStaticBuzz();
    }

    @Override
    public void populateRelationship_Follow() {
        staticGraphPopulatorService.populateStaticRelationship_Follow();
    }

    @Override
    public void populateRelationship_Publish() {
        staticGraphPopulatorService.populateStaticRelationship_Publish();
    }

    @Override
    public void populateRelationship_Like() {
        staticGraphPopulatorService.populateStaticRelationship_Like();
    }

    @Override
    public void populateRelationship_Republish() {
        staticGraphPopulatorService.populateStaticRelationship_Republish();
    }

}
