ALTER TABLE session
    ADD is_users_first_session BOOLEAN;

ALTER TABLE session
    ADD num_scrolls INTEGER;