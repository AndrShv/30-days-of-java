package backend.competing_consumers.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Data
@Builder
public class TaskMessage implements Serializable {
    String taskId;
    int durationInSeconds;

}
