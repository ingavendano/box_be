-- Database Cleanup Script
-- Goal: Remove all 'client' users and their associated data (tracking, packages, addresses).
-- Keep only 'ROLE_ADMIN' users and system configuration.

BEGIN;

-- 1. Delete Parameter Audit Logs modified by non-admin users
-- (Just in case a non-admin modified parameters, though unlikely)
DELETE FROM historial_parametros_log 
WHERE user_id IN (
    SELECT id 
    FROM users 
    WHERE role <> 'ROLE_ADMIN'
);

-- 2. Delete Tracking Events associated with packages of non-admin users
DELETE FROM tracking_events 
WHERE package_id IN (
    SELECT p.id 
    FROM packages p 
    JOIN users u ON p.user_id = u.id 
    WHERE u.role <> 'ROLE_ADMIN'
);

-- 3. Delete Packages belonging to non-admin users
DELETE FROM packages 
WHERE user_id IN (
    SELECT id 
    FROM users 
    WHERE role <> 'ROLE_ADMIN'
);

-- 4. Delete Addresses belonging to non-admin users
DELETE FROM addresses 
WHERE user_id IN (
    SELECT id 
    FROM users 
    WHERE role <> 'ROLE_ADMIN'
);

-- 5. Delete Users who are not Administrators
-- This removes clients (ROLE_CLIENTE) and delivery personnel (ROLE_REPARTIDOR) if any.
DELETE FROM users 
WHERE role <> 'ROLE_ADMIN';

COMMIT;
