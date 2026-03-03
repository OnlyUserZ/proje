--Active Sessions + Lock + Duration
CREATE OR REPLACE VIEW monitoring_active_sessions AS
SELECT
    a.pid,
    a.usename,
    a.application_name,
    a.client_addr,
    a.backend_start,
    a.xact_start,
    a.query_start,
    now() - a.query_start AS query_duration,
    now() - a.xact_start AS transaction_duration,
    a.state,
    a.wait_event_type,
    a.wait_event,
    l.locktype,
    l.mode,
    l.granted,
    a.query
FROM pg_stat_activity a
LEFT JOIN pg_locks l ON a.pid = l.pid
WHERE a.pid <> pg_backend_pid();


--Top Expensive Queries (pg_stat_statements)
CREATE OR REPLACE VIEW monitoring_top_queries AS
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    min_exec_time,
    max_exec_time,
    stddev_exec_time,
    rows,
    shared_blks_hit,
    shared_blks_read,
    shared_blks_dirtied,
    shared_blks_written,
    local_blks_hit,
    local_blks_read,
    temp_blks_read,
    temp_blks_written
FROM pg_stat_statements
ORDER BY total_exec_time DESC;


--Table Health & Bloat Indicators
CREATE OR REPLACE VIEW monitoring_table_stats AS
SELECT
    relname,
    seq_scan,
    idx_scan,
    n_live_tup,
    n_dead_tup,
    n_tup_ins,
    n_tup_upd,
    n_tup_del,
    vacuum_count,
    autovacuum_count,
    last_vacuum,
    last_autovacuum,
    last_analyze,
    last_autoanalyze
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC;


--Table IO (Cache vs Disk)
CREATE OR REPLACE VIEW monitoring_table_io AS
SELECT
    relname,
    heap_blks_read,
    heap_blks_hit,
    idx_blks_read,
    idx_blks_hit,
    toast_blks_read,
    toast_blks_hit
FROM pg_statio_user_tables
ORDER BY heap_blks_read DESC;


--Blocked Queries (Lock Waiters Only)
CREATE OR REPLACE VIEW monitoring_blocked_queries AS
SELECT
    a.pid,
    a.usename,
    a.application_name,
    a.client_addr,
    a.state,
    a.wait_event_type,
    a.wait_event,
    now() - a.query_start AS waiting_duration,
    a.query
FROM pg_stat_activity a
WHERE a.wait_event_type = 'Lock';


--Checkpoint & Write Pressure
CREATE OR REPLACE VIEW monitoring_bgwriter AS
SELECT
    checkpoints_timed,
    checkpoints_req,
    checkpoint_write_time,
    checkpoint_sync_time,
    buffers_checkpoint,
    buffers_clean,
    buffers_backend,
    buffers_backend_fsync,
    buffers_alloc
FROM pg_stat_bgwriter;