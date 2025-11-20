-- ========================================
-- 测试数据 SQL 脚本
-- ========================================

use farmproject_db;
-- 1. 插入测试用户数据
INSERT INTO
    user (name, role)
VALUES ('张三', 'FARMER'), -- ID 会自动生成,假设为 1
    ('李四', 'FARMER'), -- ID 会自动生成,假设为 2
    ('王五', 'FARMER'), -- ID 会自动生成,假设为 3
    ('赵六', 'STAFF'), -- ID 会自动生成,假设为 4 (操作员)
    ('孙七', 'STAFF');
-- ID 会自动生成,假设为 5 (操作员)

-- 2. 插入测试贷款产品数据
INSERT INTO
    loan_products (
        name,
        product_status,
        bank,
        amount,
        term,
        rate,
        fastest_disbursement,
        description,
        tags,
        created_at,
        updated_at,
        staff_id
    )
VALUES (
        '农业生产贷款A',
        'SALE',
        '农业银行',
        100000.00,
        12,
        4.35,
        '3个工作日',
        '适用于农业生产、种植、养殖等用途',
        '低利率,快速审批',
        NOW(),
        NOW(),
        4 -- 假设操作员 ID 为 4
    ),
    (
        '农业生产贷款B',
        'SALE',
        '农村信用社',
        50000.00,
        6,
        3.85,
        '1个工作日',
        '小额快速贷款,适合短期资金周转',
        '超快放款,灵活还款',
        NOW(),
        NOW(),
        4
    ),
    (
        '农机购置贷款',
        'SALE',
        '工商银行',
        200000.00,
        24,
        4.75,
        '5个工作日',
        '专门用于购买农业机械设备',
        '长期贷款,专项用途',
        NOW(),
        NOW(),
        5
    );

-- 查询插入的数据以确认
SELECT * FROM user;

SELECT * FROM loan_products;