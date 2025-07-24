package uz.consortgroup.forum_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.forum_service.event.course_group.CourseGroupOpenedEvent;
import uz.consortgroup.forum_service.service.service.ForumService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseGroupOpenedEventProcessor implements ActionProcessor<CourseGroupOpenedEvent> {

    private final ForumService forumService;

    @Override
    public void process(List<CourseGroupOpenedEvent> events) {
        if (events == null || events.isEmpty()) {
            log.info("No CourseGroupOpenedEvent events to process.");
            return;
        }

        log.info("Processing {} CourseGroupOpenedEvent events.", events.size());
        forumService.createForum(events);
        log.info("Processing of CourseGroupOpenedEvent events completed.");
    }
}
