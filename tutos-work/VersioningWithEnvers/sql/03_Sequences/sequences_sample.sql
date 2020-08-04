DO $$
BEGIN

IF NOT EXISTS (
    SELECT 0 FROM pg_class where relname = 'hibernate_sequence'
) THEN

CREATE SEQUENCE hibernate_sequence  INCREMENT 1  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

END IF;

END;
$$;