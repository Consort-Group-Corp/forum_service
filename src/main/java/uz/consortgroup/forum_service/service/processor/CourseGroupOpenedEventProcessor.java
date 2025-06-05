package uz.consortgroup.forum_service.service.processor;

import org.springframework.stereotype.Service;
import uz.consortgroup.forum_service.event.coursegroup.CourseGroupOpenedEvent;
import uz.consortgroup.forum_service.service.service.ForumService;

import java.util.List;

@Service
public class CourseGroupOpenedEventProcessor implements ActionProcessor<CourseGroupOpenedEvent> {

    private final ForumService forumService;

    public CourseGroupOpenedEventProcessor(ForumService forumService) {
        this.forumService = forumService;
    }

    @Override
    public void process(List<CourseGroupOpenedEvent> events) {
        forumService.createForum(events);
    }
}
