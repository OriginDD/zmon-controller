CREATE OR REPLACE FUNCTION get_entity_by_id(id text) RETURNS SETOF jsonb AS
$$
  SELECT e_data FROM zzm_data.entity WHERE (e_data->'id')::text = '"'||id||'"';
$$ LANGUAGE SQL VOLATILE SECURITY DEFINER;
