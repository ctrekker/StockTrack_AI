SELECT $1 FROM public.stock_data_combined
  WHERE symbol='$2' AND entry_time > now() - INTERVAL '3 days'
  ORDER BY $3 DESC
  LIMIT $4;