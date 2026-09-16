CREATE TABLE submission (
                            id             UUID PRIMARY KEY,
                            email          VARCHAR(255) NOT NULL,
                            thumbnail_key  VARCHAR(512),
                            created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);