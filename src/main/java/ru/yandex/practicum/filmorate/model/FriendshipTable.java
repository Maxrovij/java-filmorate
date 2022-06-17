package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class FriendshipTable {
    private final Long firstUserId;
    private final Long secondUserId;
    private boolean status;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
