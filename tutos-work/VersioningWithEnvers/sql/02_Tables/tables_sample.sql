DO $$
BEGIN

IF NOT EXISTS (
    SELECT 1 
    FROM information_schema.tables 
    WHERE  table_schema = 'sample' 
    AND table_name = 'customer'
) THEN

CREATE TABLE sample.customer
(
    customer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_at TIMESTAMP
);

END IF;

IF NOT EXISTS (
    SELECT 1
    FROM information_schema.tables
    WHERE  table_schema = 'sample'
    AND table_name = 'customer_aud'
) THEN

CREATE TABLE sample.customer_aud
(
    customer_id SERIAL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    rev integer NOT NULL,
    revtype smallint
);

END IF;

IF NOT EXISTS (
	SELECT 1
	FROM information_schema.tables
	WHERE  table_schema = 'sample'
	AND table_name = 'revinfo'
) THEN

CREATE TABLE sample.revinfo
(
    revision_number integer NOT NULL,
    revision_timestamp bigint,
    CONSTRAINT revinfo_pkey PRIMARY KEY (revision_number)
);

END IF;

END;
$$;