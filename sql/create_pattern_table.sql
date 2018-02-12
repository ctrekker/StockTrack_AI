CREATE TABLE IF NOT EXISTS public.pattern_data_combined
(
  id          SERIAL PRIMARY KEY NOT NULL,
  origin      VARCHAR(8),
  length      INTEGER NOT NULL,
  corrections INTEGER DEFAULT 0 NOT NULL,
  inputs      FLOAT[] NOT NULL,
  outputs     FLOAT[] NOT NULL,
  weights     FLOAT[] NOT NULL
);