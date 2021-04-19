package com.ixuxie.eventbus.event;

import com.ixuxie.utils.eventbus.Event;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEvent implements Event {

    private Long uid;

}
