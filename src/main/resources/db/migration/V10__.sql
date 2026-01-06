ALTER TABLE band_user_event
    ADD session_id BIGINT;

ALTER TABLE venue_user_event
    ADD session_id BIGINT;

ALTER TABLE video_user_event
    ADD session_id BIGINT;

ALTER TABLE band_user_event
    ADD CONSTRAINT FK_BANDUSEREVENT_ON_SESSION FOREIGN KEY (session_id) REFERENCES session (id);

ALTER TABLE venue_user_event
    ADD CONSTRAINT FK_VENUEUSEREVENT_ON_SESSION FOREIGN KEY (session_id) REFERENCES session (id);

ALTER TABLE video_user_event
    ADD CONSTRAINT FK_VIDEOUSEREVENT_ON_SESSION FOREIGN KEY (session_id) REFERENCES session (id);