package uz.consortgroup.forum_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCoverDto;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseIdsRequest;
import uz.consortgroup.forum_service.config.FeignClientConfig;

import java.util.List;


@FeignClient(
        name = "course-service",
        contextId = "courseClient",
        url = "${course.service.url}",
        configuration = FeignClientConfig.class
)
public interface CourseClient {

    @PostMapping("/internal/forum-directory/course-covers")
    List<CourseCoverDto> getCourseCovers(@RequestBody CourseIdsRequest request);
}
