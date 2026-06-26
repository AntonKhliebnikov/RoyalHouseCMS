ALTER TABLE persistent_logins
    ALTER COLUMN last_used TYPE TIMESTAMPTZ
        USING last_used AT TIME ZONE 'Europe/Kyiv';