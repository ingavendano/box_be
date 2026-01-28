CREATE TABLE IF NOT EXISTS tracking_statuses (
    code VARCHAR(255) NOT NULL,
    active BIT NOT NULL,
    description VARCHAR(255),
    name VARCHAR(255),
    sort_order INT,
    PRIMARY KEY (code)
);
