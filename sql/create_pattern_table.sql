CREATE TABLE IF NOT EXISTS public.pattern_data_combined
(
  id          SERIAL PRIMARY KEY NOT NULL,
  inputs      FLOAT[] NOT NULL,
  outputs     FLOAT[] NOT NULL,
  weights     FLOAT[] NOT NULL,
  length      INTEGER NOT NULL,
  origin      VARCHAR(8),
  corrections INTEGER DEFAULT 0 NOT NULL
);