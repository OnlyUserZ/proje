package com.birgundegelecek.proje.event;

import org.springframework.context.ApplicationEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LikeEvent extends ApplicationEvent {
	
	private final Long sorunId;
	private final boolean likeEklendi;
	
	public LikeEvent(Object source, Long sorunId, boolean likeEklendi) {
        super(source);
        this.sorunId = sorunId;
        this.likeEklendi = likeEklendi;
	}
}
