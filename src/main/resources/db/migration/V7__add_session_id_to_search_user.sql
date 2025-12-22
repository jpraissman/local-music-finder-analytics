ALTER TABLE search_user_event
    ADD session_id BIGINT;

ALTER TABLE search_user_event
    ADD CONSTRAINT FK_SEARCHUSEREVENT_ON_SESSION FOREIGN KEY (session_id) REFERENCES session (id);