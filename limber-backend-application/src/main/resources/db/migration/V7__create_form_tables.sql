CREATE SCHEMA forms;

CREATE TABLE forms.form_template
(
    guid         UUID UNIQUE NOT NULL,
    created_date TIMESTAMP   NOT NULL,
    feature_guid UUID        NOT NULL,
    title        VARCHAR     NOT NULL,
    description  VARCHAR
);

CREATE TABLE forms.form_template_question
(
    guid               UUID UNIQUE NOT NULL,
    created_date       TIMESTAMP   NOT NULL,
    form_template_guid UUID        NOT NULL REFERENCES forms.form_template (guid) ON DELETE CASCADE,
    rank               INT         NOT NULL,
    label              VARCHAR     NOT NULL,
    help_text          VARCHAR,
    required           BOOLEAN     NOT NULL,
    type               VARCHAR     NOT NULL,
    multi_line         BOOLEAN,
    placeholder        VARCHAR,
    validator          VARCHAR,
    earliest           DATE,
    latest             DATE,
    options            TEXT[],
    UNIQUE (form_template_guid, rank) DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE forms.form_instance
(
    guid                 UUID UNIQUE NOT NULL,
    created_date         TIMESTAMP   NOT NULL,
    feature_guid         UUID        NOT NULL,
    form_template_guid   UUID        NOT NULL REFERENCES forms.form_template (guid) ON DELETE RESTRICT,
    number               BIGINT      NOT NULL,
    submitted_date       TIMESTAMP,
    creator_account_guid UUID        NOT NULL
);

CREATE TABLE forms.form_instance_question
(
    created_date       TIMESTAMP NOT NULL,
    form_instance_guid UUID      NOT NULL REFERENCES forms.form_instance (guid) ON DELETE CASCADE,
    question_guid      UUID      REFERENCES forms.form_template_question (guid) ON DELETE SET NULL,
    type               VARCHAR   NOT NULL,
    text               VARCHAR,
    date               DATE,
    selections         TEXT[],
    UNIQUE (form_instance_guid, question_guid)
);
