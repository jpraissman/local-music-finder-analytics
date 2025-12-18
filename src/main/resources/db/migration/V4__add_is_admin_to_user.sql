ALTER TABLE lmf_user
    ADD is_admin BOOLEAN;

ALTER TABLE lmf_user
    ALTER COLUMN is_admin SET NOT NULL;